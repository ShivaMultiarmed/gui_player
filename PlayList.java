package gui_player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class PlayList extends VBox {
    
    public String title;
    
    public PlayList (String title)
    {
        
        this.setCustomStyle();
        this.getStyleClass().add("playlist");
        
        try
        {
            ImageView img = new ImageView(new Image(new FileInputStream("src/gui_player/playlists/"+title+"/preview.png")));
            img.setFitHeight(100);
            img.setFitWidth(100);
            this.getChildren().add(img);
        }
        catch(FileNotFoundException e)
        {
        }
        this.getChildren().add(new Label(title));
        this.title = title;
    }
    private void setCustomStyle()
    {
        this.setAlignment(Pos.CENTER);
    }
}
