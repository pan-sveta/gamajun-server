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
public class MessageEventsRuleTest {
    private final MessageEventsRule messageEventsRule;

    @Autowired
    MessageEventsRuleTest(MessageEventsRule messageEventsRule) {
        this.messageEventsRule = messageEventsRule;
    }

    @Test
    public void TestInvalidScenario() throws IOException {
        var resource = new ClassPathResource("MessageEventsInvalid.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(resource.getFile());

        var result = messageEventsRule.validate(modelInstance);

        assertFalse(result.getValid());
    }

    @Test
    public void TestValidScenario() throws IOException{
        var resource = new ClassPathResource("MessageEventsValid.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(resource.getFile());

        var result = messageEventsRule.validate(modelInstance);

        assertTrue(result.getValid());
    }
}
