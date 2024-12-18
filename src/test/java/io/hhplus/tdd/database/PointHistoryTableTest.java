package io.hhplus.tdd.database;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.domain.TransactionType;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PointHistoryTableTest {

    @Test
    public void 포인트_충전_기록을_추가한다() {
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

    @Test
    public void 포인트_사용_기록을_추가한다() {
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
