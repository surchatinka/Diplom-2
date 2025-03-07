import client.StellarBurgerClient;
import io.restassured.response.ValidatableResponse;
import model.Token;
import model.User;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import static org.apache.http.HttpStatus.*;
import java.util.Locale;

public class GetUserOrdersTest {

    private final StellarBurgerClient client = new StellarBurgerClient();
    private Token token;

    @Before
    public void before(){
        Faker faker = new Faker(Locale.UK);
        User user = new User(faker.internet().emailAddress(), faker.bothify("??##??##??"), faker.name().firstName());
        ValidatableResponse response = client.createUser(user);
        token = client.getToken(response);
    }
    @After
    public void after() {
        if(token.getAccessToken()!=null) {
            client.deleteUser(token);
        }
    }

    @Test
    public void authorizedUserTest_ok(){
        ValidatableResponse response = client.getUserOrders(token);
        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);

        Assert.assertEquals("Wrong status code",SC_OK,code);
        Assert.assertTrue("Suppose to success but fail",ok);
    }
    @Test
    public void unauthorizedUserTest_fail(){
        ValidatableResponse response = client.getUserOrders();
        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);

        Assert.assertEquals("Wrong status code",SC_UNAUTHORIZED,code);
        Assert.assertFalse("Suppose to fail but success",ok);
    }
}