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
import java.util.logging.Level;
import java.util.logging.Logger;

public class Gui_Player extends Application {
    
    private int userid;
    
    public boolean play;
    public String playlist, song, artist;
    
    public Stage TheStage;
    public BorderPane root;
    public Scene scene;
    
    public Header header;
    public Aside aside;
    public Content main_content;
    
    private File song_file;
    
    private MediaPlayer media_player;
    
    private Connection connection;
    private String ip, username, pass, db;
    
    public static void main(String[] args) {
        launch(args);
    }
    
    private void setConnection()
    {
        
        
        ip = "127.0.0.1"; db = "gui_player"; username = "root"; pass = "";
        
        try
        {
            this.connection  = DriverManager.getConnection("jdbc:mysql://"+ip+":3306/"+db, username, pass);
        }
        catch(SQLException ex)
        {
            
        }
    }
    
    @Override
    public void start(Stage stage) {
        
        this.setConnection();
       
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
        
        ((Slider)scene.lookup("#time")).valueProperty().addListener(
                new ChangeListener<Number>()
                {
                    @Override
                    public void changed(ObservableValue ov,Number old, Number update)
                    {
                        
                    }
                }
        );
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
                        if (target.getId()=="play")
                        {
                            play = !play;
                            try
                            {
                                String s = null;
                                if (play)
                                {
                                    media_player.play();
                                    s= "pause";
                                }
                                else
                                {
                                    Duration curTime = media_player.getCurrentTime();
                                    media_player.stop();
                                    media_player.setStartTime(curTime);
                                    s="play";
                                }
                                header.play.setImage(new Image(new FileInputStream("src\\assets\\icons\\"+s+".png")));

                                System.out.println(play);
                            }
                            catch(FileNotFoundException ex)
                            {
                                System.out.println("File is not found");
                            }
                        }
                    }
                }
        );
        
        aside = new Aside(connection,1);
        root.setLeft(aside);
        
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
                        set_Content(connection,pl.id);
                    }
                }
            );
        }
        
        this.set_Content(connection, 1);
        
        TheStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        TheStage.show();
        TheStage.setTitle("JAVA Audio Player");
        
        //new PlaylistDialog(1);
    }
    
    private void set_Content(Connection connection, int playlistid)
    {
        main_content = new Content(connection, playlistid);
        root.setCenter(main_content);
        
        for (Song so : main_content.tracks)
        {
            so.addEventHandler(
                    MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>()
                    {
                        @Override
                        public void handle(MouseEvent e)
                        {
                            song = so.title;
                            artist = so.artist;
                            playlist = so.playlist;
                            
                            try {
                                song_file = new File(new URL("http://guiplayer/tracks/"+so.trackid+"/track.mp3").getFile());
                            } catch (MalformedURLException ex) {
                                Logger.getLogger(Gui_Player.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            if (media_player != null)
                                media_player.stop();  
                            
                            set_media_player();
                            
                            media_player.play();
                        }
                    }
            );
        }
    }   
    
    private void set_media_player()
    {   
        Slider t = ((Slider)scene.lookup("#time"));
        Slider v = ((Slider)scene.lookup("#sound"));
        this.media_player = new MediaPlayer(new Media("http://guiplayer/"+song_file.toString().replace('\\','/')));
        this.media_player.setStartTime(new Duration(0));
        this.media_player.setVolume(v.getValue());
        
        if (!play)
        {
            play = true;
            try
            {
                header.play.setImage(new Image(new FileInputStream("src\\assets\\icons\\pause.png")));
            }
            catch(FileNotFoundException ex)
            {
                System.out.println("File not found");
            }
        }
        
        media_player.setOnReady(
                new Runnable()
                {
                    @Override
                    public void run()
                    {
                        t.setMax(media_player.getMedia().getDuration().toSeconds());
                        media_player.currentTimeProperty().addListener(
                                new ChangeListener<Duration>()
                                {
                                    @Override
                                    public void changed(ObservableValue ov, Duration old, Duration updated)
                                    {
                                        header.time.setValue((int)updated.toSeconds());
                                    }
                                }
                        );
                    }
                }
        );
    }
    
    private void set_time_slider()
    {
        Slider t = (Slider) this.header.lookup("#time");
        
        t.valueProperty().addListener(
                new ChangeListener<Number>()
                {
                    @Override
                    public void changed(ObservableValue ov, Number old, Number updated)
                    {
                        
                    }
                }
        );
    }
    
    
}
