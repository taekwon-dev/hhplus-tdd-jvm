package io.hhplus.tdd.point.domain;

public record UserPoint(
        long id,
        long point,
        long updateMillis
) {
    public static final long MAX_BALANCE = 1_000_000;

    public static UserPoint empty(long id) {
        return new UserPoint(id, 0, System.currentTimeMillis());
    }
}
