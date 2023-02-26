package app.stepanek.gamajun.validator.rules;

import app.stepanek.gamajun.domain.ValidatorRuleResult;
import app.stepanek.gamajun.repository.ValidatorRuleDao;
import app.stepanek.gamajun.validator.BaseValidatorRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.springframework.stereotype.Component;

@Component
public class PoolLanesStartsRule extends BaseValidatorRule {
    //Rozman pattern 9
    public PoolLanesStartsRule(ValidatorRuleDao validatorRuleDao) {
        super(validatorRuleDao);
    }

    @Override
    public ValidatorRuleResult validate(BpmnModelInstance submissionBpmn) {

        for (LaneSet laneSet : submissionBpmn.getModelElementsByType(LaneSet.class)) {
            int startCount = 0;

            for (Lane lane : laneSet.getLanes()) {
                for (FlowNode flowNode : lane.getFlowNodeRefs()) {
                    if (flowNode instanceof StartEvent)
                        startCount++;
                }
            }

            if (startCount > 1)
                return invalid("Pool '%s' má obsahovat jeden start ale obsauje jich %d".formatted(laneSet.getName(), startCount));
        }

        return valid();
    }

    @Override
    protected String getId() {
        return "PoolLanesStarts";
    }

    @Override
    protected String getName() {
        return "Počet startů v rámci poolu";
    }

    @Override
    protected String getDescription() {
        return "Bazén by měl obsahovat pouze jeden start";
    }
}
