package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Assignment;
import app.stepanek.gamajun.graphql.CreateAssignmentInput;
import app.stepanek.gamajun.graphql.UpdateAssignmentInput;
import app.stepanek.gamajun.repository.AssignmentDao;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AssignmentService {
    private final AssignmentDao assignmentDao;

    public AssignmentService(AssignmentDao assignmentDao) {
        this.assignmentDao = assignmentDao;
    }

    @Transactional
    public Assignment createAssignment(CreateAssignmentInput createAssignmentInput) {
        Assignment assignment = new Assignment();

        assignment.setTitle(createAssignmentInput.getTitle());
        assignment.setDescription(createAssignmentInput.getDescription());
        assignment.setXml(createAssignmentInput.getXml());
        assignment.setAuthor(createAssignmentInput.getAuthor());

        return assignmentDao.save(assignment);
    }

    @Transactional
    public Assignment updateAssignment(UpdateAssignmentInput updateAssignmentInput) {
        Assignment assignment = assignmentDao.findById(updateAssignmentInput.getId())
                .orElseThrow();

        assignment.setTitle(updateAssignmentInput.getTitle());
        assignment.setDescription(updateAssignmentInput.getDescription());
        assignment.setXml(updateAssignmentInput.getXml());
        assignment.setAuthor(updateAssignmentInput.getAuthor());

        return assignmentDao.save(assignment);
    }
}
