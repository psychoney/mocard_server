package com.boringland.mocardserver.controller;

import com.alibaba.fastjson.JSONObject;
import com.boringland.mocardserver.annotation.CurrentUser;
import com.boringland.mocardserver.annotation.NoAuth;
import com.boringland.mocardserver.entity.dto.Card;
import com.boringland.mocardserver.entity.dto.HandleReceiptVO;
import com.boringland.mocardserver.entity.dto.OrderDTO;
import com.boringland.mocardserver.entity.dto.ReceiptDTO;
import com.boringland.mocardserver.entity.model.UserAccount;
import com.boringland.mocardserver.entity.model.UserOrder;
import com.boringland.mocardserver.service.PurchaseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@Slf4j
@RequestMapping("/purchase")
public class PurchaseController {

    @Autowired
    PurchaseService purchaseService;

    @PostMapping("/createOrder")
    public UserOrder createOrder(@CurrentUser UserAccount userAccount, @RequestBody OrderDTO orderDTO) {
        return purchaseService.createOrder(userAccount, orderDTO.getOrderType());
    }

    @PostMapping("/handleReceipt")
    public HandleReceiptVO handleReceipt(@CurrentUser UserAccount userAccount, @RequestBody ReceiptDTO receiptDTO) throws IOException {
        return purchaseService.handleReceipt(userAccount, receiptDTO);
    }

    @PostMapping("/handleNotification")
    @NoAuth
    public void handleNotification(@RequestBody JSONObject notification) {
        purchaseService.handleNotification(notification);
    }


}