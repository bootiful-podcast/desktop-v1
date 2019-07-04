package fm.bootifulpodcast.desktop;

public class PodcastLoadEvent extends GenericApplicationEvent<PodcastModel> {

	public PodcastLoadEvent(PodcastModel source) {
		super(source);
	}

}
