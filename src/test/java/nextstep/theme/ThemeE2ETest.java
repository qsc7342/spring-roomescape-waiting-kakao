package nextstep.theme;

import io.restassured.RestAssured;
import nextstep.AbstractE2ETest;
import auth.domain.dto.TokenRequest;
import auth.domain.dto.TokenResponse;
import nextstep.DatabaseCleaner;
import nextstep.domain.dto.request.MemberRequest;
import nextstep.domain.dto.request.ScheduleRequest;
import nextstep.domain.dto.request.ThemeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static org.assertj.core.api.Assertions.assertThat;

public class ThemeE2ETest extends AbstractE2ETest {

    @Override
    @BeforeEach
    public void setUp() {
        super.setUp();
    }

    @DisplayName("테마를 생성한다")
    @Test
    public void Should_CreateTheme_When_Request() {
        ThemeRequest body = new ThemeRequest("테마이름", "테마설명", 22000);
        RestAssured
                .given().log().all()
                .auth().oauth2(token.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("어드민이 아닌 사람이 테마를 생성한다")
    @Test
    public void Should_ThrowUnAuthorized_When_IfAttemptToCreateTheme_WhoIsNotAdmin() {
        MemberRequest memberBody = new MemberRequest(USERNAME+1, PASSWORD, "name", "010-1234-5678", "");
        RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(memberBody)
                .when().post("/members")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());

        TokenRequest tokenBody = new TokenRequest(USERNAME+1, PASSWORD);
        var response = RestAssured
                .given().log().all()
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(tokenBody)
                .when().post("/login/token")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();

        token = response.as(TokenResponse.class);

        ThemeRequest body = new ThemeRequest("테마이름", "테마설명", 22000);
        RestAssured
                .given().log().all()
                .auth().oauth2(token.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(HttpStatus.UNAUTHORIZED.value());
    }

    @DisplayName("테마 목록을 조회한다")
    @Test
    public void Should_GetThemes_When_Request() {
        createTheme();

        var response = RestAssured
                .given().log().all()
                .param("date", "2022-08-11")
                .when().get("/themes")
                .then().log().all()
                .statusCode(HttpStatus.OK.value())
                .extract();
        assertThat(response.jsonPath().getList(".").size()).isEqualTo(1);
    }

    @DisplayName("테마를 삭제한다")
    @Test
    void Should_DeleteThemes_When_Request() {
        Long id = createTheme();

        var response = RestAssured
                .given().log().all()
                .auth().oauth2(token.getAccessToken())
                .when().delete("/admin/themes/" + id)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

    public Long createTheme() {
        ThemeRequest body = new ThemeRequest("테마이름", "테마설명", 22000);
        String location = RestAssured
                .given().log().all()
                .auth().oauth2(token.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(body)
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value())
                .extract().header("Location");
        return Long.parseLong(location.split("/")[2]);
    }

}
