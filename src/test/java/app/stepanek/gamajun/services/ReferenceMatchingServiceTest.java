package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.ReferenceMatchingResult;
import app.stepanek.gamajun.domain.ReferenceMatchingResultState;
import org.camunda.bpm.model.bpmn.Bpmn;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class ReferenceMatchingServiceTest {
    @Autowired
    private ReferenceMatchingService referenceMatchingService;


    @Test
    void CaseASolutionA() throws IOException {
        var solution = new ClassPathResource("ReferenceMatching/CaseAReference.bpmn");
        BpmnModelInstance solutionInstance = Bpmn.readModelFromFile(solution.getFile());

        var reference = new ClassPathResource("ReferenceMatching/CaseASolutionA.bpmn");
        BpmnModelInstance referenceInstance = Bpmn.readModelFromFile(reference.getFile());

        assertEquals(referenceMatchingService.match(solutionInstance, referenceInstance).getResult(), ReferenceMatchingResultState.FullMatch);
    }
}