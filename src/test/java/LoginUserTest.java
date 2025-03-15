import client.StellarBurgerClient;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
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

    @Step("Test preparation")
    @Before
    public void before(){
        Faker faker = new Faker(Locale.UK);
        user = new User(faker.internet().emailAddress(), faker.bothify("??##??##??"), faker.name().firstName());
        client.createUser(user);
    }
    @Step("Test cleanup and shutdown")
    @After
    public void after(){
        if(token.getAccessToken()!=null) {
            client.deleteUser(token);
        }
    }

    @Test
    @DisplayName("Login with existing user")
    public void authorizationExistingUserTest_ok(){
        ValidatableResponse response = client.loginUser(user);
        int code = client.getStatusCode(response);
        token = client.getToken(response);
        boolean ok = client.getStatus(response);

        Assert.assertEquals("wrong code",SC_OK,code);
        Assert.assertNotNull(token.getAccessToken());
        Assert.assertNotNull(token.getRefreshToken());
        Assert.assertTrue(ok);
    }
    @Test
    @DisplayName("Login without login-field")
    public void authorizationNoLoginTest_fail(){
        User.UserBuilder builder = User.builder().email("").name(user.getName()).password(user.getPassword());
        User userNoEmail = builder.build();
        authorizationWithout(userNoEmail);
    }
    @Test
    @DisplayName("Login without password-field")
    public void authorizationNoPasswordTest_fail(){
        User.UserBuilder builder = User.builder().email(user.getEmail()).name(user.getName()).password("");
        User userNoPassword = builder.build();
        authorizationWithout(userNoPassword);
    }
    private void authorizationWithout(User userWithout){
        ValidatableResponse response = client.loginUser(userWithout);
        int code = client.getStatusCode(response);
        token = client.getToken(response);
        boolean ok = client.getStatus(response);
        String message = client.getMessage(response);

        Assert.assertEquals("wrong code",SC_UNAUTHORIZED,code);;
        Assert.assertFalse(ok);
        Assert.assertEquals("Wrong message","email or password are incorrect", message);
    }
}