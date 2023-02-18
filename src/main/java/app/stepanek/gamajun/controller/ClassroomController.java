package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.Classroom;
import app.stepanek.gamajun.domain.Exam;
import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.graphql.CreateClassroomInput;
import app.stepanek.gamajun.repository.AssignmentDao;
import app.stepanek.gamajun.repository.ClassroomDao;
import app.stepanek.gamajun.services.ClassroomService;
import app.stepanek.gamajun.services.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class ClassroomController {

    ClassroomService classroomService;
    UserService userService;

    public ClassroomController(ClassroomService classroomService, UserService userService) {
        this.classroomService = classroomService;
        this.userService = userService;
    }

    @SchemaMapping
    public Classroom classroom(Classroom classroom) {
        return classroomService.findById(classroom.getId());
    }

    @QueryMapping
    public List<Classroom> classrooms(){
        return classroomService.findAll();
    }

    @QueryMapping
    public Classroom classroomById(@Argument UUID id){
        return classroomService.findById(id);
    }

    @MutationMapping
    public Classroom createClassroom(@Argument("input")CreateClassroomInput classroomInput){
        return classroomService.createClassroom(classroomInput);
    }

    @MutationMapping
    public Classroom addUser(@Argument String username, @Argument UUID classroomId){
        User user = userService.findByUsername(username);

        return classroomService.addUser(classroomId,user);
    }

    @MutationMapping
    public Classroom removeUser(@Argument String username, @Argument UUID classroomId){
        var classroom =  classroomService.removeUserFromClassroom(classroomId,username);
        userService.deleteUser(username);
        return classroom;
    }

    @QueryMapping
    public boolean validateInviteCode(@Argument String code){
        return classroomService.validateInviteCode(code);
    }

}
