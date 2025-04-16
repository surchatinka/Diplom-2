import client.StellarBurgerClient;
import io.qameta.allure.Allure;
import io.qameta.allure.Step;
import io.restassured.response.ValidatableResponse;
import model.Token;
import model.User;
import model.UserField;
import net.datafaker.Faker;
import org.assertj.core.api.SoftAssertions;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RunWith(Parameterized.class)
public class UpdateUserEmailOrNameTest {

    private final StellarBurgerClient client = new StellarBurgerClient();
    private Token token;
    private User user;
    private final List<String> fields;
    private final boolean authorization;
    private static final String NO_AUTHORIZATION = "You should be authorised";

    public UpdateUserEmailOrNameTest(List<String> fields, boolean authorization){
        this.fields=fields;
        this.authorization = authorization;
    }

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
    public void after() {
        if(token.getAccessToken()!=null) {
            client.deleteUser(token);
        }
    }

    @Parameterized.Parameters(name = "Update field(s): {0}; Authorization: {1}")
    public static Object[][] testData(){
        List<String> testData1 = new ArrayList<>();
        List<String> testData2 = new ArrayList<>();
        List<String> testData3 = new ArrayList<>();

        testData1.add(UserField.EMAIL.toString().toLowerCase());
        testData2.add(UserField.NAME.toString().toLowerCase());
        testData3.add(UserField.NAME.toString().toLowerCase());
        testData3.add(UserField.EMAIL.toString().toLowerCase());

        return new Object[][]{
                {testData1,true},
                {testData2,true},
                {testData1,false},
                {testData2,false},
                {testData3,true},
                {testData3,false},
        };
    }

    @Test
    public void updateUserData(){
        Allure.description("Update "+ fields + " of an user with or without authorization");
        Allure.getLifecycle().updateTestCase(result -> {
            result.setName("Update users " + fields);
        });
        User fakerUser = changeData(fields);
        ValidatableResponse response;

        if(authorization){
            response = client.updateUserEmailOrName(token,fakerUser);
        }
        else{
            response = client.updateUserEmailOrName(fakerUser);
        }

        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);

        if(authorization){
            User userUpdated = client.getEmailAndLogin(response);
            SoftAssertions softly = new SoftAssertions();

            softly.assertThat(code).isEqualTo(SC_OK);
            softly.assertThat(fakerUser.getName()).isEqualTo(userUpdated.getName());
            softly.assertThat(fakerUser.getEmail()).isEqualTo(userUpdated.getEmail());
            softly.assertThat(ok).isTrue();
            softly.assertAll();
        }
        else{
            String message = client.getMessage(response);
            SoftAssertions softly = new SoftAssertions();

            softly.assertThat(code).isEqualTo(SC_UNAUTHORIZED);
            softly.assertThat(message).isEqualTo(NO_AUTHORIZATION);
            softly.assertThat(ok).isFalse();
            softly.assertAll();
        }
    }

    @Step("Generate modified user")
    private User changeData(List<String> fields) {
        Faker faker = new Faker(Locale.CANADA);
        String email = user.getEmail();
        String name = user.getName();
        String password = user.getName();
        for (String field : fields){
            switch (field){
                case "email":
                    email = faker.internet().emailAddress();
                    break;
                case "name":
                    name = faker.name().firstName();
                    break;
                default: throw new RuntimeException(field + " - field don't exist. Nothing to update.");
            }
        }
        return User.builder()
                .email(email)
                .name(name)
                .password(password)
                .build();
    }
}