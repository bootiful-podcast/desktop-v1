package fm.bootifulpodcast.desktop;

import lombok.Builder;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.net.URI;

@Getter
public class PodcastProductionCompletedEvent extends ApplicationEvent {

	@Override
	public PodcastProductionOutput getSource() {
		return (PodcastProductionOutput) super.getSource();
	}

	public PodcastProductionCompletedEvent(String uid, URI mediaUrl) {
		super(PodcastProductionOutput.builder().uid(uid).media(mediaUrl).build());
	}

	@Builder
	@Getter
	public static class PodcastProductionOutput {

		private String uid;

		private URI media;

	}

}
