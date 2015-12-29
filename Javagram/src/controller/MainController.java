package controller;

import java.io.File;
import java.io.IOException;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.FileTreeItem;

public class MainController {
	
	private Stage stage;
	
	@FXML 
	private ThumbnailController thumbnailController;
	
	@FXML
	private TreeView<?> treeViewLibrary;
	
	 @FXML
	    private BorderPane rootBorderPane;

	
	@FXML public void initialize(){
		System.out.println("Application started");
		// thumbnailController.init(this);
	}
	
	 @FXML
	 public void newFolderAction(ActionEvent event) {
		 System.out.println("Add folder clicked");
		 DirectoryChooser  directoryChooser = new DirectoryChooser();
		 directoryChooser.setTitle("Select a directory");
		 File selectedDirectory = directoryChooser.showDialog(new Stage());
		 
		 if(selectedDirectory != null){
			 System.out.println("Selcted directory:" + selectedDirectory.getAbsolutePath() );
			 
			 treeViewLibrary = new TreeView<File>(new FileTreeItem(selectedDirectory));
		 }
	 }
	 
	 @FXML
	 void showPreviewAction(ActionEvent event) {
		 System.out.println("Show previews clicked");
		 FXMLLoader loader = new FXMLLoader();
			try {
				Parent root = loader.load(getClass().getResource("/view/Previews.fxml"));
				rootBorderPane.setCenter(root);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	 }

	 @FXML
	 void showThumbnailsAction(ActionEvent event) {
		 System.out.println("Show thumbnails clicked");
		 
		FXMLLoader loader = new FXMLLoader();
		try {
			Parent root = loader.load(getClass().getResource("/view/Thumbnail.fxml"));
			rootBorderPane.setCenter(root);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	 }

	public void setStage(Stage primaryStage) {
		this.stage = primaryStage;
		
	}

}
