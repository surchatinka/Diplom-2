import client.StellarBurgerClient;
import io.qameta.allure.Description;
import io.qameta.allure.Step;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.Token;
import model.User;
import model.UserField;
import net.datafaker.Faker;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import static org.apache.http.HttpStatus.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RunWith(Parameterized.class)
public class UpdateUserDataTest {

    private final StellarBurgerClient client = new StellarBurgerClient();
    private Token token;
    private User user;
    private final List<String> fields;
    private final boolean authorization;

    public UpdateUserDataTest(List<String> fields,boolean authorization){
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

    @Parameterized.Parameters
    public static Object[][] testData(){
        List<String> testData1 = new ArrayList<>();
        List<String> testData2 = new ArrayList<>();
        List<String> testData3 = new ArrayList<>();
        List<String> testData4 = new ArrayList<>();
        List<String> testData5 = new ArrayList<>();
        List<String> testData6 = new ArrayList<>();
        List<String> testData7 = new ArrayList<>();

        testData1.add(UserField.EMAIL.toString().toLowerCase());
        testData2.add(UserField.NAME.toString().toLowerCase());
        testData3.add(UserField.PASSWORD.toString().toLowerCase());
        testData4.add(UserField.EMAIL.toString().toLowerCase());
        testData5.add(UserField.NAME.toString().toLowerCase());
        testData6.add(UserField.PASSWORD.toString().toLowerCase());
        testData7.add(UserField.NAME.toString().toLowerCase());
        testData7.add(UserField.PASSWORD.toString().toLowerCase());

        return new Object[][]{
                {testData1,true},
                {testData2,true},
                {testData3,true},
                {testData4,false},
                {testData5,false},
                {testData6,false},
                {testData7,true}
        };
    }

    @Test
    @DisplayName("Update user data test")
    @Description("Update email, name, password of an user with or without authorization")
    public void updateUserData(){
        User fakerUser = changeData(fields);
        ValidatableResponse response;

        if(authorization){
            response = client.updateUserData(token,fields,fakerUser);
        }
        else{
            response = client.updateUserData(fields,fakerUser);
        }

        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);

        if(authorization){
            User userUpdated = client.getEmailAndLogin(response);
            Assert.assertEquals("wrong status code",SC_OK,code);
            Assert.assertTrue("operation failed",ok);
            Assert.assertEquals(fakerUser.emailAndNameAsJson(),userUpdated.emailAndNameAsJson());
        }
        else{
            String message = client.getMessage(response);
            Assert.assertEquals("wrong status code",SC_UNAUTHORIZED,code);
            Assert.assertFalse("operation failed",ok);
            Assert.assertEquals("wrong response text","You should be authorised",message);
        }
    }

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
                case "password":
                    password = faker.bothify("??##??##??");
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