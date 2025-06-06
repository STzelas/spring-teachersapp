package gr.aueb.cf.schoolapp.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "attachments")
public class Attachment extends AbstractEntity{

    private Long id;
    private String filename;
    private String savedName;
    private String filePath; // είτε storage είτε cloud(url)
    private String contentType;
    private String extension;
}
