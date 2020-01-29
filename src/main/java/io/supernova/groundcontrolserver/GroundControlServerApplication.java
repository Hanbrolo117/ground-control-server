package io.supernova.groundcontrolserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Controller;

@Controller
@SpringBootApplication
public class GroundControlServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(GroundControlServerApplication.class, args);
	}

}
