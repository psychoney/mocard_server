package com.boringland.mocardserver.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.boringland.mocardserver.config.LocalCache;
import com.boringland.mocardserver.entity.dto.Txt2imgDTO;
import com.boringland.mocardserver.entity.model.UserAccount;
import com.boringland.mocardserver.entity.request.ChatRequest;
import com.boringland.mocardserver.entity.request.ShotPromptingRequest;
import com.boringland.mocardserver.entity.response.ChatResponse;
import com.boringland.mocardserver.listener.OpenAISSEEventSourceListener;
import com.boringland.mocardserver.service.SseService;
import com.boringland.mocardserver.service.UserService;
import com.unfbx.chatgpt.OpenAiStreamClient;
import com.unfbx.chatgpt.entity.chat.ChatCompletion;
import com.unfbx.chatgpt.entity.chat.Message;
import com.unfbx.chatgpt.exception.BaseException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class SseServiceImpl implements SseService {

    private final OpenAiStreamClient openAiStreamClient;

    @Autowired
    UserService userService;

    public SseServiceImpl(OpenAiStreamClient openAiStreamClient) {
        this.openAiStreamClient = openAiStreamClient;
    }

    @Override
    public SseEmitter createSse(String uid) {
        //默认30秒超时,设置为0L则永不超时
        SseEmitter sseEmitter = new SseEmitter(0l);
        //完成后回调
        sseEmitter.onCompletion(() -> {
            log.info("[{}]结束连接...................", uid);
            LocalCache.CACHE.remove(uid);
        });
        //超时回调
        sseEmitter.onTimeout(() -> {
            log.info("[{}]连接超时...................", uid);
        });
        //异常回调
        sseEmitter.onError(
                throwable -> {
                    try {
                        log.info("[{}]连接异常,{}", uid, throwable.toString());
                        sseEmitter.send(SseEmitter.event()
                                .id(uid)
                                .name("发生异常！")
                                .data(Message.builder().content("发生异常请重试！").build())
                                .reconnectTime(3000));
                        LocalCache.CACHE.put(uid, sseEmitter);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
        LocalCache.CACHE.put(uid, sseEmitter);
        log.info("[{}]创建sse连接成功！", uid);
        return sseEmitter;
    }

    @Override
    public void closeSse(String uid) {
        SseEmitter sse = (SseEmitter) LocalCache.CACHE.get(uid);
        if (sse != null) {
            sse.complete();
            //移除
            LocalCache.CACHE.remove(uid);
        }
    }

    @Override
    public ChatResponse sseChat(String uid, ChatRequest chatRequest, UserAccount userAccount) {
        if (StrUtil.isBlank(chatRequest.getMsg())) {
            log.info("参数异常，msg为null", uid);
            throw new BaseException("参数异常，msg不能为空~");
        }
        String messageContext = (String) LocalCache.CACHE.get("sseChat" + userAccount.getDeviceId());
        List<Message> messages = new ArrayList<>();
        if (StrUtil.isNotBlank(messageContext)) {
            messages = JSONUtil.toList(messageContext, Message.class);
            if (messages.size() >= 10) {
                messages = messages.subList(messages.size() - 10, messages.size());
            }
            Message currentMessage = Message.builder().content(chatRequest.getMsg()).role(Message.Role.USER).build();
            messages.add(currentMessage);
        } else {
            Message currentMessage = Message.builder().content(chatRequest.getMsg()).role(Message.Role.USER).build();
            messages.add(currentMessage);
        }

        SseEmitter sseEmitter = (SseEmitter) LocalCache.CACHE.get(uid);

        if (sseEmitter == null) {
            log.info("聊天消息推送失败uid:[{}],没有创建连接，请重试。", uid);
            throw new BaseException("聊天消息推送失败uid:[{}],没有创建连接，请重试。~");
        }
        ChatCompletion completion = ChatCompletion
                .builder()
                .messages(messages)
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .build();
        OpenAISSEEventSourceListener openAIEventSourceListener = new OpenAISSEEventSourceListener(sseEmitter, userService, userAccount, true, null, uid, openAiStreamClient, completion);
//        openAiStreamClient.streamChatCompletion(completion, openAIEventSourceListener);
        openAIEventSourceListener.start();
        LocalCache.CACHE.put("sseChat" + userAccount.getDeviceId(), JSONUtil.toJsonStr(messages), LocalCache.TIMEOUT);
        ChatResponse response = new ChatResponse();
        response.setQuestionTokens(completion.tokens());
        return response;
    }

    @Override
    public void shotPrompting(String uid, ShotPromptingRequest shotPromptingRequest, UserAccount userAccount) {
        log.info("shotPromptingRequest:{}", JSONUtil.toJsonStr(shotPromptingRequest));
        if (CollectionUtil.isEmpty(shotPromptingRequest.getMessages())) {
            log.info("参数异常，msg为null", uid);
            throw new BaseException("参数异常，msg不能为空~");
        }
        SseEmitter sseEmitter = (SseEmitter) LocalCache.CACHE.get(uid);
        if (sseEmitter == null) {
            log.info("聊天消息推送失败uid:[{}],没有创建连接，请重试。", uid);
            throw new BaseException("聊天消息推送失败uid:[{}],没有创建连接，请重试。~");
        }
        ChatCompletion completion = ChatCompletion
                .builder()
                .temperature(0.5)//温度设置为0.5将导致较保守的输出，而温度为1将创建更富创意和自发的输出
//                .frequencyPenalty(-1)//频率惩罚，用于减少重复的模型输出
                .messages(shotPromptingRequest.getMessages())
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .build();
        OpenAISSEEventSourceListener openAIEventSourceListener = new OpenAISSEEventSourceListener(sseEmitter, userService, userAccount, false, shotPromptingRequest.getCardId(), uid, openAiStreamClient, completion);
//        openAiStreamClient.streamChatCompletion(completion, openAIEventSourceListener);
        openAIEventSourceListener.start();
        if (ObjectUtil.isNotEmpty(shotPromptingRequest.getCardId())) {
            userService.saveUserFewShot(userAccount.getDeviceId(), shotPromptingRequest.getCardId(), uid, shotPromptingRequest.getMessages().get(shotPromptingRequest.getMessages().size() - 1));
        }
    }

    @Override
    public void sseDraw(String uid, Txt2imgDTO txt2imgDTO, UserAccount userAccount) {
        txt2imgDTO.setUniqueID(userAccount.getDeviceId());
        log.info("sseDraw txt2imgDTO:{}", JSONUtil.toJsonStr(txt2imgDTO));
        // 创建一个 HttpHeaders 对象并设置 Content-Type 为 application/json
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("uid", uid);  // 这个假设 uid 是请求头的一部分

        // 创建一个 HttpEntity 对象，它代表了请求的主体和头
        HttpEntity<Txt2imgDTO> entity = new HttpEntity<>(txt2imgDTO, headers);

        // 创建一个 RestTemplate 对象并发送 POST 请求
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<Map> response = restTemplate.exchange("http://127.0.0.1:8091/boringland-mini/sse/chat",
                HttpMethod.POST, entity, Map.class);
        log.info("sseDraw response:{}", JSONUtil.toJsonStr(response));
    }
}
