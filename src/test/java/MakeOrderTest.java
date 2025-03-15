import client.StellarBurgerClient;
import io.qameta.allure.Issue;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.*;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import static org.apache.http.HttpStatus.*;

public class MakeOrderTest {

    private static final int NUMBER_OF_INGREDIENTS = 3;
    private static final String WRONG_STATUS_CODE = "Wrong status code";
    private final StellarBurgerClient client = new StellarBurgerClient();
    private Ingredients availableIngredients;
    private Token token;

    @Step("Test preparation")
    @Before
    public void before(){
        Faker faker = new Faker(Locale.UK);
        User user = new User(faker.internet().emailAddress(), faker.bothify("??##??##??"), faker.name().firstName());
        ValidatableResponse responseCreate = client.createUser(user);
        token = client.getToken(responseCreate);

        availableIngredients = client.getRandomIngredients(NUMBER_OF_INGREDIENTS);
    }
    @Step("Test cleanup and shutdown")
    @After
    public void after(){
        if(token.getAccessToken()!=null) {
            client.deleteUser(token);
        }
    }

    @Test
    @DisplayName("Make order with ingredients and authorization")
    @Issue("Bug report for WRONG SERVER STATUS CODE")
    public void makeOrderWithAuthAndIngredientsTest_ok(){
        ValidatableResponse response = client.makeOrder(availableIngredients,token);
        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);
        Order order = client.getOrder(response);
        Assert.assertEquals(WRONG_STATUS_CODE,SC_CREATED, code);
        Assert.assertTrue("Fail to make an order",ok);
        Assert.assertEquals(availableIngredients.getData(),order.getIngredients());
    }
    @Test
    @DisplayName("Make order without authorization")
    @Issue("Bug report for WRONG SERVER STATUS CODE")
    public void makeOrderWithoutAuthTest_ok(){
        ValidatableResponse response = client.makeOrder(availableIngredients);
        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);
        String message = client.getMessage(response);

        Assert.assertEquals(WRONG_STATUS_CODE,SC_UNAUTHORIZED, code);
        Assert.assertFalse("Fail to make an order",ok);
        Assert.assertEquals("Wrong message in json body","You should be authorised",message);

    }
    @Test
    @DisplayName("Make order without ingredients")
    public void makeOrderWithoutIngredientsTest_fail(){
        List<IngredientData> emptyList = new ArrayList<>();
        Ingredients.IngredientsBuilder builder = Ingredients.builder().data(emptyList);
        Ingredients emptyBurger = builder.build();

        ValidatableResponse response = client.makeOrder(emptyBurger,token);
        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);
        String message = client.getMessage(response);

        Assert.assertEquals(WRONG_STATUS_CODE,SC_BAD_REQUEST, code);
        Assert.assertFalse("Expected fail but success",ok);
        Assert.assertEquals("Response text differs",message,"Ingredient ids must be provided");
    }
    @Test
    @DisplayName("Make order with non-existing ingredients")
    public void makeOrderWithWrongIngredientsTest_fail(){
        Faker faker = new Faker(Locale.ENGLISH);
        List<IngredientData> randomList = new ArrayList<>();
        for(int i = 0; i<NUMBER_OF_INGREDIENTS;i++) {
            randomList.add(new IngredientData(faker.bothify("??????###")));
        }

        Ingredients.IngredientsBuilder builder = Ingredients.builder().data(randomList);
        Ingredients unknownComponentsBurger = builder.build();

        ValidatableResponse response = client.makeOrder(unknownComponentsBurger,token);
        int code = client.getStatusCode(response);

        Assert.assertEquals(WRONG_STATUS_CODE,SC_INTERNAL_SERVER_ERROR, code);
    }
}