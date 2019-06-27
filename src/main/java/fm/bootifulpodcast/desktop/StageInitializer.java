package fm.bootifulpodcast.desktop;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationContext;
import org.springframework.context.MessageSource;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Log4j2
@Component
class StageInitializer {

	private final String applicationTitle;

	private final ApplicationContext applicationContext;

	StageInitializer(MessageSource ms, Locale locale,
																		ApplicationContext applicationContext) {
		this.applicationTitle = ms.getMessage("ui-title", new Object[0], locale);
		this.applicationContext = applicationContext;
	}

	@EventListener
	public void stageIsReady(StageReadyEvent sre) throws Exception {
		var stage = sre.getSource();
		var fxml = new ClassPathResource("/ui.fxml");
		var fxmlLoader = new FXMLLoader(fxml.getURL());
		fxmlLoader.setControllerFactory(this.applicationContext::getBean);
		var root = (Parent) fxmlLoader.load();
		var scene = new Scene(root, 800, 350);

		stage.setScene(scene);
		stage.setTitle(this.applicationTitle);
		stage.centerOnScreen();
		stage.show();

	}

}
