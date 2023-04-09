package app.stepanek.gamajun.controller;

import app.stepanek.gamajun.domain.Classroom;
import app.stepanek.gamajun.domain.User;
import app.stepanek.gamajun.graphql.CreateClassroomInput;
import app.stepanek.gamajun.services.ClassroomService;
import app.stepanek.gamajun.services.UserService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SchemaMapping;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;

import java.util.List;
import java.util.UUID;

@Controller
public class ClassroomController {

    private final ClassroomService classroomService;
    private final UserService userService;

    public ClassroomController(ClassroomService classroomService, UserService userService) {
        this.classroomService = classroomService;
        this.userService = userService;
    }

    @SchemaMapping
    public Classroom classroom(Classroom classroom) {
        return classroomService.findById(classroom.getId());
    }

    @Secured("GAMAJUN_TEACHER")
    @QueryMapping
    public List<Classroom> classrooms(){
        return classroomService.findAll();
    }

    @Secured("GAMAJUN_TEACHER")
    @QueryMapping
    public Classroom classroomById(@Argument UUID id){
        return classroomService.findById(id);
    }

    @MutationMapping
    public Classroom createClassroom(@Argument("input")CreateClassroomInput classroomInput){
        return classroomService.createClassroom(classroomInput);
    }

    @MutationMapping
    public void deleteClassroom(@Argument UUID id){
        classroomService.deleteClassroom(id);
    }

    @Secured("GAMAJUN_TEACHER")
    @MutationMapping
    public Classroom addUser(@Argument String username, @Argument UUID classroomId){
        User user = userService.findByUsername(username);

        return classroomService.addUser(classroomId,user);
    }

    @Secured("GAMAJUN_TEACHER")
    @MutationMapping
    public Classroom removeUser(@Argument String username, @Argument UUID classroomId){
        var classroom =  classroomService.removeUserFromClassroom(classroomId,username);
        userService.deleteUser(username);
        return classroom;
    }
}
