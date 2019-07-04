package fm.bootifulpodcast.desktop;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class StageStoppedEvent extends ApplicationEvent {

	public StageStoppedEvent(Stage stage) {
		super(stage);
	}

	@Override
	public Stage getSource() {
		return (Stage) super.getSource();
	}

}
