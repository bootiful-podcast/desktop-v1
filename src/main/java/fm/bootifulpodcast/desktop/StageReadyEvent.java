package fm.bootifulpodcast.desktop;

import javafx.stage.Stage;

public class StageReadyEvent extends GenericApplicationEvent<Stage> {

	public StageReadyEvent(Stage stage) {
		super(stage);
	}

}
