package model;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Value;

@Value
@AllArgsConstructor
@JsonIgnoreProperties
public class Token {
    String accessToken;
    String refreshToken;
    User user;
}
