package controller;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.File;
import java.net.URL;
import java.util.ResourceBundle;

public class ExplorerController {
	
	private Stage stage;

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private BorderPane rootBorderPane;

	@FXML
	private ScrollPane scrollPaneFiles;

	@FXML
	void addFolderClick(ActionEvent event) {
		System.out.println("Add folder clicked");
		DirectoryChooser directoryChooser = new DirectoryChooser();
		directoryChooser.setTitle("Select a directory");
		File selectedDirectory = directoryChooser.showDialog(new Stage());

		if (selectedDirectory != null) {
			System.out.println("Selcted directory:" + selectedDirectory.getAbsolutePath());
			TreeItem<String> root = createNode(selectedDirectory);
			TreeView<String> treeViewLibrary = new TreeView<String>(root);
			rootBorderPane.setLeft(treeViewLibrary);
		}
	}

	@FXML
	void initialize() {
		File selectedDirectory = new File("C:/Users/ganes/Pictures/Screenshots");

		File[] listOfFiles = selectedDirectory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File pathname) {
				// TODO Auto-generated method stub
				String name = pathname.getName().toLowerCase();
				return pathname.isFile() && (name.endsWith(".png") || name.endsWith(".jpg") || name.endsWith(".gif"));
			}
		});

		TilePane tile = new TilePane();
		scrollPaneFiles.setStyle("-fx-background-color: DAE6F3;");
		tile.setPadding(new Insets(15, 15, 15, 15));
		tile.setHgap(15);

		for (final File file : listOfFiles) {
			ImageView imageView;
			imageView = createImageView(file);
			tile.getChildren().addAll(imageView);
		}

		scrollPaneFiles.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER); // Horizontal
		scrollPaneFiles.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED); // Vertical
																				// scroll
																				// bar
		scrollPaneFiles.setFitToWidth(true);
		scrollPaneFiles.setContent(tile);
	}

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}

	private TreeItem<String> createNode(final File f) {
		return new TreeItem<String>(f.getName()) {
			// We cache whether the File is a leaf or not. A File is a leaf if
			// it is not a directory and does not have any files contained
			// within
			// it. We cache this as isLeaf() is called often, and doing the
			// actual check on File is expensive.
			private boolean isLeaf;

			// We do the children and leaf testing only once, and then set these
			// booleans to false so that we do not check again during this
			// run. A more complete implementation may need to handle more
			// dynamic file system situations (such as where a folder has files
			// added after the TreeView is shown). Again, this is left as an
			// exercise for the reader.
			private boolean isFirstTimeChildren = true;
			private boolean isFirstTimeLeaf = true;

			@Override
			public ObservableList<TreeItem<String>> getChildren() {
				if (isFirstTimeChildren) {
					isFirstTimeChildren = false;

					// First getChildren() call, so we actually go off and
					// determine the children of the File contained in this
					// TreeItem.
					super.getChildren().setAll(buildChildren(this));
				}
				return super.getChildren();
			}

			@Override
			public boolean isLeaf() {
				if (isFirstTimeLeaf) {
					isFirstTimeLeaf = false;
					File file = f.getAbsoluteFile();
					isLeaf = file.isFile();
				}

				return isLeaf;
			}

			private ObservableList<TreeItem<String>> buildChildren(TreeItem<String> TreeItem) {
				File file = f.getAbsoluteFile();
				if (file != null && file.isDirectory()) {
					File[] files = file.listFiles();
					if (files != null) {
						ObservableList<TreeItem<String>> children = FXCollections.observableArrayList();

						for (File childFile : files) {
							children.add(createNode(childFile));
						}
						return children;
					}
				}

				return FXCollections.emptyObservableList();
			}

		};
	}

	private ImageView createImageView(final File imageFile) {
		// DEFAULT_THUMBNAIL_WIDTH is a constant you need to define
		// The last two arguments are: preserveRatio, and use smooth (slower)
		// resizing

		ImageView imageView = null;
		try {
			final Image image = new Image(new FileInputStream(imageFile), 0, 100, true, true);
			imageView = new ImageView(image);
			imageView.setFitHeight(100);
			imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

				@Override
				public void handle(MouseEvent mouseEvent) {

					if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

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
							
							
							Stage cropStage = getStage();
							FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Crop.fxml"));
							CropController controller = new CropController();
							controller.setSourceFile(imageFile);
							controller.setStage(cropStage);
							cropStage.setWidth(700);
							cropStage.setHeight(800);
							cropStage.setResizable(false);
							loader.setController(controller);
							
							try {
								Parent root = (Parent) loader.load();
								Scene scene = new Scene(root);
								cropStage.setScene(scene);

							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							
						} catch (FileNotFoundException e) {
							e.printStackTrace();
						}

					}
				}
			});
		} catch (

		FileNotFoundException ex)

		{
			ex.printStackTrace();
		}
		return imageView;
	}
}
