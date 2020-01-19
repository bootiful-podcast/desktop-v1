package fm.bootifulpodcast.desktop.client;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.assertj.core.groups.Tuple;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.FileSystemUtils;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.io.*;
import java.util.Arrays;
import java.util.List;

public class PodcastArchiveBuilderTest {

	private final File staging = new File(new File(new File(System.getProperty("user.home")), "Desktop"), "staging");

	private final File setUp = new File(new File(new File(System.getProperty("user.home")), "Desktop"), "setup");

	private final Resource img = new ClassPathResource("/sample-manifest.xml");

	private final Resource intro = new ClassPathResource("/sample-manifest.xml");

	private final Resource interview = new ClassPathResource("/sample-manifest.xml");

	private final File introFile = new File(this.setUp, "intro.mp3");

	private final File interviewFile = new File(this.setUp, "interview.mp3");

	private final File photoFile = new File(this.setUp, "photo.jpg");

	@Before
	@SneakyThrows
	public void before() {
		Arrays.asList(this.staging, this.setUp).forEach(f -> Assert.isTrue(f.exists() || f.mkdirs(),
				"the directory " + f.getAbsolutePath() + " could not be created"));
		Arrays.asList(new SetupAndTarget(img, photoFile), new SetupAndTarget(intro, introFile),
				new SetupAndTarget(interview, interviewFile))
				.forEach(sut -> copy(sut.getSource(), sut.getDestination()));
	}

	@SneakyThrows
	private void copy(Resource r, File os) {
		try (var in = r.getInputStream(); var out = new BufferedOutputStream(new FileOutputStream(os))) {
			FileCopyUtils.copy(in, out);
		}
	}

	@Data
	@RequiredArgsConstructor
	public static class SetupAndTarget {

		private final Resource source;

		private final File destination;

	}

	@Test
	public void createArchive() {
		var zip = new File(this.staging, "archive.zip");
		var podcastArchiveBuilder = new PodcastArchiveBuilder();
		File archive = podcastArchiveBuilder.createArchive(zip, "123", "Title 123", "Description 123", interviewFile,
				introFile, photoFile);
		Assert.isTrue(archive.exists(), "the archive does not exist");
	}

	@After
	public void after() {
		Arrays.asList(this.introFile, this.interviewFile, this.photoFile).forEach(this::delete);
	}

	@SneakyThrows
	private void delete(File file) {
		Assert.isTrue(!file.exists() || file.delete(), "the file " + file.getAbsolutePath() + " could not be deleted");
	}

}