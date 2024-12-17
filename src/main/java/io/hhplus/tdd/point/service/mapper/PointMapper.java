package io.hhplus.tdd.point.service.mapper;

import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.service.dto.response.PointResponse;
import org.springframework.stereotype.Component;

@Component
public class PointMapper {

    public PointResponse mapToPointResponse(UserPoint userPoint) {
        return new PointResponse(userPoint.id(), userPoint.point());
    }
}
