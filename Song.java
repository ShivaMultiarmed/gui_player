package gui_player;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaException;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class Song extends HBox {
    
    public String playlist,artist, title;
    
    private ImageView img;
    private Label label;
    private Duration dur;
    private File song;
    public int trackid;
    
    public Song(int trackid, String playlist, String artist, String title )
    {
        this.trackid = trackid;
        this.getStyleClass().add("song");
        
        this.playlist = playlist;
        this.artist = artist;
        this.title = title;
        
        this.img = new ImageView(new Image("http://guiplayer/tracks/"+ trackid +"/preview.png"));
        this.img.setFitHeight(60);
        this.img.setFitWidth(60);
        Rectangle clip = new Rectangle();
        clip.setWidth(img.getFitWidth());
        clip.setHeight(img.getFitHeight());
        clip.setArcHeight(7);
        clip.setArcWidth(7);
        img.setClip(clip);
        
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
