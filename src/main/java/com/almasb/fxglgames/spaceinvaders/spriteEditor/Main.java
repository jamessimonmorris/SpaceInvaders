package com.almasb.fxglgames.spaceinvaders.spriteEditor;

import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

@SuppressWarnings("unused")
public class Main extends Application {
	private final String title = "Sprite Editor v0.8";
	
	@Override
	public void start(Stage primaryStage) {
		try {
			BorderPane root = new BorderPane();
			
			Scene scene = new Scene(root, 536, 590);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
		    setUserAgentStylesheet(STYLESHEET_MODENA);
			
			Parent content = FXMLLoader.load(getClass().getClassLoader().getResource("16Layout.fxml"));
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
