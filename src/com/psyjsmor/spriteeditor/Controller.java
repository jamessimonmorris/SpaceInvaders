package com.psyjsmor.spriteeditor;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.scene.layout.AnchorPane;

public class Controller {
	
	@FXML AnchorPane rootPane;
	@FXML ComboBox<String> gridSize;
	@FXML ComboBox<String> optionBox;
	@FXML ColorPicker colorPicker;
	@FXML ColorPicker bgColor;
	@FXML GridPane grid;
	String[] bgBuffer;
	String selected = null;
	String style;
	static int pixels = 16;
	static int loadingImage = 0; // 0 -> not loading, 1 -> loading image
	static File file = null;
	int gridIndexBuf = 0;
	
	public void initialize() {
		System.out.println("pixels: "+pixels);
		ObservableList<String> gridOptions = FXCollections.observableArrayList(
					"16x16 grid",
					"32x32 grid",
					"64x64 grid"
				);
		gridSize.setItems(gridOptions);
		
		if (pixels == 16) {
			gridSize.getSelectionModel().select(0);
			gridIndexBuf = 0;
		} else if (pixels == 32) {
			gridSize.getSelectionModel().select(1);
			gridIndexBuf = 1;
		} else if (pixels == 64) {
			gridSize.getSelectionModel().select(2);
			gridIndexBuf = 2;
		}
		
		bgBuffer = new String[pixels*pixels];
		
		ObservableList<String> options = FXCollections.observableArrayList(
				"Save",
				"Load",
				"Reset"
			);
		
		optionBox.setItems(options);
		
		for (int i = 0; i < pixels*pixels; i++) {
			Button x = (Button) this.grid.getChildren().get(i);
			bgBuffer[i] = x.getStyle();
		}
		
		if (loadingImage == 1) {
			onLoad();
		}
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
	
	// find current bg color, replace all relevant cells with new color, make new color = bgBuf
	@FXML private void bgChange() {
		for (int i = 0; i < pixels*pixels; i++) {
			Button x = (Button) this.grid.getChildren().get(i);
			
			if (x.getStyle() == bgBuffer[i]) {
				x.setStyle("-fx-background-color: "+bgColor.getValue().toString().replace("0x", "#"));
				bgBuffer[i] = x.getStyle();
			}
		}
	}
	
	private void onSave() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Image Files (*.png, *.jpg)", "*.png", "*.jpg"));
		fileChooser.setTitle("Save Sprite");
		
		this.grid.setGridLinesVisible(false);
		
        WritableImage image = new WritableImage(pixels, pixels);
        
        SnapshotParameters spa = new SnapshotParameters();
        spa.setFill(Color.TRANSPARENT);
        
	    PixelReader pr = this.grid.snapshot(spa, null).getPixelReader();
	    PixelWriter pw = image.getPixelWriter();
	    
	    int cellWidth;
	    
	    if (pixels != 64)
	    	cellWidth = 480/pixels;
	    else
	    	cellWidth = 512/pixels;
	    
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
                System.out.println("Image saved in: " + file.getAbsolutePath());
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
	}
	
	// load image to writable, scale it down, then pixel read/write
	private void onLoad() {
		if (loadingImage == 0) {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Load Image");
			
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("Image Files (*.png, *.jpg)", "*.png", "*.jpg");
			fileChooser.getExtensionFilters().add(extFilter);
			
			file = fileChooser.showOpenDialog(grid.getScene().getWindow());
		}
		
		if (file != null) {
			try {
				onReset();
				
				bgColor.setDisable(true);
				BufferedImage bufferedImage = ImageIO.read(file);
                Image image = SwingFXUtils.toFXImage(bufferedImage, null);
                
                int imgWidth = (int) image.getWidth();
                int imgHeight = (int) image.getHeight();
                
                System.out.println("Loaded image is "+imgWidth+"x"+imgHeight+".");
                
                if (imgWidth > 32 || imgHeight > 32) {
                	loadingImage = 1;
                	gridSize.getSelectionModel().select(2);
                } else if (imgWidth > 16 || imgHeight > 16) {
                	loadingImage = 1;
                	gridSize.getSelectionModel().select(1);
                } else {
                	loadingImage = 1;
                	gridSize.getSelectionModel().select(0);
                }
                
                loadingImage = 0;
                
        		SnapshotParameters spa = new SnapshotParameters();
                spa.setTransform(javafx.scene.transform.Transform.scale(pixels/imgWidth, pixels/imgHeight));
                
                PixelReader pr = image.getPixelReader();
                
                System.out.println("imgWidth: "+imgWidth+" | imgHeight: "+imgHeight+" | pixels: "+pixels);
        	    
        	    float cellWidth = (float)imgWidth/pixels;
        	    float cellHeight = (float)imgHeight/pixels;
        	    
        	    System.out.println("cellWidth: "+cellWidth+" | cellHeight: "+cellHeight);
        	    
        	    for (int y = 0; y < pixels; y++) {
            	    int index = y*pixels;
        	    	for (int x = 0; x < pixels; x++) {
        	    		Color color = pr.getColor(Math.round(y*cellWidth), Math.round(x*cellHeight));
        	    		
        	    		Button b = null;
        	    		
        	    		if (index < pixels*pixels) {
        	    			b = (Button) this.grid.getChildren().get(index);
        	    			System.out.println(grid.getChildren().get(index));
            	    		b.setStyle("-fx-background-color: "+color.toString().replace("0x", "#"));
        	    		}
        	    		
        	    		System.out.println(index);
        	    		
        	    		index++;
        	    	}
        	    }
        	    
    			Platform.runLater(() -> optionBox.getSelectionModel().clearSelection());
    			file = null;
            } catch (IOException ex) {
//            	ex.printStackTrace();
            }
        }
	}
	
	private void onReset() {
		for (int i = 0; i < pixels*pixels; i++) {
			Button x = (Button) this.grid.getChildren().get(i);
			x.setStyle("-fx-background-color: transparent");
			bgBuffer[i] = x.getStyle();
		}
		
		bgColor.setValue(Color.WHITE);
		bgColor.setDisable(false);
	}
	
	private void loadConfirm() {
		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Load Confirmation");
		alert.setHeaderText(null);
		alert.setContentText("This will delete your current image.\nAre you sure you want to continue?");
	
		Optional<ButtonType> result = alert.showAndWait();
		
		if (result.get() == ButtonType.OK) {
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
	
	@FXML private void gridConfirm() throws IOException {
		if (loadingImage == 0) {
			if (!gridSize.getSelectionModel().isSelected(gridIndexBuf)) {
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Grid Size Change Confirmation");
				alert.setHeaderText(null);
				alert.setContentText("This will delete your current image.\nAre you sure you want to continue?");
				
				Optional<ButtonType> result = alert.showAndWait();
				
				if (result.get() == ButtonType.OK) {
				    gridSelect();
				} else {
					gridSize.getSelectionModel().select(gridIndexBuf);
				}
			}
		} else {
			gridSelect();
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
	
	private void gridSelect() throws IOException {
		if ((String)(gridSize.getSelectionModel().getSelectedItem()) == "16x16 grid") {
			System.out.println("Grid Selected: 16x16");
			pixels = 16;
			
			AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("16Layout.fxml"));
			rootPane.getChildren().setAll(pane);
		} else if ((String)(gridSize.getSelectionModel().getSelectedItem()) == "32x32 grid") {
			System.out.println("Grid Selected: 32x32");
			pixels = 32;
			
			AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("32Layout.fxml"));
			rootPane.getChildren().setAll(pane);
		} else if ((String)(gridSize.getSelectionModel().getSelectedItem()) == "64x64 grid") {
			System.out.println("Grid Selected: 64x64");
			pixels = 64;
			
			AnchorPane pane = FXMLLoader.load(getClass().getClassLoader().getResource("64Layout.fxml"));
			rootPane.getChildren().setAll(pane);
		}
		
		bgBuffer = new String[pixels*pixels];
		selected = (String)(gridSize.getSelectionModel().getSelectedItem());
	}
}
