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
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;

public class Gui_Player extends Application {
    
    private int userid, cur_track_id;
    
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
    private String ip, username, pass, db;
    
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
                                    s = "pause";
                                }
                                else
                                {
                                    media_player.pause();
                                    s = "play";
                                }
                                header.play.setImage(new Image(new FileInputStream("src\\assets\\icons\\"+s+".png")));

                                
                            }
                            catch(FileNotFoundException ex)
                            {
                                System.out.println("File is not found");
                            }
                        }
                        else if (target.getId() == "next")
                        {
                            try
                            {
                                set_media_player(ids.get(ids.indexOf(cur_track_id)+1));
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
                            main_content = new Content(connection, header.input.getText());
                            root.setCenter(main_content);
                        }
                    }
                    
                }
        );
        
        ((ImageView)scene.lookup("#next")).addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>()
                {
                    public void handle(MouseEvent e)
                    {
                        
                    }
                
                }
        );
        ((ImageView)scene.lookup("#previous")).addEventHandler(
                MouseEvent.MOUSE_CLICKED,
                new EventHandler<MouseEvent>()
                {
                    public void handle(MouseEvent e)
                    {
                        
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
        
        this.set_Content(connection,1); // fix playlist id according to  a userid
        
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
        
        ids = new ArrayList<Integer>();
        
        for (Song so : main_content.tracks)
        {
            ids.add(so.trackid);
            so.addEventHandler(
                    MouseEvent.MOUSE_CLICKED,
                    new EventHandler<MouseEvent>()
                    {
                        @Override
                        public void handle(MouseEvent e)
                        {
                            set_media_player(so.trackid);
                            media_player.play();
                        }
                    }
            );
        }
    }   
    
    private void set_media_player(int cur_track_id)
    {   
        this.cur_track_id = cur_track_id;
        if (media_player != null)
            media_player.pause(); 
        Slider t = ((Slider)scene.lookup("#time"));
        Slider v = ((Slider)scene.lookup("#sound"));
        this.media_player = new MediaPlayer(new Media("http://guiplayer/tracks/"+cur_track_id+"/track.mp3"));
        this.media_player.setStartTime(new Duration(0));
        this.media_player.setVolume(v.getValue());
        
        Song so = main_content.tracks.get(ids.indexOf(cur_track_id));
        song = so.title;
        artist = so.artist;
        playlist = so.playlist;
        
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
                        duration = media_player.getMedia().getDuration();
                        t.setMax(media_player.getMedia().getDuration().toSeconds());
                        media_player.currentTimeProperty().addListener(
                                new ChangeListener<Duration>()
                                {
                                    @Override
                                    public void changed(ObservableValue<? extends Duration> ov, Duration old, Duration updated)
                                    {
                                        header.time.setValue((int)updated.toSeconds());
                                        String timetext = (int)ov.getValue().toMinutes() + ":" + (int)(ov.getValue().toSeconds()%60) + " / " + (int)duration.toMinutes() + ":" + (int)(duration.toSeconds()%60);
                                        header.timelabel.setText(timetext);
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
