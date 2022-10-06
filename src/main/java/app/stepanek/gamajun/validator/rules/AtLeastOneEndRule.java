package app.stepanek.gamajun.validator.rules;

import app.stepanek.gamajun.domain.ValidatorRuleResult;
import app.stepanek.gamajun.repository.ValidatorRuleDao;
import app.stepanek.gamajun.validator.BaseValidatorRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.xml.instance.ModelElementInstance;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;

@Component
public class AtLeastOneEndRule extends BaseValidatorRule {

    @Autowired
    public AtLeastOneEndRule(ValidatorRuleDao validatorRuleDao) {
        super(validatorRuleDao);
    }

    @Override
    public ValidatorRuleResult validate(BpmnModelInstance instance) {
        var endEvents = instance.getModelElementsByType(EndEvent.class);

        if (endEvents.size() != 1)
            return invalid("Diagram musí obsahovat alespoň jeden konec, ale obsahuje %d".formatted(endEvents.size()));

        return valid();
    }

    @Override
    protected String getId() {
        return "AtLeastOneEnd";
    }

    @Override
    public String getName() {
        return "Alespoň jeden konec";
    }

    @Override
    public String getDescription() {
        return "Graf musí obsahovat alespoň jeden konec";
    }
}
