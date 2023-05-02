package gui_player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import com.mysql.jdbc.Driver;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.event.EventType;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;

public class Gui_Player extends Application {
    
    private int userid, cur_track_id, playlist_id;
    
    private ArrayList<Integer> ids;
    
    public boolean play;
    public String playlist, song, artist;
    
    private Duration duration;
    
    public Stage TheStage;
    public BorderPane root;
    public Scene scene;
    
    public Header header;
    public Aside aside;
    public Content main_content;
    
    private File song_file;
    
    private MediaPlayer media_player;
    
    private Connection connection;
    private String host, username, pass, db;
    
    
    public static void main(String[] args)
    {
        Gui_Player.launch(args); 
    }
   
    
    private void setConnection()
    {
        host = "server197.hosting.reg.ru"; db = "u1964695_gui_player"; username = "u1964695_default"; pass = "dtjCXRmHMfqR0456";
        
        try
        {
            this.connection  = DriverManager.getConnection("jdbc:mysql://"+host+":3306/"+db, username, pass);
        }
        catch(SQLException ex)
        {
            
        }
    }
    
    @Override
    public void start(Stage stage)
    {
        this.setConnection();
        this.auth(this.connection, stage);
    }
    
    public void openMainWindow(Connection connection, Stage stage) {
        
        this.play = false;
        
        TheStage = stage;
        
        TheStage.setWidth(800);
        TheStage.setHeight(620);
        TheStage.setResizable(false);
        
        root = new BorderPane();
        root.setId("root");
        scene = new Scene(root);
        
        header = new Header();
        root.setTop(header);
        
        
        ((Slider)scene.lookup("#time")).addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent e)
                    {
                        media_player.stop();
                        media_player.setStartTime(new Duration(((Slider)scene.lookup("#time")).getValue()*1000));
                        media_player.play();
                    }
                }
        );
        ((Slider)scene.lookup("#sound")).valueProperty().addListener(
                new ChangeListener<Number>()
                {
                    @Override
                    public void changed(ObservableValue ov, Number old, Number update)
                    {
                        media_player.setVolume(((Slider)scene.lookup("#sound")).getValue());
                    }
                }
        );
        
        header.setId("header");
        header.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent e)
                    {
                        Node target = (Node)(e.getTarget());
                        if (e.getButton()==MouseButton.PRIMARY){
                        if (target.getId()=="play")
                        {
                            play = !play;
                             
                            if (play)
                            {
                                media_player.play();
                                header.play.setImage(header.pauseImg);
                            }
                            else
                            {
                                media_player.pause();
                                header.play.setImage(header.playImg);
                            }
                        }
                        else if (target.getId() == "next")
                        {
                            try
                            {
                                set_media_player(ids.get(ids.indexOf(cur_track_id)+1));
                                System.out.println(cur_track_id);
                            }
                            catch(IndexOutOfBoundsException ex)
                            {
                                set_media_player(ids.get(0));
                            }

                            media_player.play();
                        }
                        else if (target.getId() == "previous")
                        {
                            try
                            {
                                set_media_player(ids.get(ids.indexOf(cur_track_id)-1));
                            }
                            catch(IndexOutOfBoundsException ex)
                            {
                                set_media_player(ids.get(ids.size()-1));
                            }

                            media_player.play();
                        }
                        else if (target.getId().equals("searchbtn"))
                        {
                            set_Content(connection, header.input.getText());
                        }
                    }
                    }
                }
        );
        
        setAside();
        configAllPlaylists();
        setOnAsideChanged();    
        
        this.set_Content(connection, ((PlayList)aside.content.lookup(".playlist")).id); 
        
        TheStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("css/main.css").toExternalForm());
        TheStage.show();
        TheStage.setTitle("JAVA Audio Player");
    }
    
    private void setAside()
    {
        aside = new Aside(connection,userid);
        root.setLeft(aside);
    }
    private void configAllPlaylists()
    {
        for (Node p : aside.content.lookupAll(".playlist"))
        {
            PlayList pl = (PlayList) p;
            pl.addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>()
                {
                    @Override
                    public void handle(MouseEvent e)
                    {
                        if (e.getButton()==MouseButton.PRIMARY)
                        {
                        playlist_id = pl.id;
                        set_Content(connection,pl.id);
                        }
                    }
                }
            );
        }
    }
    
    public void setOnAsideChanged()
    {
        aside.arePlaylistsChanged.addListener(
                e -> 
                {
                    if (aside.arePlaylistsChanged.get())
                    {
                        aside.arePlaylistsChanged.set(false);
                        configAllPlaylists();
                    }
                }
        );    
    }
    
    private void set_Content(Connection connection, int playlistid)
    {
        playlist_id = playlistid;
        main_content = new Content(connection, userid, playlistid);
        System.out.println("Initializing playlist " + playlistid);
        root.setCenter(main_content);
        configTracks();
        setOnContentChanged();
    }   
    
    private void set_Content(Connection connection, String searchquery)
    {
         main_content = new Content(connection,userid, searchquery);
        root.setCenter(main_content);
        configTracks();
    }
    
    private void configTracks()
    {
        
        ids = new ArrayList<>();
        
        for (Song so : main_content.tracks)
        {
            ids.add(so.trackid);
            System.out.print(ids.get(ids.size()-1));
            so.addEventHandler(MouseEvent.MOUSE_CLICKED, (MouseEvent e) -> {
                if (e.getButton()==MouseButton.PRIMARY)
                {
                set_media_player(so.trackid);
                media_player.play();
                }
                
            });
        }
       
        
    }
    
    private void set_media_player(int cur_track_id)
    {   
        this.cur_track_id = cur_track_id;
        if (media_player != null)
            media_player.pause(); 
        Slider t = ((Slider)scene.lookup("#time"));
        Slider v = ((Slider)scene.lookup("#sound"));
        this.media_player = new MediaPlayer(new Media("http://somespace.ru/player/tracks/"+cur_track_id+"/track.mp3"));
        this.media_player.setStartTime(new Duration(0));
        this.media_player.setVolume(v.getValue());
        
        Song so = main_content.tracks.get(ids.indexOf(cur_track_id));
        song = so.title;
        artist = so.artist;
        playlist = so.playlist;
        
        if (!play)
        {
            play = true;
            header.play.setImage(header.pauseImg);
        }
        
        media_player.setOnReady(() -> {
            duration = media_player.getMedia().getDuration();
            t.setMax(media_player.getMedia().getDuration().toSeconds());
            media_player.currentTimeProperty().addListener((ObservableValue<? extends Duration> ov, Duration old, Duration updated) -> {
                header.time.setValue((int)updated.toSeconds());
                String timetext = (int)ov.getValue().toMinutes() + ":" + (int)(ov.getValue().toSeconds()%60) + " / " + (int)duration.toMinutes() + ":" + (int)(duration.toSeconds()%60);
                header.timelabel.setText(timetext);
            });
        });
    }
    
    private void setOnContentChanged()
    {
        main_content.areTracksChanged.addListener(
                value ->{
                    if (main_content.areTracksChanged.get())
                    {
                        System.out.println("Tracks have been changed");
                        
                        System.out.println("Refreshing playlist "+ playlist_id);
                        set_Content(connection, playlist_id);
                        main_content.areTracksChanged.set(false);
                    }
                }
        );
    }
    
    private void auth(Connection connection, Stage stage)
    {
        AuthBox box = new AuthBox(connection);
            
        box.userid.addListener((ObservableValue<? extends Number> observable, Number oldValue, Number newValue) -> {
            if (observable.getValue().intValue() > 0)
            {
                userid = box.userid.intValue();
                openMainWindow(connection, stage);
                box.close();
            }
        });
        
    }
}


