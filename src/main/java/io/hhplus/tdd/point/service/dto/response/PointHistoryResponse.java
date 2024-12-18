package io.hhplus.tdd.point.service.dto.response;

public record PointHistoryResponse(
        long userId,
        long point,
        String transactionType,
        long transactionTime
) {
}
