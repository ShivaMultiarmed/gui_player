package gui_player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;

public class Aside extends ScrollPane {
    
    public VBox content;
    private Connection con;
    private int userid;
    public BooleanProperty arePlaylistsChanged;
    
    public Aside(Connection connection, int userid)
    {
        con = connection;
        this.userid = userid;
        this.setId("aside");
        
        this.hbarPolicyProperty().setValue(ScrollBarPolicy.NEVER);
        this.vbarPolicyProperty().setValue(ScrollBarPolicy.AS_NEEDED);
        
        this.fitToWidthProperty().set(true);
        
        content = new VBox();
        content.getStyleClass().add("container");
        this.setContent(content);
        
        addAllPlaylists();
        
        content.setSpacing(15);
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        
        arePlaylistsChanged = new SimpleBooleanProperty(false);
    }
    private VBox makeAddBox()
    {
        VBox box = new VBox();
        Label label = new Label("+");
        box.setAlignment(Pos.CENTER);
        box.getChildren().add(label);
        box.setId("addPlaylist");
        box.addEventHandler(MouseEvent.MOUSE_CLICKED, 
            (MouseEvent e)->{
                AddPlaylistBox b = new AddPlaylistBox(con, userid);
                b.complete.addListener(
                        com -> {
                            if (((BooleanProperty)com).get())
                            {    
                                addAllPlaylists();
                                arePlaylistsChanged.set(true);
                            }
                        }
                );
            }
        );
        
        return box;
    }
   
    private void addAllPlaylists()
    {
        if (!content.getChildren().isEmpty())
            content.getChildren().clear();
        content.getChildren().add(makeAddBox());
        
        try
        {
            Statement stmt = con.createStatement();
            ResultSet set=stmt.executeQuery("SELECT * FROM `playlists` WHERE `userid` = " + userid);
            while (set.next())
            {
                int id = set.getInt("id");
                String title = set.getString("title");
                PlayList p = new PlayList(con,id,userid, title);
                content.getChildren().add(p);
            }
            set.close();
            stmt.close();
        }
        catch(SQLException ex)
        {}
    }
}
