package fm.bootifulpodcast.desktop;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Arrays;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


@Log4j2
@Component
public class UploadController {

	private final AtomicInteger rowCount = new AtomicInteger(0);
	private final AtomicReference<File> introductionFile = new AtomicReference<>();
	private final AtomicReference<File> interviewFile = new AtomicReference<>();

	private final Locale locale;

	@FXML
	public Button newPodcast;

	@FXML
	public HBox buttons;

	@FXML
	public VBox dropTarget;

	@FXML
	public Button publish;

	@FXML
	public GridPane filesGridPane;

	@FXML
	public TextArea description;
	private final String dropTheMediaOnThePanelBundleCode = "drop-the-media-on-the-panel";

	private Label introductionLabel, interviewLabel;
	private final String pleaseSpecifyAFileLabelText;
	private final String publishButtonText;
	private final String interviewLabelText;
	private final String descriptionLabelText;
	private final String introductionLabelText;
	private final String newPodcastText;
	private final ApplicationEventPublisher publisher;
	private final MessageSource messageSource;
	@FXML
	public Label filePromptLabel;
	@FXML
	public Label descriptionLabel;

	UploadController(Locale locale,
																		ApplicationEventPublisher publisher,
																		MessageSource messageSource) {

		var emptyArgs = new Object[0];

		this.locale = locale;
		this.messageSource = messageSource;
		this.publisher = publisher;
		this.publishButtonText = messageSource.getMessage("publish", emptyArgs, this.locale);
		this.pleaseSpecifyAFileLabelText = messageSource.getMessage("no-file-specified", emptyArgs, this.locale);
		this.newPodcastText = messageSource.getMessage("new-podcast", emptyArgs, this.locale);
		this.introductionLabelText = messageSource.getMessage("introduction-media", emptyArgs, this.locale);
		this.interviewLabelText = messageSource.getMessage("interview-media", emptyArgs, this.locale);
		this.descriptionLabelText = messageSource.getMessage("description-prompt", emptyArgs, this.locale);
	}

	@EventListener(FormManipulationEvent.class)
	void checkIfCanPublish() {

		var text = this.description.getText();
		var dirtyTracker = Arrays.asList(
			StringUtils.hasText(text),
			this.interviewFile.get() != null,
			this.introductionFile.get() != null
		);
		this.publish.setDisable(!dirtyTracker.stream().allMatch(p -> p));
		this.newPodcast.setDisable(dirtyTracker.stream().noneMatch(p -> p));
	}

	private void handleIntroductionFileDandD(File file) {
		this.introductionFile.set(file);
		this.introductionLabel.setText(file.getAbsolutePath());
		this.filePromptLabel.setText(this.messageSource
			.getMessage(this.dropTheMediaOnThePanelBundleCode, new Object[]{this.interviewLabelText}, this.locale));
		this.publisher.publishEvent(new FormManipulationEvent(file));
	}

	private void handleInterviewFileDandD(File file) {
		this.interviewFile.set(file);
		this.interviewLabel.setText(file.getAbsolutePath());
		this.publisher.publishEvent(new FormManipulationEvent(file));
	}

	private void handlePublish() {
		log.info(String.format("ready to publish! we have " +
				"an introduction media asset (%s) and an " +
				"interview media asset (%s) and a description: %s",
			this.introductionFile.get().getAbsolutePath(),
			this.interviewFile.get().getAbsolutePath(),
			this.description.getText()
		));
	}

	public void discardPodcast() {
		this.newPodcast.setText(this.newPodcastText);
		this.publish.setText(this.publishButtonText);
		this.filePromptLabel.setText(this.messageSource
			.getMessage(this.dropTheMediaOnThePanelBundleCode, new Object[]{this.introductionLabelText}, this.locale));
		this.publish.setDisable(true);
		this.description.setText("");
		this.interviewFile.set(null);
		this.introductionFile.set(null);
		this.interviewLabel.setText(this.pleaseSpecifyAFileLabelText);
		this.introductionLabel.setText(this.pleaseSpecifyAFileLabelText);
		this.descriptionLabel.setText(this.descriptionLabelText);
		this.newPodcast.setDisable(true);
	}

	@FXML
	public void initialize() {
		this.publish.setOnMouseClicked(mouseEvent -> this.handlePublish());
		this.description.setOnKeyTyped(keyEvent -> this.checkIfCanPublish());
		this.dropTarget.setOnDragOver(event -> {
			if (event.getGestureSource() != this.dropTarget && event.getDragboard().hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
			event.consume();
		});
		this.dropTarget.setOnDragDropped(event -> {
			var eventDragboard = event.getDragboard();
			var success = false;
			if (eventDragboard.hasFiles()) {
				var files = eventDragboard.getFiles();
				Assert.isTrue(files != null && files.size() <= 1, "there must be only one file");
				if (this.introductionFile.get() == null) {
					this.handleIntroductionFileDandD(files.get(0));
				}
				else if (this.interviewFile.get() == null) {
					this.handleInterviewFileDandD(files.get(0));
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
		file.setStyle("color: gray");
		file.setPadding(new Insets(0, 0, 0, 10));
		this.filesGridPane.add(description, 0, rowNumber, 1, 1);
		this.filesGridPane.add(file, 1, rowNumber, 3, 1);
		return file;
	}
}


// Published whenever any aspect of the UI
// form is updated (new file uploaded, text entered)
//
class FormManipulationEvent extends ApplicationEvent {

	FormManipulationEvent(Object source) {
		super(source);
	}
}