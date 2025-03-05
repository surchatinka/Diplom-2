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

public class LoginUserTest {

    private final StellarBurgerClient client = new StellarBurgerClient();
    private User user;
    private Token token;

    @Before
    public void before(){
        Faker faker = new Faker(Locale.UK);
        user = new User(faker.internet().emailAddress(), faker.bothify("??##??##??"), faker.name().firstName());
        client.createUser(user);
    }
    @After
    public void after(){
        if(token.getAccessToken()!=null) {
            client.deleteUser(token);
        }
    }

    @Test
    public void authorizationExistingUserTest_ok(){
        ValidatableResponse response = client.loginUser(user);
        int code = response.extract().statusCode();
        token = response.extract().as(Token.class);
        boolean ok = response.extract().jsonPath().get("success");

        Assert.assertEquals("wrong code",SC_OK,code);
        Assert.assertNotNull(token.getAccessToken());
        Assert.assertNotNull(token.getRefreshToken());
        Assert.assertTrue(ok);

    }
    @Test
    public void authorizationNoLoginTest_fail(){
        user.setEmail("");
        ValidatableResponse response = client.loginUser(user);
        int code = response.extract().statusCode();
        token = response.extract().as(Token.class);
        boolean ok = response.extract().jsonPath().get("success");
        String message = response.extract().jsonPath().get("message");

        Assert.assertEquals("wrong code",SC_UNAUTHORIZED,code);;
        Assert.assertFalse(ok);
        Assert.assertEquals("Wrong message","email or password are incorrect", message);
    }
    @Test
    public void authorizationNoPasswordTest_fail(){
        user.setPassword("");
        ValidatableResponse response = client.loginUser(user);
        int code = response.extract().statusCode();
        token = response.extract().as(Token.class);
        boolean ok = response.extract().jsonPath().get("success");
        String message = response.extract().jsonPath().get("message");

        Assert.assertEquals("wrong code",SC_UNAUTHORIZED,code);;
        Assert.assertFalse(ok);
        Assert.assertEquals("Wrong message","email or password are incorrect", message);

    }
}
