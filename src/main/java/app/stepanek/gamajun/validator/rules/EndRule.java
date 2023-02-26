package app.stepanek.gamajun.validator.rules;

import app.stepanek.gamajun.domain.ValidatorRuleResult;
import app.stepanek.gamajun.repository.ValidatorRuleDao;
import app.stepanek.gamajun.validator.BaseValidatorRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EndRule extends BaseValidatorRule {
    //Rozman pattern 3
    @Autowired
    public EndRule(ValidatorRuleDao validatorRuleDao) {
        super(validatorRuleDao);
    }

    @Override
    public ValidatorRuleResult validate(BpmnModelInstance submissionBpmn) {
        var endEvents = submissionBpmn.getModelElementsByType(EndEvent.class);

        if (endEvents.size() < 1)
            return invalid("Diagram musí obsahovat alespoň jeden konec, ale obsahuje %d".formatted(endEvents.size()));

        return valid();
    }

    @Override
    protected String getId() {
        return "End";
    }

    @Override
    public String getName() {
        return "Přítomnost konce";
    }

    @Override
    public String getDescription() {
        return "Diagram musí obsahovat alespoň jeden konec";
    }
}
