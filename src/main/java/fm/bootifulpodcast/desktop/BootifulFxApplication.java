package fm.bootifulpodcast.desktop;

import javafx.application.Application;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Log4j2
@SpringBootApplication
public class BootifulFxApplication {

	public static void main(String[] args) {
		Application.launch(JavaFxApplication.class, args);
	}

	@Bean
	MessageSource messageSource() {
		var utf8 = StandardCharsets.UTF_8.toString();
		log.info("default encoding is: " + utf8);

		var resourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
		resourceBundleMessageSource.setBasename("classpath:messages/labels");
		resourceBundleMessageSource.setDefaultEncoding(utf8);
		return resourceBundleMessageSource;
	}

	@Bean
	Locale locale() {
		return Locale.getDefault();
	}

}
