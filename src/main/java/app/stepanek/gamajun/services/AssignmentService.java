package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Assignment;
import app.stepanek.gamajun.exceptions.AssignmentNotFoundException;
import app.stepanek.gamajun.graphql.CreateAssignmentInput;
import app.stepanek.gamajun.graphql.UpdateAssignmentInput;
import app.stepanek.gamajun.repository.AssignmentDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class AssignmentService {
    private final AssignmentDao assignmentDao;
    private final IAuthenticationFacade authenticationFacade;

    public AssignmentService(AssignmentDao assignmentDao, IAuthenticationFacade authenticationFacade) {
        this.assignmentDao = assignmentDao;
        this.authenticationFacade = authenticationFacade;
    }

    @Transactional
    public Assignment createAssignment(CreateAssignmentInput createAssignmentInput) {
        log.info("Creating assignment with title: {}", createAssignmentInput.getTitle());

        Assignment assignment = new Assignment();

        assignment.setTitle(createAssignmentInput.getTitle());
        assignment.setDescription(createAssignmentInput.getDescription());
        assignment.setXml(createAssignmentInput.getXml());
        assignment.setSandbox(createAssignmentInput.isSandbox());
        assignment.setAuthor(authenticationFacade.getUser());

        return assignmentDao.save(assignment);
    }

    @Transactional
    //TODO: Check authrorization
    public Assignment findById(UUID id) {
        log.info("Finding assignment with id: {}", id);

        return assignmentDao.findById(id)
                .orElseThrow(() -> new AssignmentNotFoundException("Exam submission with id %s was not found.".formatted(id)));
    }

    @Transactional
    public Assignment updateAssignment(UpdateAssignmentInput updateAssignmentInput) {
        log.info("Updating assignment with id: {}", updateAssignmentInput.getId());

        Assignment assignment = assignmentDao.findById(updateAssignmentInput.getId())
                .orElseThrow();

        assignment.setTitle(updateAssignmentInput.getTitle());
        assignment.setDescription(updateAssignmentInput.getDescription());
        assignment.setXml(updateAssignmentInput.getXml());
        assignment.setSandbox(updateAssignmentInput.isSandbox());

        return assignmentDao.save(assignment);
    }

    public List<Assignment> sandboxAssignments() {
        log.info("Finding sandbox assignments");
        return assignmentDao.findBySandboxTrue();
    }
}
