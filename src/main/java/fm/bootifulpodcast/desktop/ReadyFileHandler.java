package fm.bootifulpodcast.desktop;

import com.sun.javafx.application.HostServicesDelegate;
import fm.bootifulpodcast.desktop.client.ApiClient;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.awt.*;
import java.net.URI;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class ReadyFileHandler {

	private final Messages messages;

	private final HostServices hostServices;

	private final ApiClient client;

	/*
	 * @SneakyThrows private void doHandle(URI file) { var desktop =
	 * Desktop.isDesktopSupported() ? Desktop.getDesktop() : null; if (desktop != null &&
	 * desktop.isSupported(Desktop.Action.OPEN)) { desktop.browse(file); } else { throw
	 * new UnsupportedOperationException("Open action not supported"); } }
	 */

	@SneakyThrows
	private void doHandle(URI file) {
		this.hostServices.showDocument(file.toString());
	}

	public void handle(Stage stage, URI resolved) {

		Platform.runLater(() -> {
			var extFilter = new FileChooser.ExtensionFilter(
					this.messages.getMessage("file-chooser-title"), "*.mp3", "*.wav");
			var fileChooser = new FileChooser();
			fileChooser.getExtensionFilters().add(extFilter);
			Assert.notNull(stage, "the stage must have been set");
			Optional//
					.ofNullable(fileChooser.showSaveDialog(stage)) //
					.ifPresent(file -> this.doHandle(resolved)
			/*
			 * this.client.download(resolved, file) .thenAccept(downloadedFile ->
			 * Platform.runLater(() -> { var alert = new
			 * Alert(Alert.AlertType.INFORMATION); alert.setTitle(this.messages
			 * .getMessage("file-done-alert-title")); alert.setHeaderText(null);
			 * alert.setContentText( messages.getMessage(ProgressController.class,
			 * "file-has-been-downloaded", downloadedFile.getAbsolutePath()));
			 * alert.showAndWait(); }))
			 */
			);
		});
	}

}
