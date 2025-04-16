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

public class LoginUserTest {

    private static final String MESSAGE_INCORRECT_EMAIL_OR_PASSWORD = "email or password are incorrect";
    private final StellarBurgerClient client = new StellarBurgerClient();
    private User user;
    private Token token;

    @Step("Test preparation")
    @Before
    public void before(){
        Faker faker = new Faker(Locale.UK);
        user = new User(faker.internet().emailAddress(), faker.bothify("??##??##??"), faker.name().firstName());
        ValidatableResponse response = client.createUser(user);
        token = client.getToken(response);
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
    @Description("Test checks possibility to login with existing user credentials")
    public void authorizationExistingUserTest_ok(){
        ValidatableResponse response = client.loginUser(user);
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
    @DisplayName("Login without login-field")
    @Description("Test checks impossibility to login without email field")
    public void authorizationNoLoginTest_fail(){
        User.UserBuilder builder = User.builder().email("").name(user.getName()).password(user.getPassword());
        User userNoEmail = builder.build();
        authorizationWithout(userNoEmail);
    }
    @Test
    @DisplayName("Login without password-field")
    @Description("Test checks impossibility to login without password field")
    public void authorizationNoPasswordTest_fail(){
        User.UserBuilder builder = User.builder().email(user.getEmail()).name(user.getName()).password("");
        User userNoPassword = builder.build();
        authorizationWithout(userNoPassword);
    }
    @Step("Common steps to check impossibility to login without required field")
    private void authorizationWithout(User userWithout){
        ValidatableResponse response = client.loginUser(userWithout);
        int code = client.getStatusCode(response);
        token = client.getToken(response);
        boolean ok = client.getStatus(response);
        String message = client.getMessage(response);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(code).isEqualTo(SC_UNAUTHORIZED);
        softly.assertThat(message).isEqualTo(MESSAGE_INCORRECT_EMAIL_OR_PASSWORD);
        softly.assertThat(ok).isFalse();
        softly.assertAll();
    }
    @Test
    @DisplayName("Login with wrong login-field")
    @Description("Test checks impossibility to login with wrong login field")
    public void authorizationWrongLoginTest_fail(){
        Faker faker = new Faker(Locale.CANADA);
        User.UserBuilder builder = User.builder()
                .email(faker.internet().emailAddress())
                .name(user.getName())
                .password(user.getPassword());
        User userWrongEmail = builder.build();
        ValidatableResponse response = client.loginUser(userWrongEmail);
        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);
        String message = client.getMessage(response);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(code).isEqualTo(SC_UNAUTHORIZED);
        softly.assertThat(message).isEqualTo(MESSAGE_INCORRECT_EMAIL_OR_PASSWORD);
        softly.assertThat(ok).isFalse();
        softly.assertAll();
    }
    @Test
    @DisplayName("Login with wrong password-field")
    @Description("Test checks impossibility to login with wrong password field")
    public void authorizationWrongPasswordTest_fail(){
        Faker faker = new Faker(Locale.CANADA);
        User.UserBuilder builder = User.builder()
                .email(user.getEmail())
                .name(user.getName())
                .password(faker.bothify("??##??##"));
        User userWrongPassword = builder.build();
        ValidatableResponse response = client.loginUser(userWrongPassword);
        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);
        String message = client.getMessage(response);
        SoftAssertions softly = new SoftAssertions();

        softly.assertThat(code).isEqualTo(SC_UNAUTHORIZED);
        softly.assertThat(message).isEqualTo(MESSAGE_INCORRECT_EMAIL_OR_PASSWORD);
        softly.assertThat(ok).isFalse();
        softly.assertAll();
    }
}