package model;

import com.google.gson.annotations.SerializedName;
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
    @SerializedName("ingredients")
    List<String> ingredients;
    @SerializedName("createdAt")
    String createdAt;
    @SerializedName("_id")
    String id;
    @SerializedName("number")
    int number;
}
