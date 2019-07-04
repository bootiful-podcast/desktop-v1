package fm.bootifulpodcast.desktop;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import lombok.Value;
import lombok.experimental.Accessors;

import java.io.File;
import java.util.function.Function;

@Value
@Accessors(fluent = true)
class PodcastModel {

	private final SimpleObjectProperty<File> introductionFileProperty = new SimpleObjectProperty<>();

	private final SimpleObjectProperty<File> interviewFileProperty = new SimpleObjectProperty<>();

	private final StringProperty titleProperty = new SimpleStringProperty();

	private final StringProperty descriptionProperty = new SimpleStringProperty();

	@Override
	public String toString() {

		Function<File, String> fileToString = file -> file == null ? ""
				: file.getAbsolutePath();

		return "PodcastModel{" + "introductionFile="
				+ fileToString.apply(introductionFileProperty.get()) + ", interviewFile="
				+ fileToString.apply(interviewFileProperty.get()) + ", title="
				+ titleProperty.get() + ", description=" + descriptionProperty.get()
				+ '}';
	}

}
