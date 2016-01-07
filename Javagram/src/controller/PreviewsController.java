package controller;

import java.awt.image.BufferedImage;
import java.io.File;

import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class PreviewsController {

	private File sourceFile;
	private BufferedImage sourceImage;
	private Stage stage;

	@FXML
	private ImageView imageViewLarge;

	@FXML
	void initialize() {
		imageViewLarge.setImage(SwingFXUtils.toFXImage(sourceImage, null));
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	public File getSourceFile() {
		return sourceFile;
	}

	public void setSourceFile(File sourceFile) {
		this.sourceFile = sourceFile;
	}

	public BufferedImage getSourceImage() {
		return sourceImage;
	}

	public void setSourceImage(BufferedImage sourceImage) {
		this.sourceImage = sourceImage;
	}
}
