package de.lendico.annuity.controller;

import de.lendico.annuity.model.BorrowerPayments;
import de.lendico.annuity.model.LoanQuery;
import de.lendico.annuity.service.PlannerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

@RestController
public class AnnuityController {

    @Autowired
    PlannerService plannerService;

    @PostMapping("/generate-plan")
    public BorrowerPayments generatePlan(
            @RequestBody @Valid @NotEmpty LoanQuery loanQuery
    ) {

        return plannerService.calculatePlan(loanQuery);

    }
}
