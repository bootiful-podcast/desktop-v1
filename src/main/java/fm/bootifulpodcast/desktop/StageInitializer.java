package fm.bootifulpodcast.desktop;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

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

	@EventListener
	public void dand(StageReadyEvent sre) throws Exception {
		var stage = sre.getStage();
		var fxml = new ClassPathResource("/ui.fxml");
		var fxmlLoader = new FXMLLoader(fxml.getURL());
		fxmlLoader.setControllerFactory(this.applicationContext::getBean);
		var root = (Parent) fxmlLoader.load();
		var scene = new Scene(root, 800, 600);
		stage.setScene(scene);
		stage.setTitle(this.applicationTitle);
		stage.show();

	}
}
