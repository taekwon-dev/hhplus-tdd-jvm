package io.hhplus.tdd.util;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;

public class DatabaseCleaner {

    private final UserPointTable userPointTable;
    private final PointHistoryTable pointHistoryTable;

    public DatabaseCleaner(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        this.userPointTable = userPointTable;
        this.pointHistoryTable = pointHistoryTable;
    }

    public void execute() {
        userPointTable.clear();
        pointHistoryTable.clear();
    }
}
