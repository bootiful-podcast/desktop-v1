package fm.bootifulpodcast.desktop;

import fm.bootifulpodcast.desktop.client.*;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import lombok.SneakyThrows;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@Component
public class UploadController {

	private final AtomicInteger rowCount = new AtomicInteger(0);

	private final AtomicReference<File> introductionFile = new AtomicReference<>();

	private final AtomicReference<File> interviewFile = new AtomicReference<>();

	private final Executor executor;

	private final Locale locale;

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

	private final String dropTheMediaOnThePanelBundleCode = "drop-the-media-on-the-panel";

	private Label introductionLabel, interviewLabel;

	private final String interviewDandDText;

	private final String introductionDandDText;

	private final String pleaseSpecifyAFileLabelText;

	private final String publishButtonText;

	private final String interviewLabelText;

	private final String descriptionLabelText;

	private final String introductionLabelText;

	private final String newPodcastText;

	private final ApplicationEventPublisher publisher;

	private final AtomicBoolean connected = new AtomicBoolean();

	private final ApiClient client;

	private final MessageSource messageSource;

	private final ImageView connectedImageView = imageViewForResource(
			new ClassPathResource("images/connected-icon.png"));

	private final ImageView disconnectedImageView = imageViewForResource(
			new ClassPathResource("images/disconnected-icon.png"));

	@FXML
	public Label filePromptLabel;

	@FXML
	public Label descriptionLabel;

	@FXML
	public Node dropTarget;

	@FXML
	public Label connectedIcon;

	UploadController(Locale locale, ApiClient client, Executor executor,
			ApplicationEventPublisher publisher, MessageSource messageSource) {

		var emptyArgs = new Object[0];

		this.executor = executor;
		this.client = client;
		this.locale = locale;
		this.messageSource = messageSource;
		this.publisher = publisher;
		this.publishButtonText = messageSource.getMessage("publish", emptyArgs,
				this.locale);
		this.pleaseSpecifyAFileLabelText = messageSource.getMessage("no-file-specified",
				emptyArgs, this.locale);
		this.newPodcastText = messageSource.getMessage("new-podcast", emptyArgs,
				this.locale);
		this.introductionLabelText = messageSource.getMessage("introduction-media",
				emptyArgs, this.locale);
		this.interviewLabelText = messageSource.getMessage("interview-media", emptyArgs,
				this.locale);
		this.descriptionLabelText = messageSource.getMessage("description-prompt",
				emptyArgs, this.locale);
		this.introductionDandDText = this.messageSource.getMessage(
				this.dropTheMediaOnThePanelBundleCode,
				new Object[] { this.interviewLabelText }, this.locale);
		this.interviewDandDText = this.messageSource.getMessage(
				this.dropTheMediaOnThePanelBundleCode,
				new Object[] { this.interviewLabelText }, this.locale);
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
		log.info("are all forms set? " + allMatch);
		var connected = this.connected.get();
		log.debug("connected? " + connected);
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

	private void updateFilePromptAfterDandD(Label fileLabel, String promptLabelText,
			AtomicReference<File> fileAtomicReference, File file) {
		fileAtomicReference.set(file);
		fileLabel.setText(file.getAbsolutePath());
		this.filePromptLabel.setText(promptLabelText);
		this.publisher.publishEvent(new FormManipulationEvent(file));
	}

	private void handlePublish() {
		log.info(String.format(
				"ready to publish! we have an introduction media asset (%s) and an interview media asset (%s) and a description: %s",
				this.introductionFile.get().getAbsolutePath(),
				this.interviewFile.get().getAbsolutePath(), this.description.getText()));

		var uuid = UUID.randomUUID().toString();
		var podcast = new PodcastArchiveBuilder(this.description.getText(), uuid);
		var interviewFileExt = FileUtils.extensionFor(this.interviewFile.get());
		var introductionFileExt = FileUtils.extensionFor(this.introductionFile.get());
		Assert.notNull(interviewFileExt, "the interview extension must not be null");
		Assert.notNull(introductionFileExt,
				"the introduction extension must not be null");
		Assert.isTrue(interviewFileExt.equalsIgnoreCase(introductionFileExt),
				"the introduction file type and the interview file type must be the same");
		var builder = podcast.addMedia(interviewFileExt, introductionFile.get(),
				interviewFile.get());
		var archive = builder.build();
		var productionStatus = this.client.beginProduction(uuid, archive);
		var uriCompletableFuture = productionStatus.checkProductionStatus();
		uriCompletableFuture.thenAccept(uri -> log.info("the produced MP3 is " + uri));
	}

	public void discardPodcast() {
		this.connectedIcon.setGraphic(connectedImageView);
		this.newPodcast.setText(this.newPodcastText);
		this.publish.setText(this.publishButtonText);
		this.filePromptLabel.setText(
				this.messageSource.getMessage(this.dropTheMediaOnThePanelBundleCode,
						new Object[] { this.introductionLabelText }, this.locale));
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
					this.updateFilePromptAfterDandD(this.introductionLabel,
							this.introductionDandDText, this.introductionFile,
							files.get(0));
				}
				else if (this.interviewFile.get() == null) {
					this.updateFilePromptAfterDandD(this.interviewLabel,
							this.interviewDandDText, this.interviewFile, files.get(0));
				}
				success = true;
			}
			event.setDropCompleted(success);
			event.consume();
		});
		this.introductionLabel = this.newRow(this.nextRow(), this.introductionLabelText);
		this.interviewLabel = this.newRow(this.nextRow(), this.interviewLabelText);
		this.newPodcast.setOnMouseClicked(mouseEvent -> this.discardPodcast());
		this.discardPodcast();
	}

	private int nextRow() {
		return rowCount.getAndIncrement();
	}

	private Label newRow(int rowNumber, String label) {
		var description = new Label(label);
		var file = new Label();
		file.setText(this.pleaseSpecifyAFileLabelText);
		file.setStyle("color: red");
		file.setPadding(new Insets(0, 0, 0, 10));
		this.filesGridPane.add(description, 0, rowNumber, 1, 1);
		this.filesGridPane.add(file, 1, rowNumber, 3, 1);
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
