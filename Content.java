package gui_player;

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
    private int r,c, num = 0;
    private int playlistid;
    public String playlist;
    public List<Song> tracks;
    
    
    public Content(Connection connection, int playlistid) 
    { 
        this.MainInit();
        this.playlistid = playlistid;
        this.getTracks(connection, null);
        this.setConstraints();
        this.addTracks();
    }
    public Content(Connection connection, String searchquery)
    {
        this.MainInit();
        this.getTracks(connection, searchquery);
        this.setConstraints();
        this.addTracks();
    }
    
    private void MainInit()
    {
        CONTENT = new GridPane();
        this.content_style();
        this.setContent(CONTENT);
    }
    
    private void setConstraints()
    {
        c= 2;
        r = (int) Math.ceil(1.0*num/c);
        
        if (r< 5)
            r =5;
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
    }
    private void getTracks(Connection connection, String searchquery)
    {
        try 
        {
            Statement stmt = connection.createStatement();
            String sql = null;
            if (searchquery == null) 
                sql = "SELECT `id`,`file_id`, `title`, `artist` FrOM `tracks`\n" +
                    "INNER JOIN `tracks_in_playlists`\n" +
                    "ON `trackid` = `id`\n" +
                    "WHERE `playlistid` =" + playlistid;
            else
            {
            
                sql ="SELECT `playlists`.`userid`, `tracks`.`id`, `tracks`.`file_id`, `tracks`.`title`, `tracks`.`artist`, `playlists`.`userid`\n" +
                        "FROM `tracks`\n" +
                        "INNER JOIN `tracks_in_playlists`\n" +
                        "ON `tracks_in_playlists`.`trackid` = `tracks`.`id`\n" +
                        "INNER JOIN `playlists` \n" +
                        "ON `playlists`.`id` = `tracks_in_playlists`.`playlistid`\n" +
                        "WHERE \n" +
                        "(`tracks`.`artist` LIKE '%" + searchquery+ "%'\n" +
                        "OR `tracks`.`title` LIKE '%"+searchquery+"%')";
                sql = sql + " AND `userid` = " + 1 + " UNION " + sql + " AND `userid` <> " + 1;
                //!!!!!!!!!!! fix userid
                //System.out.println(sql);
            }
            
            ResultSet set = stmt.executeQuery(sql);
            
            tracks = new ArrayList<Song>();
            
            while (set.next())
            {
                int songid = set.getInt("file_id");
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
        {
            ex.printStackTrace(System.err);
        }
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
        this.setId("content");
        this.fitToWidthProperty().set(true);
        this.fitToHeightProperty().set(true);
        CONTENT.setHgap(10);
        CONTENT.setVgap(10);
        CONTENT.setId("grid");
        CONTENT.setMinHeight(this.getPrefHeight());
        this.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
    }
    
}
