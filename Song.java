package gui_player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class Song extends HBox {
    
    public String playlist,artist, title;
    
    private ImageView img;
    private Label label;
    private File song;
    public int trackid;
    
    public Song(int trackid, String playlist, String artist, String title )
    {
        this.trackid = trackid;
        this.getStyleClass().add("song");
        
        this.playlist = playlist;
        this.artist = artist;
        this.title = title;
        
        this.img = new ImageView(new Image(new File("src/gui_player/tracks/"+ trackid +"/preview.png").toURI().toString()));
        this.img.setFitHeight(60);
        this.img.setFitWidth(60);
        
        this.set_custom_class();
        
        this.getChildren().add(img);
        label = new Label(this.artist + "\n" +this.title);
        this.getChildren().add(label);
        
    }
    
    private void set_custom_class()
    {
        this.getStyleClass().add("song");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10);
    }
}
