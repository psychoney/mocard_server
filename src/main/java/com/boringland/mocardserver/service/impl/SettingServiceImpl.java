package com.boringland.mocardserver.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.boringland.mocardserver.entity.dto.Card;
import com.boringland.mocardserver.entity.dto.Message;
import com.boringland.mocardserver.entity.dto.UserFewshotVO;
import com.boringland.mocardserver.entity.model.CardContext;
import com.boringland.mocardserver.entity.model.CardContextRepository;
import com.boringland.mocardserver.entity.model.CardInfo;
import com.boringland.mocardserver.entity.model.CardInfoRepository;
import com.boringland.mocardserver.service.SettinngService;
import com.unfbx.chatgpt.exception.BaseException;
import com.unfbx.chatgpt.exception.CommonError;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class SettingServiceImpl implements SettinngService {


    private final CardInfoRepository cardInfoRepository;
    private final CardContextRepository cardContextRepository;


    public static final String MY_KEY = "fewshotSample";


    @Override
    @Cacheable(value = "cardList", key = "#cardType")
    public List<Card> cardList(String cardType) {
        List<Card> cardList = new ArrayList();
        if (StrUtil.isBlank(cardType)) {
            throw new BaseException(CommonError.PARAM_ERROR);
        }
        List<CardInfo> cardInfoList = cardInfoRepository.findByTypeOrderByCreateTime(cardType);
        if (CollectionUtil.isNotEmpty(cardInfoList)) {
            cardInfoList.forEach(cardInfo -> {
                Card card = cardInfotoCard(cardInfo);
                cardList.add(card);
            });
        }
        return cardList;
    }

    @Override
    @Cacheable(value = "getCard", key = "#cardId")
    public Card getCard(Integer cardId) {
        Optional<CardInfo> cardInfo = cardInfoRepository.findById(cardId);
        if (cardInfo.isPresent()) {
            return cardInfotoCard(cardInfo.get());
        }
        return null;
    }

    @Override
    @Cacheable(value = "fewshotSample", key = "#root.target.MY_KEY")
    public List<UserFewshotVO> fewshotSample() {
        List<CardInfo> cardInfoList = cardInfoRepository.findAll();
        if (CollectionUtil.isNotEmpty(cardInfoList)) {
            List<UserFewshotVO> userFewshotVOList = new ArrayList<>();
            cardInfoList.forEach(cardInfo -> {
                UserFewshotVO userFewshotVO = new UserFewshotVO();
                userFewshotVO.setCardId(cardInfo.getId().toString());
                userFewshotVO.setImageUrl(cardInfo.getImageUrl());
                userFewshotVO.setType(cardInfo.getType());
                userFewshotVO.setCreateTime(cardInfo.getCreateTime());
                userFewshotVO.setDeviceId("sample");
                userFewshotVO.setUid("sample");
                List<CardContext> cardContextList = cardContextRepository.findByPidOrderByOrderIndex(cardInfo.getId());
                if (CollectionUtil.isNotEmpty(cardContextList)) {
                    cardContextList.forEach(cardContext -> {
                        // OrderIndex 2为用户消息
                        if (2 == cardContext.getOrderIndex()) {
                            userFewshotVO.setQuestion(cardContext.getContent());
                        }
                        // OrderIndex 3为机器人消息
                        if (3 == cardContext.getOrderIndex()) {
                            userFewshotVO.setAnswer(cardContext.getContent());
                        }
                    });
                }
                userFewshotVOList.add(userFewshotVO);
            });
            return userFewshotVOList;
        }
        return null;
    }

    @Override
    @Caching(evict = {@CacheEvict(value = "cardList", key = "#cardType"),
            @CacheEvict(value = "fewshot", key = "#deviceId"),
            @CacheEvict(value = "vip", key = "#deviceId"),
            @CacheEvict(value = "fewshotSample", key = "#root.target.MY_KEY")})
    public void updateCache(String cardType, String deviceId) {
        log.info("---------------清除服务端缓存--------------");
    }

    private Card cardInfotoCard(CardInfo cardInfo) {
        Card card = new Card();
        card.setId(cardInfo.getId().toString());
        card.setTitle(cardInfo.getTitle());
        card.setType(cardInfo.getType());
        card.setColor(cardInfo.getColor());
        card.setDescription(cardInfo.getDescription());
        card.setImageUrl(cardInfo.getImageUrl());
        List<CardContext> cardContextList = cardContextRepository.findByPidOrderByOrderIndex(cardInfo.getId());
        if (CollectionUtil.isNotEmpty(cardContextList)) {
            cardContextList.forEach(cardContext -> {
                // 不需要使用fewshot
                if (0 == cardInfo.getFewshot()) {
                    Message message = new Message();
                    message.setRole(cardContext.getRole());
                    message.setContent(cardContext.getContent());
                    // 只添加OrderIndex为1的system消息
                    if (1 == cardContext.getOrderIndex()) {
                        card.getRole().add(message);
                    }
                    if (2 == cardContext.getOrderIndex() || 3 == cardContext.getOrderIndex()) {
                        card.getDemo().add(message);
                    }
                } else {
                    // 需要使用fewshot
                    Message message = new Message();
                    message.setRole(cardContext.getRole());
                    message.setContent(cardContext.getContent());
                    // 添加OrderIndex为1、2、3的全部消息
                    card.getRole().add(message);
                    if (2 == cardContext.getOrderIndex() || 3 == cardContext.getOrderIndex()) {
                        card.getDemo().add(message);
                    }
                }
            });
        }
        return card;
    }
}
