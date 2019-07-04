package fm.bootifulpodcast.desktop;

public class PodcastProductionStartedEvent extends GenericApplicationEvent<String> {

	public PodcastProductionStartedEvent(String uid) {
		super(uid);
	}

}
