import client.StellarBurgerClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.internal.shadowed.jackson.core.JsonProcessingException;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.*;
import net.datafaker.Faker;
import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import java.util.List;
import java.util.Locale;

public class GetUserOrdersTest {

    private static final String UNAUTHORIZED_MESSAGE = "You should be authorised";
    private final StellarBurgerClient client = new StellarBurgerClient();
    private Token token;
    private Ingredients ingredients;

    @Step("Test preparation")
    @Before
    public void before(){
        Faker faker = new Faker(Locale.UK);
        User user = new User(faker.internet().emailAddress(), faker.bothify("??##??##??"), faker.name().firstName());
        ValidatableResponse response = client.createUser(user);
        token = client.getToken(response);
        ingredients = client.getRandomIngredients(3);
        client.makeOrder(ingredients,token);
    }
    @Step("Test cleanup and shutdown")
    @After
    public void after() {
        if(token.getAccessToken()!=null) {
            client.deleteUser(token);
        }
    }

    @Test
    @DisplayName("Get user orders with authorization")
    @Description("Test checks possibility to get user orders with authorization")
    public void getUserOrdersWithAuthorizationTest_ok() throws JsonProcessingException {
        ValidatableResponse response = client.getUserOrders(token);

        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);
        List<Order> orders = client.getOrders(response);
        SoftAssertions softly = new SoftAssertions();
        softly.assertThat(code).isEqualTo(SC_OK);
        softly.assertThat(orders).isNotNull();
        for (int i = 0 ; i<orders.size();i++){
            softly.assertThat(orders.get(0).getIngredients().get(i)).isEqualTo(ingredients.getData().get(i).getId());
        }
        softly.assertThat(ok).isTrue();
        softly.assertAll();
    }
    @Test
    @DisplayName("Get user orders without authorization")
    @Description("Test checks impossibility to get user orders with authorization")
    public void getUserOrdersWithoutAuthorizationTest_ok(){
        ValidatableResponse response = client.getUserOrders();

        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);
        String message = client.getMessage(response);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(code).isEqualTo(SC_UNAUTHORIZED);
        softly.assertThat(message).isEqualTo(UNAUTHORIZED_MESSAGE);
        softly.assertThat(ok).isFalse();
        softly.assertAll();
    }
}
