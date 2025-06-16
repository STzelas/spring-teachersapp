package gr.aueb.cf.schoolapp.rest;

import gr.aueb.cf.schoolapp.core.exceptions.*;
import gr.aueb.cf.schoolapp.core.filters.Paginated;
import gr.aueb.cf.schoolapp.core.filters.TeacherFilters;
import gr.aueb.cf.schoolapp.dto.TeacherInsertDTO;
import gr.aueb.cf.schoolapp.dto.TeacherReadOnlyDTO;
import gr.aueb.cf.schoolapp.service.TeacherService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api") // root path
@RequiredArgsConstructor
public class TeacherRestController {

    private final TeacherService teacherService;

    @PostMapping("/teachers/save")
    public ResponseEntity<TeacherReadOnlyDTO> saveTeacher(
            @Valid @RequestPart(name = "teacher")TeacherInsertDTO teacherInsertDTO,  // valid ενεργοποιεί τον hibernate validator και jpa που βαλαμε στα πεδια
            @Nullable @RequestPart("amkaFile")MultipartFile amkaFile,  // request part epeidi einai 2 merh / dto kai arxeio ara einai part.
            // Αν υπάρχουν λάθη μπαίνουν στο bindingResult
            // και στον κεντρικό handler θα πάρει το bindingResult
            // θα κάνει extract και θα τα δείξει μπροστά με JSON
            BindingResult bindingResult) throws AppObjectInvalidArgumentException, ValidationException, AppObjectAlreadyExists, AppServerException {

        if (bindingResult.hasErrors())
            throw new ValidationException(bindingResult);

        try {
            TeacherReadOnlyDTO teacherReadOnlyDTO = teacherService.saveTeacher(teacherInsertDTO, amkaFile);
            return new ResponseEntity<>(teacherReadOnlyDTO, HttpStatus.OK);
        } catch (IOException e) {
            throw new AppServerException("Attachment", "Attachment can not get uploaded");
        }
    }

    @GetMapping("/teachers/paginated")
    public ResponseEntity<Page<TeacherReadOnlyDTO>> getPaginatedTeachers(@RequestParam(defaultValue = "0")int page,
                                                                         @RequestParam(defaultValue = "5")int size) {

        Page<TeacherReadOnlyDTO> teachersPage = teacherService.getPaginatedTeachers(page,size);
        return new ResponseEntity<>(teachersPage, HttpStatus.OK);
    }

    // Οταν έχουμε @RequestMapping έχουμε PostMapping
    @PostMapping("/teachers/filtered")
    public ResponseEntity<List<TeacherReadOnlyDTO>> getFilteredTeachers(@Nullable @RequestBody TeacherFilters filters)
            throws AppObjectNotAuthorizedException {
        if (filters == null) TeacherFilters.builder().build(); // Φτιάχνει ενα instance με όλα να είναι null, άρα δε θα κάνει filter. Θα επιστρέψει κανονική List<>

        return ResponseEntity.ok(teacherService.getFilteredTeachers(filters));
    }

    @PostMapping("/teachers/filtered/paginated")
    public ResponseEntity<Paginated<TeacherReadOnlyDTO>> getPaginatedFilteredTeachers(@Nullable @RequestBody TeacherFilters filters)
            throws AppObjectNotAuthorizedException {
        if (filters == null) TeacherFilters.builder().build();

        return ResponseEntity.ok(teacherService.getTeachersFilteredPaginated(filters));
    }
}
