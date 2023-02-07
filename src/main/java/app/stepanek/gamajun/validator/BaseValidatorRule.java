package app.stepanek.gamajun.validator;

import app.stepanek.gamajun.domain.ValidatorRule;
import app.stepanek.gamajun.domain.ValidatorRuleResult;
import app.stepanek.gamajun.repository.ValidatorRuleDao;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseValidatorRule {
    private final ValidatorRule validatorRuleEntity;
    private final ValidatorRuleDao validatorRuleDao;

    public abstract ValidatorRuleResult validate(BpmnModelInstance submissionBpmn, BpmnModelInstance solutionBpmn);

    protected abstract String getId();

    protected abstract String getName();

    protected abstract String getDescription();

    @Autowired
    public BaseValidatorRule(ValidatorRuleDao validatorRuleDao) {
        this.validatorRuleDao = validatorRuleDao;

        validatorRuleEntity = findOrCreateEntity();
    }

    private ValidatorRule findOrCreateEntity() {
        //Try to find existing rule in the DB
        var entity = validatorRuleDao.findById(getId());

        //If it exists, return it
        if (entity.isPresent())
            return entity.get();

        //If not, create it
        var newEntity = new ValidatorRule(getId(), getName(), getDescription());
        validatorRuleDao.save(newEntity);
        return newEntity;
    }

    protected ValidatorRuleResult valid() {
        return new ValidatorRuleResult(null, validatorRuleEntity, true, null);
    }

    protected ValidatorRuleResult invalid(String message) {
        return new ValidatorRuleResult(null, validatorRuleEntity, false, message);
    }
}
