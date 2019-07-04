package fm.bootifulpodcast.desktop;

import org.springframework.context.ApplicationEvent;

public class PodcastValidationSuccessEvent extends GenericApplicationEvent<PodcastModel> {

	PodcastValidationSuccessEvent(PodcastModel source) {
		super(source);
	}

}
