package app.stepanek.gamajun.validator.rules;

import app.stepanek.gamajun.domain.ValidatorRuleResult;
import app.stepanek.gamajun.repository.ValidatorRuleDao;
import app.stepanek.gamajun.validator.BaseValidatorRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.EndEvent;
import org.camunda.bpm.model.bpmn.instance.FlowNode;
import org.camunda.bpm.model.bpmn.instance.StartEvent;
import org.camunda.bpm.model.xml.type.ModelElementType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ValidFlowRule extends BaseValidatorRule {

    @Autowired
    public ValidFlowRule(ValidatorRuleDao validatorRuleDao) {
        super(validatorRuleDao);
    }

    @Override
    public ValidatorRuleResult validate(BpmnModelInstance submissionBpmn, BpmnModelInstance solutionBpmn) {
        //Get start
        var starts = submissionBpmn.getModelElementsByType(StartEvent.class);

        if (starts.size() != 1)
            return invalid("Diagram musí obsahovat pouze jeden start");

        StartEvent start = starts.stream().findFirst().get();
        ModelElementType endEventType = submissionBpmn.getModel().getType(EndEvent.class);

        try {
            walk(start, endEventType);
        } catch (RuntimeException exception) {
            return invalid("Jeden z elementů neobsahuje odchozí hrany a nejedná se o konec");
        }

        return valid();
    }

    protected void walk(FlowNode node, ModelElementType endEventType) throws RuntimeException {
        if (node.getOutgoing().isEmpty() && node.getElementType() != endEventType)
            throw new RuntimeException("Element does not have ongoing and its not end type");

        for (var outgoing : node.getOutgoing()) {
            var target = outgoing.getTarget();

            walk(target, endEventType);
        }
    }

    @Override
    protected String getId() {
        return "ValidFlowRule";
    }

    @Override
    protected String getName() {
        return "Existence validního toku";
    }

    @Override
    protected String getDescription() {
        return "Existuje tok ze startu do cílů";
    }
}
