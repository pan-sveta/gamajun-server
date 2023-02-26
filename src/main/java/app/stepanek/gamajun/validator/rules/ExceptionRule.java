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
    public ValidatorRuleResult validate(BpmnModelInstance submissionBpmn) {

        for (BoundaryEvent boundaryEvent : submissionBpmn.getModelElementsByType(BoundaryEvent.class)) {
            if (boundaryEvent.getOutgoing().isEmpty())
                invalid("Objekt %s s přiřazenou mezní událostí nemá odchozí hranu".formatted(boundaryEvent.getAttachedTo().getName()));
        }

        return valid();
    }

    @Override
    protected String getId() {
        return "Exception";
    }

    @Override
    protected String getName() {
        return "Ošetření vyjímek";
    }

    @Override
    protected String getDescription() {
        return "Všechny vyjímky musí být ošetřeny. Tok musí existovat i pro neočekávané stavy";
    }
}
