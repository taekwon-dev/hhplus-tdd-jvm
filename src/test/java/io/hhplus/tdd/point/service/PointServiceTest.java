package io.hhplus.tdd.point.service;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.service.dto.request.PointRequest;
import io.hhplus.tdd.point.service.dto.response.PointResponse;
import io.hhplus.tdd.point.service.mapper.PointMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PointServiceTest {

    private UserPointTable pointRepository;
    private PointHistoryTable pointHistoryRepository;
    private PointMapper pointMapper;
    private PointService pointService;

    @BeforeEach
    public void setUp() {
        pointRepository = mock(UserPointTable.class);
        pointHistoryRepository = mock(PointHistoryTable.class);
        pointMapper = mock(PointMapper.class);
        pointService = new PointService(pointRepository, pointHistoryRepository, pointMapper);
    }

    @DisplayName("등록된 ID로 유저 포인트 조회 시 해당 유저의 포인트를 반환한다.")
    @Test
    public void getPointsByUserId_Should_Return_UserPoint_When_ExistingId() {
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

    @DisplayName("등록되지 않은 ID로 유저 포인트 조회 시 유저 등록된 후 0포인트를 반환한다.")
    @Test
    public void getPointsByUserId_Should_Return_UserPointWithZeroPoint_When_NonExistingId() {
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

    @DisplayName("기존 유저 포인트에 요청 포인트만큼 충전된다.")
    @Test
    public void chargePoints_Should_Return_UpdatedUserPoint() {
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

    @DisplayName("충전 후 최대 포인트 잔고를 초과하는 경우, 충전 요청에 실패한다.")
    @Test
    public void chargePoints_Should_Throw_MaxBalanceExceededException() {
        // given
        long id = 1L;
        long currentPoint = 999_999L;
        long pointToCharge = 10L;
        PointRequest pointRequest = new PointRequest(pointToCharge);
        UserPoint currentUserPoint = new UserPoint(id, currentPoint, System.currentTimeMillis());

        when(pointRepository.selectById(id)).thenReturn(currentUserPoint);

        // when & then
        assertThatThrownBy(() -> pointService.chargePoints(id, pointRequest))
                .isInstanceOf(RuntimeException.class);
    }
}
