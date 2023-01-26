package gui_player;

import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

public class PlaylistDialog extends Stage {
    
    private int userid;
    private Scene dialogscene;
    private GridPane grid;
    
    private TextField title;
    private Button choosefiles;
    
    public PlaylistDialog(int userid)
    {
        this.userid = userid;
        this.grid = new GridPane();
        this.dialogscene = new Scene(this.grid);
        this.setScene(dialogscene);
        this.setCustomLayout();
        this.show();
    }
    private void setCustomLayout()
    {
        title = new TextField();
        grid.add(title,0, 0);
        
        choosefiles = new Button("Choose tracks");
        grid.add(choosefiles, 0,1);
    }
    
}
