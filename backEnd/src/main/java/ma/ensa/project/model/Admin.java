package ma.ensa.project.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "admin")
public class Admin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    private String password;

    @Temporal(TemporalType.TIMESTAMP)
    private Date joinedDate;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] image;

    @Column(columnDefinition = "LONGTEXT")
    private String image_base64;
}

