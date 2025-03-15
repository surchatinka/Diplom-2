package model;

import io.qameta.allure.internal.shadowed.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import java.util.List;

@Value
@Builder
@AllArgsConstructor
@JsonIgnoreProperties
public class Order {
    String _id;
    List<IngredientData> ingredients;
    String createdAt;
}
