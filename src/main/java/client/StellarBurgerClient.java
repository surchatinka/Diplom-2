package client;

import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import model.Token;
import model.User;

import static io.restassured.RestAssured.*;

public class StellarBurgerClient {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private static final String CREATE_USER_ENDPOINT = "/api/auth/register";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String LOGOUT_ENDPOINT = "/api/auth/logout";
    private static final String AUTH_USER_ENDPOINT = "/api/auth/user";

    @Step
    @DisplayName ("Client - user create")
    public ValidatableResponse createUser(User user){
        return given()
                .log().all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(CREATE_USER_ENDPOINT)
                .body(user)
                .post()
                .then()
                .log().all();
    }

    @Step
    @DisplayName ("Client - user delete")
    public void deleteUser(Token token){
        given()
                .log().all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(AUTH_USER_ENDPOINT)
                .header("Authorization", token.getAccessToken())
                .delete()
                .then()
                .log().all();
    }
    @Step
    @DisplayName ("Client - user login")
    public ValidatableResponse loginUser(User user){
        return given()
                .filter(new AllureRestAssured())
                .log().all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(LOGIN_ENDPOINT)
                .body(user)
                .post()
                .then()
                .log().all();
    }
    @Step
    @DisplayName("Client - user logout")
    public ValidatableResponse logoutUser(Token token) {
        return given()
                .log().all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(LOGOUT_ENDPOINT)
                .body(token.getRefreshToken())
                .post()
                .then()
                .log().all();
    }
    @Step
    @DisplayName("Client - get user data")
    public ValidatableResponse getUserData(Token token){
        return given()
                .log().all()
                .baseUri(baseURI)
                .contentType(ContentType.JSON)
                .basePath(AUTH_USER_ENDPOINT)
                .header("Authorization", token.getAccessToken())
                .get()
                .then()
                .log().all();
    }
    @Step
    @DisplayName("Client - update user data")
    public ValidatableResponse updateUserData(Token token, String type, String email) throws Exception{
        return given()
                .log().all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(AUTH_USER_ENDPOINT)
                .header("Authorization", token.getAccessToken())
                .body(String.format("{\"%s\":\"%s\"}",type,email))
                .patch()
                .then()
                .log().all();
    }
    @Step
    @DisplayName("Client - update user data - no authorisation")
    public ValidatableResponse updateUserData(String type, String email)throws Exception{
        return given()
                .log().all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(AUTH_USER_ENDPOINT)
                .body(String.format("{\"%s\":\"%s\"}",type,email))
                .patch()
                .then()
                .log().all();
    }

//    @Step
//    @DisplayName("Client - get refresh token")
//    public String getRefreshToken(ValidatableResponse response) {
//        return response.extract().jsonPath().get("refreshToken").toString();
//    }
//
//    @Step
//    @DisplayName("Client - get access token")
//    public String getAccessToken(ValidatableResponse response) {
//        return response.extract().jsonPath().get("accessToken").toString();
//    }

// POST https://stellarburgers.nomoreparties.site/api/auth/register — эндпоинт для регистрации пользователя.
// Создание пользователя POST https://stellarburgers.nomoreparties.site/api/auth/register
}
