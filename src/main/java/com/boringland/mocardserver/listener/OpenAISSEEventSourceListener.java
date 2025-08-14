package com.boringland.mocardserver.listener;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.boringland.mocardserver.config.LocalCache;
import com.boringland.mocardserver.entity.model.UserAccount;
import com.boringland.mocardserver.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.ChatCompletionResponse;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

import com.unfbx.chatgpt.entity.chat.Message;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okhttp3.sse.EventSource;
import okhttp3.sse.EventSourceListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
public class OpenAISSEEventSourceListener extends EventSourceListener {

    private long tokens;

    private UserService userService;

    private SseEmitter sseEmitter;

    private UserAccount userAccount;

    private Integer cardId;

    private String uid;

    private boolean collectedMessageFlag = false;

    private String collectedMessage = "";

    // 创建一个单线程的ScheduledExecutorService
    private final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();

    // 可能需要取消的定时任务的引用
    private ScheduledFuture<?> retryTask;

    private int retryCount = 0;
    private static final int MAX_RETRIES = 5;


    private OpenAiStreamClient openAiStreamClient;
    private ChatCompletion completion;


    public OpenAISSEEventSourceListener(SseEmitter sseEmitter, UserService userService, UserAccount userAccount, boolean collectedMessageFlag, Integer cardId, String uid,  OpenAiStreamClient openAiStreamClient, ChatCompletion completion){
        this.sseEmitter = sseEmitter;
        this.userService = userService;
        this.userAccount = userAccount;
        this.collectedMessageFlag = collectedMessageFlag;
        this.cardId = cardId;
        this.uid = uid;
        this.openAiStreamClient = openAiStreamClient;
        this.completion = completion;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onOpen(EventSource eventSource, Response response) {
        log.info("OpenAI建立sse连接...,time:{}", System.currentTimeMillis());
        // 连接成功，取消定时任务
        if (retryTask != null) {
            retryTask.cancel(false);
            retryTask = null;
        }


    }

    /**
     * {@inheritDoc}
     */
    @SneakyThrows
    @Override
    public void onEvent(EventSource eventSource, String id, String type, String data) {
        log.info("OpenAI返回数据：{}", data);
        tokens += 1;
        if (data.equals("[DONE]")) {
            log.info("OpenAI返回数据结束了");
            // 记录用户请求次数
            userService.addUsageCount(userAccount.getDeviceId());
            // 获取openai系统返回assistant的数据
            Message currentMessage = Message.builder().content(collectedMessage).role(Message.Role.ASSISTANT).build();
            // 如果cardId不为空，说明是卡片模式，需要将卡片id保存
            if (ObjectUtil.isNotEmpty(cardId)) {
                userService.saveUserFewShot(userAccount.getDeviceId(), cardId, uid, currentMessage);
            }
            // 保存上下文
            if (collectedMessageFlag) {
                String messageContext = (String) LocalCache.CACHE.get("sseChat" + userAccount.getDeviceId());
                List<Message> messages = new ArrayList<>();
                if (StrUtil.isNotBlank(messageContext)) {
                    messages = JSONUtil.toList(messageContext, Message.class);
                }
                messages.add(currentMessage);
                log.info("当前用户{}的聊天记录：{}", userAccount.getDeviceId(), JSONUtil.toJsonStr(messages));
                LocalCache.CACHE.put("sseChat" + userAccount.getDeviceId(), JSONUtil.toJsonStr(messages), LocalCache.TIMEOUT);
            }

            sseEmitter.send(SseEmitter.event()
                    .id("[TOKENS]")
                    .data("<br/><br/>tokens：" + tokens())
                    .reconnectTime(3000));
            sseEmitter.send(SseEmitter.event()
                    .id("[DONE]")
                    .data("[DONE]")
                    .reconnectTime(3000));
            // 传输完成后自动关闭sse
            sseEmitter.complete();
            return;
        }
        ObjectMapper mapper = new ObjectMapper();
        ChatCompletionResponse completionResponse = mapper.readValue(data, ChatCompletionResponse.class); // 读取Json
        if (StrUtil.isNotBlank(completionResponse.getChoices().get(0).getDelta().getContent())){
            collectedMessage = collectedMessage + completionResponse.getChoices().get(0).getDelta().getContent();
        }
        try {
            sseEmitter.send(SseEmitter.event()
                    .id(completionResponse.getId())
                    .data(completionResponse.getChoices().get(0).getDelta())
                    .reconnectTime(3000));
        } catch (Exception e) {
            log.error("sse信息推送失败！");
            eventSource.cancel();
            e.printStackTrace();
        }
    }


    @Override
    public void onClosed(EventSource eventSource) {
        log.info("流式输出返回值总共{}tokens", tokens() - 2);
        log.info("OpenAI关闭sse连接...");
        // 连接关闭后，重置重试计数器
        retryCount = 0;
    }


    @SneakyThrows
    @Override
    public void onFailure(EventSource eventSource, Throwable t, Response response) {
        if (Objects.isNull(response)) {
            return;
        }
        ResponseBody body = response.body();
        if (Objects.nonNull(body)) {
            log.error("OpenAI  sse连接异常data：{}，异常：{}", body.string(), t);
        } else {
            log.error("OpenAI  sse连接异常data：{}，异常：{}", response, t);
        }
        eventSource.cancel();
        // 连接失败后，重置重试计数器
        retryCount = 0;
    }

    /**
     * tokens
     *
     * @return
     */
    public long tokens() {
        return tokens;
    }

    public void start() {
        // 取消任何旧的定时任务
        if (retryTask != null) {
            retryTask.cancel(false);
            retryTask = null;
        }
        openAiStreamClient.streamChatCompletion(completion, this);
        log.info("OpenAI开始建立sse连接, time: {}", System.currentTimeMillis());
        // 开始新的定时任务，如果5秒后没有连接成功，则重试
        retryTask = executor.schedule(this::reSend, 5, TimeUnit.SECONDS);
    }

    private void reSend() {
        log.error("SSE连接超时，尝试重试...");
        retryCount++;  // 增加重试计数器
        if (retryCount <= MAX_RETRIES) {
            this.start();
        } else {
            log.error("已达到最大重试次数，停止尝试。");
        }
    }


}
