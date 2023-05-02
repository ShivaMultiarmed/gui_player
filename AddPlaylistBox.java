package gui_player;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;

public class AddPlaylistBox extends Stage {
    
    private final Connection c;
    private final int userid;
    private final Scene scene;
    private final VBox container;
    private final TextField newTitle;
    private final Button btn, cover;
    public final BooleanProperty complete;
    private String coverPath;
    
    public AddPlaylistBox(Connection c, int userid)
    {
        this.c = c;
        this.userid = userid;
        
        container  = new VBox();
        scene = new Scene(container);
        setScene(scene);
        
        newTitle = new TextField();
        CurHandler handler = new CurHandler();
        cover = new Button("Выберите обложку");
        cover.setId("chooseCover");
        
        btn = new Button("Добавить");
        btn.setId("submit");
       
        container.getChildren().addAll(newTitle, cover,btn);
        
        container.addEventFilter(MouseEvent.MOUSE_CLICKED, handler);
        
        setStyle();
        show();
        
        complete = new SimpleBooleanProperty(false);
    }
    
    private class CurHandler implements EventHandler<MouseEvent>
    {
        @Override
        public void handle(MouseEvent e)
        {
            if (e.getButton() == MouseButton.PRIMARY)
            {
                Node target = (Node)e.getTarget();
                String id = null;
                if (target.getId()!=null)
                    id = target.getId();
                else
                    id = target.getParent().getId();
                switch(id)
                {
                    case "submit":
                        if (newTitle.getText().isEmpty() || coverPath.isEmpty())
                            container.getChildren().add(new Label("Заполните все поля"));
                        else
                        {
                            int i = writePlaylistToDB();
                            upload(i);
                            complete.set(true);
                            close();
                        }
                        break;
                    case "chooseCover":
                        coverPath = chooseFile();
                        break;
                }
            
            }
        }
    }
    private int writePlaylistToDB()
    {
        int lastIndex =0;
        try
        {
            Statement stmt = c.createStatement();
            stmt.executeUpdate("INSERT INTO `playlists` (`userid`, `title`) VALUES("+userid+", '"+newTitle.getText()+"');");
            ResultSet lastTrack = stmt.executeQuery("SELECT `id` FROM `playlists` ORDER BY `id` DESC LIMIT 0,1;");
            if (lastTrack.next())
                lastIndex = lastTrack.getInt("id");
            stmt.close();
        } catch (SQLException ex) {
            ex.printStackTrace(System.err);
        }
        return lastIndex;
    }
    private void upload(int playlistId)
    {
        FTPClient ftp = new FTPClient();
        
        try
        {
            ftp.connect("31.31.196.183", 21);
            ftp.login("u1964695", "76b9Q31gPKxAyGQx");
            
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTP.BINARY_FILE_TYPE);
        }
        catch(IOException ex)
        {
            ex.printStackTrace(System.err);
        }
        try
        {
            FileInputStream coverStream = new FileInputStream(coverPath);
            ftp.changeWorkingDirectory("www/somespace.ru/player/playlists/");
            ftp.makeDirectory(""+playlistId);
            ftp.changeWorkingDirectory(""+playlistId);
            ftp.storeFile("cover.png", coverStream);
        }
        catch (FileNotFoundException ex)
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
        }
        catch(IOException | NullPointerException ex)
        {
            ex.printStackTrace(System.err);
        }
    }
    private String chooseFile()
    {
        String path = "";
        FileChooser chooser = new FileChooser();
        File file = chooser.showOpenDialog(this);
        path = file.getAbsolutePath();
        return path;
    }
    
    private void setStyle()
    {
        setWidth(400);
        setHeight(200);
        container.setSpacing(10);
        container.setPadding(new Insets(10));
        
        scene.getStylesheets().add(getClass().getResource("css/auth.css").toExternalForm());
        
        newTitle.getStyleClass().add("textfield");
        btn.getStyleClass().add("button");
        cover.getStyleClass().add("button");
    }
}
