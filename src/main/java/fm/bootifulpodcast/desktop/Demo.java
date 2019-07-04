package fm.bootifulpodcast.desktop;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
@Profile("dev")
@RequiredArgsConstructor
class Demo {

	private final ApplicationEventPublisher publisher;

	@EventListener(StageReadyEvent.class)
	public void loadSampleData() {

		var root = new File(
				"/Users/joshlong/Desktop/bootiful-podcast/sample-podcast-assets/oleg-zhurakousky");
		var intro = new File(root, "1-oleg-intro.mp3");
		var interview = new File(root, "2-oleg-interview-lower.mp3");

		var podcast = new PodcastModel();
		podcast.introductionFileProperty().set(intro);
		podcast.interviewFileProperty().set(interview);
		podcast.titleProperty().setValue("Josh talks to Oleg Zhurakousky");
		podcast.descriptionProperty()
				.setValue("Josh talks to Oleg Zhurakousky and it awesome!");
		this.publisher.publishEvent(new PodcastLoadEvent(podcast));
	}

}
