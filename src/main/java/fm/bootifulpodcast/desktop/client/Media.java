package fm.bootifulpodcast.desktop.client;

import lombok.Data;
import org.springframework.util.Assert;

import java.io.File;

@Data
public class Media {

	private final String format;

	private final File intro, interview;

	public Media(String format, File intro, File interview) {
		Assert.notNull(format, "the format must not be null");
		Assert.notNull(interview, "the interview file must not be null");
		Assert.notNull(intro, "the intro file must not be null");
		this.format = format;
		this.intro = intro;
		this.interview = interview;
	}

}
