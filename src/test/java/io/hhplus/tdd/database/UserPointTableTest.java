package io.hhplus.tdd.database;

import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPointTableTest {

    // 테스트 작성 이유: UserPointTable selectById(id) 메서드에 `등록된 ID`가 주어졌을 때 기능 테스트
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

    // 테스트 작성 이유: UserPointTable selectById(id) 메서드에 `등록되지 않은 ID`가 주어졌을 때 기능 테스트
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
}
