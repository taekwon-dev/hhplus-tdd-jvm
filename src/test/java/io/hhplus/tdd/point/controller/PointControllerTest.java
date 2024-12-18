package io.hhplus.tdd.point.controller;

import io.hhplus.tdd.config.TestConfig;
import io.hhplus.tdd.point.service.dto.request.PointRequest;
import io.hhplus.tdd.point.service.dto.response.PointResponse;
import io.hhplus.tdd.util.DatabaseCleaner;
import io.restassured.RestAssured;
import io.restassured.common.mapper.TypeRef;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeEach;
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

    @Test
    public void 유저의_현재_포인트를_조회한다() {
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

    @Test
    public void 포인트를_충전한다() {
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

    @Test
    public void 충전_포인트_요청값이_누락된_경우_예외가_발생한다() {
        // given
        long id = 1L;
        String url = String.format("/point/%d/charge", id);

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().patch(url)
                .then().log().all().statusCode(400);
    }

    @Test
    public void 충전_포인트_요청값이_0인_경우_예외가_발생한다() {
        // given
        long id = 1L;
        long pointToCharge = 0L;
        PointRequest request = new PointRequest(pointToCharge);
        String url = String.format("/point/%d/charge", id);

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(request)
                .when().patch(url)
                .then().log().all().statusCode(400);
    }


    @Test
    public void 충전_포인트_요청값이_음수인_경우_예외가_발생한다() {
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


    @Test
    public void 충전_후_포인트가_최대_포인트_잔고_초과한_경우_예외가_발생한다() {
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

    @Test
    public void 포인트를_사용한다() {
        // given
        long id = 1L;
        long currentPoint = 1_000L;
        long pointToUse = 100L;
        PointRequest chargeRequest = new PointRequest(currentPoint);
        PointRequest useRequest = new PointRequest(pointToUse);
        String chargeUrl = String.format("/point/%d/charge", id);
        String useUrl = String.format("/point/%d/use", id);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(chargeRequest)
                .when().patch(chargeUrl)
                .then().log().all().statusCode(200);

        // when
        PointResponse response = RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(useRequest)
                .when().patch(useUrl)
                .then().log().all().statusCode(200)
                .extract().as(new TypeRef<>() {
                });

        // then
        assertThat(response.userId()).isEqualTo(id);
        assertThat(response.point()).isEqualTo(currentPoint - pointToUse);
    }


    @Test
    public void 사용_포인트_요청값이_누락된_경우_예외가_발생한다() {
        // given
        long id = 1L;
        long currentPoint = 1_000L;
        PointRequest chargeRequest = new PointRequest(currentPoint);
        String chargeUrl = String.format("/point/%d/charge", id);
        String useUrl = String.format("/point/%d/use", id);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(chargeRequest)
                .when().patch(chargeUrl)
                .then().log().all().statusCode(200);

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .when().patch(useUrl)
                .then().log().all().statusCode(400);
    }

    @Test
    public void 사용_포인트_요청값이_0인_경우_예외가_발생한다() {
        // given
        long id = 1L;
        long currentPoint = 1_000L;
        long pointToUse = 0L;
        PointRequest chargeRequest = new PointRequest(currentPoint);
        PointRequest useRequest = new PointRequest(pointToUse);
        String chargeUrl = String.format("/point/%d/charge", id);
        String useUrl = String.format("/point/%d/use", id);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(chargeRequest)
                .when().patch(chargeUrl)
                .then().log().all().statusCode(200);

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(useRequest)
                .when().patch(useUrl)
                .then().log().all().statusCode(400);
    }

    @Test
    public void 사용_포인트_요청값이_음수인_경우_예외가_발생한다() {
        // given
        long id = 1L;
        long currentPoint = 1_000L;
        long pointToUse = -100L;
        PointRequest chargeRequest = new PointRequest(currentPoint);
        PointRequest useRequest = new PointRequest(pointToUse);
        String chargeUrl = String.format("/point/%d/charge", id);
        String useUrl = String.format("/point/%d/use", id);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(chargeRequest)
                .when().patch(chargeUrl)
                .then().log().all().statusCode(200);

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(useRequest)
                .when().patch(useUrl)
                .then().log().all().statusCode(400);
    }

    @Test
    public void 포인트_잔액이_부족한_경우_예외가_발생한다() {
        // given
        long id = 1L;
        long currentPoint = 1_000L;
        long pointToUse = 2_000L;
        PointRequest chargeRequest = new PointRequest(currentPoint);
        PointRequest useRequest = new PointRequest(pointToUse);
        String chargeUrl = String.format("/point/%d/charge", id);
        String useUrl = String.format("/point/%d/use", id);

        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(chargeRequest)
                .when().patch(chargeUrl)
                .then().log().all().statusCode(200);

        // when & then
        RestAssured.given().log().all()
                .contentType(ContentType.JSON)
                .body(useRequest)
                .when().patch(useUrl)
                .then().log().all().statusCode(400);
    }
}
