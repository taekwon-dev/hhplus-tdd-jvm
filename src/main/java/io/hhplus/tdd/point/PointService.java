package io.hhplus.tdd.point;

import io.hhplus.tdd.database.UserPointTable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointService {

    private final UserPointTable pointRepository;

    public UserPoint getPointsByUserId(long userId) {
        return pointRepository.selectById(userId);
    }
}
