package gui_player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import javafx.scene.layout.StackPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

public class Gui_Player extends Application {
    
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
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage stage) {
       
        this.play = false;
        
        TheStage = stage;
        
        TheStage.setWidth(800);
        TheStage.setHeight(600);
        
        root = new BorderPane();
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
                                header.play.setImage(new Image(new FileInputStream("D:\\NetBeans_Projects\\Gui_Player\\src\\gui_player\\icons\\"+s+".png")));

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
        
        aside = new Aside();
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
                        set_Content(pl.title);
                    }
                }
            );
        }
        
        this.set_Content("rock");
        
        TheStage.setScene(scene);
        scene.getStylesheets().add(getClass().getResource("style.css").toExternalForm());
        TheStage.show();
        TheStage.setTitle("JAVA Audio Player");
    }
    
    private void set_Content(String stringplaylist)
    {
        main_content = new Content(stringplaylist);
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
                            
                            song_file = new File("src/gui_player/playlists/"+playlist+"/songs/"+so.getId()+"/song.mp3");
                            if (media_player != null)
                                media_player.stop();
                                
                            set_media_player(song_file.toPath().toString());
                            media_player.play();
                        }
                    }
            );
        }
    }   
    
    private void set_media_player(String path)
    {   
        Slider t = ((Slider)scene.lookup("#time"));
        Slider v = ((Slider)scene.lookup("#sound"));
        
        this.media_player = new MediaPlayer(new Media(new File(path).toURI().toString()));
        this.media_player.setStartTime(new Duration(0));
        this.media_player.setVolume(v.getValue());
        
        if (!play)
        {
            play = true;
            try
            {
                header.play.setImage(new Image(new FileInputStream("D:\\NetBeans_Projects\\Gui_Player\\src\\gui_player\\icons\\pause.png")));
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
