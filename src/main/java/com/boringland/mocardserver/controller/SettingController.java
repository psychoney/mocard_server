package com.boringland.mocardserver.controller;

import com.boringland.mocardserver.annotation.NoAuth;
import com.boringland.mocardserver.entity.dto.Card;
import com.boringland.mocardserver.entity.dto.UserFewshotVO;
import com.boringland.mocardserver.service.SettinngService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/setting")
public class SettingController {

    @Autowired
    SettinngService settinngService;

    @GetMapping("/cardList")
    public List<Card> cardList(@RequestParam String cardType) {
        List<Card> cardLIst = settinngService.cardList(cardType);
        return cardLIst;
    }

    @GetMapping("/getCard")
    public Card getCard(@RequestParam Integer cardId) {
        return settinngService.getCard(cardId);
    }

    @GetMapping("/updateCache")
    @NoAuth
    public void updateCache(@RequestParam String cardType, @RequestParam String deviceId) {
        settinngService.updateCache(cardType, deviceId);
    }

    @GetMapping("/fewshotSample")
    public List<UserFewshotVO> fewshotSample() {
        List<UserFewshotVO> userFewshotVOList = settinngService.fewshotSample();
        return userFewshotVOList;
    }

}