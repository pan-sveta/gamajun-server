package app.stepanek.gamajun.validator.rules;

import app.stepanek.gamajun.domain.ValidatorRuleResult;
import app.stepanek.gamajun.repository.ValidatorRuleDao;
import app.stepanek.gamajun.validator.BaseValidatorRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.DataOutputAssociation;
import org.camunda.bpm.model.bpmn.instance.Event;
import org.springframework.stereotype.Component;

@Component
public class MessageFlowSourceRule extends BaseValidatorRule {
    //Rozman pattern 12
    public MessageFlowSourceRule(ValidatorRuleDao validatorRuleDao) {
        super(validatorRuleDao);
    }

    @Override
    public ValidatorRuleResult validate(BpmnModelInstance submissionBpmn) {
        for (Event event : submissionBpmn.getModelElementsByType(Event.class)) {
            if (!event.getChildElementsByType(DataOutputAssociation.class).isEmpty())
                return invalid("Událost %s obsahuje odchozí zprávu".formatted(event.getName()));
        }

        return valid();
    }

    @Override
    protected String getId() {
        return "MessageFlowSource";
    }

    @Override
    protected String getName() {
        return "Použití události jako zdroj toku zpráv";
    }

    @Override
    protected String getDescription() {
        return "Událost nesmí být použita jako zdroj toku zpráv. Pouze aktivity smějí být použity jako zdroj zpráv";
    }
}
