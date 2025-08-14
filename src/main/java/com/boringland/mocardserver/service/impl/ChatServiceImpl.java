package com.boringland.mocardserver.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.alibaba.fastjson.JSONObject;
import com.boringland.mocardserver.config.LocalCache;
import com.boringland.mocardserver.entity.dto.MsgQueueDTO;
import com.boringland.mocardserver.service.ChatService;
import com.unfbx.chatgpt.OpenAiClient;
import com.unfbx.chatgpt.entity.chat.*;
import com.unfbx.chatgpt.interceptor.OpenAILogger;
import com.unfbx.chatgpt.interceptor.OpenAiResponseInterceptor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import lombok.extern.slf4j.Slf4j;
import okhttp3.OkHttpClient;
import okhttp3.OkHttpClient.Builder;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import static com.unfbx.chatgpt.entity.chat.BaseChatCompletion.Model.GPT_3_5_TURBO_1106;

@Service
@Slf4j
public class ChatServiceImpl implements ChatService {
    @Value("${chatgpt.apiKey}")
    private List<String> apiKey;
    @Value("${chatgpt.apiHost}")
    private String apiHost;
    @Value("${wx.mp.host}")
    private String mpHost;
    @Value("${wx.mp.port}")
    private String mpPort;

    @RabbitHandler
    @RabbitListener(queuesToDeclare = {@Queue("rabbitmq_queue_chatgpt_msg")})
    public void process(MsgQueueDTO msgQueueDTO) {
        String messageContext = (String)LocalCache.CACHE.get(msgQueueDTO.getOpenid());
        List<Message> messages = new ArrayList();
        Message currentMessage;
        if (StrUtil.isNotBlank(messageContext)) {
            messages = JSONUtil.toList(messageContext, Message.class);
            if (((List)messages).size() >= 10) {
                messages = ((List)messages).subList(((List)messages).size() - 10, ((List)messages).size());
            }

            currentMessage = Message.builder().content(msgQueueDTO.getContent()).role(BaseMessage.Role.USER).build();
            ((List)messages).add(currentMessage);
        } else {
            if(StrUtil.isNotBlank(msgQueueDTO.getContent())){
                currentMessage = Message.builder().content(msgQueueDTO.getContent()).role(BaseMessage.Role.USER).build();
                ((List)messages).add(currentMessage);
            }
        }

        LocalCache.CACHE.put(msgQueueDTO.getOpenid(), JSONUtil.toJsonStr(messages), LocalCache.TIMEOUT);
        log.info("openaiApi messages:{}", JSONUtil.toJsonStr(messages));
        String result = this.openaiApi((List)messages, msgQueueDTO.getOpenid());
        log.info("openaiApi result:{}", result);
        String posturl = "http://" + this.mpHost + ":" + this.mpPort + "/boringland-mini/wxbot/kfmsg/sendMsg";
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("openid", msgQueueDTO.getOpenid());
        jsonObject.put("content", result);
        RestTemplate restTemplate = new RestTemplate();
        Object res = restTemplate.postForObject(posturl, jsonObject, Object.class, new Object[0]);
        log.info("sendMsg result:{}", JSONUtil.toJsonStr(res));
    }

    public String openaiApi(List messages, String openid) {
        String result = "";
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor(new OpenAILogger());
        httpLoggingInterceptor.setLevel(Level.BODY);
        OkHttpClient okHttpClient = (new Builder()).addInterceptor(httpLoggingInterceptor).addInterceptor(new OpenAiResponseInterceptor()).connectTimeout(100L, TimeUnit.SECONDS).writeTimeout(300L, TimeUnit.SECONDS).readTimeout(300L, TimeUnit.SECONDS).build();
        OpenAiClient openAiClient = OpenAiClient.builder().apiKey(apiKey).apiHost(this.apiHost).okHttpClient(okHttpClient).build();
        ChatCompletion chatCompletion = ChatCompletion.builder().model(GPT_3_5_TURBO_1106.getName()).messages(messages).build();
        ChatCompletionResponse chatCompletionResponse = openAiClient.chatCompletion(chatCompletion);

        ChatChoice choice;
        for(Iterator var9 = chatCompletionResponse.getChoices().iterator(); var9.hasNext(); result = result + choice.getMessage().getContent().replaceAll("\n\n", "\n")) {
            choice = (ChatChoice)var9.next();
        }

        String messageContext = (String)LocalCache.CACHE.get(openid);
        messages = JSONUtil.toList(messageContext, Message.class);
        if (messages.size() >= 10) {
            messages = messages.subList(messages.size() - 10, messages.size());
        }

        Message currentMessage = Message.builder().content(result).role(BaseMessage.Role.ASSISTANT).build();
        messages.add(currentMessage);
        LocalCache.CACHE.put(openid, JSONUtil.toJsonStr(messages), LocalCache.TIMEOUT);
        return result;
    }

    public ChatServiceImpl() {
    }
}
