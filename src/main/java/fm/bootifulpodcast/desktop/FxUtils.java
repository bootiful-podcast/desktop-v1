package fm.bootifulpodcast.desktop;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.SneakyThrows;
import org.springframework.core.io.Resource;

abstract class FxUtils {

	@SneakyThrows
	public static Image buildImageFromResource(Resource r) {
		try (var in = r.getInputStream()) {
			return new Image(in);
		}
	}

	@SneakyThrows
	public static ImageView buildImageViewFromResource(Resource resource) {
		try (var in = resource.getInputStream()) {
			var imageView = new ImageView(new Image(in));
			imageView.setSmooth(true);
			imageView.setPreserveRatio(true);
			// imageView.setFitHeight(30);
			return imageView;
		}
	}

}
