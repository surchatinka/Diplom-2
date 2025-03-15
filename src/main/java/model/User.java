package model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;


@Value
@AllArgsConstructor
@Builder
public class User {
    String email;
    String password;
    String name;

    public String emailAndNameAsJson(){
        return "{" +
                String.format("email=%s, name=%s}", email, name);
    }
}
