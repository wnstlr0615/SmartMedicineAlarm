package com.smrp.smartmedicinealarm.dto.bookmark;

import com.smrp.smartmedicinealarm.dto.medicine.SimpleMedicineDto;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;
import org.springframework.hateoas.RepresentationModel;
import org.springframework.hateoas.server.core.Relation;

import javax.validation.constraints.NotEmpty;
import java.util.List;


public class NewBookmarkDto {
    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    public static class Request {
        @ApiModelProperty(value =" 약 PK 리스트", dataType = "List", example = "1L")
        @NotEmpty(message = "medicineIds는 빌 수 없습니다.")
        List<Long> medicineIds;

        //== 생성메서드 ==//
        public static Request createNewBookmarkRequestDto(List<Long> medicineIds){
            return Request.builder()
                    .medicineIds(medicineIds)
                    .build();
        }
    }

    @Builder
    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Relation(collectionRelation = "items")
    public static class Response extends RepresentationModel<Response> {
        @ApiModelProperty(value ="사용자 PK", dataType = "Long", example = "1L")
        private Long accountId;

        @ApiModelProperty(value ="사용자 email", dataType = "String", example = "joon@naver.com")
        private String email;

        @ApiModelProperty(value ="사용자가 등록한 약 정보", dataType = "List")
        private List<SimpleMedicineDto> medicines;


        //== 생성메서드 ==//
        public static Response createNewBookmarkResponseDto(Long accountId, String email, List<SimpleMedicineDto> medicines){
            return Response.builder()
                    .accountId(accountId)
                    .email(email)
                    .medicines(medicines)
                    .build();
        }
    }
}
