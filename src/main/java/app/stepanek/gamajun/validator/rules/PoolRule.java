package app.stepanek.gamajun.validator.rules;

import app.stepanek.gamajun.domain.ValidatorRuleResult;
import app.stepanek.gamajun.repository.ValidatorRuleDao;
import app.stepanek.gamajun.validator.BaseValidatorRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Lane;
import org.camunda.bpm.model.bpmn.instance.Participant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PoolRule extends BaseValidatorRule {

    @Autowired
    public PoolRule(ValidatorRuleDao validatorRuleDao) {
        super(validatorRuleDao);
    }

    @Override
    public ValidatorRuleResult validate(BpmnModelInstance submissionBpmn, BpmnModelInstance solutionBpmn) {

        var participantsCount = submissionBpmn.getModelElementsByType(Participant.class).size();
        var solutionParticipantsCount = solutionBpmn.getModelElementsByType(Participant.class).size();
        if (participantsCount != solutionParticipantsCount)
            return invalid("Diagram neobsahuje správný počet participatů. Obsahuje %d ale má obsahovat %d".formatted(participantsCount, solutionParticipantsCount));

        var lanes = submissionBpmn.getModelElementsByType(Lane.class).size();
        var solutionLanes = solutionBpmn.getModelElementsByType(Lane.class).size();

        if (lanes != solutionLanes)
            return invalid("Diagram neobsahuje správný počet swimlines. Obsahuje %d ale má obsahovat %d".formatted(lanes, solutionLanes));


        return valid();
    }

    @Override
    protected String getId() {
        return "Pools";
    }

    @Override
    public String getName() {
        return "Participanti a bazény";
    }

    @Override
    public String getDescription() {
        return "Graf musí obsahovat stejný počet participantů a swimlines.";
    }
}
