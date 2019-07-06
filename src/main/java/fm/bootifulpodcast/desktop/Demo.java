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
				"/Users/joshlong/Dropbox/spring-cast/published/venkat-subramaniam-may-27-2019/production/wavs/");
		var intro = new File(root, "intro.mp3");
		var interview = new File(root, "interview.mp3");

		var podcast = new PodcastModel();
		podcast.introductionFileProperty().set(intro);
		podcast.interviewFileProperty().set(interview);
		podcast.titleProperty().setValue(
				"Josh Long (@starbuxman) talks to Dr. Venkat Subramaniam (@venkat_s).");
		podcast.descriptionProperty()
				.setValue("Josh Long (@starbuxman) talks to Dr. Venkat Subramaniam.");
		this.publisher.publishEvent(new PodcastLoadEvent(podcast));
	}

}
