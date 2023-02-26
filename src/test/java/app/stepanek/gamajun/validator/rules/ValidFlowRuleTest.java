package app.stepanek.gamajun.validator.rules;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ValidFlowRuleTest {

    private final ValidFlowRule validFlowRule;

    @Autowired
    ValidFlowRuleTest(ValidFlowRule validFlowRule) {
        this.validFlowRule = validFlowRule;
    }

    @Test
    public void TestInvalidScenario() throws IOException {
        var resource = new ClassPathResource("ValidFlowInvalid.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(resource.getFile());

        var result = validFlowRule.validate(modelInstance);

        assertFalse(result.getValid());
    }

    @Test
    public void TestValidScenario() throws IOException {
        var resource = new ClassPathResource("ValidFlowValid.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(resource.getFile());

        var result = validFlowRule.validate(modelInstance);

        assertTrue(result.getValid());
    }
}
