package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.dto.account.AccountDetailsDto;
import com.smrp.smartmedicinealarm.dto.account.NewAccountDto;
import com.smrp.smartmedicinealarm.dto.account.SimpleAccountDto;
import com.smrp.smartmedicinealarm.service.account.AccountService;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

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

    @PreAuthorize("hasRole('NORMAL')")
    @GetMapping("/{accountId}")
    public AccountDetailsDto accountDetails(@PathVariable Long accountId){
        return accountService.findAccount(accountId);
    }

    @GetMapping("")
    public PagedModel<EntityModel<SimpleAccountDto>> accountList(
            @ApiParam(value = "페이지 번호", defaultValue = "0", example = "0")
            @RequestParam(value = "page", defaultValue = "0") int page,
            @ApiParam(value = "보여질 페이지 사이즈", defaultValue = "10", example = "10")
            @RequestParam(defaultValue = "10") int size,
            PagedResourcesAssembler<SimpleAccountDto> assembler
    ){
        return assembler.toModel(
                accountService.findAllAccounts(page, size)
        );
    }

    @PreAuthorize("hasRole('NORMAL')")
    @DeleteMapping("/{deletedId}")
    public ResponseEntity<?> accountRemove(
            @ApiParam(value = "삭제할 유저 PK")
            @PathVariable Long deletedId
    ){
        accountService.removeAccount(deletedId);
        return ResponseEntity.ok().build();
    }


}
