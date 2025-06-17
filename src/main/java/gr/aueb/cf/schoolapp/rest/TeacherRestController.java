package gr.aueb.cf.schoolapp.rest;

import gr.aueb.cf.schoolapp.core.exceptions.*;
import gr.aueb.cf.schoolapp.core.filters.Paginated;
import gr.aueb.cf.schoolapp.core.filters.TeacherFilters;
import gr.aueb.cf.schoolapp.dto.TeacherInsertDTO;
import gr.aueb.cf.schoolapp.dto.TeacherReadOnlyDTO;
import gr.aueb.cf.schoolapp.service.TeacherService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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

    @Operation(
            summary = "Save a teacher",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Teacher inserted",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TeacherReadOnlyDTO.class)
                            )
                    )
            }
    )
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



    @Operation(
            summary = "Get all teachers paginated",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Teachers Found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TeacherReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access Denied",
                            content = @Content
                    )
            }
    )
    @GetMapping("/teachers/paginated")
    public ResponseEntity<Page<TeacherReadOnlyDTO>> getPaginatedTeachers(@RequestParam(defaultValue = "0")int page,
                                                                         @RequestParam(defaultValue = "5")int size) {

        Page<TeacherReadOnlyDTO> teachersPage = teacherService.getPaginatedTeachers(page,size);  // απλά καλώ service
        return new ResponseEntity<>(teachersPage, HttpStatus.OK);
    }

    @Operation(
            summary = "Get all teachers filtered",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Teachers Found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TeacherReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access Denied",
                            content = @Content
                    )
            }
    )
    // Όταν έχουμε @RequestBody έχουμε PostMapping
    @PostMapping("/teachers/filtered")
    public ResponseEntity<List<TeacherReadOnlyDTO>> getFilteredTeachers(@Nullable @RequestBody TeacherFilters filters)
            throws AppObjectNotAuthorizedException {
        if (filters == null) TeacherFilters.builder().build(); // Φτιάχνει ενα instance με όλα να είναι null, άρα δε θα κάνει filter. Θα επιστρέψει κανονική List<>

        return ResponseEntity.ok(teacherService.getFilteredTeachers(filters));
    }

    @Operation(
            summary = "Get all teachers filtered",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Teachers Found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TeacherReadOnlyDTO.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Access Denied",
                            content = @Content
                    )
            }
    )
    @PostMapping("/teachers/filtered/paginated")
    public ResponseEntity<Paginated<TeacherReadOnlyDTO>> getPaginatedFilteredTeachers(@Nullable @RequestBody TeacherFilters filters)
            throws AppObjectNotAuthorizedException {
        if (filters == null) TeacherFilters.builder().build();

        return ResponseEntity.ok(teacherService.getTeachersFilteredPaginated(filters));
    }
}
