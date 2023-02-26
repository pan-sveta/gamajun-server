package app.stepanek.gamajun.validator.rules;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PoolLanesStartsRuleTest {
    private final PoolLanesStartsRule poolLanesStartsRule;

    @Autowired
    PoolLanesStartsRuleTest(PoolLanesStartsRule poolLanesStartsRule) {
        this.poolLanesStartsRule = poolLanesStartsRule;
    }

    @Test
    public void TestInvalidScenario1() throws IOException {
        var resource = new ClassPathResource("PoolLanesStartsInvalid1.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(resource.getFile());

        var result = poolLanesStartsRule.validate(modelInstance);

        assertFalse(result.getValid());
    }

    @Test
    public void TestInvalidScenario2() throws IOException {
        var resource = new ClassPathResource("PoolLanesStartsInvalid2.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(resource.getFile());

        var result = poolLanesStartsRule.validate(modelInstance);

        assertFalse(result.getValid());
    }

    @Test
    public void TestValidScenario() throws IOException{
        var resource = new ClassPathResource("PoolLanesStartsValid.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(resource.getFile());

        var result = poolLanesStartsRule.validate(modelInstance);

        assertTrue(result.getValid());
    }
}
