package fm.bootifulpodcast.desktop;

public class PodcastValidationFailedEvent extends GenericApplicationEvent<PodcastModel> {

	PodcastValidationFailedEvent(PodcastModel source) {
		super(source);
	}

}
