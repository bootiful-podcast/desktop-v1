package fm.bootifulpodcast.desktop;

import javafx.stage.Stage;
import org.springframework.context.ApplicationEvent;

public class StageReadyEvent extends GenericApplicationEvent<Stage> {

	public StageReadyEvent(Stage stage) {
		super(stage);
	}

}
