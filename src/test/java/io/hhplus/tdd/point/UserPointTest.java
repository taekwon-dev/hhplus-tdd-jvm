package io.hhplus.tdd.point;

import io.hhplus.tdd.point.domain.UserPoint;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class UserPointTest {

    // 테스트 작성 이유: Java Record 에서 지원하는 내부 동작 확인 목적 - 필드 접근
    @DisplayName("UserPoint 필드 값에 접근한다.")
    @Test
    public void accessor_Should_Return_FieldValues() {
        // given
        long id = 1L;
        long point = 100L;
        long updateMillis = 1734415657846L;

        // when
        UserPoint userPoint = new UserPoint(id, point, updateMillis);

        // then
        assertThat(userPoint.id()).isEqualTo(id);
        assertThat(userPoint.point()).isEqualTo(point);
        assertThat(userPoint.updateMillis()).isEqualTo(updateMillis);
    }

    // 테스트 작성 이유: Java Record 에서 지원하는 내부 동작 확인 목적 - equals(), hashCode()
    @DisplayName("두 UserPoint 객체의 필드 값이 모두 같은 값을 가지는 경우, equals() => True, hashCode() 일치")
    @Test
    public void equalsAndHashCode_Should_Return_True_ForSameValues() {
        // given
        long id = 1L;
        long point = 100L;
        long updateMillis = 1734415657846L;

        // when
        UserPoint userPoint1 = new UserPoint(id, point, updateMillis);
        UserPoint userPoint2 = new UserPoint(id, point, updateMillis);

        // then
        assertThat(userPoint1).isEqualTo(userPoint2);
        assertThat(userPoint1.hashCode()).isEqualTo(userPoint2.hashCode());
    }

    // 테스트 작성 이유: Java Record 에서 지원하는 내부 동작 확인 목적 - equals(), hashCode()
    @DisplayName("두 UserPoint 객체의 필드 값 중 서로 다른 값을 가지는 경우, equals() => False, hashCode() 불일치")
    @Test
    public void equalsAndHashCode_Should_Return_False_ForDifferentValues() {
        // given
        long id1 = 1L;
        long id2 = 2L;
        long point = 100L;
        long updateMillis = 1734415657846L;

        // when
        UserPoint userPoint1 = new UserPoint(id1, point, updateMillis);
        UserPoint userPoint2 = new UserPoint(id2, point, updateMillis);

        // then
        assertThat(userPoint1).isNotEqualTo(userPoint2);
        assertThat(userPoint1.hashCode()).isNotEqualTo(userPoint2.hashCode());
    }

    // 테스트 작성 이유: UserPoint 정적 메서드 empty(id) 기능 테스트
    @DisplayName("UserPoint.empty(id), 유저 ID 인자를 통해 0 포인트를 가지는 UserPoint 생성한다.")
    @Test
    public void empty_Should_Return_UserPointWithZeroPoint() {
        // given
        long id = 1;

        // when
        UserPoint emptyUserPoint = UserPoint.empty(id);

        // then
        assertThat(emptyUserPoint.id()).isEqualTo(id);
        assertThat(emptyUserPoint.point()).isZero();
    }
}
