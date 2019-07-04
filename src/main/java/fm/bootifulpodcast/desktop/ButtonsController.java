package fm.bootifulpodcast.desktop;

import fm.bootifulpodcast.desktop.client.ApiClient;
import fm.bootifulpodcast.desktop.client.ApiConnectedEvent;
import fm.bootifulpodcast.desktop.client.ApiDisconnectedEvent;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@Component
public class ButtonsController implements Initializable {

	public Label connectedIcon;

	private final ImageView connectedImageView, disconnectedImageView;

	private final AtomicBoolean connected = new AtomicBoolean(false);

	private final AtomicReference<PodcastModel> podcast = new AtomicReference<>();

	private final ApiClient client;

	private final ReadyFileHandler handler;

	public Button newPodcastButton, publishButton, saveMediaToFileButton;

	private final AtomicReference<URI> fileUri = new AtomicReference<URI>();

	private final AtomicReference<Stage> stage = new AtomicReference<Stage>();

	public HBox buttons;

	public VBox root;

	ButtonsController(ApiClient client, ReadyFileHandler handler) {
		this.client = client;
		this.handler = handler;
		this.disconnectedImageView = FxUtils.buildImageViewFromResource(
				new ClassPathResource("images/disconnected-icon.png"));
		this.connectedImageView = FxUtils.buildImageViewFromResource(
				new ClassPathResource("images/connected-icon.png"));
		List.of(this.disconnectedImageView, this.connectedImageView)
				.forEach(img -> img.setFitHeight(30));
	}

	@EventListener
	public void stageIsReady(StageReadyEvent sre) {
		this.stage.set(sre.getSource());
	}

	private void updateConnectedIcon(ImageView iv) {
		Platform.runLater(() -> this.connectedIcon.setGraphic(iv));
	}

	@EventListener
	public void disconnected(ApiDisconnectedEvent e) {
		log.info("disconnected (" + e.getClass().getName() + ")");
		this.connected.set(false);
		this.updateConnectedIcon(this.disconnectedImageView);
		this.evaluatePublishButtonState();
	}

	@EventListener
	public void connected(ApiConnectedEvent e) {
		log.info("connected (" + e.getClass().getName() + ")");
		this.connected.set(true);
		this.updateConnectedIcon(this.connectedImageView);
		this.evaluatePublishButtonState();
	}

	@EventListener
	public void invalidPodcast(PodcastValidationFailedEvent pvfe) {
		log.debug("the podcast is invalid.");
		this.podcast.set(null);
		this.evaluatePublishButtonState();
	}

	@EventListener
	public void productionStarted(PodcastProductionStartedEvent ppse) {
		Platform.runLater(() -> {
			this.all.forEach(b -> b.setDisable(true));
			this.resetButtonsUi();
			this.buttons.getChildren().addAll(this.visibleDuringProcessing);

		});
	}

	@EventListener
	public void productionFinished(PodcastProductionCompletedEvent ppce) {
		this.fileUri.set(ppce.getSource().getMedia());
		Platform.runLater(() -> {
			this.resetButtonsUi();
			var nodeList = this.visibleAfterProcessing;
			nodeList.forEach(n -> n.setDisable(false));
			this.buttons.getChildren().addAll(nodeList);
		});
	}

	@EventListener
	public void validPodcast(PodcastValidationSuccessEvent pvse) {
		log.debug("the podcast is valid.");
		this.podcast.set(pvse.getSource());
		this.evaluatePublishButtonState();
	}

	private void resetButtonsUi() {
		this.buttons.getChildren().clear();
	}

	private void evaluatePublishButtonState() {
		var canPublish = (this.connected.get() && this.podcast.get() != null);
		this.publishButton.setDisable(!canPublish);
	}

	private final List<Node> all = new ArrayList<>();

	private final List<Node> visibleDuringForm = new ArrayList<>();

	private final List<Node> visibleDuringProcessing = new ArrayList<>();

	private final List<Node> visibleAfterProcessing = new ArrayList<>();

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

		this.all.addAll(List.of(this.newPodcastButton, this.saveMediaToFileButton,
				this.publishButton));
		this.visibleAfterProcessing
				.addAll(List.of(this.newPodcastButton, this.saveMediaToFileButton));
		this.visibleDuringProcessing.addAll(List.of());
		this.visibleDuringForm.addAll(List.of(this.newPodcastButton, this.publishButton));

		this.resetButtonsUi();
		this.buttons.getChildren().addAll(this.visibleDuringForm);

		//
		this.all.forEach(b -> b.setDisable(true));
		this.newPodcastButton.setDisable(false);
		this.connectedIcon.setGraphic(this.disconnectedImageView);
		this.publishButton.setOnMouseClicked(e -> {
			var model = this.podcast.get();
			var uuid = UUID.randomUUID().toString();
			this.client.produce(uuid, model.titleProperty().get(),
					model.descriptionProperty().get(),
					model.introductionFileProperty().get(),
					model.interviewFileProperty().get());
		});
		this.saveMediaToFileButton.setOnMouseClicked(
				e -> this.handler.handle(this.stage.get(), this.fileUri.get()));

	}

}
