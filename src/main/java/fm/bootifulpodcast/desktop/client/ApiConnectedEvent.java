package fm.bootifulpodcast.desktop.client;

import fm.bootifulpodcast.desktop.GenericApplicationEvent;

public class ApiConnectedEvent extends GenericApplicationEvent<ApiStatus> {

	public ApiConnectedEvent(ApiStatus source) {
		super(source);
	}

}
