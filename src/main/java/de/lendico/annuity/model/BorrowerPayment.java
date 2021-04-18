package de.lendico.annuity.model;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import de.lendico.annuity.LoanSerializer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonPropertyOrder({"borrowerPaymentAmount", "date",
        "initialOutstandingPrincipal", "interest", "principal",
        "remainingOutstandingPrincipal"})
public class BorrowerPayment {

    /*
    "borrowerPaymentAmount":"219.36",
    "date":"2018-01-01T00:00:00Z",
    "initialOutstandingPrincipal":"5000.00",
    "interest":"20.83",
    "principal":"198.53",
    "remainingOutstandingPrincipal":"4801.47"
    */

    String date;
    @JsonSerialize(using = LoanSerializer.class)
    BigDecimal borrowerPaymentAmount;
    @JsonSerialize(using = LoanSerializer.class)
    BigDecimal principal;
    @JsonSerialize(using = LoanSerializer.class)
    BigDecimal interest;
    @JsonSerialize(using = LoanSerializer.class)
    BigDecimal initialOutstandingPrincipal;
    @JsonSerialize(using = LoanSerializer.class)
    BigDecimal remainingOutstandingPrincipal;

}
