package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.dto.RemoveBookmarkDto;
import com.smrp.smartmedicinealarm.dto.bookmark.NewBookmarkDto;
import com.smrp.smartmedicinealarm.dto.bookmark.SimpleBookmarkDto;
import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.service.bookmark.BookmarkService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/api/v1/accounts/me/bookmarks")
public class BookmarkController {
    private final BookmarkService bookmarkService;

    @PostMapping()
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "약 즐겨찾기 추가")
    public ResponseEntity<?> bookmarkAdd(
            @AuthenticationPrincipal Account account,
            @Valid @RequestBody NewBookmarkDto.Request bookmarkDto
    ) {
        NewBookmarkDto.Response response = bookmarkService.addBookmark(account, bookmarkDto);
        newBookMarkResponseDtoAddLink(account, bookmarkDto, response);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "즐겨찾기 목록 보기")
    public ResponseEntity<?> findAllBookmark(@AuthenticationPrincipal Account account){
        SimpleBookmarkDto simpleBookmarkDto = bookmarkService.findAllBookmark(account);
        findAllBookmarkLinkAdd(account, simpleBookmarkDto);
        return ResponseEntity.ok(simpleBookmarkDto);
    }

    private void findAllBookmarkLinkAdd(Account account, SimpleBookmarkDto simpleBookmarkDto) {
        simpleBookmarkDto.add(
                linkTo(methodOn(BookmarkController.class).findAllBookmark(account)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/bookmark-controller/findAllBookmarkGET").withRel("profile")
        );
        simpleMedicineDtoAddDetailLink(simpleBookmarkDto.getMedicines());

    }
    @DeleteMapping
    @PreAuthorize("isAuthenticated()")
    @ApiOperation(value = "즐겨찾기 목록에서 제거", notes = "제거 요청시 제거하고 남은 즐겨찾기 목록 반환")
    public ResponseEntity<?> bookmarkRemove(
            @AuthenticationPrincipal Account account,
            @Valid  @RequestBody RemoveBookmarkDto removeBookmarkDto
    ) {
        SimpleBookmarkDto simpleBookmarkDto =  bookmarkService.bookmarkRemove(account, removeBookmarkDto);
        bookmarkRemoveAddLink(account, removeBookmarkDto, simpleBookmarkDto);
        return ResponseEntity.ok(simpleBookmarkDto);
    }

    private void bookmarkRemoveAddLink(Account account, RemoveBookmarkDto removeBookmarkDto, SimpleBookmarkDto simpleBookmarkDto) {
        simpleBookmarkDto.add(
                linkTo(methodOn(BookmarkController.class).bookmarkRemove(account, removeBookmarkDto)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/bookmark-controller/bookmarkRemobeDELETE").withRel("profile")
        );
        simpleMedicineDtoAddDetailLink(simpleBookmarkDto.getMedicines());
    }




    private void newBookMarkResponseDtoAddLink(Account account, NewBookmarkDto.Request bookmarkDto, NewBookmarkDto.Response response) {
        List<SimpleMedicineDto> medicines = response.getMedicines();
        simpleMedicineDtoAddDetailLink(medicines);
        response.add(
                linkTo(methodOn(BookmarkController.class).bookmarkAdd(account, bookmarkDto)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/bookmark-controller/bookmarkAddPOST").withRel("profile")
        );
    }

    private void simpleMedicineDtoAddDetailLink(List<SimpleMedicineDto> medicines) {
        medicines.forEach(simpleMedicineDto ->
                simpleMedicineDto.add(
                        linkTo(methodOn(MedicineController.class).medicineDetails(simpleMedicineDto.getMedicineId())).withSelfRel()
                )
                );
    }


}
