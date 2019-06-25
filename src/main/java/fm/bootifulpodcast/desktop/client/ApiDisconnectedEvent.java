package fm.bootifulpodcast.desktop.client;

import org.springframework.context.ApplicationEvent;

import java.util.Date;

public class ApiDisconnectedEvent extends ApplicationEvent {

	ApiDisconnectedEvent() {
		super(new Date());
	}

	@Override
	public Date getSource() {
		return (Date) super.getSource();
	}

}
