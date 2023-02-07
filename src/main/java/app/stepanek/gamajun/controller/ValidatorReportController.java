package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.ValidatorReport;
import app.stepanek.gamajun.services.ValidatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

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

    @QueryMapping
    public List<ValidatorReport> validatorReports() {
        return validatorService.findAll();
    }

    @QueryMapping
    public ValidatorReport validatorReportByExamSubmissionId(@Argument UUID id){
        return validatorService.findValidatorReportFromExamSubmissionId(id);
    }

}
