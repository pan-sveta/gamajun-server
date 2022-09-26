package app.stepanek.gamajun.services;

import app.stepanek.gamajun.command.ExamSubmissionCheckpointCommand;
import app.stepanek.gamajun.command.ExamSubmissionSubmitCommand;
import app.stepanek.gamajun.domain.ExamSubmissionState;
import app.stepanek.gamajun.dto.StudentExamSubmissionDTO;
import app.stepanek.gamajun.exceptions.ExamSubmissionNotFoundException;
import app.stepanek.gamajun.exceptions.ResourceNotOwnedByCurrentUserException;
import app.stepanek.gamajun.repository.ExamSubmissionDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.List;
import java.util.UUID;


@Service
public class ExamSubmissionService {
    private final ExamSubmissionDao examSubmissionDao;
    private final AdminService adminService;
    private final IAuthenticationFacade authenticationFacade;
    private final ModelMapper modelMapper;


    @Autowired
    public ExamSubmissionService(ExamSubmissionDao examSubmissionDao, AdminService adminService, IAuthenticationFacade authenticationFacade, ModelMapper modelMapper) {
        this.examSubmissionDao = examSubmissionDao;
        this.adminService = adminService;
        this.authenticationFacade = authenticationFacade;
        this.modelMapper = modelMapper;
    }

    public StudentExamSubmissionDTO getExamSubmission(UUID examSubmissionId) {
        var examSubmission = examSubmissionDao.findById(examSubmissionId)
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Exam submission with id %s was not found.".formatted(examSubmissionId)));

        if (!authenticationFacade.getUsername().equals(examSubmission.getAuthor()) && !adminService.IsUserAdministrator(authenticationFacade.getUsername()))
            throw new ResourceNotOwnedByCurrentUserException("Exam submission '%s' is not owned by user %s".formatted(examSubmissionId, authenticationFacade.getUsername()));

        return modelMapper.map(examSubmission,StudentExamSubmissionDTO.class);
    }

    @Transactional
    public List<StudentExamSubmissionDTO> mySubmissions() {
        var examSubmissions = examSubmissionDao.findByExam_Author(authenticationFacade.getUsername());

        TypeToken<List<StudentExamSubmissionDTO>> typeToken = new TypeToken<>() {  };
        return modelMapper.map(examSubmissions,typeToken.getType());
    }


    @Transactional
    public StudentExamSubmissionDTO submitStudentExam(UUID examSubmissionId, ExamSubmissionSubmitCommand submitCommand) {
        var examSubmission = examSubmissionDao.findById(examSubmissionId)
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Exam submission with id %s was not found.".formatted(examSubmissionId)));

        if (!authenticationFacade.getUsername().equals(examSubmission.getAuthor()))
            throw new ResourceNotOwnedByCurrentUserException("Exam submission '%s' is not owned by user %s".formatted(examSubmissionId, authenticationFacade.getUsername()));

        examSubmission.setSubmittedAt(Instant.now());
        examSubmission.setXml(submitCommand.getXml());
        examSubmission.setExamSubmissionState(ExamSubmissionState.Submitted);

        examSubmission = examSubmissionDao.save(examSubmission);

        return modelMapper.map(examSubmission, StudentExamSubmissionDTO.class);
    }

    @Transactional
    public StudentExamSubmissionDTO checkpointStudentExam(UUID examSubmissionId, ExamSubmissionCheckpointCommand checkpointCommand) {

        var examSubmission = examSubmissionDao.findById(examSubmissionId)
                .orElseThrow(() -> new ExamSubmissionNotFoundException("Exam submission with id %s was not found.".formatted(examSubmissionId)));

        if (!authenticationFacade.getUsername().equals(examSubmission.getAuthor()))
            throw new ResourceNotOwnedByCurrentUserException("Exam submission '%s' is not owned by user %s".formatted(examSubmissionId, authenticationFacade.getUsername()));

        examSubmission.setXml(checkpointCommand.getXml());

        examSubmission = examSubmissionDao.save(examSubmission);

        return modelMapper.map(examSubmission, StudentExamSubmissionDTO.class);
    }

}
