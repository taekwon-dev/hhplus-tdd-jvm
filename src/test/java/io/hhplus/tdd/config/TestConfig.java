package io.hhplus.tdd.config;

import io.hhplus.tdd.database.PointHistoryTable;
import io.hhplus.tdd.database.UserPointTable;
import io.hhplus.tdd.util.DatabaseCleaner;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {

    @Bean
    public DatabaseCleaner databaseCleaner(UserPointTable userPointTable, PointHistoryTable pointHistoryTable) {
        return new DatabaseCleaner(userPointTable, pointHistoryTable);
    }
}
