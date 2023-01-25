package gui_player;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;

public class Header extends HBox {
    
    public Slider time, sound;
    public ImageView play;
    
    public Header()
    {
         this.play_init();
         this.time_init();
         this.sound_init();
         this.customCss();
    }
    
    private void play_init()
    {
        play = new ImageView();
        play.setId("play");
        try{
            play.setImage(new Image(new FileInputStream("D:\\NetBeans_Projects\\Gui_Player\\src\\gui_player\\icons\\play.png")));
        }
        catch(FileNotFoundException e)
        {
            System.out.println("file not found");
        }
        play.setFitWidth(30);
        play.setFitHeight(30);
        this.getChildren().add(play);
    }
    
    private void time_init()
    {
        time = new Slider();
        time.setMin(0);
        time.setMax(3*60); // seconds
        time.setValue(0);
        time.setId("time");
        
        this.getChildren().add(time);
    }
    
    private void sound_init()
    {
        sound = new Slider();
        sound.setMin(0);
        sound.setMax(1);
        sound.setValue(0.5);
        sound.setId("sound");
        
        this.getChildren().add(sound);
    }
    private void customCss()
    {
        this.setAlignment(Pos.CENTER);
    }
    
}
