package fm.bootifulpodcast.desktop;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.VBox;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SimpleController {

	@FXML
	public VBox dropTarget;
	@FXML
	public Label dropped;
	@FXML
	public Label label;

	@FXML
	public void initialize() {

		dropTarget.setOnDragOver(event -> {
			if (event.getGestureSource() != dropTarget && event.getDragboard().hasFiles()) {
				event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
			}
			event.consume();
		});

		dropTarget.setOnDragDropped(event -> {
			Dragboard db = event.getDragboard();
			boolean success = false;
			if (db.hasFiles()) {
				dropped.setText(db.getFiles().toString());
				success = true;
			}
			event.setDropCompleted(success);
			event.consume();
		});


	}
}
