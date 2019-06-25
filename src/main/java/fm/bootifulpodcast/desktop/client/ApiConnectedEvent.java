package fm.bootifulpodcast.desktop.client;

import org.springframework.context.ApplicationEvent;

import java.util.Date;

public class ApiConnectedEvent extends ApplicationEvent {

	ApiConnectedEvent() {
		super(new Date());
	}

	@Override
	public Date getSource() {
		return (Date) super.getSource();
	}

}
