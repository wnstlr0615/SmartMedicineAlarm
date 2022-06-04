package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.dto.ResponseResult;
import com.smrp.smartmedicinealarm.dto.alarm.AlarmDetailDto;
import com.smrp.smartmedicinealarm.dto.alarm.NewAlarmDto;
import com.smrp.smartmedicinealarm.dto.alarm.UpdateAlarmDto;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
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
import java.util.List;

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
                                .alarmDetails(account, response.getAlarmId()))
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
    public ResponseEntity<?> alarmDetails(
            @AuthenticationPrincipal Account account,
            @ApiParam(value = "알람 PK", example = "1L")
            @PathVariable Long alarmId
    ) {
        AlarmDetailDto alarmDetailDto = alarmService.findAlarmDetails(account, alarmId);
        alarmDetailsLinkAdd(account, alarmId, alarmDetailDto);
        return ResponseEntity.ok(alarmDetailDto);
    }

    private void alarmDetailsLinkAdd(Account account, Long alarmId, AlarmDetailDto alarmDetailDto) {
        alarmDetailDto.add(
                linkTo(methodOn(AlarmController.class).alarmDetails(account, alarmId)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/alarm-controller/alarmDetailsGET").withRel("profile")
        );
        simpleMedicineDtoLinkAdd(alarmDetailDto.getMedicines());
    }

    private void simpleMedicineDtoLinkAdd(List<SimpleMedicineDto> medicines) {
        medicines.forEach(simpleMedicineDto ->
                simpleMedicineDto.add(
                        linkTo(methodOn(MedicineController.class).medicineDetails(simpleMedicineDto.getMedicineId())).withSelfRel()
                ));
    }

    @DeleteMapping("/{alarmId}")
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "약 알림 제거하기")
    public ResponseEntity<?> alarmRemove(
            @AuthenticationPrincipal Account account,
            @ApiParam(value = "알람 PK", example = "1L")
            @PathVariable Long alarmId
    ) {
        alarmService.removeAlarm(account, alarmId);
        ResponseResult success = ResponseResult.success("약알림이 성공적으로 제거되었습니다.");
        alarmRemoveAddLink(account, alarmId, success);
        return ResponseEntity.ok(success);
    }

    private void alarmRemoveAddLink(Account account, Long alarmId, ResponseResult success) {
        success.add(
                linkTo(methodOn(AlarmController.class).alarmRemove(account, alarmId)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/alarm-controller/alarmRemoveDELETE").withRel("profile")
        );
    }

    @PutMapping("/{alarmId}")
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "약 알림 수정")
    public ResponseEntity<?> alarmModify(
            @PathVariable Long alarmId,
            @AuthenticationPrincipal Account account,
            @Valid @RequestBody UpdateAlarmDto updateAlarmDto
    ){
        AlarmDetailDto  alarmDetailDto = alarmService.modifyAlarm(alarmId, account, updateAlarmDto);
        alarmModifyAddLink(alarmId, account, updateAlarmDto, alarmDetailDto);
        return ResponseEntity.ok(alarmDetailDto);
    }

    private void alarmModifyAddLink(Long alarmId, Account account, UpdateAlarmDto updateAlarmDto, AlarmDetailDto alarmDetailDto) {
        alarmDetailDto.add(
                linkTo(methodOn(AlarmController.class).alarmModify(alarmId, account, updateAlarmDto)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/alarm-controller/alarmModifyPUT").withRel("profile")
        );
        simpleMedicineDtoLinkAdd(alarmDetailDto.getMedicines());
    }

}
