package com.boringland.mocardserver.service;


import com.alibaba.fastjson.JSONObject;
import com.boringland.mocardserver.entity.dto.HandleReceiptVO;
import com.boringland.mocardserver.entity.dto.ReceiptDTO;
import com.boringland.mocardserver.entity.model.UserAccount;
import com.boringland.mocardserver.entity.model.UserOrder;

import java.io.IOException;

public interface PurchaseService {

    UserOrder createOrder(UserAccount userAccount, String vipType);

    HandleReceiptVO handleReceipt(UserAccount userAccount, ReceiptDTO receiptDTO) throws IOException;

    void handleNotification(JSONObject object);

    void processReceipt(ReceiptDTO receiptDTO) throws IOException;

}

