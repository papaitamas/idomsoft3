package hu.yerico.idomsofttest.rest2;

import org.apache.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Rest2Application {
	
	private static Logger logger = Logger.getLogger(Rest2Application.class);

	public static void main(String[] args) {
		logger.debug("Application is starting...");
		
		SpringApplication.run(Rest2Application.class, args);
	}

}
