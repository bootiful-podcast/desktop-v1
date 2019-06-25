package fm.bootifulpodcast.desktop;

import javafx.application.Application;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;

@Log4j2
@SpringBootApplication
public class BootifulFxApplication {

	public static void main(String[] args) {
		Application.launch(JavaFxApplication.class, args);
	}

	@Bean
	MessageSource messageSource() {
		var messageSource = new ReloadableResourceBundleMessageSource();
		messageSource.setBasename("classpath:messages/labels");
		var utf8 = StandardCharsets.UTF_8.toString();
		log.info("default encoding is " + utf8);
		messageSource.setDefaultEncoding(utf8);
		return messageSource;
	}


}
