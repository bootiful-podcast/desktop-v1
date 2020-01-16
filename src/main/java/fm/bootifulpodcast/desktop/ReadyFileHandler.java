package fm.bootifulpodcast.desktop;

import fm.bootifulpodcast.desktop.client.ApiClient;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;

import java.net.URI;
import java.util.Optional;

@Component
@RequiredArgsConstructor
class ReadyFileHandler {

	private final Messages messages;

	private final HostServices hostServices;

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
					.ifPresent(file -> this.doHandle(resolved));
		});
	}

}
