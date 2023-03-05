package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.ReferenceMatchingResult;
import app.stepanek.gamajun.domain.ReferenceMatchingResultState;
import org.camunda.bpm.model.bpmn.BpmnModelInstance;
import org.camunda.bpm.model.bpmn.instance.*;
import org.jgrapht.Graph;
import org.jgrapht.alg.isomorphism.VF2GraphIsomorphismInspector;
import org.jgrapht.alg.util.Pair;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ReferenceMatchingService {
    public ReferenceMatchingResult match(BpmnModelInstance solution, BpmnModelInstance reference) {
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
        mergeSubsequentActivities(referenceGraph);
        mergeSubsequentActivities(solutionGraph);

        //Check isomorphism
        var isomorphismInspector = new VF2GraphIsomorphismInspector<FlowNode, DefaultEdge>(solutionGraph, referenceGraph);
        referenceMatchingResult.setIsomorphismCheckResult(isomorphismInspector.isomorphismExists());
    }

    private void mergeSubsequentActivities(Graph<FlowNode, DefaultEdge> graph) {
        List<FlowNode> toRemove = new ArrayList<>();
        List<Pair<FlowNode, FlowNode>> toAdd = new ArrayList<>();

        for (DefaultEdge e : graph.edgeSet()) {
            var source = graph.getEdgeSource(e);
            var target = graph.getEdgeTarget(e);
            if (source instanceof Activity && target instanceof Activity) {
                for (var flow : source.getIncoming()) {
                    var node = flow.getSource();
                    toAdd.add(new Pair<>(node, target));
                }

                toRemove.add(source);

            }
        }

        for (var edge : toAdd) {
            graph.addEdge(edge.getFirst(), edge.getSecond());
        }

        for (var node : toRemove) {
            graph.removeVertex(node);
        }
    }

    private Graph<FlowNode, DefaultEdge> buildGraph(BpmnModelInstance instance) {
        Graph<FlowNode, DefaultEdge> graph = new DefaultDirectedGraph<FlowNode, DefaultEdge>(DefaultEdge.class);
        for (var flowNode : instance.getModelElementsByType(FlowNode.class)) {
            graph.addVertex(flowNode);
        }

        for (var seq : instance.getModelElementsByType(SequenceFlow.class)) {
            graph.addEdge(seq.getSource(), seq.getTarget());
        }

        return graph;
    }
}
