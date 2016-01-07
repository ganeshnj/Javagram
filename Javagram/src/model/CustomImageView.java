package model;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class CustomImageView extends ImageView {
	private Filter filter;

	public CustomImageView(Image image) {
		// TODO Auto-generated constructor stub
		super(image);
	}

	public Filter getFilter() {
		return filter;
	}

	public void setFilter(Filter filter) {
		this.filter = filter;
	}
}