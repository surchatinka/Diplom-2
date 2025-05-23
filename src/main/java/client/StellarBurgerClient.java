package client;

import io.qameta.allure.Step;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.http.ContentType;
import io.restassured.response.ValidatableResponse;
import model.*;
import java.util.ArrayList;
import java.util.List;
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
                .filter(new AllureRestAssured())
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
                .filter(new AllureRestAssured())
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
    @Step ("Client - update user email or name")
    public ValidatableResponse updateUserEmailOrName(Token token, User user) {
        return given()
                .filter(new AllureRestAssured())
                .log().all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(AUTH_USER_ENDPOINT)
                .header("Authorization", token.getAccessToken())
                .body(user)
                .patch()
                .then()
                .log().all();
    }
    @Step ("Client - update user email or name - no authorization")
    public ValidatableResponse updateUserEmailOrName(User user) {
        return given()
                .filter(new AllureRestAssured())
                .log().all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(AUTH_USER_ENDPOINT)
                .body(user)
                .patch()
                .then()
                .log().all();
    }
    @Step ("Client - make order with authorization")
    public ValidatableResponse makeOrder(Ingredients ingredients,Token token){
        return given()
                .filter(new AllureRestAssured())
                .log().all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .header("Authorization", token.getAccessToken())
                .basePath(ORDERS_ENDPOINT)
                .body(ingredients)
                .post()
                .then()
                .log().all();
    }
    @Step ("Client - make order without authorization")
    public ValidatableResponse makeOrder(Ingredients ingredients){
        return given()
                .filter(new AllureRestAssured())
                .log().all()
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(ORDERS_ENDPOINT)
                .body(ingredients)
                .post()
                .then()
                .log().all();
    }
    @Step ("Client - get available ingredients")
    private ValidatableResponse getAvailableIngredients(){
        return given()
                .filter(new AllureRestAssured())
                .baseUri(BASE_URI)
                .contentType(ContentType.JSON)
                .basePath(INGREDIENTS_ENDPOINT)
                .get()
                .then();
    }
    @Step ("Client - get User orders")
    public ValidatableResponse getUserOrders(Token token){
        return given()
                .filter(new AllureRestAssured())
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
                .filter(new AllureRestAssured())
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
    private Ingredients getIngredients(ValidatableResponse response){
        return response.extract().as(Ingredients.class);
    }
    @Step ("Client - get ingredients")
    public String getName(ValidatableResponse response){
        return response.extract().jsonPath().get("name");
    }
    @Step("Client - get random ingredients")
    public Ingredients getRandomIngredients(int numberOfIngredients){

        Ingredients availableIngredients = this.getIngredients(this.getAvailableIngredients());
        List<IngredientData> listOfIngredients = new ArrayList<>();

        for(int i = 0; i < numberOfIngredients; i++) {
            int number = (int)(Math.random()* availableIngredients.getData().size()) ;
            listOfIngredients.add(availableIngredients.getData().get(number));
        }
        return new Ingredients(listOfIngredients);
    }
    @Step("Client - get user orders")
    public Order getOrder(ValidatableResponse response){
        return response.extract().as(Order.class);
    }
    @Step("Client - get user orders")
    public List<Order> getOrders(ValidatableResponse response){
        return response.extract().jsonPath().getList("orders", Order.class);
    }
    @Step ("Client - get user")
    public User getEmailAndLogin(ValidatableResponse response){
        return response.extract().jsonPath().getObject("user",User.class);
    }
}