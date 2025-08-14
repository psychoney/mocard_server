package com.boringland.mocardserver.service;

import java.util.List;

public interface ChatService {
    String openaiApi(List messgaes, String openid);
}
