package gui_player;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

public class Aside extends ScrollPane {
    
    public VBox content;
    
    public Aside(Connection connection, int userid)
    {
        this.setId("aside");
        
        this.hbarPolicyProperty().setValue(ScrollBarPolicy.NEVER);
        this.vbarPolicyProperty().setValue(ScrollBarPolicy.AS_NEEDED);
        
        this.fitToWidthProperty().set(true);
        
        content = new VBox();
        this.setContent(content);
        
        try
        {
            Statement stmt = connection.createStatement();
            ResultSet set=stmt.executeQuery("SELECT * FROM `playlists` WHERE `userid` = " + userid);
                
            while (set.next())
            {
                int id = set.getInt("id");
                String title = set.getString("title");
                PlayList p = new PlayList(id, title);
                content.getChildren().add(p);
            }
            
            set.close();
            stmt.close();
        }
        catch(SQLException ex)
        {}
        
        content.setSpacing(5);
    }
    
}
