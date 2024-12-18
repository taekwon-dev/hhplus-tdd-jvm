package io.hhplus.tdd.point.service.mapper;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.service.dto.response.PointHistoryResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PointHistoryMapper {

    public List<PointHistoryResponse> mapToPointHistoryResponses(List<PointHistory> pointHistories) {
        return pointHistories.stream()
                .map(pointHistory -> new PointHistoryResponse(
                        pointHistory.userId(),
                        pointHistory.amount(),
                        pointHistory.type().name(),
                        pointHistory.updateMillis()
                ))
                .toList();
    }
}
