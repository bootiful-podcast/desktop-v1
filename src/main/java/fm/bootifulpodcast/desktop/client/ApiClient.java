package fm.bootifulpodcast.desktop.client;

import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

@Log4j2
public class ApiClient {

	private final AtomicBoolean connected = new AtomicBoolean();

	private final ScheduledExecutorService executor;

	private final RestTemplate restTemplate;

	private final ApplicationEventPublisher publisher;

	private final String serverUrl;

	private final String actuatorUrl;

	public ApiClient(String serverUrl, ScheduledExecutorService executor,
			ApplicationEventPublisher publisher, RestTemplate restTemplate) {

		this.executor = executor;
		this.restTemplate = restTemplate;
		this.publisher = publisher;

		Assert.hasText(serverUrl, "the server URL provided is null");
		this.serverUrl = serverUrl.endsWith("/")
				? serverUrl.substring(0, serverUrl.length() - 1) : serverUrl;
		this.actuatorUrl = this.serverUrl + "/actuator/health";

		log.debug("the server URL is " + this.serverUrl + " and the actuator URL is "
				+ this.actuatorUrl);

		// kick off a background thread to monitor the actuator endpoint
		this.executor.submit(this::monitorConnectedEndpoint);

		this.executor.scheduleAtFixedRate(this::monitorConnectedEndpoint, 0, 5,
				TimeUnit.SECONDS);
	}

	protected void monitorConnectedEndpoint() {
		try {
			log.debug("contacting the following endpoint to "
					+ "verify that we're connected to " + this.serverUrl);

			var typeReference = new ParameterizedTypeReference<Map<String, Object>>() {
			};
			var entity = this.restTemplate.exchange(this.actuatorUrl, HttpMethod.GET,
					HttpEntity.EMPTY, typeReference);
			var body = entity.getBody();
			var jsonMap = Objects.requireNonNull(body);
			var status = (String) jsonMap.get("status");
			var isActuatorHealthy = entity.getStatusCode().is2xxSuccessful()
					&& status.equalsIgnoreCase("UP");
			var existingConnectedStatus = this.connected.get();
			if (existingConnectedStatus != isActuatorHealthy) {
				this.connected.set(isActuatorHealthy);
			}
		}
		catch (Exception e) {
			log.warn(e);
			this.connected.set(false);
		}

		if (this.connected.get()) {
			publisher.publishEvent(new ApiConnectedEvent());
		}
		else {
			publisher.publishEvent(new ApiDisconnectedEvent());
		}
	}

}
