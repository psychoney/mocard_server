package com.boringland.mocardserver.service;

import com.boringland.mocardserver.entity.dto.Txt2imgDTO;
import com.boringland.mocardserver.entity.model.UserAccount;
import com.boringland.mocardserver.entity.request.ChatRequest;
import com.boringland.mocardserver.entity.request.ShotPromptingRequest;
import com.boringland.mocardserver.entity.response.ChatResponse;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

public interface SseService {
    /**
     * 创建SSE
     * @param uid
     * @return
     */
    SseEmitter createSse(String uid);

    /**
     * 关闭SSE
     * @param uid
     */
    void closeSse(String uid);

    /**
     * 客户端发送消息到服务端
     * @param uid
     * @param chatRequest
     */
    ChatResponse sseChat(String uid, ChatRequest chatRequest, UserAccount userAccount);

    /**
     * Shot Prompting 工具接口
     * @param uid
     * @param shotPromptingRequest
     */
    void shotPrompting(String uid, ShotPromptingRequest shotPromptingRequest, UserAccount userAccount);

    /**
     * 画图工具接口
     * @param uid
     * @param txt2imgDTO
     */
    void sseDraw(String uid, Txt2imgDTO txt2imgDTO, UserAccount userAccount);
}

