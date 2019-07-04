package fm.bootifulpodcast.desktop;

import fm.bootifulpodcast.desktop.client.ApiClient;
import javafx.application.Platform;
import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class ReadyFileHandler {

	private final Messages messages;

	private final ApiClient client;

	public void handle(Stage stage, URI resolved) {

		Platform.runLater(() -> {
			var extFilter = new FileChooser.ExtensionFilter(
					this.messages.getMessage("file-chooser-title"), "*.mp3", "*.wav");
			var fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(extFilter);
			Assert.notNull(stage, "the stage must have been set");
			Optional//
					.ofNullable(fileChooser.showSaveDialog(stage)) //
					.ifPresent(file -> this.client.download(resolved, file)
							.thenAccept(downloadedFile -> Platform.runLater(() -> {
								var alert = new Alert(Alert.AlertType.INFORMATION);
								alert.setTitle(this.messages
										.getMessage("file-done-alert-title"));
								alert.setHeaderText(null);
								alert.setContentText(
										messages.getMessage(ProgressController.class,
												"file-has-been-downloaded",
												downloadedFile.getAbsolutePath()));
								alert.showAndWait();
							})));
		});
	}

}
