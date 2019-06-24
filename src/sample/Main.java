package sample;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Main extends Application {


	@Override
	public void start(Stage primaryStage) {
		Label label = new Label("Drag a file to me.");
		Label dropped = new Label("");
		VBox dragTarget = new VBox();
		dragTarget.getChildren().addAll(label, dropped);
		dragTarget.setOnDragOver(event -> {
			if (event.getGestureSource() != dragTarget
				&& event.getDragboard().hasFiles()) {
				/* allow for both copying and moving, whatever user chooses */
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
		Scene scene = new Scene(root, 500, 250);
		primaryStage.setTitle("Drag Test");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	public static void main(String[] args) {
		launch(args);
	}

}
