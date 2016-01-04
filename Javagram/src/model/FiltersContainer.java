package model;

import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;

public class FiltersContainer {
	
	private static final int HEIGHT = 100;
	
	private String path;
	private BufferedImage sourceImage;
	private BufferedImage sourceImageSmall;
	private BufferedImage targetImage;
	private List<BufferedImage> targetImagesSmall;
	
	private Task<ObservableList<BufferedImage>> taskPreviews;
	
	public FiltersContainer(String path) {
		this.path = path;
		
		try {
			this.sourceImage = ImageIO.read(new File(path));
			this.sourceImageSmall = this.sourceImage;
			// this.sourceImageSmall = scaleBufferedImage(sourceImage, sourceImage.getWidth(), HEIGHT);
			
			File outputfile = new File("saved.png");

	        try {
				ImageIO.write(sourceImageSmall, "png", outputfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		setTaskPreviews(new Task<ObservableList<BufferedImage>>() {
	         @Override protected ObservableList<BufferedImage> call() throws Exception {
	             //updateMessage("Creating Rectangles");
	        	 
	        	 System.out.println("Task started");
	             ObservableList<BufferedImage> previews = FXCollections.observableArrayList(); 
	             
	            previews.add(getFilteredImage(sourceImageSmall, Filter.NONE));
	         	previews.add(getFilteredImage(sourceImageSmall, Filter.EMBOSS));
	         	previews.add(getFilteredImage(sourceImageSmall, Filter.GRAYSCALE));
//	         	previews.add(getFilteredImage(sourceImageSmall, Filter.OPACITY));
//	         	previews.add(getFilteredImage(sourceImageSmall, Filter.REDUCENOICE));
//	         	previews.add(getFilteredImage(sourceImageSmall, Filter.SHARPEN));
//	         	previews.add(getFilteredImage(sourceImageSmall, Filter.TILEIMAGE));
//	         	previews.add(getFilteredImage(sourceImageSmall, Filter.TRITONE));
//	         	previews.add(getFilteredImage(sourceImageSmall, Filter.VARIABLEBLUR));
//	         	previews.add(getFilteredImage(sourceImageSmall, Filter.WEAVE));
	         	System.out.println("Task ended");
	             return previews;
	         }
	     });
	}
	
	public BufferedImage getSourceImage() {
		return sourceImage;
	}
	
	public BufferedImage getSourceImageSmall() {
		return sourceImageSmall;
	}

	public BufferedImage getTargetImage() {
		return targetImage;
	}
	
	public List<BufferedImage> getTargetImagesSmall() {
		

		 
		return null;
	}
	
	public void setTargetImagesSmall(List<BufferedImage> targetImagesSmall) {
		this.targetImagesSmall = targetImagesSmall;
	}
	
	/**
    *
    * @param image The image to be scaled
    * @param imageType Target image type, e.g. TYPE_INT_RGB
    * @param newWidth The required width
    * @param newHeight The required width
    *
    * @return The scaled image
    */
    public static BufferedImage scaleImage(BufferedImage image, int newWidth, int newHeight) {
        // Make sure the aspect ratio is maintained, so the image is not distorted
        double thumbRatio = (double) newWidth / (double) newHeight;
        int imageWidth = image.getWidth(null);
        int imageHeight = image.getHeight(null);
        double aspectRatio = (double) imageWidth / (double) imageHeight;

        if (thumbRatio < aspectRatio) {
            newHeight = (int) (newWidth / aspectRatio);
        } else {
            newWidth = (int) (newHeight * aspectRatio);
        }

        BufferedImage newImage = new BufferedImage(newWidth, newHeight,  BufferedImage.SCALE_SMOOTH);

        return newImage;
    }
    
    private static BufferedImage scaleBufferedImage(BufferedImage sourceImage,
    	    int maxWidth, int maxHeight) {
    	// Calculate scaled image's dimensions
    	float calculatedWidth = 0;
    	float calculatedHeight = 0;

    	float aspectRatio = (float) sourceImage.getWidth()
    	        / (float) sourceImage.getHeight();

    	if (aspectRatio > 1) {
    	    calculatedWidth = maxWidth;
    	    calculatedHeight = maxWidth / aspectRatio;

    	    if (calculatedHeight > maxHeight) {
    		calculatedWidth = maxHeight * aspectRatio;
    		calculatedHeight = calculatedWidth / aspectRatio;
    	    }
    	} else {
    	    calculatedWidth = maxHeight * aspectRatio;
    	    calculatedHeight = maxHeight;

    	    if (calculatedWidth > maxWidth) {
    		calculatedHeight = maxWidth / aspectRatio;
    		calculatedWidth = calculatedHeight * aspectRatio;
    	    }
    	}

    	// Create empty image with new dimensions
    	BufferedImage scaledImage = new BufferedImage((int) calculatedWidth,
    	        (int) calculatedHeight, BufferedImage.TYPE_INT_RGB);

    	Graphics2D graphics = (Graphics2D) scaledImage.getGraphics();
    	graphics.setComposite(AlphaComposite.Src);

    	// Draw to scaled image
    	graphics.drawImage(sourceImage, 0, 0, (int) calculatedWidth,
    	        (int) calculatedHeight, null);

    	graphics.dispose();

    	return scaledImage;
        }

    
	public String getPath() {
		return path;
	}

	public void generatePreviews(){
		
		 

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

public Task<ObservableList<BufferedImage>> getTaskPreviews() {
	return taskPreviews;
}

public void setTaskPreviews(Task<ObservableList<BufferedImage>> taskPreviews) {
	this.taskPreviews = taskPreviews;
}
	
	
}
