package fm.bootifulpodcast.desktop.client;

import org.junit.Test;
import org.springframework.util.Assert;

import java.io.File;

public class PodcastArchiveBuilderTest {

	@Test
	public void createArchive() {
		var zip = new File(new File(System.getProperty("user.home"), "Desktop"),
				"archive.zip");

		var podcastArchiveBuilder = new PodcastArchiveBuilder();
		var root = new File("/Users/jlong/Desktop/test-podcast");
		var intro = new File(root, "intro.mp3");
		var interview = new File(root, "interview.mp3");
		var photo = new File(root, "gil-tene.jpg");
		File archive = podcastArchiveBuilder.createArchive(zip, "123", "Title 123",
				"Description 123", interview, intro, photo);

		Assert.isTrue(archive.exists(), "the archive does not exist");

	}

}