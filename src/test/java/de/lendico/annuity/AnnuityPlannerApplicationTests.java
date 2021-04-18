package de.lendico.annuity;

import de.lendico.annuity.model.BorrowerPayment;
import de.lendico.annuity.model.BorrowerPayments;
import de.lendico.annuity.model.LoanQuery;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Random;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Slf4j
class AnnuityPlannerApplicationTests {

    @Autowired
    TestRestTemplate template;

    MathContext MC2 = new MathContext(2, RoundingMode.HALF_UP);

    @Test
    @Order(1)
    public void shouldCalculatePaymentsCorrectly() {

        ZonedDateTime zdt = ZonedDateTime.parse("2018-01-01T00:00:01Z");
        LocalDateTime ldt = zdt.toLocalDateTime();

        LoanQuery loanQuery =
                LoanQuery.builder()
                        .startDate(ldt)
                        .duration(24)
                        .loanAmount(BigDecimal.valueOf(5000))
                        .nominalRate(BigDecimal.valueOf(5.0))
                        .build();

        log.info("Started testing for a loan query of {} Euros to be paid" +
                        " in {} months with a nominal rate of {}%",
                loanQuery.getLoanAmount(), loanQuery.getDuration(),
                loanQuery.getNominalRate());

        BorrowerPayments borrowerPayments = template.postForObject(
                "/generate-plan", loanQuery, BorrowerPayments.class);

        Assertions.assertNotNull(borrowerPayments);
        Assertions.assertEquals(24,
                borrowerPayments.getBorrowerPayments().size());

        for (int i = 0; i < borrowerPayments.getBorrowerPayments().size(); i++) {

            // check for all installments if ->  principal+interest = annuity
            Assertions.assertEquals(
                    borrowerPayments.getBorrowerPayments().get(i).getBorrowerPaymentAmount(),
                    borrowerPayments.getBorrowerPayments().get(i).getInterest().add(borrowerPayments.getBorrowerPayments().get(i).getPrincipal())
            );

        }

        Assertions.assertEquals(BigDecimal.ZERO.setScale(2),
                borrowerPayments.getBorrowerPayments().get(23).getRemainingOutstandingPrincipal());

    }

    @Test
    @Order(2)
    public void shouldCalculateRandomPaymentsCorrectly() {

        for (int l = 0; l < 20; l++) {

            LoanQuery loanQuery = generateRandomLoanQuery();

            log.info("Started testing for a loan query of {} Euros to be paid" +
                            " in {} months with a nominal rate of {}%",
                    loanQuery.getLoanAmount(), loanQuery.getDuration(),
                    loanQuery.getNominalRate());

            BorrowerPayments borrowerPayments = template.postForObject(
                    "/generate-plan", loanQuery, BorrowerPayments.class);

            Assertions.assertNotNull(borrowerPayments);
            Assertions.assertEquals(loanQuery.getDuration(),
                    borrowerPayments.getBorrowerPayments().size());

            BorrowerPayment bp = null;

            // 1st and last installments may differ 1 cent, so do not test
            // against
            for (int i = 1; i < borrowerPayments.getBorrowerPayments().size()-1; i++) {

                bp = borrowerPayments.getBorrowerPayments().get(i);

                log.info("Now checking installment {} of {}", i + 1,
                        loanQuery.getDuration());

                log.info(bp.toString());

                Assertions.assertEquals(
                        bp.getBorrowerPaymentAmount(),
                        bp.getInterest().add(bp.getPrincipal())
                );

            }

            Assertions.assertEquals(BigDecimal.ZERO.setScale(2),
                    borrowerPayments.getBorrowerPayments().get(loanQuery.getDuration() - 1).getRemainingOutstandingPrincipal());
        }

    }

    @Test
    void contextLoads() {
    }

    /*
     * Generates sample loan queries within 2000-10000 Euros, 3 to 240 months
     * ranges and 3% - 40% nominal interest rates
     * */
    public LoanQuery generateRandomLoanQuery() {
        ZonedDateTime zdt = ZonedDateTime.parse("2018-01-01T00:00:01Z");
        LocalDateTime ldt = zdt.toLocalDateTime();

        Random rn = new Random();

        int maximumLoanAmount = 100000;
        int minimumLoanAmount = 2000;
        int range = maximumLoanAmount - minimumLoanAmount + 1;
        int randomLoanAmount = rn.nextInt(range) + minimumLoanAmount;

        int maximumDuration = 240;
        int minimumDuration = 3;
        range = maximumDuration - minimumDuration + 1;
        int randomDuration = rn.nextInt(range) + minimumDuration;

        int maximumNominalRate = 40;
        int minimumNominalRate = 3;
        range = maximumNominalRate - minimumNominalRate + 1;
        int randomNominalRate = rn.nextInt(range) + minimumNominalRate;

        LoanQuery loanQuery =
                LoanQuery.builder()
                        .startDate(ldt)
                        .duration(randomDuration)
                        .loanAmount(BigDecimal.valueOf(randomLoanAmount))
                        .nominalRate(BigDecimal.valueOf(randomNominalRate))
                        .build();

        return loanQuery;
    }

}
