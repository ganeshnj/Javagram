package controller;

import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import com.jhlabs.image.EmbossFilter;
import com.jhlabs.image.GrayscaleFilter;
import com.jhlabs.image.OpacityFilter;
import com.jhlabs.image.ReduceNoiseFilter;
import com.jhlabs.image.SharpenFilter;
import com.jhlabs.image.TileImageFilter;
import com.jhlabs.image.TritoneFilter;
import com.jhlabs.image.VariableBlurFilter;
import com.jhlabs.image.WeaveFilter;

import javafx.application.Platform;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.ReadOnlyStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.concurrent.Worker.State;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.TilePane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import model.Filter;
import model.FiltersContainer;
import model.FilteredImageView;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;

public class PreviewController {
	File selectedFile;
	@FXML
	BorderPane borderPanePreview;
	@FXML ScrollPane scrollPanePreviews;
	FiltersContainer filterContainer;
	
	private BufferedImage source;
	private BufferedImage sourceSmall;
	
	private static final int IMG_WIDTH = 100;
	private static final int IMG_HEIGHT = 100;
	
	private ArrayList<Filter> filters = new ArrayList<>();

	
	private Stage stage;
	
	@FXML
    void initialize() {
        assert borderPanePreview != null : "fx:id=\"borderPanePreview\" was not injected: check your FXML file 'Previews.fxml'.";
        assert scrollPanePreviews != null : "fx:id=\"scrollPanePreviews\" was not injected: check your FXML file 'Previews.fxml'.";
        
        filters.add(Filter.NONE);
        filters.add(Filter.EMBOSS);
        filters.add(Filter.GRAYSCALE);
        filters.add(Filter.OPACITY);
        filters.add(Filter.REDUCENOICE);
        filters.add(Filter.SHARPEN);
        filters.add(Filter.TILEIMAGE);
        filters.add(Filter.TRITONE);
        filters.add(Filter.VARIABLEBLUR);
        filters.add(Filter.WEAVE);
        
        String path =  "C:/Users/ganes/OneDrive/Pictures/Camera Roll/WP_20151212_15_35_27_Rich_LI.jpg";
        try {
			source = ImageIO.read(new File(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        final Task<ArrayList<FilteredImageView>> taskPreviews = new Task<ArrayList<FilteredImageView>>() {
			
			@Override
			protected ArrayList<FilteredImageView> call() throws Exception {
				ArrayList<FilteredImageView> previews = new ArrayList<>();
				
				int i=1;
			
	            for (Filter filter : filters) {
	            	
	            	 if (isCancelled()) {
	                        break;
	                    }

	            	previews.add(getFilteredImageView(source, filter, 100, 100));
	            	updateMessage("Creating previews " + i + "/" + filters.size());
	            	i++;
				}
	         	
	         	return previews;
			}
		};
		
		taskPreviews.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
			
			@Override
			public void handle(WorkerStateEvent event) {
				// TODO Auto-generated method stub
				
			    HBox hBox = new HBox();
		        scrollPanePreviews.setStyle("-fx-background-color: DAE6F3;");
		        hBox.setPadding(new Insets(15, 15, 15, 15));
		        hBox.setSpacing(15);
		        //tile.setHgap(15);  
		        
		        hBox.getChildren().addAll(taskPreviews.getValue());
			    
	        	scrollPanePreviews.setContent(hBox);
			}
		});
		
		new Thread(taskPreviews).start();   
    }
	
	private FilteredImageView createImageView(final BufferedImage bufferedImage) throws FileNotFoundException {
        // DEFAULT_THUMBNAIL_WIDTH is a constant you need to define
        // The last two arguments are: preserveRatio, and use smooth (slower)
        // resizing

		FilteredImageView imageView = null;
        final Image image = SwingFXUtils.toFXImage(bufferedImage, null);
		imageView = new FilteredImageView(image);
		imageView.setFitHeight(100);
		imageView.setFitWidth(100);
		imageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

		    @Override
		    public void handle(MouseEvent mouseEvent) {

		        if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){
		        	
		        	FilteredImageView clickedImageView = (FilteredImageView) mouseEvent.getSource();
		        	System.out.println(clickedImageView);
		        	System.out.println(clickedImageView.getFilter());
		        	
		        	BufferedImage large = getFilteredImage(source, clickedImageView.getFilter());
		        	
		        	FilteredImageView imageView;
					try {
						imageView = createImageView(large);
						borderPanePreview.setCenter(imageView);
					} catch (FileNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
		        	
		        	
		        }
		    }
		});
        return imageView;
    }

	public Stage getStage() {
		return stage;
	}

	public void setStage(Stage stage) {
		this.stage = stage;
	}
	
	private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height){
		BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();
			
		return resizedImage;
	}

	
	public static BufferedImage getFilteredImage(BufferedImage src, Filter type) {
		
		BufferedImage dest = null;
		
		switch (type) {
		case NONE:
			dest = src;
			break;
		case EMBOSS:
			EmbossFilter embossFilter = new EmbossFilter();
			dest = embossFilter.filter(src, dest);
			break;
		case GRAYSCALE:
			GrayscaleFilter grayscaleFilter = new GrayscaleFilter();
			dest = grayscaleFilter.filter(src, dest);
			break;
		case OPACITY:
			OpacityFilter opactiyFilter = new OpacityFilter();
			dest = opactiyFilter.filter(src, dest);
			break;
		case REDUCENOICE:
			ReduceNoiseFilter reduceNoiseFilter = new ReduceNoiseFilter();
			dest = reduceNoiseFilter.filter(src, dest);
			break;
		case SHARPEN:
			SharpenFilter sharpenFilter = new SharpenFilter();
			dest = sharpenFilter.filter(src, dest);
			break;
		case TILEIMAGE:
			TileImageFilter tileImageFilter = new TileImageFilter();
			dest = tileImageFilter.filter(src, dest);
			break;
		case TRITONE: 
			TritoneFilter tritoneFilter = new TritoneFilter();
			dest = tritoneFilter.filter(src, dest);
			break;
		case VARIABLEBLUR:
			VariableBlurFilter variableBlurFilter = new VariableBlurFilter();
			dest = variableBlurFilter.filter(src, dest);
			break;
		case WEAVE:
			WeaveFilter weaveFilter = new WeaveFilter();
			dest = weaveFilter.filter(src, dest);
			break;
		default:
			dest = src;
			break;
		}
		return dest;
	}

	public static FilteredImageView getFilteredImageView(BufferedImage src, Filter type){
		BufferedImage filteredImage = getFilteredImage(src, type);
				
		FilteredImageView filteredImageView = new FilteredImageView(SwingFXUtils.toFXImage(filteredImage, null));
		filteredImageView.setFilter(type);
		filteredImageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

             @Override
             public void handle(MouseEvent mouseEvent) {

                 if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){

                	 FilteredImageView selectedImageView = (FilteredImageView)mouseEvent.getSource();
                	 
                     System.out.println(selectedImageView.getFilter());
                 }
             }
         });
		return filteredImageView;
	}

	public FilteredImageView getFilteredImageView(BufferedImage src, Filter type, int width, int height){
		BufferedImage resizedSource = resizeImage(src, width, height);
		
		BufferedImage filteredImage = getFilteredImage(resizedSource, type);
		
		FilteredImageView filteredImageView = new FilteredImageView(SwingFXUtils.toFXImage(filteredImage, null));
		filteredImageView.setFilter(type);
		filteredImageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

             @Override
             public void handle(MouseEvent mouseEvent) {

                 if(mouseEvent.getButton().equals(MouseButton.PRIMARY)){

                	 FilteredImageView selectedImageView = (FilteredImageView)mouseEvent.getSource();
                	 
                     System.out.println(selectedImageView.getFilter());
                     showLarge(src, selectedImageView.getFilter());
                 }
             }
         });
		return filteredImageView;
	}
	
	public void showLarge(BufferedImage src, Filter filter){
		
		FilteredImageView previewLarge = getFilteredImageView(src, filter);
		//previewLarge.fitWidthProperty().bind(borderPanePreview.getWidth()); 
		
		// previewLarge.s(borderPanePreview.getWidth());
		borderPanePreview.setCenter(previewLarge);
	}



}
