package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.service.dto.request.PointRequest;
import io.hhplus.tdd.point.service.dto.response.PointResponse;
import io.hhplus.tdd.point.service.exception.MaxBalanceExceededException;
import io.hhplus.tdd.point.service.mapper.PointMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable pointRepository;
    private final PointHistoryTable pointHistoryRepository;
    private final PointMapper pointMapper;

    public PointResponse getPointsByUserId(long userId) {
        UserPoint userPoint = pointRepository.selectById(userId);

        return pointMapper.mapToPointResponse(userPoint);
    }

    public PointResponse chargePoints(long userId, PointRequest pointRequest) {
        UserPoint currentUserPoint = pointRepository.selectById(userId);
        long afterChargePoint = currentUserPoint.point() + pointRequest.amount();

        if (afterChargePoint > UserPoint.MAX_BALANCE) {
            throw new MaxBalanceExceededException();
        }
        UserPoint userPoint = pointRepository.insertOrUpdate(userId, afterChargePoint);
        pointHistoryRepository.insert(userId, pointRequest.amount(), TransactionType.CHARGE, System.currentTimeMillis());

        return pointMapper.mapToPointResponse(userPoint);
    }
}
