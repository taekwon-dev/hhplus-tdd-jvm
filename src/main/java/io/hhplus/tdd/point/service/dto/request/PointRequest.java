package io.hhplus.tdd.point.service.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record PointRequest(

        @NotNull(message = "포인트를 입력해주세요.")
        @Positive(message = "포인트는 0보다 커야 합니다.")
        Long amount
) {
}
