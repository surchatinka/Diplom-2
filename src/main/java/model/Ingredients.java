package model;

import com.google.gson.annotations.SerializedName;
import io.qameta.allure.internal.shadowed.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Value;
import java.util.List;

@Value
@AllArgsConstructor
@JsonIgnoreProperties
@Builder
public class Ingredients {
    @SerializedName(value="ingredients", alternate={"data"})
    List<IngredientData> data;
}
