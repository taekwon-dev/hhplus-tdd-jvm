package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.service.dto.request.PointRequest;
import io.hhplus.tdd.point.service.dto.response.PointHistoryResponse;
import io.hhplus.tdd.point.service.dto.response.PointResponse;
import io.hhplus.tdd.point.service.exception.InsufficientPointException;
import io.hhplus.tdd.point.service.exception.MaxBalanceExceededException;
import io.hhplus.tdd.point.service.mapper.PointHistoryMapper;
import io.hhplus.tdd.point.service.mapper.PointMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PointServiceTest {

    private UserPointTable pointRepository;
    private PointHistoryTable pointHistoryRepository;
    private PointMapper pointMapper;
    private PointHistoryMapper pointHistoryMapper;
    private PointService pointService;

    @BeforeEach
    public void setUp() {
        pointRepository = mock(UserPointTable.class);
        pointHistoryRepository = mock(PointHistoryTable.class);
        pointMapper = mock(PointMapper.class);
        pointHistoryMapper = mock(PointHistoryMapper.class);
        pointService = new PointService(pointRepository, pointHistoryRepository, pointMapper, pointHistoryMapper);
    }

    @Test
    public void 등록된_ID로_유저_포인트_조회_시_해당_유저의_포인트를_반환한다() {
        // given
        long id = 1L;
        long point = 100L;
        UserPoint userPoint = new UserPoint(id, point, System.currentTimeMillis());

        when(pointRepository.selectById(id)).thenReturn(userPoint);
        when(pointMapper.mapToPointResponse(userPoint)).thenReturn(new PointResponse(id, point));

        // when
        PointResponse response = pointService.getPointsByUserId(id);

        // then
        assertThat(response.userId()).isEqualTo(id);
        assertThat(response.point()).isEqualTo(point);
    }

    @Test
    public void 등록되지_않은_ID로_유저_포인트_조회_시_유저_등록된_후_0포인트를_반환한다() {
        // given
        long id = 1L;
        UserPoint userPoint = UserPoint.empty(id);

        when(pointRepository.selectById(id)).thenReturn(userPoint);
        when(pointMapper.mapToPointResponse(userPoint)).thenReturn(new PointResponse(id, userPoint.point()));

        // when
        PointResponse response = pointService.getPointsByUserId(id);

        // then
        assertThat(response.userId()).isEqualTo(id);
        assertThat(response.point()).isZero();
    }

    @Test
    public void 요청_포인트만큼_충전된다() {
        // given
        long id = 1L;
        long cursorId = 1L;
        long pointToCharge = 100L;
        UserPoint currentUserPoint = UserPoint.empty(id);
        PointRequest pointRequest = new PointRequest(pointToCharge);
        UserPoint afterChargePoint = new UserPoint(id, currentUserPoint.point() + pointToCharge, System.currentTimeMillis());
        PointHistory pointHistory = new PointHistory(cursorId, id, pointToCharge, TransactionType.CHARGE, System.currentTimeMillis());

        when(pointRepository.selectById(id)).thenReturn(currentUserPoint);
        when(pointRepository.insertOrUpdate(id, pointToCharge)).thenReturn(afterChargePoint);
        when(pointHistoryRepository.insert(id, pointToCharge, TransactionType.CHARGE, System.currentTimeMillis())).thenReturn(pointHistory);
        when(pointMapper.mapToPointResponse(afterChargePoint)).thenReturn(new PointResponse(id, afterChargePoint.point()));

        // when
        PointResponse response = pointService.chargePoints(id, pointRequest);

        // then
        assertThat(response.userId()).isEqualTo(afterChargePoint.id());
        assertThat(response.point()).isEqualTo(afterChargePoint.point());
    }

    @Test
    public void 충전_후_최대_포인트_잔고를_초과하는_경우_예외가_발생한다() {
        // given
        long id = 1L;
        long currentPoint = 999_999L;
        long pointToCharge = 10L;
        PointRequest pointRequest = new PointRequest(pointToCharge);
        UserPoint currentUserPoint = new UserPoint(id, currentPoint, System.currentTimeMillis());

        when(pointRepository.selectById(id)).thenReturn(currentUserPoint);

        // when & then
        assertThatThrownBy(() -> pointService.chargePoints(id, pointRequest))
                .isInstanceOf(MaxBalanceExceededException.class);
    }

    @Test
    public void 요청_포인트만큼_차감된다() {
        // given
        long id = 1L;
        long cursorId = 1L;
        long currentPoint = 100L;
        long pointToUse = 90L;

        UserPoint currentUserPoint = new UserPoint(id, currentPoint, System.currentTimeMillis());
        PointRequest useRequest = new PointRequest(pointToUse);
        UserPoint afterUsePoint = new UserPoint(id, currentUserPoint.point() - pointToUse, System.currentTimeMillis());
        PointHistory usePointHistory = new PointHistory(cursorId, id, pointToUse, TransactionType.USE, System.currentTimeMillis());

        when(pointRepository.selectById(id)).thenReturn(currentUserPoint);
        when(pointRepository.insertOrUpdate(id, currentUserPoint.point() - pointToUse)).thenReturn(afterUsePoint);
        when(pointHistoryRepository.insert(id, pointToUse, TransactionType.USE, System.currentTimeMillis())).thenReturn(usePointHistory);
        when(pointMapper.mapToPointResponse(afterUsePoint)).thenReturn(new PointResponse(id, afterUsePoint.point()));

        // when
        PointResponse response = pointService.usePoints(id, useRequest);

        // then
        assertThat(response.userId()).isEqualTo(afterUsePoint.id());
        assertThat(response.point()).isEqualTo(afterUsePoint.point());
    }

    @Test
    public void 요청_포인트보다_잔액이_적은_경우_예외가_발생한다() {
        // given
        long id = 1L;
        long currentPoint = 100L;
        long pointToUse = 200L;
        PointRequest pointRequest = new PointRequest(pointToUse);
        UserPoint currentUserPoint = new UserPoint(id, currentPoint, System.currentTimeMillis());

        when(pointRepository.selectById(id)).thenReturn(currentUserPoint);

        // when & then
        assertThatThrownBy(() -> pointService.usePoints(id, pointRequest))
                .isInstanceOf(InsufficientPointException.class);
    }

    @Test
    public void 유저의_포인트_거래_기록을_조회한다() {
        // given
        long id = 1L;
        long pointToCharge = 2_000L;
        long pointToUse = 1_000L;

        PointHistory chargePointHistory = new PointHistory(1L, id, pointToCharge, TransactionType.CHARGE, System.currentTimeMillis());
        PointHistory usePointHistory = new PointHistory(2L, id, pointToUse, TransactionType.USE, System.currentTimeMillis());
        List<PointHistory> pointHistories = List.of(chargePointHistory, usePointHistory);

        PointHistoryResponse chargePointHistoryResponse = new PointHistoryResponse(id, pointToCharge, TransactionType.CHARGE.name(), System.currentTimeMillis());
        PointHistoryResponse usePointHistoryResponse = new PointHistoryResponse(id, pointToUse, TransactionType.USE.name(), System.currentTimeMillis());
        List<PointHistoryResponse> pointHistoryResponses = List.of(chargePointHistoryResponse, usePointHistoryResponse);

        when(pointHistoryRepository.selectAllByUserId(id)).thenReturn(pointHistories);
        when(pointHistoryMapper.mapToPointHistoryResponses(pointHistories)).thenReturn(pointHistoryResponses);

        // when
        List<PointHistoryResponse> response = pointService.getPointHistoryByUserId(id);

        // then
        assertThat(response.size()).isEqualTo(2);
    }
}
