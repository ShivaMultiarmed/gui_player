package gui_player;

import java.io.File;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class Aside extends ScrollPane {
    
    public VBox content;
    
    public Aside()
    {
        this.setId("aside");
        
        this.hbarPolicyProperty().setValue(ScrollBarPolicy.NEVER);
        this.vbarPolicyProperty().setValue(ScrollBarPolicy.AS_NEEDED);
        
        this.fitToWidthProperty().set(true);
        
        content = new VBox();
        this.setContent(content);
        
        File dir = new File("src/gui_player/playlists/");
        File[] arr = dir.listFiles();
        
        for (File f : arr)
        {
            PlayList p = new PlayList(f.getName());
            content.getChildren().add(p);
        }
            
        
        content.setSpacing(5);
    }
}
