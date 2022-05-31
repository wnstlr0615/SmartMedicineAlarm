package com.smrp.smartmedicinealarm.dto.bookmark;

import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.util.List;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SimpleBookmarkDto extends RepresentationModel<SimpleBookmarkDto> {
    @ApiModelProperty(value ="사용자 PK", dataType = "Long", example = "1L")
    private Long accountId;

    @ApiModelProperty(value ="사용자 email", dataType = "String", example = "joon@naver.com")
    private String email;

    @ApiModelProperty(value ="사용자가 등록한 약 정보", dataType = "List")
    private List<SimpleMedicineDto> medicines;

    //== 생성메서드 ==//
    public static SimpleBookmarkDto createSimpleBookmarkDto(Long accountId, String email, List<SimpleMedicineDto> medicines){
        return SimpleBookmarkDto.builder()
                .accountId(accountId)
                .email(email)
                .medicines(medicines)
                .build();
    }
}
