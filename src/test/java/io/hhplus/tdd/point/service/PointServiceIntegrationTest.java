package io.hhplus.tdd.point.service;

import io.hhplus.tdd.config.TestConfig;
import io.hhplus.tdd.point.service.dto.request.PointRequest;
import io.hhplus.tdd.point.service.dto.response.PointResponse;
import io.hhplus.tdd.util.DatabaseCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE, classes = TestConfig.class)
class PointServiceIntegrationTest {

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @Autowired
    private PointService pointService;

    @BeforeEach
    void setUp() {
        databaseCleaner.execute();
    }

    // 10명의 유저가 각각 2번의 포인트 충전 요청이 들어온 경우에 대한 동시성 테스트 & 10개 스레드
    @Test
    public void 포인트_충전_동시성_테스트() throws InterruptedException {
        // given
        int threadCount = 10;
        int userCount = 10;
        int requestCount = 2;
        long pointToCharge = 100L;
        PointRequest request = new PointRequest(pointToCharge);

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(userCount * requestCount);

        // when
        for (long userId = 1; userId <= userCount; userId++) {
            long currentUserId = userId;

            Runnable chargeTask = () -> {
                try {
                    pointService.chargePoints(currentUserId, request);
                } finally {
                    countDownLatch.countDown();
                }
            };
            executorService.submit(chargeTask);
            executorService.submit(chargeTask);
        }

        countDownLatch.await();
        executorService.shutdown();

        // then
        for (long userId = 1; userId <= userCount; userId++) {
            PointResponse response = pointService.getPointsByUserId(userId);
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.point()).isEqualTo(pointToCharge * requestCount);
        }
    }

    // 10명의 유저가 각각 2번의 포인트 이용 요청이 들어온 경우에 대한 동시성 테스트 & 10개 스레드
    @Test
    public void 포인트_이용_동시성_테스트() throws InterruptedException {
        // given
        int threadCount = 10;
        int userCount = 10;
        int requestCount = 2;
        long currentPoint = 1_000L;
        long pointToUse = 100L;
        PointRequest request = new PointRequest(pointToUse);;

        ExecutorService executorService = Executors.newFixedThreadPool(threadCount);
        CountDownLatch countDownLatch = new CountDownLatch(userCount * requestCount);

        // when
        for (long userId = 1; userId <= userCount; userId++) {
            long currentUserId = userId;
            pointService.chargePoints(currentUserId, new PointRequest(currentPoint));

            Runnable useTask = () -> {
                try {
                    pointService.usePoints(currentUserId, request);
                } finally {
                    countDownLatch.countDown();
                }
            };
            executorService.submit(useTask);
            executorService.submit(useTask);
        }

        countDownLatch.await();
        executorService.shutdown();

        // then
        for (long userId = 1; userId <= userCount; userId++) {
            PointResponse response = pointService.getPointsByUserId(userId);
            assertThat(response.userId()).isEqualTo(userId);
            assertThat(response.point()).isEqualTo(currentPoint - (pointToUse * requestCount));
        }
    }
}
