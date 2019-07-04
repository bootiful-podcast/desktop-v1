package fm.bootifulpodcast.desktop;

import org.springframework.context.ApplicationEvent;

public class PodcastProductionStartedEvent extends ApplicationEvent {

	@Override
	public String getSource() {
		return (String) super.getSource();
	}

	public PodcastProductionStartedEvent(String uid) {
		super(uid);
	}

}
