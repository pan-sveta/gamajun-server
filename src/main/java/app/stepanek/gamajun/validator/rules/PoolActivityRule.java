package app.stepanek.gamajun.validator.rules;

import app.stepanek.gamajun.domain.ValidatorRuleResult;
import app.stepanek.gamajun.repository.ValidatorRuleDao;
import app.stepanek.gamajun.validator.BaseValidatorRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.camunda.bpm.model.bpmn.instance.SequenceFlow;
import org.camunda.bpm.model.bpmn.instance.Process;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.springframework.stereotype.Component;

@Component
public class PoolActivityRule extends BaseValidatorRule {
    public PoolActivityRule(ValidatorRuleDao validatorRuleDao) {
        super(validatorRuleDao);
    }

    @Override
    public ValidatorRuleResult validate(BpmnModelInstance submissionBpmn, BpmnModelInstance solutionBpmn) {

        for (Participant participant : submissionBpmn.getModelElementsByType(Participant.class)) {
            for (Activity activity : participant.getProcess().getChildElementsByType(Activity.class)){
                boolean hasOutgoingWithinParticipant = false;

                for (SequenceFlow outgoingFlow : activity.getOutgoing()) {
                    if (outgoingFlow.getTarget().getParentElement() == activity.getParentElement()) {
                        hasOutgoingWithinParticipant = true;
                    }
                }

                if (!hasOutgoingWithinParticipant)
                    return invalid("Aktivita '%s' není propojená výstupní hranou s žádným objektem v rámci participanta/poolu '%s'".formatted(activity.getName(), participant.getName()));
            }
        }

        return valid();
    }

    @Override
    protected String getId() {
        return "ParticipantActivityConnection";
    }

    @Override
    protected String getName() {
        return "Propojení aktivit v rámci poolu/participanta";
    }

    @Override
    protected String getDescription() {
        return "Aktivity v rámci poolu/participanta musí být propojeny";
    }
}
