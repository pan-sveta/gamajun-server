package app.stepanek.gamajun.validator.rules;

import app.stepanek.gamajun.domain.ValidatorRuleResult;
import app.stepanek.gamajun.repository.ValidatorRuleDao;
import app.stepanek.gamajun.validator.BaseValidatorRule;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.Activity;
import org.camunda.bpm.model.bpmn.instance.BoundaryEvent;
import org.camunda.bpm.model.bpmn.instance.IntermediateThrowEvent;
import org.springframework.stereotype.Component;

@Component
public class ExceptionRule extends BaseValidatorRule {
    //Rozman pattern 15
    public ExceptionRule(ValidatorRuleDao validatorRuleDao) {
        super(validatorRuleDao);
    }

    @Override
    public ValidatorRuleResult validate(BpmnModelInstance submissionBpmn, BpmnModelInstance solutionBpmn) {

        for (BoundaryEvent boundaryEvent : submissionBpmn.getModelElementsByType(BoundaryEvent.class)) {
            if (boundaryEvent.getOutgoing().isEmpty())
                invalid("Objekt %s s přiřazenou mezní událostí nemá odchozí hranu".formatted(boundaryEvent.getAttachedTo().getName()));
        }

        return valid();
    }

    @Override
    protected String getId() {
        return "ExceptionRule";
    }

    @Override
    protected String getName() {
        return "Kontrola zavěšení";
    }

    @Override
    protected String getDescription() {
        return "Aktivity/event, která má vstup musí mít i výstup";
    }
}
