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
public class HangingRuleTest {
    private final HangingRule hangingRule;

    @Autowired
    HangingRuleTest(HangingRule hangingRule) {
        this.hangingRule = hangingRule;
    }

    @Test
    public void TestInvalidScenario1() throws IOException {
        var resource = new ClassPathResource("HangingRuleInvalid1.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(resource.getFile());

        var result = hangingRule.validate(modelInstance);

        assertFalse(result.getValid());
    }

    @Test
    public void TestInvalidScenario2() throws IOException {
        var resource = new ClassPathResource("HangingRuleInvalid2.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(resource.getFile());

        var result = hangingRule.validate(modelInstance);

        assertFalse(result.getValid());
    }

    @Test
    public void TestValidScenario() throws IOException{
        var resource = new ClassPathResource("HangingRuleValid.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(resource.getFile());

        var result = hangingRule.validate(modelInstance);

        assertTrue(result.getValid());
    }
}
