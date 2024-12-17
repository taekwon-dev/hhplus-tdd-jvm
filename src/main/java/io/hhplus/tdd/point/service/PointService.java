package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.service.dto.response.PointResponse;
import io.hhplus.tdd.point.service.mapper.PointMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable pointRepository;
    private final PointMapper pointMapper;

    public PointResponse getPointsByUserId(long userId) {
        UserPoint userPoint = pointRepository.selectById(userId);

        return pointMapper.mapToPointResponse(userPoint);
    }
}
