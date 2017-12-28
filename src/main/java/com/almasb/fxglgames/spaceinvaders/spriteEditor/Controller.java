package com.almasb.fxglgames.spaceinvaders.spriteEditor;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

public class Controller {
	
	@FXML ColorPicker colorPicker;
	@FXML GridPane grid;
	@FXML ComboBox<String> gridSize;
	@FXML Button loadBut;
	String style;
	String selected = null;
	
	public GridPane getGrid() {
		return grid;
	}
	
	public void initialize() {
		ObservableList<String> gridOptions = FXCollections.observableArrayList(
					"16x16 grid",
					"32x32 grid",
					"64x64 grid"
				);
		gridSize.setItems(gridOptions);
		gridSize.getSelectionModel().selectFirst();
	}
	
	@FXML public String getGridOption() {
		if ((String)(gridSize.getSelectionModel().getSelectedItem()) == "16x16 grid") {
			System.out.println("16x16");
		} else if ((String)(gridSize.getSelectionModel().getSelectedItem()) == "32x32 grid") {
			System.out.println("32x32");
		} else {
			System.out.println("64x64");
		}
		
		selected = (String)(gridSize.getSelectionModel().getSelectedItem());
		return selected;
	}
	
	@FXML public void onButtonPress(ActionEvent event) {
        Button x = (Button) event.getSource();
		x.setStyle("-fx-background-color: "+colorPicker.getValue().toString().replace("0x", "#"));
		style = x.getStyle();
	}
	
	@FXML public void onMouseEnter(MouseEvent event) {
		Button x = (Button) event.getSource();
		style = x.getStyle();
		x.setStyle("-fx-background-color: "+colorPicker.getValue().toString().replace("0x", "#"));
	}
	
	@FXML public void onMouseExit(MouseEvent event) {
		Button x = (Button) event.getSource();
		x.setStyle(style);
	}
	
	@FXML public void onLoadBut() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.setTitle("Open Resource File");
		
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("png Files (*.png)", "*.png");
		fileChooser.getExtensionFilters().add(extFilter);
		
		fileChooser.showOpenDialog(gridSize.getScene().getWindow());
	}
	
	@FXML public void onSaveBut() {
		FileChooser fileChooser = new FileChooser();
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));
		
		this.grid.setGridLinesVisible(false);
		
        WritableImage image = this.grid.snapshot(new SnapshotParameters(), null);
        
		this.grid.setGridLinesVisible(true);
		
        File file = fileChooser.showSaveDialog(null);
		
        if (file != null) {
        	try {
        		ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
                System.out.println("snapshot saved: " + file.getAbsolutePath());
            } catch (IOException ex) {
            	ex.printStackTrace();
            }
        }
	}
}
