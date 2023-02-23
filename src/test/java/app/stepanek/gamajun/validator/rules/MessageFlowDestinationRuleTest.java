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
public class MessageFlowDestinationRuleTest {
    private final MessageFlowDestinationRule messageFlowDestinationRule;

    @Autowired
    MessageFlowDestinationRuleTest(MessageFlowDestinationRule messageFlowDestinationRule) {
        this.messageFlowDestinationRule = messageFlowDestinationRule;
    }

    @Test
    public void TestInvalidScenario() throws IOException {
        var resource = new ClassPathResource("MessageFlowDestinationInvalid.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(resource.getFile());

        var result = messageFlowDestinationRule.validate(modelInstance, null);

        assertFalse(result.getValid());
    }

    @Test
    public void TestValidScenario() throws IOException{
        var resource = new ClassPathResource("MessageFlowDestinationValid.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(resource.getFile());

        var result = messageFlowDestinationRule.validate(modelInstance, null);

        assertTrue(result.getValid());
    }
}
