package com.pintor.sbb_remind;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication
public class SbbRemindApplication {

	public static void main(String[] args) {
		SpringApplication.run(SbbRemindApplication.class, args);
	}

}
