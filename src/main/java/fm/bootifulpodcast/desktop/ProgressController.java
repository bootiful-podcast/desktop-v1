package fm.bootifulpodcast.desktop;

import fm.bootifulpodcast.desktop.client.ApiClient;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicReference;

@Component
public class ProgressController implements Initializable, EventHandler<MouseEvent> {

	private final Messages messages;

	private final AtomicReference<URI> uri = new AtomicReference<URI>();

	private final AtomicReference<Stage> stage = new AtomicReference<Stage>();

	private final ApiClient client;

	public Label processingLabel;

	public Hyperlink downloadMediaHyperlink;

	public ImageView processingImage;

	public VBox root;

	private final ReadyFileHandler handler;

	private final List<Node> loading = new ArrayList<>();

	private final List<Node> loaded = new ArrayList<>();

	public ProgressController(Messages messages, ReadyFileHandler rfh, ApiClient client) {
		this.messages = messages;
		this.handler = rfh;
		this.client = client;
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

		this.processingLabel.setText(this.messages.getMessage("processing-status"));
		this.processingImage.setImage(FxUtils
				.buildImageFromResource(new ClassPathResource("images/loading.gif")));

		this.downloadMediaHyperlink
				.setText(this.messages.getMessage(getClass(), "click-to-download"));
		this.downloadMediaHyperlink.setVisible(false);
		this.downloadMediaHyperlink.setOnMouseClicked(this);
		Platform.runLater(() -> this.show(this.loading));
		this.loaded.add(this.downloadMediaHyperlink);
		this.loading.addAll(List.of(this.processingLabel, this.processingImage));
	}

	@EventListener
	public void processingCompleted(PodcastProductionCompletedEvent completed) {
		this.uri.set(completed.getSource().getMedia());
		Platform.runLater(() -> {
			this.show(this.loaded);
		});
	}

	private void show(Collection<Node> nodes) {
		this.root.getChildren().clear();
		nodes.forEach(n -> n.setVisible(true));
		this.root.getChildren().addAll(nodes);
	}

	@EventListener
	public void stageIsReady(StageReadyEvent readyEvent) {
		this.stage.set(readyEvent.getSource());
	}

	@Override
	public void handle(MouseEvent mouseEvent) {
		var actualStage = this.stage.get();
		var resolvedUri = this.uri.get();
		handler.handle(actualStage, resolvedUri);
	}

}
