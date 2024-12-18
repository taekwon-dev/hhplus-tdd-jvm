package io.hhplus.tdd.util;

import io.hhplus.tdd.database.UserPointTable;

public class DatabaseCleaner {

    private final UserPointTable userPointTable;

    public DatabaseCleaner(UserPointTable userPointTable) {
        this.userPointTable = userPointTable;
    }

    public void execute() {
        userPointTable.clear();
    }
}
