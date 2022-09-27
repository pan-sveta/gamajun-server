package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.Exam;
import app.stepanek.gamajun.domain.ExamSubmission;
import app.stepanek.gamajun.domain.ExamSubmissionState;
import app.stepanek.gamajun.dto.StudentExamDTO;
import app.stepanek.gamajun.dto.StudentExamSubmissionDTO;
import app.stepanek.gamajun.exceptions.ExamNotFoundException;
import app.stepanek.gamajun.exceptions.ExamSubmissionLockedException;
import app.stepanek.gamajun.repository.ExamDao;
import app.stepanek.gamajun.repository.ExamSubmissionDao;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Service
public class ExamService {
    private final ExamDao examDao;
    private final ExamSubmissionDao examSubmissionDao;
    private final IAuthenticationFacade authenticationFacade;
    private final ModelMapper modelMapper;

    @Autowired
    public ExamService(ExamDao examDao, ExamSubmissionDao examSubmissionDao, IAuthenticationFacade authenticationFacade, ModelMapper modelMapper) {
        this.examDao = examDao;
        this.examSubmissionDao = examSubmissionDao;
        this.authenticationFacade = authenticationFacade;
        this.modelMapper = modelMapper;
    }

    public StudentExamSubmissionDTO beginExam(UUID examId) {
        var exam = examDao.findById(examId).orElseThrow(() -> new ExamNotFoundException("Exam with id '%s' was not found".formatted(examId)));

        var userExamSubmissions = examSubmissionDao.findByExam_Author(authenticationFacade.getUsername());
        if (userExamSubmissions.stream().map(ExamSubmission::getExam).anyMatch(e -> e.equals(exam)))
            throw new ExamSubmissionLockedException("You already attended exam with id '%s'".formatted(examId));

        Random rand = new Random();

        ExamSubmission examSubmission = new ExamSubmission();
        examSubmission.setAssignment(exam.getAssignments().get(rand.nextInt(exam.getAssignments().size())));
        examSubmission.setAuthor(authenticationFacade.getUsername());
        examSubmission.setStartedAt(Instant.now());
        examSubmission.setExam(exam);
        examSubmission.setExamSubmissionState(ExamSubmissionState.Draft);

        examSubmission = examSubmissionDao.save(examSubmission);
        return modelMapper.map(examSubmission, StudentExamSubmissionDTO.class);
    }

    public List<StudentExamDTO> getOpenedExams() {
        var exams = examDao.findByAccessibleFromLessThanEqualAndAccessibleToGreaterThanEqual(Instant.now(), Instant.now());
        var submissions = examSubmissionDao.findByExam_Author(authenticationFacade.getUsername());

        var attendedExams = submissions.stream().map(ExamSubmission::getExam).toList();
        exams.removeAll(attendedExams);

        TypeToken<List<StudentExamDTO>> typeToken = new TypeToken<>() {};
        return modelMapper.map(exams, typeToken.getType());
    }
}
