package com.boringland.mocardserver.controller;

import cn.hutool.core.util.StrUtil;
import com.boringland.mocardserver.annotation.CheckMembership;
import com.boringland.mocardserver.annotation.CurrentUser;
import com.boringland.mocardserver.entity.dto.Txt2imgDTO;
import com.boringland.mocardserver.entity.model.UserAccount;
import com.boringland.mocardserver.entity.request.ChatRequest;
import com.boringland.mocardserver.entity.request.ShotPromptingRequest;
import com.boringland.mocardserver.service.SseService;

import com.unfbx.chatgpt.exception.BaseException;
import com.unfbx.chatgpt.exception.CommonError;

import java.util.Map;


import lombok.extern.slf4j.Slf4j;

import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@Slf4j
@RequestMapping("chat")
public class ChatController {

    private final SseService sseService;

    public ChatController(SseService sseService) {
        this.sseService = sseService;
    }

    /**
     * 创建sse连接
     *
     * @param headers
     * @return
     */
    @GetMapping("/createSse")
    public SseEmitter createConnect(@RequestHeader Map<String, String> headers) {
        String uid = getUid(headers);
        return sseService.createSse(uid);
    }

    /**
     * 聊天接口
     *
     * @param chatRequest
     * @param headers
     */
    @PostMapping("/chat")
    @ResponseBody
    @CheckMembership
    public void sseChat(@RequestBody ChatRequest chatRequest, @RequestHeader Map<String, String> headers, @CurrentUser UserAccount userAccount) {
        String uid = getUid(headers);
        sseService.sseChat(uid, chatRequest, userAccount);
    }

    /**
     *  Shot Prompting 工具接口
     *
     * @param shotPromptingRequest
     * @param headers
     */
    @PostMapping("/shotPrompting")
    @ResponseBody
    @CheckMembership
    public void sseShotPrompting(@RequestBody ShotPromptingRequest shotPromptingRequest, @RequestHeader Map<String, String> headers, @CurrentUser UserAccount userAccount) {
        String uid = getUid(headers);
        sseService.shotPrompting(uid, shotPromptingRequest, userAccount);
        return;
    }

    /**
     * 画画接口
     *
     * @param headers
     */
    @PostMapping("/draw")
    @ResponseBody
    @CheckMembership
    public void draw(@RequestBody Txt2imgDTO txt2imgDTO, @RequestHeader Map<String, String> headers, @CurrentUser UserAccount userAccount) {
        String uid = getUid(headers);
        sseService.sseDraw(uid, txt2imgDTO, userAccount);
    }

    /**
     * 关闭连接
     *
     * @param headers
     */
    @GetMapping("/closeSse")
    public void closeConnect(@RequestHeader Map<String, String> headers) {
        String uid = getUid(headers);
        sseService.closeSse(uid);
    }


    /**
     * 获取uid
     *
     * @param headers
     * @return
     */
    private String getUid(Map<String, String> headers) {
        String uid = headers.get("uid");
        if (StrUtil.isBlank(uid)) {
            throw new BaseException(CommonError.SYS_ERROR);
        }
        return uid;
    }
}