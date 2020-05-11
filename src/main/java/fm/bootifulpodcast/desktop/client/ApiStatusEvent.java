package fm.bootifulpodcast.desktop.client;

import fm.bootifulpodcast.desktop.GenericApplicationEvent;

public class ApiStatusEvent extends GenericApplicationEvent<ApiStatus> {

	public ApiStatusEvent(ApiStatus source) {
		super(source);
	}

}
