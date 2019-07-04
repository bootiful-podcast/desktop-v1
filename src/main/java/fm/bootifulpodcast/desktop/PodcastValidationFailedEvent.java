package fm.bootifulpodcast.desktop;

import org.springframework.context.ApplicationEvent;

public class PodcastValidationFailedEvent extends ApplicationEvent {

	PodcastValidationFailedEvent(PodcastModel source) {
		super(source);
	}

	@Override
	public PodcastModel getSource() {
		return (PodcastModel) super.getSource();
	}

}
