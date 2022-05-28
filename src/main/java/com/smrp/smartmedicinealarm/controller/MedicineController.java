package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.dto.medicine.CreateMedicineDto;
import com.smrp.smartmedicinealarm.dto.medicine.MedicineDetailsDto;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.dto.medicine.UpdateMedicineDto;
import com.smrp.smartmedicinealarm.model.medicine.MedicineSearchCondition;
import com.smrp.smartmedicinealarm.service.medicine.MedicineService;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/medicines")
public class MedicineController {
    private final MedicineService medicineService;

    @GetMapping("")
    @ApiOperation("약 동적(모양, 색상, 구분선, 등) 조회 API")
    public ResponseEntity<?> findAllMedicine(
            @ApiParam(value = "페이지 번호", defaultValue = "0", required = true)
            @RequestParam(defaultValue = "0")int page,
            @ApiParam(value = "페이지 사이즈", defaultValue = "10", required = true)
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
                Link.of(linkTo(SwaggerController.class) + "#/medicine-controller/findAllMedicineGET").withRel("profile")
        );
        pageModel.getContent().forEach(medicineDto -> {
            Assert.notNull(medicineDto.getContent(), "content of medicineDto  must not null");
                medicineDto.add(
                        linkTo(methodOn(MedicineController.class).medicineDetails(medicineDto.getContent().getMedicineId())).withSelfRel()
                );
        });
    }
    @GetMapping("/{medicineId}")
    @ApiOperation("약 상세정보 조회 API")
    public ResponseEntity<?> medicineDetails(
            @ApiParam(value = "약 PK", defaultValue = "1L")
            @PathVariable Long medicineId
    ){
        MedicineDetailsDto medicineDetailsDto = medicineService.findMedicineDetails(medicineId);

        //링크 추가
        medicineDetailsDto.add(
                linkTo(methodOn(MedicineController.class).medicineDetails(medicineId)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "#/medicine-controller/medicineDetailsGET").withRel("profile")
        );
        return ResponseEntity.ok(medicineDetailsDto);
    }

    @PostMapping("")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation("약 정보 추가 API")
    public ResponseEntity<?> medicineAdd(
            @Valid @RequestBody CreateMedicineDto medicineDto
    ) {
        MedicineDetailsDto medicineDetailsDto = medicineService.addMedicine(medicineDto);
        medicineDetailsDto.add(
                linkTo(methodOn(MedicineController.class).medicineAdd(medicineDto)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "#/medicine-controller/medicineAddPOST").withRel("profile")
        );
        return ResponseEntity.created(
                        linkTo(
                                methodOn(MedicineController.class)
                                        .medicineDetails(medicineDetailsDto.getMedicineId()
                                        )
                        ).toUri()
        ).body(medicineDetailsDto);
    }

    @PatchMapping("/{medicineId}")
    @PreAuthorize("hasRole('ADMIN')")
    @ApiOperation("약 정보 업데이트 API")
    public ResponseEntity<?> medicineModify(
            @ApiParam(value = "약 PK", defaultValue = "1L")
            @PathVariable Long medicineId,
            @Valid @RequestBody UpdateMedicineDto medicineDto
    ) {
        MedicineDetailsDto medicineDetailsDto = medicineService.modifyMedicine(medicineId, medicineDto);
        medicineDetailsDto.add(
                linkTo(methodOn(MedicineController.class).medicineModify(medicineId, medicineDto)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "#/medicine-controller/medicineModifyPATCH").withRel("profile")
        );
        return ResponseEntity.ok(medicineDetailsDto);
    }
}
