package fm.bootifulpodcast.desktop.client;

import fm.bootifulpodcast.desktop.GenericApplicationEvent;

import java.util.Date;

public class ApiDisconnectedEvent extends GenericApplicationEvent<Date> {

	ApiDisconnectedEvent() {
		super(new Date());
	}

}
