package fm.bootifulpodcast.desktop;

import org.springframework.context.ApplicationEvent;

public class PodcastLoadEvent extends ApplicationEvent {

	public PodcastLoadEvent(PodcastModel podcastModel) {
		super(podcastModel);
	}

	@Override
	public PodcastModel getSource() {
		return (PodcastModel) super.getSource();
	}

}
