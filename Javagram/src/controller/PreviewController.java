package controller;

import java.io.File;
import javafx.fxml.FXML;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.ScrollPane;

public class PreviewController {
	File selectedFile;
	@FXML BorderPane borderPanePreview;
	@FXML ScrollPane scrollPanePreviews;
	
	@FXML
    void initialize() {
        assert borderPanePreview != null : "fx:id=\"borderPanePreview\" was not injected: check your FXML file 'Previews.fxml'.";
        assert scrollPanePreviews != null : "fx:id=\"scrollPanePreviews\" was not injected: check your FXML file 'Previews.fxml'.";
        
        selectedFile = new File("C:/Users/ganes/OneDrive/Pictures/Camera Roll/WP_20151212_15_35_27_Rich_LI.jpg");
    }
}
