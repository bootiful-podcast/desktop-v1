package fm.bootifulpodcast.desktop;

import javafx.stage.Stage;

public class StageStoppedEvent extends GenericApplicationEvent<Stage> {

	public StageStoppedEvent(Stage stage) {
		super(stage);
	}

}
