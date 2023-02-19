package gui_player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Song extends HBox {
    
    public String playlist,artist, title;
    
    private boolean isMine;
    private ImageView img;
    private Label title_label, artist_label;
    private Duration dur;
    private File song;
    public int trackid;
    public VBox info;
    
    public Song(int trackid, String playlist, String artist, String title )
    {
        this.trackid = trackid;
        this.getStyleClass().add("song");
        
        this.playlist = playlist;
        this.artist = artist;
        this.title = title;
        
        /*
        try {
            
        } catch (FileNotFoundException ex) {
            this.img = new ImageView(new Image(new File("src/assets/icons/music.png").toURI().toString()));
        }*/
        this.img = new ImageView(new Image("http://guiplayer/tracks/"+trackid + "/preview.png"));
            
        this.img.setFitHeight(60);
        this.img.setFitWidth(60);
        Rectangle clip = new Rectangle();
        clip.setWidth(img.getFitWidth());
        clip.setHeight(img.getFitHeight());
        clip.setArcHeight(7);
        clip.setArcWidth(7);
        img.setClip(clip);
        this.set_custom_style();
        
        this.getChildren().add(img);
        
        info = new VBox();
        this.getChildren().add(info);
        info.setAlignment(Pos.CENTER_LEFT);
        
        title_label = new Label(this.title);
        title_label.setStyle("-fx-text-fill: #333333; -fx-font-size: 18px; -fx-font-weight: bold;");
        info.getChildren().add(title_label);
        
        artist_label = new Label(this.artist);
        artist_label.setStyle("-fx-text-fill: #555555; -fx-font-size: 15px; -fx-font-weight: normal;");
        info.getChildren().add(artist_label);
        
    }
    
    private void set_custom_style()
    {
        this.getStyleClass().add("song");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10);
    }
    
   
}
