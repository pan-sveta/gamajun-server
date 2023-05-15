package app.stepanek.gamajun.services;

import app.stepanek.gamajun.domain.*;
import app.stepanek.gamajun.utilities.IAuthenticationFacade;
import app.stepanek.gamajun.validator.BaseValidatorRule;
import app.stepanek.gamajun.validator.rules.ValidFlowRule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ValidatorServiceTest {

    @Mock
    ReferenceMatchingService referenceMatchingService;

    @Mock
    IAuthenticationFacade authenticationFacade;

    @Mock
    List<BaseValidatorRule> ruleList = new ArrayList<>();

    @InjectMocks
    ValidatorService validatorService;

    User mockAuthentication() {
        Set<Role> roles = new HashSet<>();

        var studentRole = new Role("GAMAJUN_STUDENT");
        var teacherRole = new Role("GAMAJUN_TEACHER");
        roles.add(studentRole);
        roles.add(teacherRole);

        User user = new User();
        user.setUsername("username");
        user.setPassword("password");
        user.setEmail("email");
        user.setName("name");
        user.setSurname("surname");
        user.setRoles(roles);


        lenient().when(authenticationFacade.getUser()).thenReturn(user);
        lenient().when(authenticationFacade.isResourceOwner(user)).thenReturn(true);
        lenient().when(authenticationFacade.getUsername()).thenReturn(user.getUsername());

        return user;
    }

    @BeforeEach
    void setUp() {
        BaseValidatorRule mockRule = mock(BaseValidatorRule.class);

        when(mockRule.validate(any())).thenReturn(new ValidatorRuleResult(){{
            setValid(true);
        }});

        Iterator<BaseValidatorRule> iterator = Arrays.asList(mockRule).iterator();

        when(ruleList.iterator()).thenReturn(iterator);

        ruleList.add(mockRule);
    }


    @Test
    void validateSubmission() throws IOException {
        mockAuthentication();

        var resource = new ClassPathResource("ValidFlowInvalid.bpmn");
        var xml = Files.readString(resource.getFile().toPath());

        var submission = new ExamSubmission();
        submission.setXml(xml);
        submission.setAssignment(Assignment.builder().xml(xml).build());

        when(referenceMatchingService.match(any(), any())).thenReturn(new ReferenceMatchingResult(){{
            setResult(ReferenceMatchingResultState.FullMatch);
            setIsomorphismCheckResult(true);
            setParticipantsCheckResult(true);
        }});

        var res = validatorService.validateSubmission(submission);

        assertTrue(res.getReferenceMatchingResult().isIsomorphismCheckResult());
        assertTrue(res.getReferenceMatchingResult().isParticipantsCheckResult());

        assertTrue(res.getValidatorRuleResults().stream().findFirst().get().getValid());
    }
}