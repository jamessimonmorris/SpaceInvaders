package com.almasb.fxglgames.spaceinvaders.spriteEditor;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Optional;

import javax.imageio.ImageIO;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

public class Controller {
	
	@FXML ComboBox<String> gridSize;
	@FXML ComboBox<String> optionBox;
	@FXML ColorPicker colorPicker;
	@FXML Button loadBut;
	@FXML GridPane grid;
	String selected = null;
	String style;
	int pixels = 0;
	int mouseClicked = 0; // 1 if mouse is currently being held
	
	public void initialize() {
		ObservableList<String> gridOptions = FXCollections.observableArrayList(
					"16x16 grid",
					"32x32 grid",
					"64x64 grid"
				);
		gridSize.setItems(gridOptions);
		gridSize.getSelectionModel().selectFirst();
		pixels = 16;
		
		ObservableList<String> options = FXCollections.observableArrayList(
				"Save",
				"Load",
				"Reset"
			);
		optionBox.setItems(options);
	}
	
	@FXML private void onButtonPress(ActionEvent event) {
        Button x = (Button) event.getSource();
		x.setStyle("-fx-background-color: "+colorPicker.getValue().toString().replace("0x", "#"));
		style = x.getStyle();
	}
	
	@FXML private void onMouseEnter(MouseEvent event) {
		Button x = (Button) event.getSource();
		style = x.getStyle();
		x.setStyle("-fx-background-color: "+colorPicker.getValue().toString().replace("0x", "#"));
	}
	
	@FXML private void onMouseExit(MouseEvent event) {
		Button x = (Button) event.getSource();
		x.setStyle(style);
	}
	
	private void onSave() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));
		fileChooser.setTitle("Save Sprite");
		
		this.grid.setGridLinesVisible(false);
		
        WritableImage image = new WritableImage(pixels, pixels);
        
        SnapshotParameters spa = new SnapshotParameters();
        spa.setFill(Color.TRANSPARENT);
        
	    PixelReader pr = this.grid.snapshot(spa, null).getPixelReader();
	    PixelWriter pw = image.getPixelWriter();
	    
	    int cellWidth = 480/pixels;
	    
	    for (int y = 0; y < pixels; y++) {
	    	for (int x = 0; x < pixels; x++) {
	    		Color color = pr.getColor(x*cellWidth+1, y*cellWidth+1);
	    		pw.setColor(x, y, color);
	    	}
	    }
	    
		this.grid.setGridLinesVisible(true);
		
        File file = fileChooser.showSaveDialog(null);
		
        if (file != null) {
        	try {
        		ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                System.out.println("Image saved: " + file.getAbsolutePath());
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
	}
	
	// load image to writable, scale it down, then pixel read/write
	private void onLoad() {
		onReset();
		
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Load Image");
		
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("png Files (*.png)", "*.png");
		fileChooser.getExtensionFilters().add(extFilter);
		
		File file = fileChooser.showOpenDialog(grid.getScene().getWindow());
		
		if (file != null) {
			try {
				BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                
                int imgWidth = (int) image.getWidth();
                int imgHeight = (int) image.getHeight();
                
                System.out.println("Loaded image is "+imgWidth+"x"+imgHeight+".");
                
                if (imgWidth >= 64 || imgHeight >= 64) {
                	gridSize.getSelectionModel().select(2);
                } else if (imgWidth >= 32 || imgHeight >= 32) {
                	gridSize.getSelectionModel().select(1);
                } else {
                	gridSize.getSelectionModel().select(0);
                }
                
        		SnapshotParameters spa = new SnapshotParameters();
                spa.setTransform(javafx.scene.transform.Transform.scale(pixels/imgWidth, pixels/imgHeight));
                
                PixelReader pr = image.getPixelReader();
        	    
        	    int cellWidth = imgWidth/pixels;
        	    int cellHeight = imgHeight/pixels;
        	    
        	    for (int y = 0; y < pixels; y++) {
            	    int index = y*pixels;
        	    	for (int x = 0; x < pixels; x++) {
        	    		Color color = pr.getColor(y*cellWidth, x*cellHeight);
        	    		
        	    		Button b = (Button) this.grid.getChildren().get(index);
        	    		b.setStyle("-fx-background-color: "+color.toString().replace("0x", "#"));
        	    		
        	    		index++;
        	    	}
        	    }
        	    
    			Platform.runLater(() -> optionBox.getSelectionModel().clearSelection());
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
	}
	
	private void onReset() {
		for (int i = 0; i < pixels*pixels; i++) {
			Button x = (Button) this.grid.getChildren().get(i);
			x.setStyle("-fx-background-color: transparent");
		}
	}
	
	private void loadConfirm() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Load Confirmation");
		alert.setHeaderText(null);
		alert.setContentText("This will delete your current image.\nAre you sure you want to continue?");

		Optional<ButtonType> result = alert.showAndWait();
		
		if (result.get() == ButtonType.OK) {
			Alert warning = new Alert(AlertType.WARNING);
			warning.setTitle("Warning");
			warning.setHeaderText(null);
			warning.setContentText("Best results achieved when loading small sprites");
			warning.showAndWait();
			
		    onLoad();
			Platform.runLater(() -> optionBox.getSelectionModel().clearSelection());
		}
	}
	
	private void resetConfirm() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Reset Confirmation");
		alert.setHeaderText(null);
		alert.setContentText("This will delete your current image.\nAre you sure you want to continue?");

		Optional<ButtonType> result = alert.showAndWait();
		
		if (result.get() == ButtonType.OK) {
		    onReset();
		}
	}
	
	@FXML private void optionSelect() {
		if ((String)(optionBox.getSelectionModel().getSelectedItem()) == "Save") {
			onSave();
			Platform.runLater(() -> optionBox.getSelectionModel().clearSelection());
		} else if ((String)(optionBox.getSelectionModel().getSelectedItem()) == "Load") {
			loadConfirm();
			Platform.runLater(() -> optionBox.getSelectionModel().clearSelection());
		} else if ((String)(optionBox.getSelectionModel().getSelectedItem()) == "Reset") {
			resetConfirm();
			Platform.runLater(() -> optionBox.getSelectionModel().clearSelection());
		}
	}
	
	@FXML private void gridSelect() {
		if ((String)(gridSize.getSelectionModel().getSelectedItem()) == "16x16 grid") {
			System.out.println("16x16");
			pixels = 16;
		} else if ((String)(gridSize.getSelectionModel().getSelectedItem()) == "32x32 grid") {
			System.out.println("32x32");
			pixels = 32;
		} else if ((String)(gridSize.getSelectionModel().getSelectedItem()) == "64x64 grid") {
			System.out.println("64x64");
			pixels = 64;
		}
		selected = (String)(gridSize.getSelectionModel().getSelectedItem());
	}
}
