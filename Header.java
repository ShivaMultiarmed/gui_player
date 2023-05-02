package gui_player;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class Header extends VBox {
    
    public HBox playbox, searchbox;
    public Slider time, sound;
    public ImageView play, next, previous, search;
    public Label timelabel; // time
    public TextField input;
    
    public Image playImg, pauseImg;
    
    public Header()
    {
        playbox = new HBox();
        playbox.setId("playbox");
        this.getChildren().add(playbox);
         this.play_init();
         this.prev_next_init();
         this.time_init();
         this.label_init();
         this.sound_init();
         this.playboxCss();
         
        searchbox = new HBox();
        searchbox.setId("searchbox");
        this.getChildren().add(searchbox);
        this.input_init();
        this.search_init();
        this.searchboxCss();
    }
    
    private void play_init()
    {
        play = new ImageView();
        play.setId("play");
        playImg = new Image("http://somespace.ru/player/assets/icons/play.png");
        pauseImg = new Image("http://somespace.ru/player/assets/icons/pause.png");
        play.setImage(playImg);
        play.setFitWidth(30);
        play.setFitHeight(30);
        playbox.getChildren().add(play);
    }
    
    private void prev_next_init()
    {
        previous = new ImageView(new Image("http://somespace.ru/player/assets/icons/previous.png"));
        next = new ImageView(new Image("http://somespace.ru/player/assets/icons/next.png"));
        previous.setId("previous");
        next.setId("next");
        playbox.getChildren().addAll(previous, next);
        next.setFitHeight(24);
        next.setFitWidth(24);
        previous.setFitWidth(24);
        previous.setFitHeight(24);
    }
    
    private void time_init()
    {
        time = new Slider();
        time.setMin(0);
        time.setMax(3*60); // seconds
        time.setValue(0);
        time.setId("time");
        
        playbox.getChildren().add(time);
    }
    
    private void label_init()
    {
        timelabel = new Label();
        timelabel.setId("timelabel");
        playbox.getChildren().add(timelabel);
    }
    
    private void sound_init()
    {
        sound = new Slider();
        sound.setMin(0);
        sound.setMax(1);
        sound.setValue(0.5);
        sound.setId("sound");
        
        playbox.getChildren().add(sound);
    }
    private void playboxCss()
    {
        playbox.setAlignment(Pos.CENTER);
        playbox.setSpacing(10);
    }
    
    private void input_init()
    {
        input = new TextField();
        input.setPromptText("Введите название песни");
        input.setId("searchinput"); 
        input.setPrefWidth(450);
        input.setPrefHeight(32);
        searchbox.getChildren().add(input);
    }
    private void search_init()
    {
        search = new ImageView(new Image("http://somespace.ru/player/assets/icons/search.png"));
        search.setFitHeight(32);
        search.setFitWidth(search.getFitHeight());
        Rectangle clip = new Rectangle();
        clip.setWidth(search.getFitWidth());
        clip.setHeight(search.getFitHeight());
        clip.setArcHeight(8);
        clip.setArcWidth(clip.getArcHeight());
        search.setClip(clip);
        search.setId("searchbtn");
        searchbox.getChildren().add(search);
    }
    private void searchboxCss()
    {
        searchbox.setAlignment(Pos.CENTER);
        searchbox.setSpacing(10);
    }
}
