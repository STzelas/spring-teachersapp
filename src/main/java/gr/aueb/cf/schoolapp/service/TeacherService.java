package gr.aueb.cf.schoolapp.service;

import gr.aueb.cf.schoolapp.core.exceptions.AppObjectAlreadyExists;
import gr.aueb.cf.schoolapp.core.exceptions.AppObjectInvalidArgumentException;
import gr.aueb.cf.schoolapp.core.filters.Paginated;
import gr.aueb.cf.schoolapp.core.filters.TeacherFilters;
import gr.aueb.cf.schoolapp.core.specifications.TeacherSpecification;
import gr.aueb.cf.schoolapp.dto.TeacherInsertDTO;
import gr.aueb.cf.schoolapp.dto.TeacherReadOnlyDTO;
import gr.aueb.cf.schoolapp.mapper.Mapper;
import gr.aueb.cf.schoolapp.model.Attachment;
import gr.aueb.cf.schoolapp.model.PersonalInfo;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.repository.PersonalInfoRepository;
import gr.aueb.cf.schoolapp.repository.TeacherRepository;
import gr.aueb.cf.schoolapp.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TeacherService {

    private final Logger LOGGER = LoggerFactory.getLogger(TeacherService.class);
    private final TeacherRepository teacherRepository;
    private final Mapper mapper;
    private final UserRepository userRepository;
    private final PersonalInfoRepository personalInfoRepository;

    @Transactional(rollbackOn = {AppObjectAlreadyExists.class})
    public TeacherReadOnlyDTO saveTeacher(TeacherInsertDTO teacherInsertDTO, MultipartFile amkaFile)
            throws AppObjectAlreadyExists, AppObjectInvalidArgumentException, IOException {

        if (userRepository.findByVat(teacherInsertDTO.getUser().getVat()).isPresent()) {
            throw new AppObjectAlreadyExists("User", "User with vat: " + teacherInsertDTO.getUser().getVat() + " already exists");
        }

        if (userRepository.findByUsername(teacherInsertDTO.getUser().getUsername()).isPresent()) {
            throw new AppObjectAlreadyExists("User", "User with username: " + teacherInsertDTO.getUser().getUsername() + " already exists");
        }

        // Επειδή είχαμε CascadeType.ALL
        // Saving τον teacher αλλάζει και το user & personal info
        Teacher teacher = mapper.mapToTeacherEntity(teacherInsertDTO);

        saveAmkaFile(teacher.getPersonalInfo(), amkaFile);
        Teacher savedTeacher = teacherRepository.save(teacher); // τώρα έχει και id o save teacher

        return mapper.mapToTeacherReadOnlyDTO(savedTeacher);
    }

    public void saveAmkaFile(PersonalInfo personalInfo, MultipartFile amkaFile)
            throws IOException {

        if (amkaFile == null || amkaFile.isEmpty()) return;

        String originalFileName = amkaFile.getOriginalFilename();
        String savedName = UUID.randomUUID().toString() + getFileExtension(originalFileName);

        String uploadDirectory = "uploads/";
        Path filePath = Paths.get(uploadDirectory + savedName);
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, amkaFile.getBytes());

        Attachment attachment = new Attachment();

        attachment.setFilename(originalFileName);
        attachment.setSavedName(savedName);
        attachment.setFilePath(filePath.toString());
        attachment.setContentType(amkaFile.getContentType());
        attachment.setExtension(getFileExtension(originalFileName));

        personalInfo.setAmkaFile(attachment);
    }

    private String getFileExtension(String filename) {

        if (filename == null || !filename.contains(".")) return "";

        return filename.substring(filename.lastIndexOf("."));

    }

    /**
     * What page? πχ page 5
     * What size? πχ size 20
     * How many total elements? πχ 50
     * How many current elements? πχ 20
     * <p>
     * Page container έχει όλη την πληροφορία
     * εκτός απο τα παραπάνω
     * έχει και totalElements και totalPages και currentPage
     */
    @Transactional
    public Page<TeacherReadOnlyDTO> getPaginatedTeachers(int page, int size) {
        String defaultSort = "id";


        // Pageable = Interface που το κάνουμε populate με το PageRequest
        // έχει getPageSize, getPageNumber, getSort κλπ και άλλες μεθόδους

        // Sort container που έχει πληροφορία για τα πεδία με τα οποία θα κάνουμε sort
        // τα αποτελέσματα και για το direction (ascending, descending)
        // και το κάνουμε populate με την .by()
        Pageable pageable = PageRequest.of(page, size, Sort.by(defaultSort).ascending());
        return teacherRepository.findAll(pageable).map(mapper::mapToTeacherReadOnlyDTO);
    }

    /**
     * Και paginated
     * και sorted
     */
    @Transactional
    public Page<TeacherReadOnlyDTO> getPaginatedSortedTeachers(int page, int size, String sortBy, String sortDirection) {

        // Παίρνει το direction / και Πληροφορία απο τί θα γίνει το sort
        Sort sort = Sort.by(Sort.Direction.fromString(sortDirection), sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        // Επιστρέφει PAGE - ΑΥΤΉ ΜΑΣ ΔΙΝΕΙ ΤΟ .map
        return teacherRepository.findAll(pageable).map(mapper::mapToTeacherReadOnlyDTO);
    }

    @Transactional
    public Paginated<TeacherReadOnlyDTO> getTeachersFilteredPaginated (TeacherFilters filters) {
        var filtered = teacherRepository.findAll(getSpecsFromFilters(filters), filters.getPageable());

        return new Paginated<>(filtered.map(mapper::mapToTeacherReadOnlyDTO));
    }


    private Specification<Teacher> getSpecsFromFilters (TeacherFilters teacherFilters) {
        return Specification
                .where(TeacherSpecification.teacherStringFieldLike("uuid", teacherFilters.getUuid()))
                .and(TeacherSpecification.teacherUserAfmIs(teacherFilters.getUserAfm()))
                .and(TeacherSpecification.teacherPersonalInfoAmkaIs(teacherFilters.getUserAfm()))
                .and(TeacherSpecification.teacherIsActive(teacherFilters.getIsActive()));
    }
}
