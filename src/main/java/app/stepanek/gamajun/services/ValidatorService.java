package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Submission;
import app.stepanek.gamajun.domain.ValidatorReport;
import app.stepanek.gamajun.domain.ValidatorRuleResult;
import app.stepanek.gamajun.exceptions.ValidatorReportNotFoundException;
import app.stepanek.gamajun.repository.ValidatorReportDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import app.stepanek.gamajun.validator.BaseValidatorRule;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Slf4j
public class ValidatorService {
    private final ValidatorReportDao validatorReportDao;
    private final ReferenceMatchingService referenceMatchingService;
    private final List<BaseValidatorRule> list;

    private final IAuthenticationFacade authenticationFacade;

    @Autowired
    public ValidatorService(ValidatorReportDao reportDao, ReferenceMatchingService referenceMatchingService, List<BaseValidatorRule> list, IAuthenticationFacade authenticationFacade) {
        this.validatorReportDao = reportDao;
        this.referenceMatchingService = referenceMatchingService;
        this.list = list;
        this.authenticationFacade = authenticationFacade;
    }

    @Transactional
    public List<ValidatorReport> findAll() {
        log.info("User {} is finding all validator reports", authenticationFacade.getUsername());
        return validatorReportDao.findAll();
    }

    @Transactional
    public ValidatorReport findById(UUID id) {
        log.info("User {} is finding validator report with id {}", authenticationFacade.getUsername(), id);
        return validatorReportDao.findById(id).orElseThrow(() -> new ValidatorReportNotFoundException("Validator report with id '%s' was not found".formatted(id)));
    }

    @Transactional
    public ValidatorReport validateSubmission(Submission sub) {
        log.info("User {} is validating submission with id {}", authenticationFacade.getUsername(), sub.getId());
        InputStream inputStream = new ByteArrayInputStream(sub.getXml().getBytes());
        var bpmn = Bpmn.readModelFromStream(inputStream);

        InputStream solutionStream = new ByteArrayInputStream(sub.getAssignment().getXml().getBytes());
        var solutionBpmn = Bpmn.readModelFromStream(solutionStream);

        return validateInstance(bpmn, solutionBpmn);
    }

    protected ValidatorReport validateInstance(BpmnModelInstance bpmn, BpmnModelInstance solutionBpmn) {
        ValidatorReport report = new ValidatorReport();

        report.setStartedAt(Instant.now());


        //Reference matching
        var referenceMatchingResult = referenceMatchingService.match(bpmn,solutionBpmn);
        report.setReferenceMatchingResult(referenceMatchingResult);

        //Validator rules
        Set<ValidatorRuleResult> results = new HashSet<>();
        for (var rule : list) {
            results.add(rule.validate(bpmn));
        }
        report.setValidatorRuleResults(results);


        report.setFinishedAt(Instant.now());

        return report;
    }


}
