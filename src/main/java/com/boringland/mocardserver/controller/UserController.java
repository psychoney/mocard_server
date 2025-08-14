package com.boringland.mocardserver.controller;

import com.boringland.mocardserver.annotation.CurrentUser;
import com.boringland.mocardserver.annotation.NoAuth;
import com.boringland.mocardserver.entity.dto.UserAccountVO;
import com.boringland.mocardserver.entity.dto.UserFewshotVO;
import com.boringland.mocardserver.entity.model.UserAccount;
import com.boringland.mocardserver.entity.request.LoginRequest;
import com.boringland.mocardserver.entity.request.VIPRequest;
import com.boringland.mocardserver.service.TokenService;
import com.boringland.mocardserver.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    private final TokenService tokenService;

    @PostMapping("/login")
    @NoAuth
    public UserAccountVO login(@RequestBody LoginRequest loginRequest) {
        UserAccountVO userAccountVO = userService.login(loginRequest.getUniqueID());
        userAccountVO.setToken(tokenService.createToken(loginRequest.getUniqueID()));
        return userAccountVO;
    }


    @GetMapping("/getLimit")
    public Map getLimit(@CurrentUser UserAccount userAccount) {
        return userService.getLimit(userAccount);
    }


    @GetMapping("/fewshotHistory")
    public List<UserFewshotVO> fewshotHistory(@CurrentUser UserAccount userAccount) {
        List<UserFewshotVO> userFewshotVOList = userService.getUserFewShot(userAccount.getDeviceId());
        return userFewshotVOList;
    }


    @GetMapping("/deleteFewshotHistory")
    public void deleteFewshotHistory(@CurrentUser UserAccount userAccount) {
        userService.deleteFewshotHistory(userAccount.getDeviceId());
    }
}
