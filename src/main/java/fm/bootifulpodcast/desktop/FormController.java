package fm.bootifulpodcast.desktop;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.util.function.Tuple4;
import reactor.util.function.Tuples;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@Component
public class FormController implements Initializable {

	public Label interviewFileLabel;

	public Label photoLabel;

	public Label introFileLabel;

	public Label titlePromptLabel;

	public Label descriptionPromptLabel;

	public TextArea description;

	public TextField title;

	public Label introLabel;

	public Label photoFileLabel;

	public Label interviewLabel;

	public Label filePromptLabel;

	public Button introFileChooserButton;

	public Button photoFileChooserButton;

	public Button interviewFileChooserButton;

	public ImageView photoImageView;

	private final AtomicReference<Stage> stage = new AtomicReference<>();

	private final AtomicBoolean valid = new AtomicBoolean(false);

	private final PodcastModel podcastModel = new PodcastModel();

	private final Messages messages;

	private final ApplicationEventPublisher publisher;

	private final FileChooser mp3FileChooser, imageFileChooser;

	FormController(Messages messages, ApplicationEventPublisher publisher) {
		this.messages = messages;
		this.publisher = publisher;
		this.imageFileChooser = this.initializeFileChooseFor("*.jpg");
		this.mp3FileChooser = this.initializeFileChooseFor("*.mp3");
	}

	private FileChooser initializeFileChooseFor(String... exts) {
		var fileChooser = new FileChooser();
		fileChooser.setTitle(messages.getMessage(getClass(), "file-chooser-title"));
		fileChooser.getExtensionFilters().add(
				new FileChooser.ExtensionFilter(messages.getMessage(getClass(), "file-chooser-description"), exts));
		return fileChooser;
	}

	@EventListener
	public void stageIsReady(StageReadyEvent sre) {
		this.stage.set(sre.getSource());
	}

	private void onChange(Object o) {
		PodcastModel model = this.podcastModel;
		var intro = model.introductionFileProperty().get();
		var interview = model.interviewFileProperty().get();
		var description = model.descriptionProperty().get();
		var profilePhoto = model.photoFileProperty().get();
		var title = model.titleProperty().get();
		var wasValidBefore = this.valid.get();
		var isValidNow = (StringUtils.hasText(title) && StringUtils.hasText(description) && intro != null
				&& interview != null && profilePhoto != null);
		this.valid.set(isValidNow);
		this.repaintProfilePhotoFile(model.photoFileProperty().get());
		if (wasValidBefore != isValidNow) { // if they are different
			var event = isValidNow ? new PodcastValidationSuccessEvent(model) : new PodcastValidationFailedEvent(model);
			this.publisher.publishEvent(event);
		}
	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

		var dimension = 200;
		this.photoImageView.setPreserveRatio(true);
		this.photoImageView.setFitHeight(dimension);

		this.titlePromptLabel.setText(this.messages.getMessage(getClass(), "title-prompt"));
		this.descriptionPromptLabel.setText(this.messages.getMessage(getClass(), "description-prompt"));
		this.filePromptLabel.setText(this.messages.getMessage(getClass(), "file-prompt"));

		List.of(this.interviewFileLabel, this.photoFileLabel, this.introFileLabel)
				.forEach(label -> label.setText(this.messages.getMessage(getClass(), "no-file-selected")));

		List.of(this.interviewFileChooserButton, this.photoFileChooserButton, this.introFileChooserButton)
				.forEach(btn -> btn.setText(this.messages.getMessage(getClass(), "choose-file")));

		this.introLabel.setText(this.messages.getMessage(getClass(), "introduction-file"));
		this.interviewLabel.setText(this.messages.getMessage(getClass(), "interview-file"));
		this.photoLabel.setText(this.messages.getMessage(getClass(), "photo-file"));

		this.title.textProperty().bindBidirectional(this.podcastModel.titleProperty());
		this.description.textProperty().bindBidirectional(this.podcastModel.descriptionProperty());

		List.of(fileSelectionTuple(this.interviewFileLabel, this.podcastModel.interviewFileProperty(),
				this.interviewFileChooserButton, this.mp3FileChooser),
				fileSelectionTuple(this.introFileLabel, this.podcastModel.introductionFileProperty(),
						this.introFileChooserButton, this.mp3FileChooser),
				fileSelectionTuple(this.photoFileLabel, this.podcastModel.photoFileProperty(),
						this.photoFileChooserButton, this.imageFileChooser))
				.forEach(tuple -> {
					var label = tuple.getT1();
					label.setTextAlignment(TextAlignment.RIGHT);
					label.setAlignment(Pos.CENTER_RIGHT);
					HBox.setHgrow(label, Priority.ALWAYS);
					var fileProperty = tuple.getT2();
					fileProperty.addListener((observableValue, oldValue, newValue) -> Optional.ofNullable(newValue)
							.ifPresentOrElse(f -> label.setText(Objects.requireNonNull(newValue).getAbsolutePath()),
									() -> label.setText("")));
					var button = tuple.getT3();
					var fileChooser = tuple.getT4();
					button.setOnMouseClicked(e -> Optional.ofNullable(fileChooser.showOpenDialog(this.stage.get()))
							.ifPresent(fileProperty::set));
				});

		this.podcastModel.descriptionProperty().addListener(this::onChange);
		this.podcastModel.titleProperty().addListener(this::onChange);
		this.podcastModel.interviewFileProperty().addListener(this::onChange);
		this.podcastModel.introductionFileProperty().addListener(this::onChange);
		this.podcastModel.photoFileProperty().addListener(this::onChange);
	}

	private Tuple4<Label, SimpleObjectProperty<File>, Button, FileChooser> fileSelectionTuple(Label label,
			SimpleObjectProperty<File> prop, Button btn, FileChooser fc) {
		return Tuples.of(label, prop, btn, fc);
	}

	private void repaintProfilePhotoFile(File photoFile) {
		if (photoFile != null) {
			var image = FxUtils.buildImageFromResource(new FileSystemResource(photoFile));
			this.photoImageView.setImage(image);
		}
		else {
			this.photoImageView.setImage(null);
		}
	}

	@EventListener
	public void loadEvent(PodcastLoadEvent ple) {
		Platform.runLater(() -> {
			var source = ple.getSource();
			this.podcastModel.descriptionProperty().setValue(source.descriptionProperty().getValue());
			this.podcastModel.titleProperty().setValue(source.titleProperty().getValue());
			this.podcastModel.interviewFileProperty().setValue(source.interviewFileProperty().getValue());
			this.podcastModel.introductionFileProperty().setValue(source.introductionFileProperty().getValue());
			this.podcastModel.photoFileProperty().setValue(source.photoFileProperty().getValue());

			var photoFile = source.photoFileProperty().get();
			this.repaintProfilePhotoFile(photoFile);
		});
	}

}
