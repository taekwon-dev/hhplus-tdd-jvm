package io.hhplus.tdd.point.service.exception;

import io.hhplus.tdd.point.domain.UserPoint;

public class MaxBalanceExceededException extends RuntimeException {

    public MaxBalanceExceededException() {
        super("충전할 포인트가 너무 많습니다. 최대 포인트 잔고는 %d 입니다.".formatted(UserPoint.MAX_BALANCE));
    }
}
