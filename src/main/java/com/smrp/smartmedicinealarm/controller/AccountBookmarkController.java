package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.dto.bookmark.SimpleBookmarkDto;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.service.account.AccountBookmarkService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
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
@RequestMapping(value = "/api/v1/accounts/me/bookmarks")
@RequiredArgsConstructor
public class AccountBookmarkController {
    private final AccountBookmarkService accountBookmarkService;

    @GetMapping()
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "즐겨찾기 목록 보기")
    public ResponseEntity<?> findAllBookmark(
            @AuthenticationPrincipal Account account
    ){
        SimpleBookmarkDto simpleBookmarkDto = accountBookmarkService.findAllBookmark(account);
        findAllBookmarkLinkAdd(account, simpleBookmarkDto);
        return ResponseEntity.ok(simpleBookmarkDto);
    }
    private void findAllBookmarkLinkAdd(Account account, SimpleBookmarkDto simpleBookmarkDto) {
        simpleBookmarkDto.add(
                linkTo(methodOn(AccountBookmarkController.class).findAllBookmark(account)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/account-bookmark-controller/findAllBookmarkGET").withRel("profile")
        );
        simpleMedicineDtoAddDetailLink(simpleBookmarkDto.getMedicines());
    }
    private void simpleMedicineDtoAddDetailLink(List<SimpleMedicineDto> medicines) {
        medicines.forEach(simpleMedicineDto ->
                simpleMedicineDto.add(
                        linkTo(methodOn(MedicineController.class).medicineDetails(simpleMedicineDto.getMedicineId())).withSelfRel()
                )
        );
    }
}
