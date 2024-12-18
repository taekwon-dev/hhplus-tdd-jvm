package io.hhplus.tdd.database;

import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPointTableTest {

    @DisplayName("등록된 ID로 유저 포인트 조회 시 해당 유저의 포인트를 반환한다.")
    @Test
    public void selectById_Should_Return_UserPoint_When_ExistingId() {
        // given
        long id = 1L;
        long amount = 100L;
        UserPointTable userPointTable = new UserPointTable();
        userPointTable.insertOrUpdate(id, amount);

        // when
        UserPoint userPoint = userPointTable.selectById(id);

        // then
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(amount);
    }

    @DisplayName("등록되지 않은 ID로 유저 포인트 조회 시 유저 등록된 후 0포인트를 반환한다.")
    @Test
    public void selectById_Should_Return_UserPointWithZeroPoint_When_NonExistingId() {
        // given
        long id = 1L;
        UserPointTable userPointTable = new UserPointTable();

        // when
        UserPoint userPoint = userPointTable.selectById(id);

        // then
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isZero();
    }

    @DisplayName("기존 유저 포인트에 요청 포인트만큼 충전된다.")
    @Test
    public void insertOrUpdate_Should_Return_Updated_UserPoint_When_Charge() {
        // given
        long id = 1L;
        long pointToCharge = 100L;
        UserPointTable userPointTable = new UserPointTable();
        userPointTable.selectById(id);

        // when
        UserPoint userPoint = userPointTable.insertOrUpdate(id, pointToCharge);

        // then
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(pointToCharge);
    }

    @DisplayName("기존 유저 포인트에 요청 포인트만큼 차감된다.")
    @Test
    public void insertOrUpdate_Should_Return_Updated_UserPoint_When_Use() {
        // given
        long id = 1L;
        long currentPoint = 100L;
        long pointToUse = 50L;
        UserPointTable userPointTable = new UserPointTable();
        userPointTable.selectById(id);
        userPointTable.insertOrUpdate(id, currentPoint);

        // when
        UserPoint userPoint = userPointTable.insertOrUpdate(id, currentPoint - pointToUse);

        // then
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(currentPoint - pointToUse);
    }
}
