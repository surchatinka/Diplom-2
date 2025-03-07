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
        token = client.getToken(response);
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
    public void updateUserDataWithAuthTest_ok() throws Exception {
        String fakerString = changeData(field);
        ValidatableResponse response = client.updateUserData(token,field,fakerString);
        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);

        Assert.assertEquals("wrong status code",SC_OK,code);
        Assert.assertTrue("operation failed",ok);
    }
    @Test
    public void updateUserDataWithoutAuthTest_fail() {
        String fakerString = changeData(field);
        ValidatableResponse response = client.updateUserData(field,fakerString);
        int code = client.getStatusCode(response);
        boolean ok = client.getStatus(response);
        String message = client.getMessage(response);

        Assert.assertEquals("wrong status code",SC_UNAUTHORIZED,code);
        Assert.assertFalse("operation failed",ok);
        Assert.assertEquals("wrong response text","You should be authorised",message);
    }
    private String changeData(String field){
        Faker faker = new Faker(Locale.CHINESE);
        switch (field){
            case "email": return faker.internet().emailAddress();
            case "name": return faker.name().firstName();
            case "password": return faker.bothify("??##??##??");
            default: throw new RuntimeException(field + " - field don't exist. Nothing to update.");
        }
    }
}