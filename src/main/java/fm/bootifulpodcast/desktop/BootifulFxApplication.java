package fm.bootifulpodcast.desktop;

import fm.bootifulpodcast.desktop.client.ApiClient;
import javafx.application.Application;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Log4j2
@SpringBootApplication
public class BootifulFxApplication {

	public static void main(String[] args) {
		Application.launch(JavaFxApplication.class, args);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplateBuilder().build();
	}

	@Bean
	MessageSource messageSource() {
		var utf8 = StandardCharsets.UTF_8.toString();
		log.debug("default system encoding is: " + utf8);
		var resourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
		resourceBundleMessageSource.setBasename("classpath:messages/labels");
		resourceBundleMessageSource.setDefaultEncoding(utf8);
		return resourceBundleMessageSource;
	}

	@Bean
	ApiClient apiClient(@Value("${podcast.monitor.interval}") int interval,
																					@Value("${podcast.api.url}") String apiUrl,
																					ScheduledExecutorService executorService, ApplicationEventPublisher publisher,
																					RestTemplate restTemplate) {
		log.info("connecting to API endpoint: [" + apiUrl + ']');
		return new ApiClient(apiUrl, executorService, publisher, restTemplate, interval);
	}

	@Bean
	ScheduledExecutorService scheduledExecutorService() {
		return Executors.newScheduledThreadPool(threadPoolCount());
	}

	@Bean
	Executor executor() {
		return Executors.newFixedThreadPool(threadPoolCount());
	}

	@Bean
	Locale locale() {
		return Locale.getDefault();
	}

	private int threadPoolCount() {
		var processors = Runtime.getRuntime().availableProcessors();
		return processors > 2 ? processors / 2 : processors;
	}

}
