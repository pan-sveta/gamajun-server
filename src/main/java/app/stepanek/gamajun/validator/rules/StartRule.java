package app.stepanek.gamajun.validator.rules;

import app.stepanek.gamajun.domain.ValidatorRuleResult;
import app.stepanek.gamajun.repository.ValidatorRuleDao;
import app.stepanek.gamajun.validator.BaseValidatorRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class StartRule extends BaseValidatorRule {
    //Rozman pattern 2
    @Autowired
    public StartRule(ValidatorRuleDao validatorRuleDao) {
        super(validatorRuleDao);
    }

    @Override
    public ValidatorRuleResult validate(BpmnModelInstance submissionBpmn, BpmnModelInstance solutionBpmn) {
        var startEvents = submissionBpmn.getModelElementsByType(StartEvent.class);

        if (startEvents.size() < 1)
            return invalid("Diagram musí obsahovat alespoň jeden start");

        return valid();
    }

    @Override
    protected String getId() {
        return "Start";
    }

    @Override
    public String getName() {
        return "Přítomnost startu";
    }

    @Override
    public String getDescription() {
        return "Diagram musí obsahovat alespoň jeden start.";
    }
}
