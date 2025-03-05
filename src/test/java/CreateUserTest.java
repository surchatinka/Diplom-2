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


public class CreateUserTest {

    private static final String USER_ALREADY_EXISTS = "User already exists";
    private static final String WRONG_STATUS_CODE = "Wrong status code";
    private static final String MESSAGE_TEXT_DON_T_MATCH = "Message text don't match";
    private static final String REQUIRED_FIELD_MISSING = "Email, password and name are required fields";
    private static final String SUPPOSED_TO_FAIL_BUT_SUCCEED = "Operation passed but shouldn't";
    private final StellarBurgerClient client = new StellarBurgerClient();
    private Token token;
    private User user;

    @Before
    public void before(){
        Faker faker = new Faker(Locale.UK);
        user = new User(faker.internet().emailAddress(), faker.bothify("??##??##??"), faker.name().firstName());
    }
    @After
    public void after() {
        if(token.getAccessToken()!=null) {
            client.deleteUser(token);
        }
    }

    @Test
    public void createUser_ok() {
        ValidatableResponse response = client.createUser(user);
        int code = response.extract().statusCode();
        token = response.extract().as(Token.class);
        boolean ok = response.extract().jsonPath().get("success");

        Assert.assertEquals(WRONG_STATUS_CODE, SC_CREATED, code);
        Assert.assertNotNull("No token received", token.getAccessToken());
        Assert.assertNotNull("No token received", token.getRefreshToken());
        Assert.assertTrue("Create user fails",ok);

    }
    @Test
    public void createExistingUser_fail(){
        ValidatableResponse responseOriginal = client.createUser(user);
        ValidatableResponse responseExisted = client.createUser(user);
        int code = responseExisted.extract().statusCode();
        boolean ok = responseExisted.extract().jsonPath().get("success");
        String message = responseExisted.extract().jsonPath().get("message");
        token = responseOriginal.extract().as(Token.class);

        Assert.assertEquals(WRONG_STATUS_CODE, SC_FORBIDDEN, code);
        Assert.assertFalse(SUPPOSED_TO_FAIL_BUT_SUCCEED,ok);
        Assert.assertEquals(MESSAGE_TEXT_DON_T_MATCH, USER_ALREADY_EXISTS,message);
    }
    @Test
    public void createUserWithoutEmail_fail(){
        user.setEmail("");
        ValidatableResponse response = client.createUser(user);
        int code = response.extract().statusCode();
        boolean ok = response.extract().jsonPath().get("success");
        String message = response.extract().jsonPath().get("message");
        token = response.extract().as(Token.class);

        Assert.assertEquals(WRONG_STATUS_CODE, SC_FORBIDDEN, code);
        Assert.assertFalse(SUPPOSED_TO_FAIL_BUT_SUCCEED,ok);
        Assert.assertEquals(MESSAGE_TEXT_DON_T_MATCH, REQUIRED_FIELD_MISSING,message);
    }
    @Test
    public void createUserWithoutPassword_fail(){
        user.setPassword("");
        ValidatableResponse response = client.createUser(user);
        int code = response.extract().statusCode();
        boolean ok = response.extract().jsonPath().get("success");
        String message = response.extract().jsonPath().get("message");
        token = response.extract().as(Token.class);

        Assert.assertEquals(WRONG_STATUS_CODE, SC_FORBIDDEN, code);
        Assert.assertFalse(SUPPOSED_TO_FAIL_BUT_SUCCEED,ok);
        Assert.assertEquals(MESSAGE_TEXT_DON_T_MATCH,REQUIRED_FIELD_MISSING,message);
    }
    @Test
    public void createUserWithoutName_fail(){
        user.setName("");
        ValidatableResponse response = client.createUser(user);
        int code = response.extract().statusCode();
        boolean ok = response.extract().jsonPath().get("success");
        String message = response.extract().jsonPath().get("message");
        token = response.extract().as(Token.class);

        Assert.assertEquals(WRONG_STATUS_CODE, SC_FORBIDDEN, code);
        Assert.assertFalse(SUPPOSED_TO_FAIL_BUT_SUCCEED,ok);
        Assert.assertEquals(MESSAGE_TEXT_DON_T_MATCH,REQUIRED_FIELD_MISSING,message);
    }
}
