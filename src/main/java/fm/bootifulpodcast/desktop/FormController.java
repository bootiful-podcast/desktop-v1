package fm.bootifulpodcast.desktop;

import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;

import java.io.File;
import java.net.URL;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@Component
public class FormController implements Initializable {

	public Label interviewFileLabel;

	public Label introFileLabel;

	public Label titlePromptLabel;

	public Label descriptionPromptLabel;

	public TextArea description;

	public TextField title;

	public Label introLabel;

	public Label interviewLabel;

	public Button introFileChooserButton;

	public Button interviewFileChooserButton;

	public Label filePromptLabel;

	private final AtomicReference<Stage> stage = new AtomicReference<>();

	private final AtomicBoolean valid = new AtomicBoolean(false);

	private final PodcastModel podcastModel = new PodcastModel();

	private final Messages messages;

	private final ApplicationEventPublisher publisher;

	private final FileChooser fileChooser;

	FormController(Messages messages, ApplicationEventPublisher publisher) {
		this.messages = messages;
		this.publisher = publisher;

		this.fileChooser = new FileChooser();
		this.fileChooser.setTitle(messages.getMessage(getClass(), "file-chooser-title"));
		this.fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter(
				messages.getMessage(getClass(), "file-chooser-description"), "*.mp3"));
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
		var title = model.titleProperty().get();
		var wasValidBefore = this.valid.get();
		var isValidNow = (StringUtils.hasText(title) && StringUtils.hasText(description)
				&& intro != null && interview != null);
		this.valid.set(isValidNow);
		if (wasValidBefore != isValidNow) { // if they are different
			var event = isValidNow ? new PodcastValidationSuccessEvent(model)
					: new PodcastValidationFailedEvent(model);
			this.publisher.publishEvent(event);
		}

	}

	@Override
	public void initialize(URL url, ResourceBundle resourceBundle) {

		this.titlePromptLabel
				.setText(this.messages.getMessage(getClass(), "title-prompt"));
		this.descriptionPromptLabel
				.setText(this.messages.getMessage(getClass(), "description-prompt"));
		this.filePromptLabel.setText(this.messages.getMessage(getClass(), "file-prompt"));

		List.of(this.interviewFileLabel, this.introFileLabel).forEach(label -> label
				.setText(this.messages.getMessage(getClass(), "no-file-selected")));

		List.of(this.interviewFileChooserButton, this.introFileChooserButton).forEach(
				btn -> btn.setText(this.messages.getMessage(getClass(), "choose-file")));

		this.introLabel
				.setText(this.messages.getMessage(getClass(), "introduction-file"));
		this.interviewLabel
				.setText(this.messages.getMessage(getClass(), "interview-file"));

		this.title.textProperty().bindBidirectional(this.podcastModel.titleProperty());
		this.description.textProperty()
				.bindBidirectional(this.podcastModel.descriptionProperty());

		List.of(fileSelectionTuple(this.interviewFileLabel,
				this.podcastModel.interviewFileProperty(),
				this.interviewFileChooserButton),
				fileSelectionTuple(this.introFileLabel,
						this.podcastModel.introductionFileProperty(),
						this.introFileChooserButton))
				.forEach(tuple -> {
					var label = tuple.getT1();
					label.setTextAlignment(TextAlignment.RIGHT);
					label.setAlignment(Pos.CENTER_RIGHT);
					HBox.setHgrow(label, Priority.ALWAYS);

					var fileProperty = tuple.getT2();
					fileProperty.addListener((observableValue, oldValue,
							newValue) -> label.setText(newValue.getAbsolutePath()));

					var button = tuple.getT3();
					button.setOnMouseClicked(e -> Optional
							.ofNullable(this.fileChooser.showOpenDialog(this.stage.get()))
							.ifPresent(fileProperty::set));
				});

		this.podcastModel.descriptionProperty().addListener(this::onChange);
		this.podcastModel.titleProperty().addListener(this::onChange);
		this.podcastModel.interviewFileProperty().addListener(this::onChange);
		this.podcastModel.introductionFileProperty().addListener(this::onChange);

	}

	private Tuple3<Label, SimpleObjectProperty<File>, Button> fileSelectionTuple(
			Label label, SimpleObjectProperty<File> prop, Button btn) {
		return Tuples.of(label, prop, btn);
	}

	@EventListener
	public void loadEvent(PodcastLoadEvent ple) {
		Platform.runLater(() -> {
			var source = ple.getSource();
			this.podcastModel.descriptionProperty()
					.setValue(source.descriptionProperty().getValue());
			this.podcastModel.titleProperty().setValue(source.titleProperty().getValue());
			this.podcastModel.interviewFileProperty()
					.setValue(source.interviewFileProperty().getValue());
			this.podcastModel.introductionFileProperty()
					.setValue(source.introductionFileProperty().getValue());
		});
	}

}
