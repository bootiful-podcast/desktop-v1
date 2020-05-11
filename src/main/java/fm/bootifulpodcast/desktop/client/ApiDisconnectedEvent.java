package fm.bootifulpodcast.desktop.client;

import fm.bootifulpodcast.desktop.GenericApplicationEvent;

public class ApiDisconnectedEvent extends GenericApplicationEvent<ApiStatus> {

	ApiDisconnectedEvent(ApiStatus apiStatus) {
		super(apiStatus);
	}

}
