package gr.aueb.cf.schoolapp.core.filters;

import lombok.*;
import org.springframework.lang.Nullable;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class TeacherFilters {

    @Nullable
    private String uuid;

    @Nullable
    private String userAfm;

    @Nullable
    private String userAmka;

    @Nullable
    private Boolean isActive;

}
