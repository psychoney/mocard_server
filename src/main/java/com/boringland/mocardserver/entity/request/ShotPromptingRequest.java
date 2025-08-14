package com.boringland.mocardserver.entity.request;

import com.unfbx.chatgpt.entity.chat.Message;
import lombok.Data;

import java.util.List;

@Data
public class ShotPromptingRequest {
        /**
         * 客户端发送的问题参数
         */
        private List<Message> messages;

        /**
         * 卡片Id
         */
        private Integer cardId;
}
