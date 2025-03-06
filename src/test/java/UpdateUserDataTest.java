import client.StellarBurgerClient;
import io.restassured.response.ValidatableResponse;
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

import java.util.Locale;

@RunWith(Parameterized.class)
public class UpdateUserDataTest {

    private final StellarBurgerClient client = new StellarBurgerClient();
    private Token token;
    private final String field;

    public UpdateUserDataTest(String field){
        this.field=field;
    }

    @Before
    public void before(){
        Faker faker = new Faker(Locale.UK);
        User user = new User(faker.internet().emailAddress(), faker.bothify("??##??##??"), faker.name().firstName());
        ValidatableResponse response = client.createUser(user);
        token = response.extract().as(Token.class);
    }
    @After
    public void after() {
        if(token.getAccessToken()!=null) {
            client.deleteUser(token);
        }
    }

    @Parameterized.Parameters
    public static Object[][] testData(){
        return new Object[][]{
                {"email"},
                {"name"},
                {"password"}
        };
    }

    @Test
    public void updateUserDataWithAuth_ok() throws Exception {
        Faker faker = new Faker(Locale.CHINESE);
        String fakerString;
        switch (field){
            case "email": fakerString = faker.internet().emailAddress(); break;
            case "name": fakerString = faker.name().firstName(); break;
            case "password": fakerString = faker.bothify("??##??##??"); break;
            default: throw new RuntimeException(field + " - field don't exist. Nothing to update.");
        }

        ValidatableResponse response = client.updateUserData(token,field,fakerString);
        int code = response.extract().statusCode();
        boolean ok = response.extract().jsonPath().get("success");

        Assert.assertEquals("wrong status code",SC_OK,code);
        Assert.assertTrue("operation failed",ok);
    }

    @Test
    public void updateUserDataWithoutAuth_fail() throws Exception {
        Faker faker = new Faker(Locale.CHINESE);
        String fakerString;
        switch (field){
            case "email": fakerString = faker.internet().emailAddress(); break;
            case "name": fakerString = faker.name().firstName(); break;
            case "password": fakerString = faker.bothify("??##??##??"); break;
            default: throw new RuntimeException(field + " - field don't exist. Nothing to update.");
        }

        ValidatableResponse response = client.updateUserData(field,fakerString);
        int code = response.extract().statusCode();
        boolean ok = response.extract().jsonPath().get("success");
        String message = response.extract().jsonPath().get("message");

        Assert.assertEquals("wrong status code",SC_UNAUTHORIZED,code);
        Assert.assertFalse("operation failed",ok);
        Assert.assertEquals("wrong response text","You should be authorised",message);
    }
}
