package de.lendico.annuity.service;

import de.lendico.annuity.model.BorrowerPayments;
import de.lendico.annuity.model.LoanQuery;

import java.math.BigDecimal;

public interface PlannerService {

    public BorrowerPayments calculatePlan(LoanQuery loanQuery);

    public BigDecimal calculateAnnuity(LoanQuery loanQuery);

}
