package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.config.TestConfig;
import io.hhplus.tdd.point.service.dto.request.PointRequest;
import io.hhplus.tdd.point.service.dto.response.PointResponse;
import io.hhplus.tdd.util.DatabaseCleaner;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = TestConfig.class)
class PointControllerTest {

    @LocalServerPort
    private Integer port;

    @Autowired
    private DatabaseCleaner databaseCleaner;

    @BeforeEach
    void setUp() {
        RestAssured.port = port;
        databaseCleaner.execute();
    }

    @DisplayName("유저의 현재 포인트를 조회한다.")
    @Test
    public void get_UserPoint() {
        // given
        long id = 1L;
        String url = String.format("/point/%d", id);

        // when
        PointResponse response = RestAssured.given().log().all()
                .when().get(url)
                .then().log().all().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        // then
        assertThat(response.userId()).isEqualTo(id);
        assertThat(response.point()).isZero();
    }

    @DisplayName("포인트를 충전한다.")
    @Test
    public void patch_UserPoint_When_Charge() {
        // given
        long id = 1L;
        long pointToCharge = 100L;
        PointRequest request = new PointRequest(pointToCharge);
        String url = String.format("/point/%d/charge", id);

        // when
        PointResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().patch(url)
                .then().log().all().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        // then
        assertThat(response.userId()).isEqualTo(id);
        assertThat(response.point()).isEqualTo(pointToCharge);
    }

    @DisplayName("충전 포인트 요청 값이 누락된 경우 예외가 발생한다.")
    @Test
    public void patch_UserPoint_When_Charge2() {
        // given
        long id = 1L;
        String url = String.format("/point/%d/charge", id);

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().patch(url)
                .then().log().all().statusCode(400);
    }

    @DisplayName("충전 포인트 요청 값이 음수인 경우 예외가 발생한다.")
    @Test
    public void patch_UserPoint_When_Charge3() {
        // given
        long id = 1L;
        long pointToCharge = -100L;
        PointRequest request = new PointRequest(pointToCharge);
        String url = String.format("/point/%d/charge", id);

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().patch(url)
                .then().log().all().statusCode(400);
    }


    @DisplayName("충전 후 포인트가 최대 포인트 잔고 초과한 경우 예외가 발생한다.")
    @Test
    public void patch_Failure_UserPoint_When_Charge_MaxBalanceExceededException() {
        // given
        long id = 1L;
        long currentPoint = 999_999L;
        long pointToCharge = 100L;
        PointRequest request1 = new PointRequest(currentPoint);
        PointRequest request2 = new PointRequest(pointToCharge);
        String url = String.format("/point/%d/charge", id);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request1)
                .when().patch(url)
                .then().log().all().statusCode(200);

        // when & Then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request2)
                .when().patch(url)
                .then().log().all().statusCode(400);
    }
}
