package fm.bootifulpodcast.desktop.client;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.Date;

@Data
@RequiredArgsConstructor
public class ApiStatus {

	private final Date date;

	private final URI uri;

}
