package model;

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
    List<IngredientData> data;

    public String toIngredientsJSON(){
        StringBuilder builder = new StringBuilder();
        builder.append("{\"ingredients\": [");
        if (data.isEmpty()) {
            builder.append("]}");
        }
        else {
            for(IngredientData ingredient:data) {
                builder.append(String.format("\"%s\",",ingredient.get_id()));
            }
            builder.replace(builder.lastIndexOf(","),builder.length(),"]}");
        }
        return builder.toString();
    }
}
