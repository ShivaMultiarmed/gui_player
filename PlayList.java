package gui_player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class PlayList extends VBox {
    
    public String title;
    public int id;
    
    public PlayList (int id, String title)
    {
        this.id = id;
        
        this.setCustomStyle();
        this.getStyleClass().add("playlist");
        
        
        ImageView img = new ImageView(new Image("http://guiplayer/playlists/"+id+"/cover.png"));
        img.setFitHeight(130);
        img.setFitWidth(img.getFitHeight());

        Rectangle clip = new Rectangle();
        clip.setWidth(img.getFitWidth());
        clip.setHeight(img.getFitHeight());
        clip.setArcHeight(20);
        clip.setArcWidth(20);
        img.setClip(clip);

        this.getChildren().add(img);
        
        this.getChildren().add(new Label(title));
        this.title = title;
    }
    private void setCustomStyle()
    {
        this.setAlignment(Pos.CENTER);
    }
}
