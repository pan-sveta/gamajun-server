package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Submission;
import app.stepanek.gamajun.domain.ValidatorReport;
import app.stepanek.gamajun.domain.ValidatorRuleResult;
import app.stepanek.gamajun.exceptions.ValidatorReportNotFoundException;
import app.stepanek.gamajun.repository.ValidatorReportDao;
import app.stepanek.gamajun.validator.BaseValidatorRule;
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
public class ValidatorService {
    private final ExamSubmissionService examSubmissionService;
    private final ValidatorReportDao validatorReportDao;
    private List<BaseValidatorRule> list;

    @Autowired
    public ValidatorService(ExamSubmissionService examSubmissionService, ValidatorReportDao reportDao, List<BaseValidatorRule> list) {
        this.examSubmissionService = examSubmissionService;
        this.validatorReportDao = reportDao;
        this.list = list;
    }

    @Transactional
    public List<ValidatorReport> findAll() {
        return validatorReportDao.findAll();
    }

    @Transactional
    public ValidatorReport findById(UUID id) {
        return validatorReportDao.findById(id).orElseThrow(() -> new ValidatorReportNotFoundException("Validator report with id '%s' was not found".formatted(id)));
    }

    @Transactional
    public ValidatorReport findValidatorReportFromExamSubmissionId(UUID examSubmissionId) {
        var examSubmission = examSubmissionService.findById(examSubmissionId);

        return examSubmission.getValidatorReport();
    }

    @Transactional
    public ValidatorReport validateSubmission(Submission sub) {
        InputStream inputStream = new ByteArrayInputStream(sub.getXml().getBytes());
        var bpmn = Bpmn.readModelFromStream(inputStream);

        InputStream solutionStream = new ByteArrayInputStream(sub.getAssignment().getXml().getBytes());
        var solutionBpmn = Bpmn.readModelFromStream(solutionStream);

        return validateInstance(bpmn, solutionBpmn);
    }

    protected ValidatorReport validateInstance(BpmnModelInstance bpmn, BpmnModelInstance solutionBpmn) {
        ValidatorReport report = new ValidatorReport();

        report.setStartedAt(Instant.now());

        Set<ValidatorRuleResult> results = new HashSet<>();

        for (var rule : list) {
            results.add(rule.validate(bpmn, solutionBpmn));
        }

        report.setValidatorRuleResults(results);
        report.setFinishedAt(Instant.now());

        return report;
    }


}
