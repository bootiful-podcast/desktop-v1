package fm.bootifulpodcast.desktop;

import org.springframework.context.ApplicationEvent;

public class GenericApplicationEvent<T> extends ApplicationEvent {

	public GenericApplicationEvent(T source) {
		super(source);
	}

	@Override
	public T getSource() {
		return (T) super.getSource();
	}

}
