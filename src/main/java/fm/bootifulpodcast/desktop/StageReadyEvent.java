package fm.bootifulpodcast.desktop;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

class StageReadyEvent extends ApplicationEvent {

	StageReadyEvent(Stage stage) {
		super(stage);
	}

	@Override
	public Stage getSource() {
		return (Stage) super.getSource();
	}

}
