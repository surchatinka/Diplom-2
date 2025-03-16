import client.StellarBurgerClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.*;
import net.datafaker.Faker;
import org.assertj.core.api.SoftAssertions;
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
    private static final String MESSAGE_NO_INGREDIENTS = "Ingredient ids must be provided";
    private static final String NO_AUTHORIZATION = "You should be authorised";
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
    @Description("Test checks possibility to make an order with authorization and ingredients")
    public void makeOrderWithAuthAndIngredientsTest_ok(){
        ValidatableResponse response = client.makeOrder(availableIngredients,token);
        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);
        Order order = client.getOrder(response);
        String name = client.getName(response);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(code).isEqualTo(SC_OK);
        softly.assertThat(order.getNumber()).isNotNull();
        softly.assertThat(name).isNotNull();
        softly.assertThat(ok).isTrue();
        softly.assertAll();
    }
    @Test
    @DisplayName("Make order without authorization")
    @Description("Test checks possibility to make an order without authorization")
    public void makeOrderWithoutAuthTest_ok(){
        ValidatableResponse response = client.makeOrder(availableIngredients);
        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);
        Order order = client.getOrder(response);
        String name = client.getName(response);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(code).isEqualTo(SC_OK);
        softly.assertThat(ok).isTrue();
        softly.assertThat(order.getNumber()).isNotNull();
        softly.assertThat(name).isNotNull();
        softly.assertAll();
    }
    @Test
    @DisplayName("Make order without ingredients")
    @Description("Test checks impossibility to make an order without ingredients")
    public void makeOrderWithoutIngredientsTest_fail(){
        List<IngredientData> emptyList = new ArrayList<>();
        Ingredients.IngredientsBuilder builder = Ingredients.builder().data(emptyList);
        Ingredients emptyBurger = builder.build();

        ValidatableResponse response = client.makeOrder(emptyBurger,token);
        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);
        String message = client.getMessage(response);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(code).isEqualTo(SC_BAD_REQUEST);
        softly.assertThat(message).isEqualTo(MESSAGE_NO_INGREDIENTS);
        softly.assertThat(ok).isFalse();
        softly.assertAll();
    }
    @Test
    @DisplayName("Make order with non-existing ingredients")
    @Description("Test checks impossibility to make an order with wrong ingredients")
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