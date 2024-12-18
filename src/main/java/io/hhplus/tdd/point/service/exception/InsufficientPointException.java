package io.hhplus.tdd.point.service.exception;

public class InsufficientPointException extends RuntimeException {

    public InsufficientPointException() {
        super("포인트 잔액이 부족합니다.");
    }
}
