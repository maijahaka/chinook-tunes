package fi.experis.chinooktunes.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class Customer {
    @JsonProperty("id")
    private long customerId;

    @JsonProperty("first_name")
    private String firstName;

    @JsonProperty("last_name")
    private String lastName;

    @JsonProperty("country")
    private String country;

    @JsonProperty("postal_code")
    private String postalCode;

    @JsonProperty("phone")
    private String phone;

    @JsonProperty("email")
    private String email;
}
