package fm.bootifulpodcast.desktop;

public class PodcastValidationSuccessEvent extends GenericApplicationEvent<PodcastModel> {

	PodcastValidationSuccessEvent(PodcastModel source) {
		super(source);
	}

}
