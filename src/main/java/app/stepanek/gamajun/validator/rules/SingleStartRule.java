package app.stepanek.gamajun.validator.rules;

import app.stepanek.gamajun.domain.ValidatorRuleResult;
import app.stepanek.gamajun.repository.ValidatorRuleDao;
import app.stepanek.gamajun.validator.BaseValidatorRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class SingleStartRule extends BaseValidatorRule {

    @Autowired
    public SingleStartRule(ValidatorRuleDao validatorRuleDao) {
        super(validatorRuleDao);
    }

    @Override
    public ValidatorRuleResult validate(BpmnModelInstance submissionBpmn, BpmnModelInstance solutionBpmn) {
        var startEvents = submissionBpmn.getModelElementsByType(StartEvent.class);


        if (startEvents.size() != 1)
            return invalid("Diagram musí obsahovat pouze jeden start, ale obsahuje %d".formatted(startEvents.size()));

        return valid();
    }

    @Override
    protected String getId() {
        return "SingleStart";
    }

    @Override
    public String getName() {
        return "Jeden start";
    }

    @Override
    public String getDescription() {
        return "Graf musí obsahovat pouze jeden start.";
    }
}
