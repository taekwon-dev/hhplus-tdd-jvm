package io.hhplus.tdd.database;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PointHistoryTableTest {

    @DisplayName("포인트 충전 기록을 추가한다.")
    @Test
    public void insert_Should_Return_PointHistory_When_Charge() {
        // given
        long id = 1L;
        long pointToCharge = 100L;
        PointHistoryTable pointHistoryTable = new PointHistoryTable();

        // when
        PointHistory pointHistory = pointHistoryTable.insert(id, pointToCharge, TransactionType.CHARGE, System.currentTimeMillis());

        // then
        assertThat(pointHistory.userId()).isEqualTo(id);
        assertThat(pointHistory.amount()).isEqualTo(pointToCharge);
        assertThat(pointHistory.type()).isEqualTo(TransactionType.CHARGE);
    }

    @DisplayName("포인트 사용 기록을 추가한다.")
    @Test
    public void insert_Should_Return_PointHistory_When_Use() {
        // given
        long id = 1L;
        long pointToUse = 100L;
        PointHistoryTable pointHistoryTable = new PointHistoryTable();

        // when
        PointHistory pointHistory = pointHistoryTable.insert(id, pointToUse, TransactionType.USE, System.currentTimeMillis());

        // then
        assertThat(pointHistory.userId()).isEqualTo(id);
        assertThat(pointHistory.amount()).isEqualTo(pointToUse);
        assertThat(pointHistory.type()).isEqualTo(TransactionType.USE);
    }
}
