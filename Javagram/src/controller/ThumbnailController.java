package controller;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;


public class ThumbnailController {
	
	private MainController mainController;
	
	private File selectedDirectory;
	
	@FXML
    private AnchorPane rootAnchorPane;
	
	private Stage stage;

    @FXML
    private ResourceBundle resources;

    @FXML
    private URL location;
    
    @FXML
    private ScrollPane scrollPaneThumbnails;



    @FXML
    void initialize() {
    	System.out.println("Thumbnails view loaded");
    	selectedDirectory = new File("C:/Users/ganes/Pictures/Screenshots");
    	
    	File[] listOfFiles = selectedDirectory.listFiles(new FileFilter() {
    	    @Override
    	    public boolean accept(File pathname) {
    	     // TODO Auto-generated method stub
    	     String name = pathname.getName().toLowerCase();
    	     return pathname.isFile() && (name.endsWith(".png")
    	         || name.endsWith(".jpg") 
    	         || name.endsWith(".gif"));
    	    }
    	   });
    	
    	TilePane tile = new TilePane();
    	scrollPaneThumbnails.setStyle("-fx-background-color: DAE6F3;");
        tile.setPadding(new Insets(15, 15, 15, 15));
        tile.setHgap(15);
    	
    	 for (final File file : listOfFiles) {
             ImageView imageView;
             imageView = createImageView(file);
             tile.getChildren().addAll(imageView);
         }
    	 
    	 scrollPaneThumbnails.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Horizontal
    	 scrollPaneThumbnails.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical scroll bar
    	 scrollPaneThumbnails.setFitToWidth(true);
    	 scrollPaneThumbnails.setContent(tile);
    }


	public void init(MainController mainController) {
		// TODO Auto-generated method stub
		this.mainController = mainController;
	}
	
	  private ImageView createImageView(final File imageFile) {
	        // DEFAULT_THUMBNAIL_WIDTH is a constant you need to define
	        // The last two arguments are: preserveRatio, and use smooth (slower)
	        // resizing

	        ImageView imageView = null;
	        try {
	            final Image image = new Image(new FileInputStream(imageFile), 0, 100, true,
	                    true);
	            imageView = new ImageView(image);
	            imageView.setFitHeight(100);
	            imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

	                @Override
	                public void handle(MouseEvent mouseEvent) {

	                    if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){

	                        if(mouseEvent.getClickCount() == 2){
	                            try {
	                                BorderPane borderPane = new BorderPane();
	                                ImageView imageView = new ImageView();
	                                Image image = new Image(new FileInputStream(imageFile));
	                                imageView.setImage(image);
	                                imageView.setStyle("-fx-background-color: BLACK");
	                                imageView.setFitHeight(getStage().getHeight() - 10);
	                                imageView.setPreserveRatio(true);
	                                imageView.setSmooth(true);
	                                imageView.setCache(true);
	                                borderPane.setCenter(imageView);
	                                borderPane.setStyle("-fx-background-color: BLACK");
	                                borderPane.setId("thumbnail");
	                                Stage newStage = new Stage();
	                                newStage.setWidth(getStage().getWidth());
	                                newStage.setHeight(getStage().getHeight());
	                                newStage.setTitle(imageFile.getName());
	                                Scene scene = new Scene(borderPane, Color.BLACK);
	                                newStage.setScene(scene);
	                                newStage.show();
	                            } catch (FileNotFoundException e) {
	                                e.printStackTrace();
	                            }

	                        }
	                    }
	                }
	            });
	        } catch (FileNotFoundException ex) {
	            ex.printStackTrace();
	        }
	        return imageView;
	    }


	public Stage getStage() {
		return stage;
	}


	public void setStage(Stage stage) {
		this.stage = stage;
	}


}
