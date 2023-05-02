package gui_player;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.RowConstraints;

public class Content extends ScrollPane {
    public BooleanProperty areTracksChanged;
    public GridPane CONTENT;
    private int r,c, num = 0;
    private int playlistid;
    public String playlist;
    public List<Song> tracks;
    public HBox add;
    private long userId;
    private Connection DB; 
    
    public Content(Connection connection,long userId, int playlistid) 
    { 
        System.out.println("playlist of userid " + userId);
        MainInit(connection, (int)userId);
        this.playlistid = playlistid;
        getTracks(connection, null);
        setContent();
    }
    public Content(Connection connection,long userId, String searchquery)
    {
       this.MainInit(connection, (int)userId);
        getTracks(connection, searchquery);
        setContent();
    }
    
    private void setTracksChanged()
    {
        areTracksChanged = new SimpleBooleanProperty(false);
    }
    
    private void MainInit(Connection connection, int userId)
    {
        CONTENT = new GridPane();
        this.content_style();
        this.setContent(CONTENT);
        this.DB = connection;
        this.userId = userId;
    }
    
    private void setContent()
    {
        this.setConstraints();
        this.addTracks();
        setTracksChanged();
    }
    
    private void setConstraints()
    {
        c= 2;
        r = (int) Math.ceil(1.0*(num+1)/c);
        
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
        System.out.println("Getting tracks from playlist "+playlistid);
        try 
        {
            Statement stmt = connection.createStatement();
            String sql = null;
            if (searchquery == null) 
                sql = "SELECT `id`,`file_id`, `title`, `artist` FrOM `tracks`\n" +
                    "INNER JOIN `tracks_in_playlists`\n" +
                    "ON `trackid` = `id`\n" +
                    "WHERE `playlistid` =" + playlistid + ";";
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
                sql = sql + " AND `userid` = " + userId + " UNION " + sql + " AND `userid` <> " + userId +";";
               
            }
            
            ResultSet set = stmt.executeQuery(sql);
            
            tracks = new ArrayList<>();
            num=0;
            while (set.next())
            {
                long uId;
                if (searchquery != null)
                    uId= set.getLong("userid");
                else 
                    uId = userId;
                long track_id = set.getLong("id");
                int file_id = set.getInt("file_id");
                String title = set.getString("title");
                String artist = set.getString("artist");
                Song s = new Song(connection,(long)playlistid,userId,track_id, file_id, this.playlist, artist, title, uId == userId);
                s.setId((num+1)+"");
                tracks.add(s);
                num++;
            }
            
            System.out.println(num+" tracks are got.");

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
        
        int i;
        for (i =0;i<num;i++)
        {
            CONTENT.add(tracks.get(i),i%c,i/c);
        }
        initAdd(i%c, i/c);
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
    private void initAdd(int c, int r)
    {
        add = new HBox();
        
        add.getStyleClass().add("song");
        add.setAlignment(Pos.CENTER_LEFT);
        add.setSpacing(10);
        add.getChildren().add(new Label("Загрузить трек"));
        add.addEventHandler(MouseEvent.MOUSE_CLICKED,
           e -> {
               if (e.getButton()==MouseButton.PRIMARY)
               {
                   UploadBox b = new UploadBox(DB, playlistid);
                   b.isComplete.addListener(
                          (value) -> {
                                areTracksChanged.set(true);
                          }
                   );
               }
           }
        );
        
        CONTENT.add(add,c, r);
    }
    
}
