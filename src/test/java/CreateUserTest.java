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
    public void createUserTest_ok() {
        ValidatableResponse response = client.createUser(user);
        int code = client.getStatusCode(response);
        token = client.getToken(response);
        boolean ok = client.getStatus(response);

        Assert.assertEquals(WRONG_STATUS_CODE, SC_CREATED, code);
        Assert.assertNotNull("No token received", token.getAccessToken());
        Assert.assertNotNull("No token received", token.getRefreshToken());
        Assert.assertTrue("Create user fails",ok);

    }
    @Test
    public void createExistingUserTest_fail(){
        ValidatableResponse responseOriginal = client.createUser(user);
        token = client.getToken(responseOriginal);
        ValidatableResponse responseExisted = client.createUser(user);
        int code = client.getStatusCode(responseExisted);
        boolean ok = client.getStatus(responseExisted);
        String message = client.getMessage(responseExisted);

        Assert.assertEquals(WRONG_STATUS_CODE, SC_FORBIDDEN, code);
        Assert.assertFalse(SUPPOSED_TO_FAIL_BUT_SUCCEED,ok);
        Assert.assertEquals(MESSAGE_TEXT_DON_T_MATCH, USER_ALREADY_EXISTS,message);
    }
    @Test
    public void createUserWithoutEmailTest_fail(){
        User.UserBuilder builder = User.builder().email("").name(user.getName()).password(user.getPassword());
        User userNoEmail = builder.build();
        createUserWithout(userNoEmail);
    }
    @Test
    public void createUserWithoutPasswordTest_fail(){
        User.UserBuilder builder = User.builder().email(user.getEmail()).name(user.getName()).password("");
        User userNoPassword = builder.build();
        createUserWithout(userNoPassword);
    }
    @Test
    public void createUserWithoutNameTest_fail(){
        User.UserBuilder builder = User.builder().email(user.getEmail()).name("").password(user.getPassword());
        User userNoName = builder.build();
        createUserWithout(userNoName);
    }

    private void createUserWithout(User user) {
        ValidatableResponse response = client.createUser(user);
        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);
        String message = client.getMessage(response);
        token = client.getToken(response);

        Assert.assertEquals(WRONG_STATUS_CODE, SC_FORBIDDEN, code);
        Assert.assertFalse(SUPPOSED_TO_FAIL_BUT_SUCCEED,ok);
        Assert.assertEquals(MESSAGE_TEXT_DON_T_MATCH,REQUIRED_FIELD_MISSING,message);
    }
}