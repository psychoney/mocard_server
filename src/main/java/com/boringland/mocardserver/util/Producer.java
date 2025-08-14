package com.boringland.mocardserver.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class Producer {
    private static final Logger log = LoggerFactory.getLogger(Producer.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;

    public Producer() {
    }

    public void produce(String queue, Object data) {
        this.rabbitTemplate.convertAndSend(queue, data);
    }
}