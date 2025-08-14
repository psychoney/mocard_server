package com.boringland.mocardserver.service.impl;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import com.boringland.mocardserver.entity.dto.UserAccountVO;
import com.boringland.mocardserver.entity.dto.UserFewshotVO;
import com.boringland.mocardserver.entity.model.*;
import com.boringland.mocardserver.service.UserService;
import com.unfbx.chatgpt.entity.chat.Message;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserAccountRepository userAccountRepository;

    private final UserFewshotRepository userFewshotRepository;

    private final CardInfoRepository cardInfoRepository;

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${useagelimit.count}")
    private Integer useagelimit;

    @Override
    @CacheEvict(value = "vip", key = "#uniqueID")
    public UserAccountVO login(String uniqueID) {
        // Look for an existing UserAccount with the same deviceId
        UserAccount userAccount = userAccountRepository.findByDeviceId(uniqueID);

        if (userAccount == null) {
            // If there's no existing UserAccount, create a new one
            userAccount = new UserAccount();
            userAccount.setDeviceId(uniqueID);
            userAccount.setCreateTime(LocalDateTime.now());
        }

        // Update the lastLogin field
        userAccount.setLastLogin(LocalDateTime.now());

        // Save the UserAccount (either the updated existing one or the new one)
        UserAccount account = userAccountRepository.save(userAccount);

        UserAccountVO userAccountVO = new UserAccountVO();
        BeanUtil.copyProperties(account, userAccountVO);
        return userAccountVO;
    }

    @Override
    @CacheEvict(value = "vip", key = "#userAccount.deviceId")
    public UserAccountVO createVIP(String type, UserAccount userAccount) {
        userAccount.setMemberType(type);
        userAccount.setPurchaseTime(LocalDateTime.now());
        userAccount.setExpiryTime(LocalDateTime.now().plusDays(getExpiryTime(type)));
        userAccountRepository.save(userAccount);
        UserAccountVO userAccountVO = new UserAccountVO();
        BeanUtil.copyProperties(userAccount, userAccountVO);
        return userAccountVO;
    }

    @Override
    @CacheEvict(value = "vip", key = "#userAccount.deviceId")
    public UserAccountVO refundVIP(UserAccount userAccount) {
        userAccount.setMemberType("normal");
        userAccount.setUpdateTime(LocalDateTime.now());
        userAccountRepository.save(userAccount);
        UserAccountVO userAccountVO = new UserAccountVO();
        BeanUtil.copyProperties(userAccount, userAccountVO);
        return userAccountVO;
    }

    private int getExpiryTime(String type) {
        switch (type) {
            case "week":
                return 7;
            case "month":
                return 31;
            case "year":
                return 365;
            default:
                return 0;
        }
    }

    @Override
    @Cacheable(value = "vip", key = "#uniqueID")
    public boolean isVIP(String uniqueID){
        UserAccount userAccount = userAccountRepository.findByDeviceId(uniqueID);
        if (userAccount.getMemberType().equals("normal")) {
            return false;
        }
        if(userAccount.getExpiryTime().isBefore(LocalDateTime.now())){
            userAccount.setMemberType("normal");
            userAccountRepository.save(userAccount);
            return false;
        }
        return true;
    }

    @Override
    public boolean limitCheckCanUse(String uniqueID) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String redisKey = "user_usage_app:" + uniqueID + ":" + currentDate;
        if (stringRedisTemplate.hasKey(redisKey)) {
            int usage = Integer.parseInt(stringRedisTemplate.opsForValue().get(redisKey));
            log.info("uniqueID:{} ******* usage: {}", uniqueID, usage);
            return usage < useagelimit;
        } else {
            return true;
        }
    }


    @Override
    @Async
    public void addUsageCount(String uniqueID) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String redisKey = "user_usage_app:" + uniqueID + ":" + currentDate;
        if (stringRedisTemplate.hasKey(redisKey)) {
            stringRedisTemplate.opsForValue().increment(redisKey, 1);
        } else {
            stringRedisTemplate.opsForValue().set(redisKey, String.valueOf(1));
            LocalDateTime currentTime = LocalDateTime.now();
            LocalDateTime nextDayMidnight = currentTime.toLocalDate().plusDays(1).atStartOfDay();
            int secondsUntilMidnight = (int) Duration.between(currentTime, nextDayMidnight).getSeconds();
            stringRedisTemplate.expire(redisKey, Duration.ofSeconds(secondsUntilMidnight)); // Expire key at midnight
        }
    }


    @Override
    @Async
    @CacheEvict(value = "fewshot", key = "#deviceId")
    public void saveUserFewShot(String deviceId, Integer cardId, String uid, Message message) {
        UserFewshot userFewshot = new UserFewshot();
        userFewshot.setDeviceId(deviceId);
        userFewshot.setCardId(cardId);
        userFewshot.setRole(message.getRole());
        userFewshot.setContent(message.getContent());
        userFewshot.setUid(uid);
        userFewshot.setDeleteFlag(0);
        userFewshotRepository.save(userFewshot);
    }

    @Override
    public Map getLimit(UserAccount userAccount) {
        Map limitMap = new HashMap();
        limitMap.put("isVIP", isVIP(userAccount.getDeviceId()));
        limitMap.put("useagelimit", useagelimit);
        limitMap.put("useage", getUseage(userAccount.getDeviceId()));
        limitMap.put("expiryTime", userAccount.getExpiryTime() == null ? null : userAccount.getExpiryTime().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));
        limitMap.put("memberType", getMemberTypeString(userAccount.getMemberType()));
        return limitMap;
    }

    private String getMemberTypeString(String memberType){
        switch (memberType) {
            case "week":
                return "周卡会员";
            case "month":
                return "月卡会员";
            case "year":
                return "年卡会员";
            default:
                return "普通会员";
        }
    }

    @Override
    public Integer getUseage(String uniqueID) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
        String redisKey = "user_usage_app:" + uniqueID + ":" + currentDate;
        if (stringRedisTemplate.hasKey(redisKey)) {
            int usage = Integer.parseInt(stringRedisTemplate.opsForValue().get(redisKey));
            log.info("uniqueID:{} ******* usage: {}", uniqueID, usage);
            return usage;
        } else {
            return 0;
        }
    }

    @Override
    @Cacheable(value = "fewshot", key = "#deviceId")
    public List<UserFewshotVO> getUserFewShot(String deviceId) {
        Map<String, UserFewshotVO> userFewshotVOMap = new HashMap<>();
        List<UserFewshot> userFewshotList = userFewshotRepository.findByDeviceIdAndDeleteFlagOrderByCreateTimeDesc(deviceId, 0);
        if (CollectionUtil.isEmpty(userFewshotList)) {
            return null;
        }
        userFewshotList.forEach(userFewshot -> {
            UserFewshotVO userFewshotVO = userFewshotVOMap.get(userFewshot.getUid());
            if (userFewshotVO == null) {
                userFewshotVO = new UserFewshotVO();
                BeanUtil.copyProperties(userFewshot, userFewshotVO);
                Optional<CardInfo> cardInfo = cardInfoRepository.findById(userFewshot.getCardId());
                if (cardInfo.isPresent()) {
                    userFewshotVO.setImageUrl(cardInfo.get().getImageUrl());
                    userFewshotVO.setType(cardInfo.get().getType());
                }
            }
            if (userFewshot.getRole().equals("user")){
                userFewshotVO.setQuestion(userFewshot.getContent());
            } else {
                userFewshotVO.setAnswer(userFewshot.getContent());
            }
            userFewshotVOMap.put(userFewshot.getUid(), userFewshotVO);
        });
        return  userFewshotVOMap.values().stream()
                .sorted(Comparator.comparing(UserFewshotVO::getCreateTime).reversed())
                .collect(Collectors.toList());
    }


    @Override
    @CacheEvict(value = "fewshot", key = "#deviceId")
    public void deleteFewshotHistory(String deviceId) {
        int count = userFewshotRepository.setDeleteFlagForDeviceId(deviceId);
        log.info("deleteFewshotHistory count:{}, deviceId: {}", count, deviceId);
    }
}
