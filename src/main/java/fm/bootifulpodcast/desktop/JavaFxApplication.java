package fm.bootifulpodcast.desktop;

import javafx.application.Application;
import javafx.application.HostServices;
import javafx.application.Platform;
import javafx.stage.Stage;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.GenericApplicationContext;

import java.util.concurrent.atomic.AtomicReference;

@Log4j2
public class JavaFxApplication extends Application {

	private ConfigurableApplicationContext context;

	private final AtomicReference<Stage> stage = new AtomicReference<>();

	@Override
	public void init() {
		ApplicationContextInitializer<GenericApplicationContext> initializer = context -> {
			context.registerBean(Application.class, () -> JavaFxApplication.this);
			context.registerBean(Parameters.class, this::getParameters);
			context.registerBean(HostServices.class, this::getHostServices);
		};
		this.context = new SpringApplicationBuilder()//
				.sources(DesktopApplication.class)//
				.initializers(initializer)//
				.run(getParameters().getRaw().toArray(new String[0]));
	}

	@Override
	public void start(Stage stage) {
		this.stage.set(stage);
		this.context.publishEvent(new StageReadyEvent(this.stage.get()));
	}

	@Override
	public void stop() {
		this.context.publishEvent(new StageStoppedEvent(this.stage.get()));
		this.context.close();
		this.stage.set(null);
		Platform.exit();
	}

}
