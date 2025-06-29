package gr.aueb.cf.schoolapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;


/**
 * todo:
 * 	1. fix swagger, fix bcrypt - Empty encoded password
 */
@SpringBootApplication
@EnableJpaAuditing // created_at, updated_at
public class SchoolAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(SchoolAppApplication.class, args);
	}

}
