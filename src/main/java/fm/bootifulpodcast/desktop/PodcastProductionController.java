package fm.bootifulpodcast.desktop;

import fm.bootifulpodcast.desktop.client.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.FileCopyUtils;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;

@Log4j2
@Component
public class PodcastProductionController {

	private final AtomicInteger rowCount = new AtomicInteger(0);

	private final AtomicReference<File> introductionFile = new AtomicReference<>();

	private final AtomicReference<File> interviewFile = new AtomicReference<>();

	private final AtomicReference<Stage> stage = new AtomicReference<>();

	private final Executor executor;

	private final String interviewDandDText;

	private final String introductionDandDText;

	private final String pleaseSpecifyAFileLabelText;

	private final String publishButtonText;

	private final String interviewLabelText;

	private final String descriptionLabelText;
	private final String pleaseChooseFileLabelText;
	private final String introductionLabelText;
	private final String newPodcastText;

	private final ApplicationEventPublisher publisher;

	private final AtomicBoolean connected = new AtomicBoolean();

	private final ApiClient client;

	private final Messages messages;

	private final ImageView connectedImageView;

	private final String hyperlinkText;

	private final ImageView disconnectedImageView;

	private final AtomicReference<URI> uri = new AtomicReference<>();

	@FXML
	public Button newPodcast;

	@FXML
	public HBox buttons;

	@FXML
	public Button publish;

	@FXML
	public GridPane filesGridPane;

	@FXML
	public TextArea description;

	@FXML
	public Label filePromptLabel;

	@FXML
	public Label descriptionLabel;

	@FXML
	public Node dropTarget;

	@FXML
	public Label connectedIcon;

	private Label introductionLabel, interviewLabel;

	private Hyperlink hyperlink;

	private final int padding = 10;


	PodcastProductionController(ApiClient client, Executor executor,
																													ApplicationEventPublisher publisher, Messages messages) {

		this.executor = executor;
		this.client = client;
		this.messages = messages;
		this.publisher = publisher;

		this.disconnectedImageView = this.imageViewForResource(
			new ClassPathResource("images/disconnected-icon.png"));
		this.connectedImageView = this
			.imageViewForResource(new ClassPathResource("images/connected-icon.png"));

		this.pleaseChooseFileLabelText = messages.getMessage("choose-a-file");
		this.publishButtonText = messages.getMessage("publish");
		this.pleaseSpecifyAFileLabelText = messages.getMessage("no-file-specified");
		this.newPodcastText = messages.getMessage("new-podcast");
		this.introductionLabelText = messages.getMessage("introduction-media");
		this.interviewLabelText = messages.getMessage("interview-media");
		this.descriptionLabelText = messages.getMessage("description-prompt");

		var dropTheMediaOnThePanelBundleCode = "drop-the-media-on-the-panel";
		this.introductionDandDText = messages.getMessage(dropTheMediaOnThePanelBundleCode,
			this.introductionLabelText);
		this.interviewDandDText = messages.getMessage(dropTheMediaOnThePanelBundleCode,
			this.interviewLabelText);
		this.hyperlinkText = messages.getMessage("production-media-is-done");
	}

	@EventListener
	public void stageReady(StageReadyEvent stageReadyEvent) {
		this.stage.set(stageReadyEvent.getSource());
	}

	@EventListener(FormManipulationEvent.class)
	public void handleInputUpdates() {
		this.repaint();
	}

	private void repaint() {
		var text = this.description.getText();
		var dirtyTracker = Arrays.asList(StringUtils.hasText(text.trim()),
			this.interviewFile.get() != null, this.introductionFile.get() != null);
		var allMatch = dirtyTracker.stream().allMatch(p -> p);
		var connected = this.connected.get();
		var formFilledAndConnected = allMatch && connected;
		this.publish.setDisable(!formFilledAndConnected);
		this.newPodcast.setDisable(dirtyTracker.stream().noneMatch(p -> p));
		if (this.connected.get()) {
			this.connectedIcon.setGraphic(this.connectedImageView);
		}
		else {
			this.connectedIcon.setGraphic(this.disconnectedImageView);
		}
	}

	private void updateFilePromptAfterDnD(Label fileLabel, String promptLabelText,
																																							AtomicReference<File> fileAtomicReference, File file) {
		fileAtomicReference.set(file);
		fileLabel.setText(file.getAbsolutePath());
		this.filePromptLabel.setText(promptLabelText);
		this.publisher.publishEvent(new FormManipulationEvent(file));
	}

	private void handlePublish() {
		var introFile = this.introductionFile.get();
		var interviewFile = this.interviewFile.get();
		var descriptionText = this.description.getText();
		var uuid = UUID.randomUUID().toString();

		log.debug(String.format("ready to publish! we have an introduction media "
				+ "asset (%s) and an interview media asset (%s) and a description: %s and a UID: %s",
			introFile.getAbsolutePath(), interviewFile.getAbsolutePath(), descriptionText, uuid));

		// todo hotel wifi is garbage. so, instead of actually hitting
		//  the microservice on my local computer, i'm gonna spin up
		//  an async thread and call the same method with the callback
		//  just to allow me to get on with the work of fixing the UI
		//  once the publish button is submitted.
		var ui = true;
		if (ui)
			this.executor.execute(new Runnable() {

				@Override
				@SneakyThrows
				public void run() {
					log.info("fake async thing to take up time so the UI can fade out");
					Thread.sleep(5_000);
					handleProducedMediaURI(URI.create("http://localhost:8080/podcasts/526fdf4e-3747-4ff9-b423-c981405f10f0/output"));
				}
			});

		if (!ui) {

			var podcast = new PodcastArchiveBuilder(descriptionText, uuid);
			var interviewFileExt = FileUtils.extensionFor(interviewFile);
			var introductionFileExt = FileUtils.extensionFor(introFile);
			Assert.notNull(interviewFileExt, "the interview extension must not be null");
			Assert.notNull(introductionFileExt,
				"the introduction extension must not be null");
			Assert.isTrue(interviewFileExt.equalsIgnoreCase(introductionFileExt),
				"the introduction file type and the interview file type must be the same");
			var builder = podcast.addMedia(interviewFileExt, introFile, interviewFile);
			var archive = builder.build();
			var productionStatus = this.client.beginProduction(uuid, archive);
			productionStatus.checkProductionStatus().thenAccept(this::handleProducedMediaURI);
		}
	}

	private void handleProducedMediaURI(URI uri) {
		log.debug("URI for the media has returned " + uri.toString());
		this.uri.set(uri);
		Platform.runLater(() -> this.hyperlink.setText(this.hyperlinkText));
	}

	private void updateDescription(String descriptionLabelText) {
		this.description.setText(descriptionLabelText);
		this.repaint();
	}


	private void discardPodcast() {
		this.connectedIcon.setGraphic(this.connectedImageView);
		this.newPodcast.setText(this.newPodcastText);
		this.publish.setText(this.publishButtonText);
		this.filePromptLabel.setText(this.introductionDandDText);
		this.publish.setDisable(true);
		this.description.setText("");
		this.interviewFile.set(null);
		this.introductionFile.set(null);
		this.interviewLabel.setText(this.pleaseSpecifyAFileLabelText);
		this.introductionLabel.setText(this.pleaseSpecifyAFileLabelText);
		this.descriptionLabel.setText(this.descriptionLabelText);
		this.newPodcast.setDisable(true);
	}

	@SneakyThrows
	private ImageView imageViewForResource(Resource resource) {
		try (var in = resource.getInputStream()) {
			ImageView imageView = new ImageView(new Image(in));
			imageView.setSmooth(true);
			imageView.setPreserveRatio(true);
			imageView.setFitHeight(30);
			return imageView;
		}
	}

	@FXML
	@SneakyThrows
	public void initialize() {

		this.publish
			.setOnMouseClicked(mouseEvent -> executor.execute(this::handlePublish));
		this.description.setOnKeyTyped(keyEvent -> this.repaint());
		this.dropTarget.setOnDragOver(event -> {
			if (event.getGestureSource() != this.dropTarget
				&& event.getDragboard().hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
			event.consume();
		});
		this.dropTarget.setOnDragDropped(event -> {
			var eventDragboard = event.getDragboard();
			var success = false;
			if (eventDragboard.hasFiles()) {
				var files = eventDragboard.getFiles();
				Assert.isTrue(files != null && files.size() <= 1,
					"there must be only one file");
				if (this.introductionFile.get() == null) {
					updateIntroductionFile(files.get(0));
				}
				else if (this.interviewFile.get() == null) {
					updateInterviewFile(files.get(0));
				}
				success = true;
			}
			event.setDropCompleted(success);
			event.consume();
		});

		this.introductionLabel = this.newRow(this.introductionLabelText, this::updateIntroductionFile);
		this.interviewLabel = this.newRow(this.interviewLabelText, this::updateInterviewFile);
		this.hyperlink = this.registerHyperlink();
		this.newPodcast.setOnMouseClicked(mouseEvent -> this.discardPodcast());
		this.hyperlink.setOnAction(actionEvent -> {

			var currentURI = uri.get();
			Assert.notNull(currentURI, "the URI to download must not be null");

			var extFilter = new FileChooser.ExtensionFilter(
				messages.getMessage("save-dialog-extension-filter-description"),
				"*.mp3", "*.wav");
			var fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(extFilter);

			var stage = this.stage.get();
			Assert.notNull(stage, "the stage must have been set");
			var file = fileChooser.showSaveDialog(stage);
			if (null != file) {
				log.debug("you've selected " + file.getAbsolutePath() + ".");
				this.executor
					.execute(() -> this.downloadMediaFileToFile(currentURI, file));
			}
		});

		this.filesGridPane.setVgap(this.padding);
		this.filesGridPane.setHgap(this.padding);

		this.discardPodcast();

		//todo remove this!!!! it's only for development
		if (false)
			this.loadPodcastForm(new File("/Users/joshlong/Desktop/sample-podcast/1-oleg-intro.mp3"),
				new File("/Users/joshlong/Desktop/sample-podcast/2-oleg-interview-lower.mp3"), "In this interview Josh Long (@starbuxman) talks to Oleg Zhurakousky.");
	}

	private void loadPodcastForm(File intro, File interview, String desc) {
		this.updateIntroductionFile(intro);
		this.updateInterviewFile(interview);
		this.updateDescription(desc);
	}

	private void updateIntroductionFile(File intro) {
		this.updateFilePromptAfterDnD(this.introductionLabel,
			this.introductionDandDText, this.introductionFile, intro);
	}

	private void updateInterviewFile(File file) {
		this.updateFilePromptAfterDnD(this.interviewLabel,
			this.interviewDandDText, this.interviewFile, file);
	}

	@SneakyThrows
	private void downloadMediaFileToFile(URI mediaUri, File file) {
		var urlResource = new UrlResource(mediaUri);
		try (var inputStream = urlResource.getInputStream();
							var outputStream = new FileOutputStream(file)) {
			FileCopyUtils.copy(inputStream, outputStream);
			log.debug("downloaded " + mediaUri.toString() + " to "
				+ file.getAbsolutePath() + "!");
		}
	}

	private Hyperlink registerHyperlink() {
		var rowNumber = this.nextRow();
		var hyperlink = new Hyperlink();
		hyperlink.setPadding(new Insets(0, 0, 0, 10));
		this.filesGridPane.add(hyperlink, 1, rowNumber, 3, 1);
		return hyperlink;
	}

	private int nextRow() {
		return rowCount.getAndIncrement();
	}

	private Label newRow(String label, Consumer<File> onceFileHasBeenSpecified) {

		var description = new Label(label);

		var file = new Label(this.pleaseSpecifyAFileLabelText);

		var button = new Button(this.pleaseChooseFileLabelText);
		button.setOnMouseClicked(mouseEvent -> {
			var fileChooser = new FileChooser();
			var selectedFile = fileChooser.showOpenDialog(stage.get());
			if (null != selectedFile) {
				onceFileHasBeenSpecified.accept(selectedFile);
			}
		});

		var rowNumber = nextRow();
		this.filesGridPane.add(description, 0, rowNumber, 1, 1);
		this.filesGridPane.add(button, 1, rowNumber, 1, 1);
		this.filesGridPane.add(file, 2, rowNumber, 3, 1);

//		this.filesGridPane.add(new BorderPane(), 0, nextRow(),  5, 1);

		return file;
	}

	@EventListener(ApiConnectedEvent.class)
	public void connected() {
		this.connected.set(true);
		Platform.runLater(this::repaint);
	}

	@EventListener(ApiDisconnectedEvent.class)
	public void disconnected() {
		this.connected.set(false);
		Platform.runLater(this::repaint);
	}

}
