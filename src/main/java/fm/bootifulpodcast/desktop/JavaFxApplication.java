package fm.bootifulpodcast.desktop;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

public class JavaFxApplication extends Application {

	private ConfigurableApplicationContext context;

	@Override
	public void init() {
		ApplicationContextInitializer<GenericApplicationContext> initializer =
			context -> {
				context.registerBean(Application.class, () -> JavaFxApplication.this);
				context.registerBean(Parameters.class, this::getParameters);
				context.registerBean(HostServices.class, this::getHostServices);
			};
		this.context = new SpringApplicationBuilder()
			.sources(BootifulFxApplication.class)
			.initializers(initializer)
			.run(getParameters().getRaw().toArray(new String[0]));
	}

	@Override
	public void start(Stage stage) {
		this.context.publishEvent(new StageReadyEvent(stage));
	}

	@Override
	public void stop() {
		this.context.close();
		Platform.exit();
	}
}

