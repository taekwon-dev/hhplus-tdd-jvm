package io.hhplus.tdd.database;

import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPointTableTest {

    @Test
    public void 이미_등록되어_있는_ID로_유저_포인트_조회_시_해당_유저의_포인트를_반환한다() {
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

    @Test
    public void 등록되지_않은_ID로_유저_포인트_조회_시_유저_등록된_후_0포인트를_반환한다() {
        // given
        long id = 1L;
        UserPointTable userPointTable = new UserPointTable();

        // when
        UserPoint userPoint = userPointTable.selectById(id);

        // then
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isZero();
    }

    @Test
    public void 요청_포인트만큼_충전된다() {
        // given
        long id = 1L;
        long pointToCharge = 100L;
        UserPointTable userPointTable = new UserPointTable();

        // when
        UserPoint userPoint = userPointTable.insertOrUpdate(id, pointToCharge);

        // then
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(pointToCharge);
    }

    @Test
    public void 요청_포인트만큼_차감된다() {
        // given
        long id = 1L;
        long currentPoint = 100L;
        long pointToUse = 50L;
        UserPointTable userPointTable = new UserPointTable();
        userPointTable.insertOrUpdate(id, currentPoint);

        // when
        UserPoint userPoint = userPointTable.insertOrUpdate(id, currentPoint - pointToUse);

        // then
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(currentPoint - pointToUse);
    }
}
