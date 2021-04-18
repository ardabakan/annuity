package de.lendico.annuity.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
public class LoanQuery {
    /*
         "loanAmount": "5000",
         "nominalRate": "5.0",
         "duration": 24,
         "startDate": "2018-01-01T00:00:01Z
     */
    @NotNull
    @Positive
    BigDecimal loanAmount;
    @NotNull
    @Positive
    BigDecimal nominalRate;
    @Min(value = 1L, message = "The loan duration must be positive")
    int duration;
    @NotNull
    LocalDateTime startDate;

}
