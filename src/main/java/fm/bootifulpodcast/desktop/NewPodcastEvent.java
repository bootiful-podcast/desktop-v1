package fm.bootifulpodcast.desktop;

public class NewPodcastEvent extends GenericApplicationEvent<PodcastModel> {

	public NewPodcastEvent(PodcastModel source) {
		super(source);
	}

}
