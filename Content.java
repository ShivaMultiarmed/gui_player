package gui_player;

import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class Content extends ScrollPane {
    
    public GridPane CONTENT;
    public int r,c, num = 0;
    private int playlistid;
    public String playlist;
    public List<Song> tracks;
    
    public Content(Connection connection,int playlistid) 
    { 
        CONTENT = new GridPane();
       
        this.playlistid = playlistid;
        this.getTracks(connection);
        
        c= 2;
        r = (int) Math.ceil(1.0*num/c);
        
        
        
        for (int i=0;i<c;i++)
        {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100/c);
            CONTENT.getColumnConstraints().add(col);
        }
            
        for (int j=0;j<r;j++)
        {
            CONTENT.getRowConstraints().add(new RowConstraints(80));
        }
            
        this.content_style();
        
        this.setContent(CONTENT);
        addTracks();
    }
    
    private void getTracks(Connection connection)
    {
        try 
        {
            Statement stmt = connection.createStatement();
            String sql = "SELECT `id`, `title`, `artist` FrOM `tracks`\n" +
                "INNER JOIN `tracks_in_playlists`\n" +
                "ON `trackid` = `id`\n" +
                "WHERE `playlistid` =" + playlistid;
            ResultSet set = stmt.executeQuery(sql);
            
            tracks = new ArrayList<Song>();
            
            while (set.next())
            {
                int songid = set.getInt("id");
                String title = set.getString("title");
                String artist = set.getString("artist");
                Song s = new Song(songid, this.playlist, artist, title);
                s.setId((num+1)+"");
                tracks.add(s);
                num++;
            }

            set.close();
            stmt.close();
        }
        catch (SQLException ex)
        {}
    }
    
    private void addTracks()
    {
        for (int i =0;i<num;i++)
        {
            CONTENT.add(tracks.get(i),i%c,i/c);
        }
    }
    private void content_style()
    {
        CONTENT.setId("content");
        this.fitToWidthProperty().set(true);
        CONTENT.setHgap(10);
        CONTENT.setVgap(10);
    }
    
}
