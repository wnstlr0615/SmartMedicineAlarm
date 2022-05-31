package com.smrp.smartmedicinealarm.controller;

import com.smrp.smartmedicinealarm.dto.bookmark.NewBookmarkDto;
import com.smrp.smartmedicinealarm.entity.account.Account;
import com.smrp.smartmedicinealarm.service.bookmark.BookmarkService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.hateoas.Link;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

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
        log.info(account.getEmail());
        NewBookmarkDto.Response response = bookmarkService.addBookmark(account, bookmarkDto);
        newBookMarkResponseDtoAddLink(account, bookmarkDto, response);
        return ResponseEntity.ok(response);
    }

    private void newBookMarkResponseDtoAddLink(Account account, NewBookmarkDto.Request bookmarkDto, NewBookmarkDto.Response response) {
        response.getMedicines().forEach(simpleMedicineDto ->
                simpleMedicineDto.add(
                        linkTo(methodOn(MedicineController.class).medicineDetails(simpleMedicineDto.getMedicineId())).withSelfRel()
                )
                );
        response.add(
                linkTo(methodOn(BookmarkController.class).bookmarkAdd(account, bookmarkDto)).withSelfRel(),
                Link.of(linkTo(SwaggerController.class) + "/#/bookmark-controller/bookmarkAddPOST").withRel("profile")
        );
    }


}
