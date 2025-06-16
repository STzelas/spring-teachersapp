package gr.aueb.cf.schoolapp.core.specifications;

import gr.aueb.cf.schoolapp.model.PersonalInfo;
import gr.aueb.cf.schoolapp.model.Teacher;
import gr.aueb.cf.schoolapp.model.User;
import gr.aueb.cf.schoolapp.model.static_data.Region;
import jakarta.persistence.criteria.Join;
import org.springframework.data.jpa.domain.Specification;

public class TeacherSpecification {

    private TeacherSpecification() {

    }

    // Φτιάχνουμε κομματάκια από το query
    //  τα οποία τα υλοποιούμε με το criteria builder
    //  ο οποίος μας δίνει τη δυνατότητα να ορίζουμε
    //  προγραμματιστικά Predicates (WHERE clauses)
    public static Specification<Teacher> teacherUserAfmIs(String afm) {
        return ((root, query, criteriaBuilder) -> {
            // Υλοποίηση μεθόδου του predicate
            if (afm == null || afm.isBlank()) return criteriaBuilder.isTrue(criteriaBuilder.literal(true)); // Πάντα true δεν φιλτράρουμε άρα αφού δεν υπάρχει afm δεν θα περιορίσει τις εγγραφές WHERE = true

            // root είναι ο teacher
            Join<Teacher, User> user = root.join("user"); // πεδίο μέσα στον teacher
            return criteriaBuilder.equal(user.get("afm"), afm);
        });
    }

    public static Specification<Teacher> teacherIsActive(Boolean isActive) {
        return ((root, query, criteriaBuilder) -> {
            if (isActive == null) return criteriaBuilder.isTrue(criteriaBuilder.literal(true));

            Join<Teacher, User> user = root.join("user"); // πεδίο μέσα στον teacher
            return criteriaBuilder.equal(user.get("isActive"), isActive);
        });
    }

    public static Specification<Teacher> teacherPersonalInfoAmkaIs(String amka) {
        return ((root, query, criteriaBuilder) -> {
            if (amka == null || amka.isBlank()) return criteriaBuilder.isTrue(criteriaBuilder.literal(true));

            Join<Teacher, PersonalInfo> personalInfo = root.join("personalInfo"); // πεδίο μέσα στον teacher
            return criteriaBuilder.equal(personalInfo.get("amka"), amka);
        });
    }

    public static Specification<Teacher> teacherRegionNameIs(String name) {
        return ((root, query, criteriaBuilder) -> {
            if (name == null || name.isBlank()) return criteriaBuilder.isTrue(criteriaBuilder.literal(true));

            Join<Teacher, Region> region = root.join("region");
            return criteriaBuilder.equal(region.get("name"), name);
        });
    }

    /**
     * Είναι ενα general specification (filter)
     */
    public static Specification<Teacher> teacherStringFieldLike(String field, String value) {
        return ((root, query, criteriaBuilder) -> {
            if (value == null || value.trim().isEmpty()) return criteriaBuilder.isTrue(criteriaBuilder.literal(true));

            return criteriaBuilder.like(criteriaBuilder.upper(root.get(field)), "%" + value.toUpperCase() + "%");
        });
    }
}
