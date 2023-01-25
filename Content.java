package gui_player;

import java.io.File;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;

public class Content extends ScrollPane {
    
    public GridPane CONTENT;
    public int r,c, num;
    public String playlist;
    public File[] songs;
    public Song[] tracks;
    
    public Content(String playlist) // number of tracks
    { 
        CONTENT = new GridPane();
       
        this.playlist = playlist;
        this.songs = new File("src/gui_player/playlists/"+playlist+"/songs").listFiles();
        
        this.num = this.songs.length;
        c= 2;
        r = (int) Math.ceil(1.0*num/c);
        
        
        
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
            
        this.content_style();
        
        this.setContent(CONTENT);
        addTracks();
    }
    
    private void addTracks()
    {
        tracks = new Song[num];
        for (int i =0;i<num;i++)
        {
            Song s =new Song(songs[i]);
            tracks[i] = s;
            s.setId((i+1)+"");
            CONTENT.add(s,i%c,i/c);
        }
    }
    private void content_style()
    {
        CONTENT.setId("content");
        this.fitToWidthProperty().set(true);
        CONTENT.setHgap(10);
        CONTENT.setVgap(10);
    }
    
}
