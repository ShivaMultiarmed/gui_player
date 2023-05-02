package gui_player;

import java.sql.Connection;
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
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;

public class PlayList extends VBox implements EventHandler<ActionEvent> {
    
    public String title;
    public int id, userid;
    private final ContextMenu menu;
    private final Connection connection;
    
    public PlayList (Connection c,int id, int userid, String title)
    {
        connection = c;
        this.id = id;
        this.userid = userid;
        this.title = title;
        
        this.setCustomStyle();
        this.getStyleClass().add("playlist");
        
        ImageView img = null;
        if (this.title == null)
            img = new ImageView(new Image("http://somespace.ru/player/assets/icons/playlist.png"));
        else
            img = new ImageView(new Image("http://somespace.ru/player/playlists/"+id+"/cover.png"));
        img.setFitHeight(130);
        img.setFitWidth(img.getFitHeight());

        Rectangle clip = new Rectangle();
        clip.setWidth(img.getFitWidth());
        clip.setHeight(img.getFitHeight());
        clip.setArcHeight(20);
        clip.setArcWidth(20);
        img.setClip(clip);

        this.getChildren().add(img);
        
        if (this.title == null)
            this.title = "Моя музыка";
        else
            this.title = title;
        this.getChildren().add(new Label(this.title));
        
        menu = new ContextMenu();
        MenuItem removeOption = new MenuItem("Удалить");
        removeOption.setId("rmPlaylist");
        menu.getItems().add(removeOption);
        for (MenuItem option : menu.getItems())
            option.setOnAction(this);
        this.setOnContextMenuRequested(e -> { menu.show(this.getScene().getWindow(),e.getScreenX(), e.getScreenY()); } );
    }
    private void setCustomStyle()
    {
        this.setAlignment(Pos.CENTER);
    }
    private void remove()
    {
        try
        {
            Statement stmt = connection.createStatement();
            stmt.executeUpdate("DELETE FROM `playlists` WHERE `id` = "+id+";");
            stmt.close();
            BorderPane rootNode  = (BorderPane)getScene().lookup("#root");
            Aside asidePane = (Aside) getScene().lookup("#aside");
            asidePane = new Aside(connection,userid);
            rootNode.setLeft(asidePane);
        }
        catch(SQLException ex)
        {
            ex.printStackTrace(System.err);
        }
    }
    @Override
    public void handle(ActionEvent e)
    {
        MenuItem chosen = (MenuItem)e.getTarget();
        switch(chosen.getId())
        {
            case "rmPlaylist":
                remove();
                break;
        }
    }
}
