package de.lendico.annuity.service;

import de.lendico.annuity.model.BorrowerPayment;
import de.lendico.annuity.model.BorrowerPayments;
import de.lendico.annuity.model.LoanQuery;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
public class PlannerServiceImpl implements PlannerService {

    public static final int DAYS_PER_MONTH = 30;
    public static final int DAYS_PER_YEAR = 360;
    public static final float MONTHLY_RATE_RATIO =
            DAYS_PER_YEAR / DAYS_PER_MONTH;

    MathContext MC20 = new MathContext(10, RoundingMode.HALF_UP);

    DateTimeFormatter planDateFormatter = DateTimeFormatter.ofPattern("dd.MM" +
            ".yyyy");

    @Override
    public BorrowerPayments calculatePlan(LoanQuery loanQuery) {

        List<BorrowerPayment> borrowerPaymentList =
                new ArrayList<>();

        BigDecimal monthlyInterestRate =
                loanQuery.getNominalRate().divide(new BigDecimal(MONTHLY_RATE_RATIO), MC20);

        // initial installment

        LocalDateTime bpLocalDateTime = loanQuery.getStartDate();

        BigDecimal bpAnnuity = calculateAnnuity(loanQuery);

        BigDecimal bpInitialOutstandingPrincipal =
                loanQuery.getLoanAmount();

        BigDecimal bpInterest =
                bpInitialOutstandingPrincipal.multiply(monthlyInterestRate.divide(BigDecimal.valueOf(100), MC20));

        BigDecimal bpPrincipal = bpAnnuity.subtract(bpInterest);

        BigDecimal bpRemainingOutstandingPrincipal =
                bpInitialOutstandingPrincipal.subtract(bpPrincipal);

        BorrowerPayment currentBorrowerPayment =
                BorrowerPayment.builder().date(bpLocalDateTime.format(planDateFormatter)).
                        borrowerPaymentAmount(bpAnnuity).
                        principal(bpPrincipal).
                        interest(bpInterest).
                        initialOutstandingPrincipal(bpInitialOutstandingPrincipal).
                        remainingOutstandingPrincipal(bpRemainingOutstandingPrincipal).
                        build();

        borrowerPaymentList.add(currentBorrowerPayment);

        BorrowerPayment previousBorrowerPayment;

        for (int i = 1; i < loanQuery.getDuration(); i++) {

            bpLocalDateTime = bpLocalDateTime.plusMonths(1);

            // current values depend on previous installment
            previousBorrowerPayment = borrowerPaymentList.get(i - 1);

            bpInitialOutstandingPrincipal =
                    previousBorrowerPayment.getRemainingOutstandingPrincipal();

            bpInterest =
                    previousBorrowerPayment.getRemainingOutstandingPrincipal().multiply(monthlyInterestRate.divide(BigDecimal.valueOf(100), MC20));

            bpPrincipal = bpAnnuity.subtract(bpInterest);

            bpRemainingOutstandingPrincipal =
                    bpInitialOutstandingPrincipal.subtract(bpPrincipal);

            // last installment
            if (i == loanQuery.getDuration()-1){
                if (bpPrincipal.compareTo(bpInitialOutstandingPrincipal) > 0){
                    bpPrincipal = bpInitialOutstandingPrincipal;
                    bpAnnuity = bpPrincipal.add(bpInterest);
                }
                bpRemainingOutstandingPrincipal = BigDecimal.ZERO;
            }

            currentBorrowerPayment =
                    BorrowerPayment.builder().date(bpLocalDateTime.format(planDateFormatter)).
                            borrowerPaymentAmount(bpAnnuity).
                            principal(bpPrincipal).
                            interest(bpInterest).
                            initialOutstandingPrincipal(bpInitialOutstandingPrincipal).
                            remainingOutstandingPrincipal(bpRemainingOutstandingPrincipal).
                            build();

            borrowerPaymentList.add(currentBorrowerPayment);

        }

        return BorrowerPayments.builder().borrowerPayments(borrowerPaymentList).build();
    }

    @Override
    public BigDecimal calculateAnnuity(LoanQuery loanQuery) {

        BigDecimal monthlyInterestRate =
                loanQuery.getNominalRate().divide(new BigDecimal(MONTHLY_RATE_RATIO), MC20);

        BigDecimal annuityNumerator =
                loanQuery.getLoanAmount().multiply(monthlyInterestRate.divide(BigDecimal.valueOf(100), MC20));

        BigDecimal annuityDenominator =

                BigDecimal.valueOf(1).subtract(
                        BigDecimal.valueOf(1).divide(
                                BigDecimal.valueOf(1).add(
                                        monthlyInterestRate.divide(BigDecimal.valueOf(100), MC20)
                                ).pow(loanQuery.getDuration()), MC20
                        )
                );

        return annuityNumerator.divide(annuityDenominator, MC20).setScale(2,
                RoundingMode.HALF_UP);
    }
}
