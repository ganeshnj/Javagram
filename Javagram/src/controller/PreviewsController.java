package controller;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
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

import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.CustomImageView;
import model.Filter;

public class PreviewsController {

    @FXML
    private ScrollPane scrollPanePreviews;

    @FXML
    private ImageView imageViewLarge;

    @FXML
    private BorderPane borderPanePreview;

	private File sourceFile;
	private BufferedImage sourceImage;
	private Stage stage;
	private ArrayList<Filter> filters = new ArrayList<>();
	
    @FXML
    void saveAction(ActionEvent event) {
    	   	
    	  FileChooser fileChooser = new FileChooser();
          fileChooser.setTitle("Save Image");
          
          File file = fileChooser.showSaveDialog(stage);
          if (file != null) {
    	  try {
              ImageIO.write(SwingFXUtils.fromFXImage(imageViewLarge.getImage(), null), "png", file);
          } catch (IOException e) {
              // TODO Auto-generated catch block
              e.printStackTrace();
          }

          }

    }

	@FXML
	void initialize() {
		imageViewLarge.setImage(SwingFXUtils.toFXImage(sourceImage, null));
		
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

		final Task<ArrayList<CustomImageView>> taskPreviews = new Task<ArrayList<CustomImageView>>() {

			@Override
			protected ArrayList<CustomImageView> call() throws Exception {
				
				System.out.println("Prevuew Generation started");
				
				ArrayList<CustomImageView> previews = new ArrayList<>();

				int i = 1;

				for (Filter filter : filters) {

					if (isCancelled()) {
						break;
					}

					previews.add(createCustomFilteredView(sourceImage, filter, 100, 100));
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

				System.out.println("Preview generation finished");
				
				HBox hBox = new HBox();
				scrollPanePreviews.setStyle("-fx-background-color: DAE6F3;");
				hBox.setPadding(new Insets(15, 15, 15, 15));
				hBox.setSpacing(15);
				// tile.setHgap(15);

				hBox.getChildren().addAll(taskPreviews.getValue());

				scrollPanePreviews.setContent(hBox);
			}
		});

		new Thread(taskPreviews).start();
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
		
		File file = new File("source.jpg");
		  try {
          ImageIO.write(sourceImage, "jpg", file);
      } catch (IOException ex) {
          System.out.println(ex.getMessage());
      }
	}

	public static CustomImageView createCustomFilteredView(BufferedImage src, Filter type) {
		BufferedImage filteredImage = getFilteredImage(src, type);

		CustomImageView filteredImageView = new CustomImageView(SwingFXUtils.toFXImage(filteredImage, null));
		filteredImageView.setFilter(type);
		filteredImageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {

				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

					CustomImageView selectedImageView = (CustomImageView) mouseEvent.getSource();
					System.out.println(selectedImageView.getFilter());
				}
			}
		});
		return filteredImageView;
	}

	public CustomImageView createCustomFilteredView(BufferedImage src, Filter type, int width, int height) {
		BufferedImage resizedSource = resizeImage(src, width, height);

		BufferedImage filteredImage = getFilteredImage(resizedSource, type);

		CustomImageView filteredImageView = new CustomImageView(SwingFXUtils.toFXImage(filteredImage, null));
		filteredImageView.setFilter(type);
		filteredImageView.setOnMouseClicked(new EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent mouseEvent) {

				if (mouseEvent.getButton().equals(MouseButton.PRIMARY)) {

					CustomImageView selectedImageView = (CustomImageView) mouseEvent.getSource();

					System.out.println(selectedImageView.getFilter());
					changePreview(src, selectedImageView.getFilter());
				}
			}
		});
		return filteredImageView;
	}

	public void changePreview(BufferedImage src, Filter filter) {

		CustomImageView previewLarge = createCustomFilteredView(src, filter);
		// previewLarge.fitWidthProperty().bind(borderPanePreview.getWidth());

		// previewLarge.s(borderPanePreview.getWidth());
		imageViewLarge.setImage(previewLarge.getImage());;
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
	
		  
		File file = new File("filtered.jpg");
		  try {
              ImageIO.write(dest, "jpg", file);
          } catch (IOException ex) {
              System.out.println(ex.getMessage());
          }
		  
		return dest;
	}
	
	private static BufferedImage resizeImage(BufferedImage originalImage, int width, int height){
		BufferedImage resizedImage = new BufferedImage(width, height, originalImage.getType());
		Graphics2D g = resizedImage.createGraphics();
		g.drawImage(originalImage, 0, 0, width, height, null);
		g.dispose();
			
		return resizedImage;
	}
}
