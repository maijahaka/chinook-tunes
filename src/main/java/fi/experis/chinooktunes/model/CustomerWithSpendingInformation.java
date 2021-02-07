package fi.experis.chinooktunes.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Data
public class CustomerWithSpendingInformation extends Customer {

    @JsonProperty("total_spending")
    private double totalSpending;

    public CustomerWithSpendingInformation(long customerId, String firstName, String lastName,
                                           String country, String postalCode, String phone,
                                           String email, double totalSpending) {
        super(customerId, firstName, lastName, country, postalCode, phone, email);
        this.totalSpending = totalSpending;
    }

    public double getTotalSpending() {
        // remove extra digits caused by imprecision in calculating total spending in the database
        return roundDoubleToTwoDecimalPoints(totalSpending);
    }

    private double roundDoubleToTwoDecimalPoints(double doubleValue) {
        BigDecimal bigDecimalValue = new BigDecimal(doubleValue)
                .setScale(2,RoundingMode.HALF_UP);

        return bigDecimalValue.doubleValue();
    }
}
