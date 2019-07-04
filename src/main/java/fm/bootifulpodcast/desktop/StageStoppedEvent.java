package fm.bootifulpodcast.desktop;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class StageStoppedEvent extends GenericApplicationEvent<Stage> {

	public StageStoppedEvent(Stage stage) {
		super(stage);
	}

}
