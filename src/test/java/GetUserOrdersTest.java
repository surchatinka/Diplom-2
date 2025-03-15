import client.StellarBurgerClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Ingredients;
import model.Order;
import model.Token;
import model.User;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.*;
import java.util.List;
import java.util.Locale;

@RunWith(Parameterized.class)
public class GetUserOrdersTest {

    public static final String UNAUTHORIZED_MESSAGE = "You should be authorised";
    private final StellarBurgerClient client = new StellarBurgerClient();
    private Token token;
    private final boolean authorization;

    public GetUserOrdersTest(boolean authorization){
        this.authorization=authorization;
    }

    @Step("Test preparation")
    @Before
    public void before(){
        Faker faker = new Faker(Locale.UK);
        User user = new User(faker.internet().emailAddress(), faker.bothify("??##??##??"), faker.name().firstName());
        ValidatableResponse response = client.createUser(user);
        token = client.getToken(response);
        Ingredients ingredients = client.getRandomIngredients(3);
        client.makeOrder(ingredients,token);
    }
    @Step("Test cleanup and shutdown")
    @After
    public void after() {
        if(token.getAccessToken()!=null) {
            client.deleteUser(token);
        }
    }

    @Parameterized.Parameters
    public static Object[][] parameters(){
        return new Object[][]{
                {true},
                {false}
        };
    }

    @Test
    @DisplayName("Get user orders")
    public void authorizationTest_ok(){
        ValidatableResponse response;
        if(authorization){
            response = client.getUserOrders(token);
        }
        else{
            response = client.getUserOrders();
        }

        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);

        if(authorization){
            List<Order> order = client.getOrders(response);
            Assert.assertEquals("Wrong status code",SC_OK,code);
            Assert.assertTrue("Suppose to success but fail",ok);
            Assert.assertNotNull("Json get null id",order.get(0));
        }
        else{
            String message = client.getMessage(response);
            Assert.assertEquals("Wrong status code",SC_UNAUTHORIZED,code);
            Assert.assertFalse("Suppose to fail but success",ok);
            Assert.assertEquals("Wrong message text", UNAUTHORIZED_MESSAGE,message);
        }
    }
}
