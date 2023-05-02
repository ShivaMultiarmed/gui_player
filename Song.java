package gui_player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.stage.DirectoryChooser;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class Song extends HBox implements EventHandler<ActionEvent> {
    
    public String playlist,artist, title;
    
    private final boolean isMine;
    private ImageView img;
    private final Label title_label;
    private final Label artist_label;
    public int trackid;
    public VBox info;
    private ContextMenu menu;
    private MenuItem[] menuItems;
    private final Connection connection;
    private long songid, curUserId, playlistId;
    
    // song id in a mysql table
    // track id is a file name
    
    public Song(Connection connection,long playlistId, long curUserId,long songid, int trackid, String playlist, String artist, String title, boolean isMine)
    {
        this.connection = connection;
        this.curUserId = curUserId;
        
        this.songid = songid;
        this.trackid = trackid;
        this.getStyleClass().add("song");
        
        this.curUserId = curUserId;
        this.playlistId = playlistId;
        
        this.playlist = playlist;
        this.artist = artist;
        this.title = title;
        this.isMine = isMine;
        
        this.img = new ImageView(new Image("http://somespace.ru/player/assets/icons/song.png"));
        this.img = new ImageView(new Image("http://somespace.ru/player/tracks/"+trackid + "/preview.png"));
            
        this.img.setFitHeight(60);
        this.img.setFitWidth(60);
        Rectangle clip = new Rectangle();
        clip.setWidth(img.getFitWidth());
        clip.setHeight(img.getFitHeight());
        clip.setArcHeight(7);
        clip.setArcWidth(7);
        img.setClip(clip);
        this.set_custom_style();
        
        this.getChildren().add(img);
        
        info = new VBox();
        this.getChildren().add(info);
        info.setAlignment(Pos.CENTER_LEFT);
        
        title_label = new Label(this.title);
        title_label.setStyle("-fx-text-fill: #333333; -fx-font-size: 18px; -fx-font-weight: bold;");
        info.getChildren().add(title_label);
        
        artist_label = new Label(this.artist);
        artist_label.setStyle("-fx-text-fill: #555555; -fx-font-size: 15px; -fx-font-weight: normal;");
        info.getChildren().add(artist_label);
        
        
        addContextMenu();
    }
    
    private void set_custom_style()
    {
        this.getStyleClass().add("song");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10);
    }
    
    private void addContextMenu()
    {
        menu = new ContextMenu();
        
        if (!isMine)
        {
            menuItems = new MenuItem[2];

            menuItems[0] = new MenuItem("Добавить");
            menuItems[0].setId("addTrack");
        }
        else
        {
            menuItems = new MenuItem[2];
            menuItems[0] = new MenuItem("Удалить");
            menuItems[0].setId("remove");
        }
        menuItems[1] = new MenuItem("Скачать");
        menuItems[1].setId("download");
                
        for (int i =0 ; i<menuItems.length;i++)
        {
            menu.getItems().add(menuItems[i]);
            menuItems[i].setOnAction(this);
        }
        
        this.setOnContextMenuRequested(e -> { menu.show(this.getScene().getWindow(),e.getScreenX(), e.getScreenY()); } );
    }
    
    private void remove()
    {
        System.out.println("The song is removed.");
        try
        {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM `tracks` WHERE `id` = "+songid+";");
            stmt.executeUpdate("DELETE FROM `tracks_in_playlists` WHERE `trackid` = "+songid+";");
            stmt.close();
            Content mainContent = (Content)getScene().lookup("#content");
            System.out.println(mainContent);
            mainContent = new Content(connection, (int)curUserId, (int)playlistId);
            System.out.println(mainContent);
            BorderPane rootNode = (BorderPane) getScene().lookup("#root");
            rootNode.setCenter(mainContent);
        }
        catch(SQLException ex)
        {
            ex.printStackTrace(System.err);
        }
        
    }
    
    
    @Override
    public void handle(ActionEvent e)
    {
        System.out.println("Something is clicked");
        MenuItem target = (MenuItem)e.getTarget();
        System.out.println(target.getId());
        switch(target.getId())
        {
            case "remove":
                remove();
                break;
            case "download":
                download(choosePath());
                break;
            case "addTrack":
                add();
                break;
        }
    }
    private void add()
    {
        AddTrackBox box = new AddTrackBox(connection, curUserId);
        box.show();
        box.chosenPlaylistId.addListener(
                (observable, oldValue, newValue)->
                {
                    try {
                        String getIdQuery = "SELECT AUTO_INCREMENT as newTrackId "
                                + "FROM `information_schema`.`tables`"
                                + "WHERE `table_name` = 'tracks';";
                        ResultSet set = connection.createStatement().executeQuery(getIdQuery);
                        set.next();
                        long newTrackId = set.getLong("newTrackId");
                        set.close();

                        String sqltrack = "INSERT INTO `tracks` (`file_id`, `title`, `artist`)"
                                + "VALUES ('"+ trackid +"', '"+ title +"', '"+ artist +"')";
                        connection.createStatement().executeUpdate(sqltrack);

                        long newPlayListId = newValue.longValue();

                        String sqlplaylistassign = "INSERT INTO `tracks_in_playlists`"
                                + "(`trackid`, `playlistid`)"
                                + "VALUES"
                                + "("+newTrackId+", "+newPlayListId+");";
                        connection.createStatement().executeUpdate(sqlplaylistassign);
                    } catch (SQLException ex) {
                        ex.printStackTrace(System.err);
                    }
                    box.close();
                }
        );
    }
    private String choosePath()
    {
        DirectoryChooser chooser = new DirectoryChooser();
        chooser.setTitle("Скачать трек");
        return chooser.showDialog(getScene().getWindow()).getAbsolutePath();
    }
    private void download(String localPath)
    {
        FTPClient ftp = null;
        try
        {
            ftp = new FTPClient();
            ftp.connect("31.31.196.183", 21);
            ftp.login("u1964695","76b9Q31gPKxAyGQx");
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
        } catch (IOException ex) {
            ex.printStackTrace(System.err);
        }
        
        try
        {
            File local = new File(localPath + "/" + this.title + ".mp3");
            OutputStream localStream = new FileOutputStream(local);
            ftp.retrieveFile("www/somespace.ru/player/tracks/"+trackid+"/track.mp3", localStream);
        }
        catch(FileNotFoundException ex)
        {
            ex.printStackTrace(System.err);
        }    
        catch(NullPointerException | IOException ex)
        {
            ex.printStackTrace(System.err);
        }
        try
        {
            ftp.logout();
            ftp.disconnect();
        } catch (IOException | NullPointerException ex) {
            ex.printStackTrace(System.err);
        }
        
        
    }
}
