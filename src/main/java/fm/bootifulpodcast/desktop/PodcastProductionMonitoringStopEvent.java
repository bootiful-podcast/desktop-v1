package fm.bootifulpodcast.desktop;

public class PodcastProductionMonitoringStopEvent
		extends GenericApplicationEvent<String> {

	PodcastProductionMonitoringStopEvent(String uid) {
		super(uid);
	}

}
