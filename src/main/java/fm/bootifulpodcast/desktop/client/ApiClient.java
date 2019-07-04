package fm.bootifulpodcast.desktop.client;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import fm.bootifulpodcast.desktop.PodcastProductionCompletedEvent;
import fm.bootifulpodcast.desktop.PodcastProductionStartedEvent;
import fm.bootifulpodcast.desktop.StageReadyEvent;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.io.FileOutputStream;
import java.net.SocketException;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class ApiClient {

	private final AtomicBoolean connected = new AtomicBoolean(false);

	private final ObjectMapper objectMapper;

	private final ScheduledExecutorService executor;

	private final RestTemplate restTemplate;

	private final ApplicationEventPublisher publisher;

	private final String serverUrl, actuatorUrl;

	private final int monitorDelayInSeconds;

	public ApiClient(String serverUrl, ObjectMapper om, ScheduledExecutorService executor,
			ApplicationEventPublisher publisher, RestTemplate restTemplate,
			int interval) {
		this.objectMapper = om;
		this.monitorDelayInSeconds = interval;
		this.executor = executor;
		this.restTemplate = restTemplate;
		this.publisher = publisher;

		Assert.hasText(serverUrl, "The server URL provided is null");
		this.serverUrl = serverUrl.endsWith("/")
				? serverUrl.substring(0, serverUrl.length() - 1) : serverUrl;
		this.actuatorUrl = this.serverUrl + "/actuator/health";
		log.debug(
				"going to monitor the Actuator health endpoint every " + interval + "s.");
		log.debug("The server URL is " + this.serverUrl + " and the actuator URL is "
				+ this.actuatorUrl);
	}

	@Async
	@SneakyThrows
	public CompletableFuture<File> download(URI mediaUri, File file) {
		var urlResource = new UrlResource(mediaUri);
		try (var inputStream = urlResource.getInputStream();
				var outputStream = new FileOutputStream(file)) {
			FileCopyUtils.copy(inputStream, outputStream);
			log.debug("downloaded " + mediaUri.toString() + " to "
					+ file.getAbsolutePath() + "!");
		}
		return CompletableFuture.completedFuture(file);
	}

	/*
	 * An event is published only once, as soon as the API is connected or disconnected.
	 * We can't afford to poll for the connection until other components managed by other
	 * controllers are able to respond, once the stage is ready.
	 */
	@EventListener(StageReadyEvent.class)
	public void stageIsReady() {
		this.executor.scheduleWithFixedDelay(this::monitorConnectedEndpoint, 0,
				this.monitorDelayInSeconds, TimeUnit.SECONDS);
	}

	private void monitorConnectedEndpoint() {
		try {
			var response = this.restTemplate.getForEntity(this.actuatorUrl, String.class);
			var responseBody = response.getBody();
			log.debug("Response from Actuator status endpoint: " + responseBody);
			Map<String, Object> jsonMap = Objects.requireNonNull(objectMapper
					.readValue(responseBody, new TypeReference<Map<String, Object>>() {
					}));
			var status = (String) jsonMap.get("status");
			var isActuatorHealthy = response.getStatusCode().is2xxSuccessful()
					&& status.equalsIgnoreCase("UP");
			if (isActuatorHealthy && this.connected.compareAndSet(false, true)) {
				publisher.publishEvent(new ApiConnectedEvent());
			}
		}
		catch (Exception e) {
			if (e instanceof ResourceAccessException || e instanceof SocketException) {
				log.debug("Could not connect to " + this.actuatorUrl);
			}
			if (this.connected.compareAndSet(true, false)) {
				this.publisher.publishEvent(new ApiDisconnectedEvent());
			}
		}
	}

	@Async
	public void produce(String uid, String title, String description, File introduction,
			File interview) {
		this.publisher.publishEvent(new PodcastProductionStartedEvent(uid));
		var archive = this.createArchive(uid, title, description, introduction,
				interview);
		try {
			var uri = this.submitForProduction(uid, archive);
			this.publisher.publishEvent(new PodcastProductionCompletedEvent(uid, uri));
		}
		finally {
			Assert.isTrue(!archive.exists() || archive.delete(),
					"The file " + archive.getAbsolutePath()
							+ " could not be deleted, but should be.");
		}
	}

	private File createArchive(String uuid, String title, String description, File intro,
			File interview) {
		return new PodcastArchiveBuilder(title, description, uuid)
				.addMp3Media(intro, interview).build();
	}

	private URI submitForProduction(String uid, File archive) {
		var headers = new HttpHeaders();
		headers.setContentType(MediaType.MULTIPART_FORM_DATA);
		var resource = new FileSystemResource(archive);
		var body = new LinkedMultiValueMap<String, Object>();
		body.add("file", resource);
		var requestEntity = new HttpEntity<MultiValueMap<String, Object>>(body, headers);
		var url = this.serverUrl + "/podcasts/" + uid;
		var response = restTemplate.postForEntity(url, requestEntity, String.class);
		var location = response.getHeaders().getLocation();
		Assert.notNull(location, "The location URI must be non-null");
		var uri = URI.create(this.serverUrl + location.getPath());
		return this.pollProductionStatus(uri);
	}

	@SneakyThrows
	private URI pollProductionStatus(URI statusUrl) {
		var parameterizedTypeReference = new ParameterizedTypeReference<Map<String, String>>() {
		};
		while (true) {
			var result = this.restTemplate.exchange(statusUrl, HttpMethod.GET, null,
					parameterizedTypeReference);
			Assert.isTrue(result.getStatusCode().is2xxSuccessful(),
					"The HTTP request must return a valid 20x series HTTP status");
			var status = Objects.requireNonNull(result.getBody());
			var key = "media-url";
			if (status.containsKey(key)) {
				return URI.create(status.get(key));
			}
			else {
				var seconds = 10;
				TimeUnit.SECONDS.sleep(seconds);
				log.debug("Sleeping " + seconds
						+ "s while checking the production status at '" + statusUrl
						+ "'.");
			}
		}
	}

}
