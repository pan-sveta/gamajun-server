package app.stepanek.gamajun.validator.rules;

import app.stepanek.gamajun.domain.ValidatorRuleResult;
import app.stepanek.gamajun.repository.ValidatorRuleDao;
import app.stepanek.gamajun.validator.BaseValidatorRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.DataInputAssociation;
import org.springframework.stereotype.Component;

@Component
public class MessageFlowDestinationRule extends BaseValidatorRule {
    //Rozman pattern 13
    public MessageFlowDestinationRule(ValidatorRuleDao validatorRuleDao) {
        super(validatorRuleDao);
    }

    @Override
    public ValidatorRuleResult validate(BpmnModelInstance submissionBpmn) {
        for (Activity activity : submissionBpmn.getModelElementsByType(Activity.class)) {
            if (!activity.getChildElementsByType(DataInputAssociation.class).isEmpty())
                return invalid("Aktivita %s obsahuje příchozí zprávu".formatted(activity.getName()));
        }

        return valid();
    }

    @Override
    protected String getId() {
        return "MessageFlowDestination";
    }

    @Override
    protected String getName() {
        return "Použití aktivity jako cíl toku zpráv";
    }

    @Override
    protected String getDescription() {
        return "Aktivita nesmí být použita jako cíl toku zpráv";
    }
}
