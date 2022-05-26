package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.model.medicine.MedicineSearchCondition;
import com.smrp.smartmedicinealarm.service.medicine.MedicineService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;

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
        Page<SimpleMedicineDto> simpleMedicineDtoPage = medicineService.findAllMedicine(page, size, MedicineSearchCondition);
        PagedModel<EntityModel<SimpleMedicineDto>> pageModel = assembler.toModel(simpleMedicineDtoPage);

        //링크 추가
        addSelfLink(pageModel);

        return ResponseEntity.ok(pageModel);
    }

    private void addSelfLink(PagedModel<EntityModel<SimpleMedicineDto>> pageModel) {
        pageModel.add(
                linkTo(MedicineController.class).withRel("profile")
        );
        //상세 바로가기 링크 추가
//        pageModel.getContent().forEach(medicineDto ->
//                medicineDto.add(
//                        linkTo(methodOn(MedicineController.class)).withSelfRel()
//                ));
    }

}
