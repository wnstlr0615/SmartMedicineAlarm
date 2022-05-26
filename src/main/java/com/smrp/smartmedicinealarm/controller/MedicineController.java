package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.model.medicine.MedicineSearchCondition;
import com.smrp.smartmedicinealarm.service.medicine.MedicineService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/medicines")
public class MedicineController {
    private final MedicineService medicineService;

    @GetMapping("")
    @ApiOperation("약 동적(모양, 색상, 구분선, 등) 조회 하기")
    public ResponseEntity<?> findAllMedicine(
            @ApiParam(value = "페이지 번호", defaultValue = "0")
            @RequestParam(defaultValue = "0")int page,
            @ApiParam(value = "페이지 사이즈", defaultValue = "10")
            @RequestParam(defaultValue = "10")int size,
            @RequestBody MedicineSearchCondition MedicineSearchCondition,
            PagedResourcesAssembler<SimpleMedicineDto> assembler
    ){

        return ResponseEntity.ok(
                assembler.toModel(
                        medicineService.findAllMedicine(page, size, MedicineSearchCondition)
                )
        );
    }

}
