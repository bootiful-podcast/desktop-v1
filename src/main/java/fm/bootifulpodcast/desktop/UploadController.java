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
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.File;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;


@Log4j2
@Component
public class UploadController {

	private final AtomicInteger rowCount = new AtomicInteger(0);
	private final AtomicReference<File> introductionFile = new AtomicReference<>();
	private final AtomicReference<File> interviewFile = new AtomicReference<>();

	@FXML
	public Label prompt;

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


	private final Locale locale = Locale.getDefault();
	private Label introductionLabel, interviewLabel;
	private final String interviewLabelText;
	private final String introductionLabelText;
	private final String dropTheMediaOnToPanelText;

	public UploadController(MessageSource messageSource) {


		this.introductionLabelText = messageSource.getMessage("introduction-media",
			new Object[0],
			this.locale);

		this.interviewLabelText = messageSource.getMessage("interview-media",
			new Object[0],
			this.locale);

		this.dropTheMediaOnToPanelText = messageSource.getMessage("drop-the-media-on-the-panel",
			new Object[0],
			this.locale);

	}

	void checkIfCanPublish() {
		var text = this.description.getText();
		var shouldBeEnabled = StringUtils.hasText(text) && this.interviewFile.get() != null && this.introductionFile.get() != null;
		publish.setDisable(!shouldBeEnabled);

	}

	void handleIntroductionFileDandD(File file) {
		this.introductionFile.set(file);
		this.introductionLabel.setText(file.getAbsolutePath());
		this.interviewLabel = this.newRow(nextRow(), this.interviewLabelText);
		this.prompt.setText(String.format(this.dropTheMediaOnToPanelText, this.interviewLabelText));
		checkIfCanPublish();
	}

	void handleInterviewFileDandD(File file) {
		this.interviewFile.set(file);
		this.interviewLabel.setText(file.getAbsolutePath());
		checkIfCanPublish();
	}

	void handlePublish() {
		log.info(String.format("ready to publish! we have an introduction media asset (%s) and an " +
				"interview media asset (%s) and a description: %s",
			this.introductionFile.get().getAbsolutePath(),
			this.interviewFile.get().getAbsolutePath(),
			this.description.getText()
		));

	}

	@FXML
	public void initialize() {

		this.publish.setOnMouseClicked(mouseEvent -> this.handlePublish());
		this.description.setOnKeyTyped(keyEvent -> this.checkIfCanPublish());
		this.dropTarget.setOnDragOver(event -> {
			if (event.getGestureSource() != dropTarget && event.getDragboard().hasFiles()) {
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
					handleIntroductionFileDandD(files.get(0));
				}
				else if (this.interviewFile.get() == null) {
					handleInterviewFileDandD(files.get(0));
				}
				success = true;
			}
			event.setDropCompleted(success);
			event.consume();
		});
		this.introductionLabel = this.newRow(nextRow(), this.introductionLabelText);
		this.prompt.setText(String.format(this.dropTheMediaOnToPanelText, this.introductionLabelText));
	}

	private int nextRow() {
		return rowCount.getAndIncrement();
	}

	private Label newRow(int rowNumber, String label) {
		var description = new Label(label);

		var file = new Label("file://");
		file.setPadding(new Insets(0, 0, 0, 10));
		filesGridPane.add(description, 0, rowNumber, 1, 1);
		filesGridPane.add(file, 1, rowNumber, 3, 1);
		return file;
	}


}
