package com.boringland.mocardserver.service;


import com.boringland.mocardserver.entity.dto.UserAccountVO;
import com.boringland.mocardserver.entity.dto.UserFewshotVO;
import com.boringland.mocardserver.entity.model.UserAccount;
import com.unfbx.chatgpt.entity.chat.Message;

import java.util.List;
import java.util.Map;

public interface UserService {

    UserAccountVO login(String uniqueID);

    UserAccountVO createVIP(String type, UserAccount userAccount);

    UserAccountVO refundVIP(UserAccount userAccount);

    boolean isVIP(String uniqueID);

    boolean limitCheckCanUse(String uniqueID);

    void addUsageCount(String uniqueID);

    void saveUserFewShot(String deviceId, Integer cardId, String uid, Message message);

    Map getLimit(UserAccount userAccount);

    Integer getUseage(String deviceId);

    List<UserFewshotVO> getUserFewShot(String deviceId);

    void deleteFewshotHistory(String deviceId);

}

