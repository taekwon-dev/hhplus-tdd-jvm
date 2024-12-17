package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.service.dto.response.PointResponse;
import io.hhplus.tdd.point.service.mapper.PointMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class PointServiceTest {

    // 테스트 작성 이유: PointService getPointsByUserId(id) 메서드에 `등록된 ID`가 주어졌을 때 기능 테스트
    @DisplayName("등록된 ID로 유저 포인트 조회 시 해당 유저의 포인트를 반환한다.")
    @Test
    public void getPointsByUserId_Should_Return_UserPoint_When_ExistingId() {
        // given
        long id = 1L;
        long point = 100L;
        UserPoint userPoint = new UserPoint(id, point, System.currentTimeMillis());
        UserPointTable pointRepository = mock(UserPointTable.class);
        PointMapper pointMapper = mock(PointMapper.class);

        when(pointRepository.selectById(id)).thenReturn(userPoint);
        when(pointMapper.mapToPointResponse(userPoint)).thenReturn(new PointResponse(id, point));

        PointService pointService = new PointService(pointRepository, pointMapper);

        // when
        PointResponse response = pointService.getPointsByUserId(id);

        // then
        assertThat(response.userId()).isEqualTo(id);
        assertThat(response.point()).isEqualTo(point);
    }

    // 테스트 작성 이유: PointService getPointsByUserId(id) 메서드에 `등록되지 않은 ID`가 주어졌을 때 기능 테스트
    @DisplayName("등록되지 않은 ID로 유저 포인트 조회 시 유저 등록된 후 0포인트를 반환한다.")
    @Test
    public void getPointsByUserId_Should_Return_UserPointWithZeroPoint_When_NonExistingId() {
        // given
        long id = 1L;
        UserPoint userPoint = UserPoint.empty(id);
        UserPointTable pointRepository = mock(UserPointTable.class);
        PointMapper pointMapper = mock(PointMapper.class);

        when(pointRepository.selectById(id)).thenReturn(userPoint);
        when(pointMapper.mapToPointResponse(userPoint)).thenReturn(new PointResponse(id, userPoint.point()));

        PointService pointService = new PointService(pointRepository, pointMapper);

        // when
        PointResponse response = pointService.getPointsByUserId(id);

        // then
        assertThat(response.userId()).isEqualTo(id);
        assertThat(response.point()).isZero();
    }
}
