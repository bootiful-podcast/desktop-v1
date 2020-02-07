package fm.bootifulpodcast.desktop;

import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Profile;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

@Component
@Profile("dev")
@RequiredArgsConstructor
class Demo {

	private final ApplicationEventPublisher publisher;

	@EventListener(StageReadyEvent.class)
	public void loadSampleData() {
		var home = System.getProperty("user.home");
		var root = new File(home + "/Dropbox/spring-cast/0-PUBLISHED/2019/simon-baslé-10-21-2019/");
		var intro = new File(root, "intro.mp3");
		var interview = new File(root, "interview.mp3");
		var photo = new File(root, "simon-basle-image.jpg");
		Assert.isTrue(intro.exists() && interview.exists() && photo.exists(),
				"the sample files " + intro.getAbsolutePath() + " and " + interview.getAbsolutePath());
		var sdf = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss.SSS");
		var text2 = "Josh Long (@starbuxman) talks to another amazing guest @ " + sdf.format(new Date());
		var podcast = new PodcastModel();
		podcast.introductionFileProperty().set(intro);
		podcast.interviewFileProperty().set(interview);
		podcast.titleProperty().setValue(text2);
		podcast.photoFileProperty().setValue(photo);
		podcast.descriptionProperty().setValue(text2);
		this.publisher.publishEvent(new PodcastLoadEvent(podcast));
	}

}
