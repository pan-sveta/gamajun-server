package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.ReferenceMatchingResult;
import app.stepanek.gamajun.domain.ReferenceMatchingResultState;
import lombok.extern.slf4j.Slf4j;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class ReferenceMatchingService {
    public ReferenceMatchingResult match(BpmnModelInstance solution, BpmnModelInstance reference) {
        log.info("Matching solution and reference");

        ReferenceMatchingResult referenceMatchingResult = new ReferenceMatchingResult();

        //Check isomorphism
        isomorphismCheck(referenceMatchingResult, solution, reference);

        //Check number of participants
        participantsCheck(referenceMatchingResult, solution, reference);

        //Evaluate overall result
        evaluateResult(referenceMatchingResult);

        return referenceMatchingResult;
    }

    private void evaluateResult(ReferenceMatchingResult referenceMatchingResult) {
        if (referenceMatchingResult.isParticipantsCheckResult() && referenceMatchingResult.isIsomorphismCheckResult())
            referenceMatchingResult.setResult(ReferenceMatchingResultState.FullMatch);
        else if (!referenceMatchingResult.isParticipantsCheckResult() && !referenceMatchingResult.isIsomorphismCheckResult())
            referenceMatchingResult.setResult(ReferenceMatchingResultState.NoMatch);
        else
            referenceMatchingResult.setResult(ReferenceMatchingResultState.PartialMatch);
    }

    //Participants check

    private void participantsCheck(ReferenceMatchingResult referenceMatchingResult, BpmnModelInstance solution, BpmnModelInstance reference) {
        boolean valid = true;
        StringBuilder sb = new StringBuilder();

        var solutionParticipantCount = solution.getModelElementsByType(Participant.class).size();
        var referenceParticipantCount = reference.getModelElementsByType(Participant.class).size();

        if (solutionParticipantCount != referenceParticipantCount) {
            valid = false;
            sb.append("Diagram neobsahuje referenční počet účastníků. Odevzdané řešení obsahuje %d účastníků, ale referenční %d."
                    .formatted(solutionParticipantCount, referenceParticipantCount)
            );
        } else {
            sb.append("Diagram obsahuje správný počet účastníků a to %d."
                    .formatted(referenceParticipantCount)
            );
        }

        var solutionLanesCount = solution.getModelElementsByType(Lane.class).size();
        var referenceLanesCount = reference.getModelElementsByType(Lane.class).size();

        if (solutionLanesCount != referenceLanesCount) {
            valid = false;
            sb.append("\nDiagram neobsahuje referenční počet swimlines. Odevzdané řešení obsahuje %d swimlines, ale referenční %d."
                    .formatted(solutionLanesCount, referenceLanesCount)
            );
        } else {
            sb.append("\nDiagram obsahuje správný počet swimlines a to %d."
                    .formatted(referenceLanesCount)
            );
        }

        referenceMatchingResult.setParticipantsCheckResult(valid);
        referenceMatchingResult.setParticipantsCheckMessage(sb.toString());
    }

    //Isomorphism check

    private void isomorphismCheck(ReferenceMatchingResult referenceMatchingResult, BpmnModelInstance solution, BpmnModelInstance reference) {
        //Build graph
        var referenceGraph = buildGraph(reference);
        var solutionGraph = buildGraph(solution);

        //Merge activities
        mergeSubsequentActivities(referenceGraph, reference);
        mergeSubsequentActivities(solutionGraph, solution);

        //Check isomorphism
        var isomorphismInspector = new VF2GraphIsomorphismInspector<>(solutionGraph, referenceGraph);
        referenceMatchingResult.setIsomorphismCheckResult(isomorphismInspector.isomorphismExists());
    }

    private void mergeSubsequentActivities(Graph<FlowNode, DefaultEdge> graph, BpmnModelInstance model) {
        List<Lane> lanes = new ArrayList<>(model.getModelElementsByType(Lane.class));
        List<FlowNode> toRemove = new ArrayList<>();

        for (DefaultEdge e : graph.edgeSet()) {
            var source = graph.getEdgeSource(e);
            var target = graph.getEdgeTarget(e);
            if (source instanceof Activity && target instanceof Activity) {
                var sourceParent = lanes.stream().filter(l -> l.getFlowNodeRefs().contains(source)).findFirst().orElse(null);
                var targetParent = lanes.stream().filter(l -> l.getFlowNodeRefs().contains(target)).findFirst().orElse(null);

                if (sourceParent != null && targetParent != null) {
                    if (sourceParent.equals(targetParent))
                        toRemove.add(target);
                } else
                    toRemove.add(target);

            }
        }

        Graphs.removeVertexAndPreserveConnectivity(graph, toRemove);
    }

    private Graph<FlowNode, DefaultEdge> buildGraph(BpmnModelInstance instance) {
        Graph<FlowNode, DefaultEdge> graph = new DefaultDirectedGraph<>(DefaultEdge.class);
        for (var flowNode : instance.getModelElementsByType(FlowNode.class)) {
            graph.addVertex(flowNode);
        }

        for (var seq : instance.getModelElementsByType(SequenceFlow.class)) {
            graph.addEdge(seq.getSource(), seq.getTarget());
        }

        return graph;
    }
}
