package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.ValidatorReport;
import app.stepanek.gamajun.services.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

@Controller
public class ValidatorReportController {
    private final ValidatorService validatorService;

    @Autowired
    public ValidatorReportController(ValidatorService validatorService) {
        this.validatorService = validatorService;
    }

    @SchemaMapping
    public ValidatorReport validatorReport(ValidatorReport validatorReport) {
        return validatorService.findById(validatorReport.getId());
    }
}
