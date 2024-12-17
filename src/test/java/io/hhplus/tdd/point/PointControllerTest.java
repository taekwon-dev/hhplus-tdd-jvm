package io.hhplus.tdd.point;

import io.hhplus.tdd.point.domain.UserPoint;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class PointControllerTest {

    @LocalServerPort
    private Integer port;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
    }

    // 테스트 작성 이유: PointController GET /point/{id} `등록되지 않은 ID`가 주어졌을 때 테스트
    @DisplayName("등록되지 않은 ID로 유저 포인트 조회 시 유저 등록된 후 0포인트가 조회된다.")
    @Test
    void get_UserPoint_Should_Return_UserPointWithZeroPoint_When_NonExistingId() {
        // given
        long id = 1L;
        String url = String.format("/point/%d", id);

        // when
        UserPoint response = RestAssured.given().log().all()
                .when().get(url)
                .then().log().all().statusCode(200)
                .extract().as(new TypeRef<>() {});

        // then
        assertThat(response.id()).isEqualTo(id);
        assertThat(response.point()).isZero();
    }
}
