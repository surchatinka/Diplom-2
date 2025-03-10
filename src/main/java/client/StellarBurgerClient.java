package client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import model.Ingredients;
import model.Token;
import model.User;
import static io.restassured.RestAssured.*;

public class StellarBurgerClient {
    private static final String BASE_URI = "https://stellarburgers.nomoreparties.site";
    private static final String CREATE_USER_ENDPOINT = "/api/auth/register";
    private static final String LOGIN_ENDPOINT = "/api/auth/login";
    private static final String AUTH_USER_ENDPOINT = "/api/auth/user";
    private static final String ORDERS_ENDPOINT = "/api/orders";
    private static final String INGREDIENTS_ENDPOINT = "/api/ingredients";

    @Step ("Client - user create")
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

    @Step ("Client - user delete")
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
    @Step ("Client - user login")
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
    @Step ("Client - update user data")
    public ValidatableResponse updateUserData(Token token, String type, String email) {
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
    @Step ("Client - update user data - no authorisation")
    public ValidatableResponse updateUserData(String type, String email) {
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
    @Step ("Client - make order with authorization")
    public ValidatableResponse makeOrder(Ingredients ingredients,Token token){
        return given()
                .log().all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .header("Authorization", token.getAccessToken())
                .basePath(ORDERS_ENDPOINT)
                .body(ingredients.toIngredientsJSON())
                .post()
                .then()
                .log().all();
    }
    @Step ("Client - make order without authorization")
    public ValidatableResponse makeOrder(Ingredients ingredients){
        return given()
                .log().all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(ORDERS_ENDPOINT)
                .body(ingredients.toIngredientsJSON())
                .post()
                .then()
                .log().all();
    }
    @Step ("Client - get available ingredients")
    public ValidatableResponse getAvailableIngredients(){
        return given()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(INGREDIENTS_ENDPOINT)
                .get()
                .then();
    }
    @Step ("Client - get User orders")
    public ValidatableResponse getUserOrders(Token token){
        return given()
                .log().all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(ORDERS_ENDPOINT)
                .header("Authorization", token.getAccessToken())
                .get()
                .then()
                .log().all();
    }
    @Step ("Client - get User orders")
    public ValidatableResponse getUserOrders(){
        return given()
                .log().all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(ORDERS_ENDPOINT)
                .get()
                .then()
                .log().all();
    }
    @Step ("Client - get response code")
    public int getStatusCode(ValidatableResponse response) {
        return response.extract().statusCode();
    }
    @Step ("Client - get response status")
    public boolean getStatus(ValidatableResponse response) {
        return response.extract().jsonPath().get("success");
    }
    @Step ("Client - get message")
    public String getMessage(ValidatableResponse response){
        return response.extract().jsonPath().get("message");
    }
    @Step ("Client - get token")
    public Token getToken(ValidatableResponse response){
        return response.extract().as(Token.class);
    }
    @Step ("Client - get ingredients")
    public Ingredients getIngredients(ValidatableResponse response){
        return response.extract().as(Ingredients.class);
    }
}
