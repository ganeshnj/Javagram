package controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

public class CropController {
	private File sourceFile;
	private BufferedImage sourceImage;
	private Stage stage;
	private ImageView imageView;
	private RubberBandSelection rubberBandSelection;

	// image layer: a group of images
	private Group imageLayer = new Group();

	@FXML
	private ResourceBundle resources;

	@FXML
	private URL location;

	@FXML
	private BorderPane borderPaneCrop;

	@FXML
	void backAction(ActionEvent event) {
	}

	@FXML
	void nextAction(ActionEvent event) {
		// get bounds for image crop
		Bounds selectionBounds = rubberBandSelection.getBounds();

		// show bounds info
		System.out.println("Selected area: " + selectionBounds);

		
		BufferedImage tempImage = null;
		if (selectionBounds.getWidth() == 1 && selectionBounds.getHeight() == 1) {
			// No selection performed
			System.out.println("Original");
			
			
			  tempImage= sourceImage;
			  
		} else {
			// Selected performed
			tempImage = crop(selectionBounds);
			System.out.println("Cropped");
		}

		Stage previewStage = getStage();
		previewStage.setWidth(previewStage.getWidth()-1);
		FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/Previews.fxml"));
		PreviewsController controller = new PreviewsController();
		controller.setSourceImage(tempImage);
		controller.setStage(previewStage);
		loader.setController(controller);

		try {
			Parent root = (Parent) loader.load();
			Scene scene = new Scene(root);
			previewStage.setScene(scene);

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@FXML
	void initialize() {

		try {
			// throws exception if file not found
			BufferedImage in = ImageIO.read(sourceFile);
			
			sourceImage = new BufferedImage(in.getWidth(), in.getHeight(), BufferedImage.TYPE_INT_RGB);

			Graphics2D g = sourceImage.createGraphics();
			g.drawImage(in, 0, 0, in.getWidth(), in.getHeight(), null);
			g.dispose();

			imageView = new ImageView(new Image(sourceFile.toURI().toString()));
			imageLayer.getChildren().add(getImageView());

			ScrollPane scrollPane = new ScrollPane();
			// use scrollpane for image view in case the image is large
			scrollPane.setContent(imageLayer);

			// put scrollpane in scene
			borderPaneCrop.setCenter(scrollPane);

			rubberBandSelection = new RubberBandSelection(imageLayer);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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

	public ImageView getImageView() {
		return imageView;
	}

	public void setImageView(ImageView imageView) {
		this.imageView = imageView;
	}

	public Group getImageLayer() {
		return imageLayer;
	}

	public void setImageLayer(Group imageLayer) {
		this.imageLayer = imageLayer;
	}

	private BufferedImage crop(Bounds bounds) {

		  int width = (int) bounds.getWidth();
	        int height = (int) bounds.getHeight();

	        SnapshotParameters parameters = new SnapshotParameters();
	        parameters.setFill(Color.TRANSPARENT);
	        parameters.setViewport(new Rectangle2D( bounds.getMinX(), bounds.getMinY(), width, height));

	        WritableImage wi = new WritableImage( width, height);
	        imageView.snapshot(parameters, wi);

	        // save image 
	        // !!! has bug because of transparency (use approach below) !!!
	        // --------------------------------
//	        try {
//	          ImageIO.write(SwingFXUtils.fromFXImage( wi, null), "jpg", file);
//	      } catch (IOException e) {
//	          e.printStackTrace();
//	      }


	        // save image (without alpha)
	        // --------------------------------
	        BufferedImage bufImageARGB = SwingFXUtils.fromFXImage(wi, null);
	        BufferedImage bufImageRGB = new BufferedImage(bufImageARGB.getWidth(), bufImageARGB.getHeight(), BufferedImage.OPAQUE);

	        Graphics2D graphics = bufImageRGB.createGraphics();
	        graphics.drawImage(bufImageARGB, 0, 0, null);

	        try {
	        	File file = new File("cropped");
	            ImageIO.write(bufImageRGB, "jpg", file); 

	            System.out.println( "Image saved to " + file.getAbsolutePath());

	        } catch (IOException e) {
	            e.printStackTrace();
	        }

	        graphics.dispose();
	        
	        return bufImageRGB;

	}

	public RubberBandSelection getRubberBandSelection() {
		return rubberBandSelection;
	}

	public void setRubberBandSelection(RubberBandSelection rubberBandSelection) {
		this.rubberBandSelection = rubberBandSelection;
	}

	/**
	 * Drag rectangle with mouse cursor in order to get selection bounds
	 */
	public static class RubberBandSelection {

		final DragContext dragContext = new DragContext();
		Rectangle rect = new Rectangle();

		Group group;

		public Bounds getBounds() {
			return rect.getBoundsInParent();
		}

		public RubberBandSelection(Group group) {

			this.group = group;

			rect = new Rectangle(0, 0, 0, 0);
			rect.setStroke(Color.BLUE);
			rect.setStrokeWidth(1);
			rect.setStrokeLineCap(StrokeLineCap.ROUND);
			rect.setFill(Color.LIGHTBLUE.deriveColor(0, 1.2, 1, 0.6));

			group.addEventHandler(MouseEvent.MOUSE_PRESSED, onMousePressedEventHandler);
			group.addEventHandler(MouseEvent.MOUSE_DRAGGED, onMouseDraggedEventHandler);
			group.addEventHandler(MouseEvent.MOUSE_RELEASED, onMouseReleasedEventHandler);

		}

		EventHandler<MouseEvent> onMousePressedEventHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				if (event.isSecondaryButtonDown())
					return;

				// remove old rect
				rect.setX(0);
				rect.setY(0);
				rect.setWidth(0);
				rect.setHeight(0);

				group.getChildren().remove(rect);

				// prepare new drag operation
				dragContext.mouseAnchorX = event.getX();
				dragContext.mouseAnchorY = event.getY();

				rect.setX(dragContext.mouseAnchorX);
				rect.setY(dragContext.mouseAnchorY);
				rect.setWidth(0);
				rect.setHeight(0);

				group.getChildren().add(rect);

			}
		};

		EventHandler<MouseEvent> onMouseDraggedEventHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				if (event.isSecondaryButtonDown())
					return;

				double offsetX = event.getX() - dragContext.mouseAnchorX;
				double offsetY = event.getY() - dragContext.mouseAnchorY;

				if (offsetX > 0)
					rect.setWidth(offsetX);
				else {
					rect.setX(event.getX());
					rect.setWidth(dragContext.mouseAnchorX - rect.getX());
				}

				if (offsetY > 0) {
					rect.setHeight(offsetY);
				} else {
					rect.setY(event.getY());
					rect.setHeight(dragContext.mouseAnchorY - rect.getY());
				}
			}
		};

		EventHandler<MouseEvent> onMouseReleasedEventHandler = new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {

				if (event.isSecondaryButtonDown())
					return;

				// remove rectangle
				// note: we want to keep the ruuberband selection for the
				// cropping => code is just commented out
				/*
				 * rect.setX(0); rect.setY(0); rect.setWidth(0);
				 * rect.setHeight(0);
				 * 
				 * group.getChildren().remove( rect);
				 */

			}
		};

		private static final class DragContext {

			public double mouseAnchorX;
			public double mouseAnchorY;

		}
	}

}
