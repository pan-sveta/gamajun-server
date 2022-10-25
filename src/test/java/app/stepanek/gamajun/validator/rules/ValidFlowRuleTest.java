package app.stepanek.gamajun.validator.rules;

import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.GatewayDirection;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class ValidFlowRuleTest {

    private final ValidFlowRule validFlowRule;

    @Autowired
    ValidFlowRuleTest(ValidFlowRule validFlowRule) {
        this.validFlowRule = validFlowRule;
    }

    @Test
    public void TestInvalidScenario(){
        BpmnModelInstance modelInstance = Bpmn.createProcess()
                .name("BPMN API Invoice Process")
                .startEvent()
                .exclusiveGateway()
                .name("Invoice approved?")
                .gatewayDirection(GatewayDirection.Diverging)
                .condition("yes", "${approved}")
                .userTask()
                .moveToLastGateway()
                .condition("no", "${approved}")
                .userTask()
                .endEvent()
                .done();

        var result = validFlowRule.validate(modelInstance, null);

        assertFalse(result.getValid());
    }

    @Test
    public void TestValiddScenario(){
        BpmnModelInstance modelInstance = Bpmn.createProcess()
                .name("BPMN Test")
                .startEvent()
                .userTask()
                .endEvent()
                .done();

        var result = validFlowRule.validate(modelInstance, null);

        assertTrue(result.getValid());
    }
}
