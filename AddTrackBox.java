package gui_player;

import java.awt.event.MouseListener;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


public class AddTrackBox extends Stage  {
    
    public final LongProperty chosenPlaylistId;
    
    private final Scene scene;
    private final VBox root;
    
    private final ToggleGroup group;
    private final HashMap<Long,RadioButton> playlists;
    
    
    public AddTrackBox(Connection c, long userId) // not file id
    {
        chosenPlaylistId = new SimpleLongProperty(0);
        
        root = new VBox();
        scene = new Scene(root);
        setScene(scene);
        
        group = new ToggleGroup();
        playlists = getPlaylists(c, userId);
        addPlaylists();
        
        setStyle();
    }
    private HashMap<Long, RadioButton> getPlaylists(Connection c, long userId)
    {
        HashMap<Long, RadioButton> hashmap = new HashMap<>();
        try
        {
            Statement stmt = c.createStatement();
            String sql = "SELECT * FROM `playlists`"
                    + "WHERE `userid` = "+userId+";";
            ResultSet set = stmt.executeQuery(sql);
            while(set.next())
            {
                long pId = set.getLong("id"); // playlistid
                String title = set.getString("title");
                if (title == null)
                    title = "Моя музыка";
                RadioButton r = new RadioButton(title);
                hashmap.put(pId, r);
            }
            set.close();
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        
        return hashmap;
    }
    private void addPlaylists()
    {
        for (Map.Entry<Long, RadioButton> entry : playlists.entrySet())
        {
            RadioButton r = entry.getValue();
            long pId = entry.getKey(); // playlistId
            r.setOnMouseClicked(new Listener(pId));
            root.getChildren().add(r);
        }
    }
    private class Listener implements EventHandler
    {
        private final long pId;
        public Listener(long pId)
        {
            this.pId = pId;
        }
        @Override
        public void handle(Event event) {
            System.out.println("playlistid: " + pId);
            chosenPlaylistId.set(pId);
        }
    }
    private void setStyle()
    {
        setWidth(300);
        setHeight(300);
        root.setPadding(new Insets(10));
        root.setSpacing(10);
        scene.getStylesheets().add(getClass().getResource("css/auth.css").toExternalForm());
        for (Map.Entry<Long, RadioButton> entry : playlists.entrySet())
        {
            entry.getValue().getStyleClass().add("radio");
        }
    }
}
