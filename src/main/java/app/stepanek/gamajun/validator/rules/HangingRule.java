package app.stepanek.gamajun.validator.rules;

import app.stepanek.gamajun.domain.ValidatorRuleResult;
import app.stepanek.gamajun.repository.ValidatorRuleDao;
import app.stepanek.gamajun.validator.BaseValidatorRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.IntermediateThrowEvent;
import org.springframework.stereotype.Component;

@Component
public class HangingRule extends BaseValidatorRule {
    //Rozman pattern 8
    public HangingRule(ValidatorRuleDao validatorRuleDao) {
        super(validatorRuleDao);
    }

    @Override
    public ValidatorRuleResult validate(BpmnModelInstance submissionBpmn) {

        for (Activity activity : submissionBpmn.getModelElementsByType(Activity.class)) {
            if (activity.getIncoming().isEmpty() && !activity.getOutgoing().isEmpty())
                return invalid("Aktivita '%s' je zavěšená".formatted(activity.getName()));

        }

        for (IntermediateThrowEvent intermediateThrowEvent : submissionBpmn.getModelElementsByType(IntermediateThrowEvent.class)) {
            if (intermediateThrowEvent.getIncoming().isEmpty() && !intermediateThrowEvent.getOutgoing().isEmpty())
                return invalid("Událost '%s' je zavěšená".formatted(intermediateThrowEvent.getName()));

        }

        return valid();
    }

    @Override
    protected String getId() {
        return "Hanging";
    }

    @Override
    protected String getName() {
        return "Kontrola zavěšení";
    }

    @Override
    protected String getDescription() {
        return "Aktivity/eventy musí obsahovat vstupní hranu";
    }
}
