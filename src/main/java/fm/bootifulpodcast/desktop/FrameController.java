package fm.bootifulpodcast.desktop;

import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.Executor;

@Log4j2
@Component
public class FrameController implements Initializable {

	public Node progressScreen;

	public Node form;

	public VBox activePanel;

	public ImageView photoImageView;

	@EventListener
	public void stageReady(StageReadyEvent sre) {
		log.info("stage is ready");
	}

	@EventListener
	public void stageFinished(StageStoppedEvent sse) {
		log.info("stage is finished");
	}

	@EventListener
	public void newPodcast(PodcastLoadEvent ple) {
		show(this.form);
	}

	@EventListener
	public void podcastCompleted(PodcastProductionCompletedEvent ding) {

		log.info("the podcast has been completed and is available for download at "
				+ ding.getSource().getMedia());
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {
		show(this.form);
	}

	@EventListener
	public void publicationInProgress(PodcastProductionStartedEvent pse) {
		show(this.progressScreen);
	}

	private void show(Node node) {
		if (null != node) {
			Platform.runLater(() -> {
				this.activePanel.getChildren().clear();
				this.activePanel.getChildren().add(node);
			});
		}
	}

}