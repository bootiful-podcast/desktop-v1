package fm.bootifulpodcast.desktop.client;

import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;

@Log4j2
public class ProductionStatus {

	private Executor executor;

	private RestTemplate template;

	public ProductionStatus(URI serverRootUrl, Executor ex, RestTemplate rt,
			String errMsg, boolean published, String uid, HttpStatus status,
			URI statusUrl) {

		this.executor = ex;
		this.template = rt;
		Assert.notNull(this.executor, "the executor must be non-null");
		Assert.notNull(this.template,
				"the " + RestTemplate.class.getName() + " must be non-null");
		Assert.notNull(uid, "the UID must be non-null");
		this.statusUrl = statusUrl;

		// this.rootServerUrl = serverRootUrl;
	}

	/*
	 * public String getUid() { return uid; }
	 *
	 * public boolean isPublished() { return published; }
	 *
	 * public String getErrorMessage() { return errorMessage; }
	 *
	 * public HttpStatus getHttpStatus() { return httpStatus; }
	 *
	 * public URI getStatusUrl() { return statusUrl; }
	 */

	private URI statusUrl;

	/*
	 * public CompletableFuture<URI> checkStatus() { Assert.notNull(this.executor,
	 * "the executor must not be null"); return
	 * CompletableFuture.supplyAsync(this::doPollProductionStatus, this.executor); }
	 */

	@SneakyThrows
	private URI pollProductionStatus() {
		var parameterizedTypeReference = new ParameterizedTypeReference<Map<String, String>>() {
		};
		while (true) {
			var result = this.template.exchange(this.statusUrl, HttpMethod.GET, null,
					parameterizedTypeReference);
			Assert.isTrue(result.getStatusCode().is2xxSuccessful(),
					"the HTTP request must return a valid 20x series HTTP status");
			var status = Objects.requireNonNull(result.getBody());
			var key = "media-url";
			if (status.containsKey(key)) {
				return URI.create(status.get(key));
			}
			else {
				var seconds = 10;
				TimeUnit.SECONDS.sleep(seconds);
				log.debug("sleeping " + seconds
						+ "s while checking the production status at '" + this.statusUrl
						+ "'.");
			}
		}
	}

}
