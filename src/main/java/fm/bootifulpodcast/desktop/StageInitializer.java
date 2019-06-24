package fm.bootifulpodcast.desktop;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Log4j2
@Component
class StageInitializer {

	private final String applicationTitle;
	private final ApplicationContext applicationContext;

	StageInitializer(@Value("${spring.application.ui.title}") String applicationTitle,
																		ApplicationContext applicationContext) {
		this.applicationTitle = applicationTitle;
		this.applicationContext = applicationContext;
	}

/*	@EventListener
	void prepareStage(StageReadyEvent stageReadyEvent) {
		try {
			var stage = stageReadyEvent.getStage();
			var fxml = new ClassPathResource("/ui.fxml");
			var fxmlLoader = new FXMLLoader(fxml.getURL());
			fxmlLoader.setControllerFactory(this.applicationContext::getBean);
			var root = (Parent) fxmlLoader.load();
			var scene = new Scene(root, 800, 600);
			stage.setScene(scene);
			stage.setTitle(this.applicationTitle);
			stage.show();
		}
		catch (IOException e) {
			throw new RuntimeException(e);
		}
	}*/

	@EventListener
	public void dand(StageReadyEvent sre) {
		var stage = sre.getStage();
		var label = new Label("Drag a file to me.");
		var dropped = new Label("");
		var dragTarget = new VBox();
		dragTarget
			.getChildren()
			.addAll(label, dropped);

		dragTarget.setOnDragOver(event -> {
			if (event.getGestureSource() != dragTarget && event.getDragboard().hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
			event.consume();
		});

		dragTarget.setOnDragDropped(event -> {
			Dragboard db = event.getDragboard();
			boolean success = false;
			if (db.hasFiles()) {
				dropped.setText(db.getFiles().toString());
				success = true;
			}
			event.setDropCompleted(success);
			event.consume();
		});


		StackPane root = new StackPane();
		root.getChildren().add(dragTarget);
		Scene scene = new Scene(root, 800, 600);
		stage.setTitle("Drag Test");
		stage.setScene(scene);
		stage.show();
	}
}
