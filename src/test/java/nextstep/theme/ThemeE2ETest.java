package nextstep.theme;

import auth.domain.dto.TokenRequest;
import auth.domain.dto.TokenResponse;
import io.restassured.RestAssured;
import nextstep.AbstractE2ETest;
import nextstep.domain.dto.request.MemberRequest;
import nextstep.domain.dto.request.ThemeRequest;
import nextstep.util.RequestBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

import static nextstep.util.RequestBuilder.*;
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
        RestAssured
                .given().log().all()
                .auth().oauth2(token.getAccessToken())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(themeRequest())
                .when().post("/admin/themes")
                .then().log().all()
                .statusCode(HttpStatus.CREATED.value());
    }

    @DisplayName("어드민이 아닌 사람이 테마를 생성한다")
    @Test
    public void Should_ThrowUnAuthorized_When_IfAttemptToCreateTheme_WhoIsNotAdmin() {
        createUser();

        RestAssured
                .given().log().all()
                .auth().oauth2(tokenForUser())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .body(themeRequest())
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
        Long themeId = createTheme();

        var response = RestAssured
                .given().log().all()
                .auth().oauth2(token.getAccessToken())
                .when().delete("/admin/themes/" + themeId)
                .then().log().all()
                .extract();

        assertThat(response.statusCode()).isEqualTo(HttpStatus.NO_CONTENT.value());
    }

}
