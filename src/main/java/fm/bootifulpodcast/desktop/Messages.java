package fm.bootifulpodcast.desktop;

import lombok.RequiredArgsConstructor;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@RequiredArgsConstructor
class Messages {

	private final MessageSource messageSource;

	private final Locale locale;

	public String getMessage(String key, Object... params) {
		return this.messageSource.getMessage(key, params, this.locale);
	}

	public String getMessage(String key) {
		return this.messageSource.getMessage(key, new Object[0], this.locale);
	}

}
