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
public class MessageFlowSourceRuleTest {
    private final MessageFlowSourceRule messageFlowSourceRule;

    @Autowired
    MessageFlowSourceRuleTest(MessageFlowSourceRule messageFlowSourceRule) {
        this.messageFlowSourceRule = messageFlowSourceRule;
    }

    @Test
    public void TestInvalidScenario() throws IOException {
        var resource = new ClassPathResource("MessageFlowSourceInvalid.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(resource.getFile());

        var result = messageFlowSourceRule.validate(modelInstance, null);

        assertFalse(result.getValid());
    }

    @Test
    public void TestValidScenario() throws IOException{
        var resource = new ClassPathResource("MessageFlowSourceValid.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(resource.getFile());

        var result = messageFlowSourceRule.validate(modelInstance, null);

        assertTrue(result.getValid());
    }
}
