package com.boringland.mocardserver.service;

import com.boringland.mocardserver.entity.dto.Card;
import com.boringland.mocardserver.entity.dto.UserFewshotVO;

import java.util.List;

public interface SettinngService {

    List<Card> cardList(String cardType);

    Card getCard(Integer cardId);

    void updateCache(String cardType, String deviceId);
    List<UserFewshotVO> fewshotSample();

}

