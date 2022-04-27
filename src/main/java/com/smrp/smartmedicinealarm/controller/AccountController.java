package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.dto.account.NewAccountDto;
import com.smrp.smartmedicinealarm.service.account.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(value = "/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService accountService;

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public NewAccountDto.Response accountAdd(
            @Valid @RequestBody NewAccountDto.Request request,
            BindingResult error
    ){
        return accountService.addAccount(request);
    }

}
