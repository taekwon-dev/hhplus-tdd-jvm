package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.point.domain.PointHistory;
import io.hhplus.tdd.point.service.PointService;
import io.hhplus.tdd.point.domain.UserPoint;
import io.hhplus.tdd.point.service.dto.request.PointRequest;
import io.hhplus.tdd.point.service.dto.response.PointResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/point")
public class PointController {

    private static final Logger log = LoggerFactory.getLogger(PointController.class);

    private final PointService pointService;

    /**
     * TODO - 특정 유저의 포인트를 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}")
    public ResponseEntity<PointResponse> point(
            @PathVariable long id
    ) {
        PointResponse response = pointService.getPointsByUserId(id);
        return ResponseEntity.ok(response);
    }

    /**
     * TODO - 특정 유저의 포인트 충전/이용 내역을 조회하는 기능을 작성해주세요.
     */
    @GetMapping("{id}/histories")
    public List<PointHistory> history(
            @PathVariable long id
    ) {
        return List.of();
    }

    /**
     * TODO - 특정 유저의 포인트를 충전하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/charge")
    public ResponseEntity<PointResponse> charge(
            @PathVariable long id,
            @Valid @RequestBody PointRequest request
    ) {
        PointResponse response = pointService.chargePoints(id, request);
        return ResponseEntity.ok(response);
    }

    /**
     * TODO - 특정 유저의 포인트를 사용하는 기능을 작성해주세요.
     */
    @PatchMapping("{id}/use")
    public UserPoint use(
            @PathVariable long id,
            @RequestBody long amount
    ) {
        return new UserPoint(0, 0, 0);
    }
}
