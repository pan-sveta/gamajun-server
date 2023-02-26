package app.stepanek.gamajun.validator.rules;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class PoolActivityRuleTest {
    private final PoolActivityRule poolActivityRule;

    @Autowired
    PoolActivityRuleTest(PoolActivityRule poolActivityRule) {
        this.poolActivityRule = poolActivityRule;
    }

    @Test
    public void TestInvalidScenario() throws IOException {
        var resource = new ClassPathResource("PoolActivityRuleInvalid.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(resource.getFile());

        var result = poolActivityRule.validate(modelInstance);

        assertFalse(result.getValid());
    }

    @Test
    public void TestValidScenario() throws IOException{
        var resource = new ClassPathResource("PoolActivityRuleValid.bpmn");
        BpmnModelInstance modelInstance = Bpmn.readModelFromFile(resource.getFile());

        var result = poolActivityRule.validate(modelInstance);

        assertFalse(result.getValid());
    }
}
