package fm.bootifulpodcast.desktop;

import com.fasterxml.jackson.databind.ObjectMapper;
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
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ConcurrentTaskExecutor;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Log4j2
@EnableAsync
@SpringBootApplication
public class DesktopApplication {

	public static void main(String[] args) {
		Application.launch(JavaFxApplication.class, args);
	}

	@Bean
	ApiClient apiClient(@Value("${podcast.api.url}") String serverUrl,
			@Value("${podcast.monitor.interval}") int interval, ObjectMapper om,
			ApplicationEventPublisher publisher) {
		return new ApiClient(serverUrl, om, executor(), publisher, restTemplate(),
				interval);
	}

	@Bean
	RestTemplate restTemplate() {
		return new RestTemplateBuilder().build();
	}

	@Bean
	MessageSource messageSource() {
		var utf8 = StandardCharsets.UTF_8.toString();
		var resourceBundleMessageSource = new ReloadableResourceBundleMessageSource();
		resourceBundleMessageSource.setBasename("classpath:messages/labels");
		resourceBundleMessageSource.setDefaultEncoding(utf8);
		return resourceBundleMessageSource;
	}

	@Bean
	ScheduledExecutorService executor() {
		return Executors.newScheduledThreadPool(threadPoolCount());
	}

	@Bean
	TaskExecutor taskExecutor() {
		return new ConcurrentTaskExecutor(executor());
	}

	@Bean
	TaskScheduler taskScheduler() {
		return new ConcurrentTaskScheduler(executor());
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
