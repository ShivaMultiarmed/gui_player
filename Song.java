package gui_player;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class Song extends HBox {
    
    public String playlist,artist, title;
    
    private ImageView img;
    private Label label;
    private File dir, song, info;
    
    public Song(File dir)
    {
        this.getStyleClass().add("song");
        this.dir = dir;
        
        //this.song = new File(dir.toURI()+"song.wav");
        
        this.info = new File((dir.toURI()+"info.txt").substring(6));
        
        String[] str = dir.toURI().toString().split("/");
        this.playlist = str[str.length-3];
        
        this.img = new ImageView(new Image(dir.toURI()+"preview.png"));
        this.img.setFitHeight(60);
        this.img.setFitWidth(60);
        
        this.init_info();
        
        this.set_custom_class();
        
        this.getChildren().add(img);
        label = new Label(this.artist + "\n" +this.title);
        this.getChildren().add(label);
        
    }
    private void init_info()
    {
        try 
        {
            FileReader fr = new FileReader(this.info);      
            Scanner scanner = new Scanner(fr);
            this.artist = scanner.nextLine();
            this.title = scanner.nextLine();
            fr.close();
        }
        catch(FileNotFoundException e)
        {
            System.out.println("info file not found");
        } catch (IOException ex) {
            System.out.println("i/o issues occured");
        }
    }
    private void set_custom_class()
    {
        this.getStyleClass().add("song");
        this.setAlignment(Pos.CENTER_LEFT);
        this.setSpacing(10);
    }
}
