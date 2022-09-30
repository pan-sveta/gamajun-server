package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Assignment;
import app.stepanek.gamajun.graphql.CreateAssignmentInput;
import app.stepanek.gamajun.graphql.UpdateAssignmentInput;
import app.stepanek.gamajun.repository.AssignmentDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AssignmentService {
    private final AssignmentDao assignmentDao;
    private final IAuthenticationFacade authenticationFacade;

    public AssignmentService(AssignmentDao assignmentDao, IAuthenticationFacade authenticationFacade) {
        this.assignmentDao = assignmentDao;
        this.authenticationFacade = authenticationFacade;
    }

    @Transactional
    public Assignment createAssignment(CreateAssignmentInput createAssignmentInput) {
        Assignment assignment = new Assignment();

        assignment.setTitle(createAssignmentInput.getTitle());
        assignment.setDescription(createAssignmentInput.getDescription());
        assignment.setXml(createAssignmentInput.getXml());
        assignment.setAuthor(authenticationFacade.getUsername());

        return assignmentDao.save(assignment);
    }

    @Transactional
    public Assignment updateAssignment(UpdateAssignmentInput updateAssignmentInput) {
        Assignment assignment = assignmentDao.findById(updateAssignmentInput.getId())
                .orElseThrow();

        assignment.setTitle(updateAssignmentInput.getTitle());
        assignment.setDescription(updateAssignmentInput.getDescription());
        assignment.setXml(updateAssignmentInput.getXml());

        return assignmentDao.save(assignment);
    }
}
