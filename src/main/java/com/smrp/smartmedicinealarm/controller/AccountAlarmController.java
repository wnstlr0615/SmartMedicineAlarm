package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.dto.alarm.SimpleAlarmDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.service.account.AccountAlarmService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping(value = "/api/v1/accounts/me/alarms")
@RequiredArgsConstructor
public class AccountAlarmController {
    private final AccountAlarmService accountAlarmService;

    @GetMapping("")
    @PreAuthorize("isAuthenticated()")
    @ApiOperation("사용자 알람 조회 API")
    public ResponseEntity<?> findAllAlarm(
            @AuthenticationPrincipal Account account
    ){
        List<SimpleAlarmDto> simpleAlarmDtos = accountAlarmService.findAllAlarm(account);
        CollectionModel<SimpleAlarmDto> simpleAlarmDtoModel = findAllAlarmAddLink(account, simpleAlarmDtos);
        return ResponseEntity.ok(simpleAlarmDtoModel);
    }

    private CollectionModel<SimpleAlarmDto> findAllAlarmAddLink(Account account, List<SimpleAlarmDto> simpleAlarmDtos) {
        simpleAlarmDtoLinkAdd(simpleAlarmDtos, account);
        CollectionModel<SimpleAlarmDto> simpleAlarmDtoModel = CollectionModel.of(simpleAlarmDtos);
        simpleAlarmDtoModel.add(
                linkTo(methodOn(AccountAlarmController.class).findAllAlarm(account)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/account-alarm-controller/findAllAlarmGET").withRel("profile")
        );
        return simpleAlarmDtoModel;
    }

    private void simpleAlarmDtoLinkAdd(List<SimpleAlarmDto> simpleAlarmDtos, Account account) {
        simpleAlarmDtos.forEach(simpleAlarmDto ->
        simpleAlarmDto.add(
                        linkTo(methodOn(AlarmController.class).alarmDetails(account, simpleAlarmDto.getAlarmId())).withSelfRel()
                ));
    }
}
