package app.stepanek.gamajun.validator.rules;

import app.stepanek.gamajun.domain.ValidatorRuleResult;
import app.stepanek.gamajun.repository.ValidatorRuleDao;
import app.stepanek.gamajun.validator.BaseValidatorRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.IntermediateCatchEvent;
import org.camunda.bpm.model.bpmn.instance.IntermediateThrowEvent;
import org.springframework.stereotype.Component;

@Component
public class MessageEventsRule extends BaseValidatorRule {
    //Rozman pattern 11
    public MessageEventsRule(ValidatorRuleDao validatorRuleDao) {
        super(validatorRuleDao);
    }

    @Override
    public ValidatorRuleResult validate(BpmnModelInstance submissionBpmn, BpmnModelInstance solutionBpmn) {

        for (IntermediateCatchEvent intermediateCatchEvent : submissionBpmn.getModelElementsByType(IntermediateCatchEvent.class)) {
            if (intermediateCatchEvent.getEventDefinitions().isEmpty())
                continue;
            if (intermediateCatchEvent.getIncoming().stream().anyMatch(n -> n.getSource() instanceof Activity) &&
                    intermediateCatchEvent.getOutgoing().stream().anyMatch(n -> n.getTarget() instanceof Activity))
                return invalid("IntermediateCatchEvent '%s' je použit nesprávně".formatted(intermediateCatchEvent.getName()));

        }

        return valid();
    }

    @Override
    protected String getId() {
        return "MessageEvents";
    }

    @Override
    protected String getName() {
        return "Události se zprávou";
    }

    @Override
    protected String getDescription() {
        return "";
    }
}
