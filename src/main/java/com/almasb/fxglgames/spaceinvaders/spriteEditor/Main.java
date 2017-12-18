package com.almasb.fxglgames.spaceinvaders.spriteEditor;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;

public class Main extends Application {
	private final String title = "Sprite Editor v0.2.1";
	
	protected Color colorPicker() {
		
		
		return null;
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			Scene scene = new Scene(root,550,610);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		    setUserAgentStylesheet(STYLESHEET_MODENA);
			
			Parent content = FXMLLoader.load(getClass().getClassLoader().getResource("Layout.fxml"));
			root.setCenter(content);
		    
			primaryStage.setTitle(title);
			primaryStage.setResizable(false);
			
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
}
