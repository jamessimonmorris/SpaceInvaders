package com.almasb.fxglgames.spaceinvaders.spriteEditor;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.control.Button;

public class Controller {
	
	@FXML ColorPicker colorPicker;
	@FXML GridPane grid;
	@FXML ComboBox<String> gridSize;
	String style;
	
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
		
		String selected = (String)(gridSize.getSelectionModel().getSelectedItem());
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
}
