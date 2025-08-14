package com.boringland.mocardserver.service.impl;


import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.boringland.mocardserver.constant.RabbitConstant;
import com.boringland.mocardserver.entity.dto.HandleReceiptVO;
import com.boringland.mocardserver.entity.dto.NotificationDTO;
import com.boringland.mocardserver.entity.dto.ReceiptDTO;
import com.boringland.mocardserver.entity.model.*;
import com.boringland.mocardserver.service.PurchaseService;

import com.boringland.mocardserver.service.UserService;
import com.boringland.mocardserver.util.JwsUtil;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import okhttp3.*;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;


@Service
@Slf4j
@RequiredArgsConstructor
public class PurchaseServiceImpl implements PurchaseService {


    private final UserOrderRepository userOrderRepository;
    private final UserReceiptRepository userReceiptRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserService userService;
    private final JwsUtil jwsUtil;
    private final RabbitTemplate rabbitTemplate;

    @Value("${apple.verifyReceipt.testUrl}")
    private String verifyReceiptTestUrl;

    @Value("${apple.verifyReceipt.url}")
    private String verifyReceiptUrl;

    @Value("${apple.verifyReceipt.password}")
    private String password;


    @Override
    public UserOrder createOrder(UserAccount userAccount, String vipType) {
        UserOrder userOrder = new UserOrder();
        userOrder.setUserId(userAccount.getId());
        userOrder.setDeviceId(userAccount.getDeviceId());
        userOrder.setOrderType(vipType);
        userOrder.setStatus("pending");
        return userOrderRepository.save(userOrder);
    }

    @Override
    public HandleReceiptVO handleReceipt(UserAccount userAccount, ReceiptDTO receiptDTO) throws IOException {
        HandleReceiptVO handleReceiptVO = new HandleReceiptVO();
        UserOrder userOrder = new UserOrder();
        if (receiptDTO.getOrderId() == -1) {
            // 继续验证之前未结束的订单
            log.info("handleReceipt continue: orderId is null");
            List<UserReceipt> userReceiptList = userReceiptRepository.findByDeviceIdAndProductId(userAccount.getDeviceId(), receiptDTO.getProductId());
            if (CollectionUtil.isNotEmpty(userReceiptList)) {
                UserReceipt userReceipt = userReceiptList.get(0);
                receiptDTO.setOrderId(userReceipt.getOrderId());
                Optional<UserOrder> userOrderOptional = userOrderRepository.findById(userReceipt.getOrderId());
                if (userOrderOptional.isPresent()) {
                    userOrder = userOrderOptional.get();
                } else {
                    log.error("handleReceipt error: userOrder not found");
                    handleReceiptVO.setSuccess(false);
                    return handleReceiptVO;
                }
            }
        } else {
            // 验证订单
            Optional<UserOrder> userOrderOptional = userOrderRepository.findById(receiptDTO.getOrderId());
            if (!userOrderOptional.isPresent()) {
                log.error("handleReceipt error: userOrder not found");
                handleReceiptVO.setSuccess(false);
                return handleReceiptVO;
            }
            userOrder = userOrderOptional.get();
        }
        // 先返回成功再验证
        handleReceiptVO.setOrderId(receiptDTO.getOrderId());
        handleReceiptVO.setSuccess(true);
        userOrder.setStatus("purchased");
        userService.createVIP(userOrder.getOrderType(), userAccount);
        userOrderRepository.save(userOrder);
        saveReceipt(userAccount, receiptDTO);
        rabbitTemplate.convertAndSend(RabbitConstant.RABBITMQ_QUEUE_RECEIPT_APPLE_MSG, receiptDTO);
        return handleReceiptVO;
    }

    @RabbitHandler
    @RabbitListener(queuesToDeclare = @Queue(RabbitConstant.RABBITMQ_QUEUE_RECEIPT_APPLE_MSG))
    public void processReceipt(ReceiptDTO receiptDTO) throws IOException {
        log.info("process receiptDTO, productId:{}, orderId:{}, transactionId:{}", receiptDTO.getProductId(), receiptDTO.getOrderId(), receiptDTO.getTransactionId());
        boolean verifyResult = verifyReceipt(receiptDTO);
        log.info("verifyResult: {}", verifyResult);
    }


    @Async
    void saveReceipt(UserAccount userAccount, ReceiptDTO receiptDTO) {
        List<UserReceipt> userReceiptList = userReceiptRepository.findByTransactionId(receiptDTO.getTransactionId());
        if (CollectionUtil.isEmpty(userReceiptList)) {
            UserReceipt userReceipt = new UserReceipt();
            userReceipt.setOrderId(receiptDTO.getOrderId());
            userReceipt.setPurchaseStatus(receiptDTO.getPurchaseStatus());
            userReceipt.setReceipt(receiptDTO.getReceipt());
            userReceipt.setDeviceId(userAccount.getDeviceId());
            userReceipt.setProductId(receiptDTO.getProductId());
            userReceipt.setTransactionId(receiptDTO.getTransactionId());
            userReceipt.setCheckStatus("uncheck");
            userReceiptRepository.save(userReceipt);
        }
    }


    private boolean verifyReceipt(ReceiptDTO receiptDTO) throws IOException {
        Map body = new HashMap();
        body.put("receipt-data", receiptDTO.getReceipt());
        body.put("password", password);
        OkHttpClient client = new OkHttpClient();
        RequestBody requestBody = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), JSON.toJSONString(body));
        Request request = new Request.Builder().url(verifyReceiptUrl).post(requestBody).addHeader("Content-Type", "application/json").build();
        Response response = client.newCall(request).execute();
        String responseString = response.body().string();
//        log.info("verifyReceipt response: {}", responseString);
        // 21007 receipt是Sandbox receipt，但却发送至生产系统的验证服务,需要验证沙盒环境
        JSONObject responseJson = JSONObject.parseObject(responseString);
        if ((Integer) responseJson.get("status") == 21007) {
            request = new Request.Builder().url(verifyReceiptTestUrl).post(requestBody).addHeader("Content-Type", "application/json").build();
            response = client.newCall(request).execute();
            responseString = response.body().string();
//            log.info("verifyReceiptTest response: {}", responseString);
            responseJson = JSONObject.parseObject(responseString);
        }
        UserReceipt userReceipt = userReceiptRepository.findByTransactionId(receiptDTO.getTransactionId()).get(0);
        userReceipt.setReceiptStatus(responseJson.getString("status"));
        userReceipt.setEnvironment(responseJson.getString("environment"));
        userReceipt.setCheckStatus("fail");
        userReceipt.setUpdateTime(LocalDateTime.now());
        if (responseJson.getInteger("status") == 0) {
            JSONObject receiptInfo = responseJson.getJSONObject("receipt");
            JSONArray latestReceiptInfoList = responseJson.getJSONArray("latest_receipt_info");
            userReceipt.setReceiptInfo(receiptInfo.toJSONString());
            userReceipt.setLatestReceiptInfo(latestReceiptInfoList.toJSONString());
            Set transactionIds = new HashSet();
            if (!CollectionUtils.isEmpty(latestReceiptInfoList)) {
                // PurchaseStatus.restored不判断transaction_id是否存在，直接保存最新的交易信息并返回
                if (receiptDTO.getPurchaseStatus().equals("PurchaseStatus.restored")) {
                    JSONObject latestReceiptInfoJson = latestReceiptInfoList.getJSONObject(0);
                    userReceipt.setOriginalTransactionId(latestReceiptInfoJson.getString("original_transaction_id"));
                    ZoneId zoneId = ZoneId.systemDefault();
                    LocalDateTime originalPurchaseDate = Instant.ofEpochMilli(latestReceiptInfoJson.getLong("original_purchase_date_ms")).atZone(zoneId).toLocalDateTime();
                    LocalDateTime expiresDate = Instant.ofEpochMilli(latestReceiptInfoJson.getLong("expires_date_ms")).atZone(zoneId).toLocalDateTime();
                    LocalDateTime purchaseDate = Instant.ofEpochMilli(latestReceiptInfoJson.getLong("purchase_date_ms")).atZone(zoneId).toLocalDateTime();
                    userReceipt.setOriginalPurchaseDate(originalPurchaseDate);
                    userReceipt.setExpiresDate(expiresDate);
                    userReceipt.setPurchaseDate(purchaseDate);
                    userReceipt.setCheckStatus("success");
                    transactionIds.add(receiptDTO.getTransactionId());
                } else {
                    // PurchaseStatus.purchased需要判断transaction_id是否在收据中存在
                    latestReceiptInfoList.stream().forEach(latestReceiptInfo -> {
                        JSONObject latestReceiptInfoJson = (JSONObject) latestReceiptInfo;
                        String transactionId = latestReceiptInfoJson.getString("transaction_id");
                        transactionIds.add(transactionId);
                        if (receiptDTO.getTransactionId().equals(transactionId)) {
                            userReceipt.setOriginalTransactionId(latestReceiptInfoJson.getString("original_transaction_id"));
                            ZoneId zoneId = ZoneId.systemDefault();
                            LocalDateTime originalPurchaseDate = Instant.ofEpochMilli(latestReceiptInfoJson.getLong("original_purchase_date_ms")).atZone(zoneId).toLocalDateTime();
                            LocalDateTime expiresDate = Instant.ofEpochMilli(latestReceiptInfoJson.getLong("expires_date_ms")).atZone(zoneId).toLocalDateTime();
                            LocalDateTime purchaseDate = Instant.ofEpochMilli(latestReceiptInfoJson.getLong("purchase_date_ms")).atZone(zoneId).toLocalDateTime();
                            userReceipt.setOriginalPurchaseDate(originalPurchaseDate);
                            userReceipt.setExpiresDate(expiresDate);
                            userReceipt.setPurchaseDate(purchaseDate);
                            userReceipt.setCheckStatus("success");
                        }
                    });
                }
            }
            userReceiptRepository.save(userReceipt);
            return transactionIds.contains(receiptDTO.getTransactionId());
        }
        userReceiptRepository.save(userReceipt);
        log.info("verifyReceipt error: {}", responseJson.getString("status"));
        return false;
    }


    @Override
    public void handleNotification(JSONObject notification) {
        String signedPayload = new String(Base64.getUrlDecoder().decode(notification.getString("signedPayload").split("\\.")[0]));
        JSONObject jsonObject = JSONObject.parseObject(signedPayload);
        Jws<Claims> result = jwsUtil.verifyJWT(jsonObject.getJSONArray("x5c").get(0).toString(), notification.getString("signedPayload"));
        String notificationType = result.getBody().get("notificationType").toString();
        Claims map = result.getBody();
        HashMap<String, Object> envmap = map.get("data", HashMap.class);
        String env = envmap.get("environment").toString();
        String resulttran = new String(Base64.getUrlDecoder().decode(envmap.get("signedTransactionInfo").toString().split("\\.")[0]));
        JSONObject jsonObjecttran = JSONObject.parseObject(resulttran);
        Jws<Claims> result3 = jwsUtil.verifyJWT(jsonObjecttran.getJSONArray("x5c").get(0).toString(), envmap.get("signedTransactionInfo").toString());
        log.info(result3.getBody().toString());
        log.info("苹果通知类型：" + notificationType + "环境:" + env);
        String transactionId = result3.getBody().get("transactionId").toString();
        String productId = result3.getBody().get("productId").toString();
        String originalTransactionId = result3.getBody().get("originalTransactionId").toString();
        List<UserReceipt> userReceiptList = userReceiptRepository.findByOriginalTransactionId(originalTransactionId);
        if (CollectionUtil.isEmpty(userReceiptList)) {
            log.info("userReceiptList is empty, originalTransactionId: {}", originalTransactionId);
            return;
        }
        UserReceipt userReceipt = userReceiptList.get(0);
        String deviceId = userReceipt.getDeviceId();
        Integer orderId = userReceipt.getOrderId();
        UserAccount userAccount = userAccountRepository.findByDeviceId(deviceId);
        NotificationDTO notificationDTO = new NotificationDTO();
        notificationDTO.setDeviceId(deviceId);
        notificationDTO.setOrderId(orderId);
        notificationDTO.setProductId(productId);
        notificationDTO.setTransactionId(transactionId);
        notificationDTO.setOriginalTransactionId(originalTransactionId);
        notificationDTO.setNotificationType(notificationType);
        notificationDTO.setEnvironment(env);
        notificationDTO.setExpiresDateMs((Long) result3.getBody().get("expiresDate"));
        notificationDTO.setOriginalPurchaseDateMs((Long) result3.getBody().get("originalPurchaseDate"));
        notificationDTO.setPurchaseDateMs((Long) result3.getBody().get("purchaseDate"));
        notificationDTO.setReceiptInfo(result3.getBody().toString());
        saveNotification(userAccount, notificationDTO);
        if (notificationType.equals("DID_RENEW")) {
            //自动续订成功
            log.info("自动续订成功");
            userService.createVIP(getTypeByProductId(productId), userAccount);
        } else if (notificationType.equals("DID_FAIL_TO_RENEW")) {
            //自动续订失败
            log.info("自动续订失败");
        } else if (notificationType.equals("DID_RECOVER")) {
            //恢复购买
            log.info("恢复购买");
            userService.createVIP(getTypeByProductId(productId), userAccount);
        } else if (notificationType.equals("DID_CHANGE_RENEWAL_PREF")) {
            //续订降级
            log.info("续订降级");
            userService.createVIP(getTypeByProductId(productId), userAccount);
        } else if (notificationType.equals("DID_CHANGE_RENEWAL_STATUS")) {
            //续订状态发生变化
            log.info("续订状态发生变化");
        } else if (notificationType.equals("REFUND")) {
            //退款成功
            log.info("退款成功");
            userService.refundVIP(userAccount);
        } else if (notificationType.equals("CANCEL")) {
            //取消订阅
            log.info("取消订阅");
        } else if (notificationType.equals("INITIAL_BUY")) {
            //首次购买
            log.info("首次购买");
        } else {
            log.info("notificationType未处理：" + notificationType);
        }
    }

    private String getTypeByProductId(String productId) {
        switch (productId) {
            case "com.mtclanguage_3'":
                return "week";
            case "com.mtclanguage_9":
                return "month";
            case "com.mtclanguage_30":
                return "year";
            default:
                return "week";
        }
    }

    @Async
    void saveNotification(UserAccount userAccount, NotificationDTO notification) {
        List<UserReceipt> userReceiptList = userReceiptRepository.findByTransactionId(notification.getTransactionId());
        if (CollectionUtil.isEmpty(userReceiptList)) {
            UserReceipt userReceipt = new UserReceipt();
            userReceipt.setOrderId(notification.getOrderId());
            userReceipt.setReceiptInfo(notification.getReceiptInfo());
            userReceipt.setDeviceId(userAccount.getDeviceId());
            userReceipt.setProductId(notification.getProductId());
            userReceipt.setTransactionId(notification.getTransactionId());
            userReceipt.setCheckStatus("success");
            userReceipt.setReceiptStatus(notification.getNotificationType());
            userReceipt.setEnvironment(notification.getEnvironment());
            userReceipt.setOriginalTransactionId(notification.getOriginalTransactionId());
            ZoneId zoneId = ZoneId.systemDefault();
            LocalDateTime originalPurchaseDate = Instant.ofEpochMilli(notification.getOriginalPurchaseDateMs()).atZone(zoneId).toLocalDateTime();
            LocalDateTime expiresDate = Instant.ofEpochMilli(notification.getExpiresDateMs()).atZone(zoneId).toLocalDateTime();
            LocalDateTime purchaseDate = Instant.ofEpochMilli(notification.getPurchaseDateMs()).atZone(zoneId).toLocalDateTime();
            userReceipt.setOriginalPurchaseDate(originalPurchaseDate);
            userReceipt.setPurchaseDate(expiresDate);
            userReceipt.setExpiresDate(purchaseDate);
            userReceipt.setPurchaseStatus(notification.getNotificationType());
            userReceiptRepository.save(userReceipt);
        }
    }
}
