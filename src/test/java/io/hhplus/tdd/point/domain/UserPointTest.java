package io.hhplus.tdd.point.domain;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPointTest {

    @Test
    public void 유저_포인트가_0인_상태로_초기화된다() {
        // given
        long id = 1;

        // when
        UserPoint emptyUserPoint = UserPoint.empty(id);

        // then
        assertThat(emptyUserPoint.id()).isEqualTo(id);
        assertThat(emptyUserPoint.point()).isZero();
    }
}
