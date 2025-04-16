import client.StellarBurgerClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Token;
import model.User;
import net.datafaker.Faker;
import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.apache.http.HttpStatus.*;
import java.util.Locale;

public class CreateUserTest {

    private static final String USER_ALREADY_EXISTS = "User already exists";
    private static final String REQUIRED_FIELD_MISSING = "Email, password and name are required fields";
    private final StellarBurgerClient client = new StellarBurgerClient();
    private Token token;
    private User user;

    @Step("Test preparation")
    @Before
    public void before(){
        Faker faker = new Faker(Locale.UK);
        user = new User(faker.internet().emailAddress(), faker.bothify("??##??##??"), faker.name().firstName());
    }
    @Step("Test cleanup and shutdown")
    @After
    public void after() {
        if(token.getAccessToken()!=null) {
            client.deleteUser(token);
        }
    }

    @Test
    @DisplayName("Create default user")
    @Description("Base test for create an user")
    public void createUserTest_ok() {
        ValidatableResponse response = client.createUser(user);
        int code = client.getStatusCode(response);
        token = client.getToken(response);
        boolean ok = client.getStatus(response);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(code).isEqualTo(SC_OK);
        softly.assertThat(token.getAccessToken()).isNotNull();
        softly.assertThat(token.getRefreshToken()).isNotNull();
        softly.assertThat(ok).isTrue();
        softly.assertAll();
    }
    @Test
    @DisplayName("Create existing user")
    @Description("Test checks that it wouldn't possible to create an existing user")
    public void createExistingUserTest_fail(){
        ValidatableResponse responseOriginal = client.createUser(user);
        token = client.getToken(responseOriginal);
        ValidatableResponse responseExisted = client.createUser(user);
        int code = client.getStatusCode(responseExisted);
        boolean ok = client.getStatus(responseExisted);
        String message = client.getMessage(responseExisted);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(code).isEqualTo(SC_FORBIDDEN);
        softly.assertThat(message).isEqualTo(USER_ALREADY_EXISTS);
        softly.assertThat(ok).isFalse();
        softly.assertAll();
    }
    @Test
    @DisplayName("Create user without email")
    @Description("Test checks that it wouldn't possible to create an user without email")
    public void createUserWithoutEmailTest_fail(){
        User.UserBuilder builder = User.builder().email("").name(user.getName()).password(user.getPassword());
        User userNoEmail = builder.build();
        createUserWithout(userNoEmail);
    }
    @Test
    @DisplayName("Create user without password")
    @Description("Test checks that it wouldn't possible to create an user without password")
    public void createUserWithoutPasswordTest_fail(){
        User.UserBuilder builder = User.builder().email(user.getEmail()).name(user.getName()).password("");
        User userNoPassword = builder.build();
        createUserWithout(userNoPassword);
    }
    @Test
    @DisplayName("Create user without name")
    @Description("Test checks that it wouldn't possible to create an user without name")
    public void createUserWithoutNameTest_fail(){
        User.UserBuilder builder = User.builder().email(user.getEmail()).name("").password(user.getPassword());
        User userNoName = builder.build();
        createUserWithout(userNoName);
    }

    @Step("Common steps for create user tests without any required field")
    private void createUserWithout(User user) {
        ValidatableResponse response = client.createUser(user);
        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);
        String message = client.getMessage(response);
        token = client.getToken(response);

        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(code).isEqualTo(SC_FORBIDDEN);
        softly.assertThat(message).isEqualTo(REQUIRED_FIELD_MISSING);
        softly.assertThat(ok).isFalse();
        softly.assertAll();
    }
}