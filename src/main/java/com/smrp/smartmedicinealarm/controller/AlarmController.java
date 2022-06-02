package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.dto.alarm.NewAlarmDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.service.alarm.AlarmService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/alarms")
public class AlarmController {
    private final AlarmService alarmService;
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "약 알림 추가")
    public ResponseEntity<?> alarmAdd(
            @AuthenticationPrincipal Account account,
            @Valid @RequestBody NewAlarmDto.Request request
    ){
        NewAlarmDto.Response  response = alarmService.addAlarm(account, request);
        alarmAddLinkAdd(account, request, response);
        return ResponseEntity.created(
                linkTo(
                        methodOn(AlarmController.class)
                                .medicineDetails(account, response.getAlarmId()))
                        .toUri())
                .body(response);
    }

    private void alarmAddLinkAdd(Account account, NewAlarmDto.Request request, NewAlarmDto.Response response) {
        response.add(
                linkTo(methodOn(AlarmController.class).alarmAdd(account, request)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/alarm-controller/alarmAddPOST").withRel("profile")
        );
    }

    @GetMapping("/{alarmId}")
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "약 알림 추가")
    public ResponseEntity<?> medicineDetails(
            @AuthenticationPrincipal Account account,
            @ApiParam(value = "알람 PK", example = "1L")
            @PathVariable Long alarmId
    ) {
        return ResponseEntity.ok(null);
    }

}
