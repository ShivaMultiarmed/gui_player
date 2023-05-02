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
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
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

public class UploadBox extends Stage {
    public final BooleanProperty isComplete;
    private final Scene scene;
    private final VBox container;
    private TextField title, artist;
    private Button chooseFile, chooseCover, submit;
    private final Connection DB;
    private final int playlistid;
    private String trackPath, coverPath;
    public UploadBox(Connection connection, int playlistid)
    {
        super();
        DB = connection;
        this.playlistid = playlistid;
        isComplete = new SimpleBooleanProperty(false);
        container = new VBox();
        scene = new Scene(container);
        setScene(scene);
        setTitle("Загрузить трек");
        setLayout();
        setStyle();
        show();
    }
    private void setLayout()
    {
        MouseHandler m = new MouseHandler();
        container.addEventFilter(MouseEvent.MOUSE_CLICKED, m);
        container.addEventHandler(MouseEvent.MOUSE_CLICKED, m);
        title = new TextField();
        title.setPromptText("Название трека");
        artist = new TextField();
        artist.setPromptText("имя исполнителя");
        chooseFile = new Button("Выберите трек");
        chooseFile.setId("chooseTrack");
        chooseCover = new Button("Выберите обложку");
        chooseCover.setId("chooseCover");
        submit = new Button("Загрузить");
        submit.setId("upload");
        container.getChildren().addAll(title, artist, chooseFile, chooseCover, submit);
    }
    private class MouseHandler implements EventHandler<MouseEvent>
    {
        @Override
        public void handle(MouseEvent e)
        {
            if (e.getButton() == MouseButton.PRIMARY)
            {
                Node target = (Node) e.getTarget();
                String nodeId = null;
                if (target.getId()==null)
                    nodeId = target.getParent().getId();
                else 
                    nodeId = target.getId();
                switch(nodeId)
                {
                    case "chooseTrack":
                        trackPath = chooseFile("Загрузить трек").getAbsolutePath();
                        System.out.println(trackPath);
                        break;
                    case "chooseCover":
                        coverPath = chooseFile("Выберите обложку").getAbsolutePath();
                        System.out.println(coverPath);
                        break;
                    case "upload":
                        if (trackPath!="" && coverPath!="" && title.getText()!="" && artist.getText()!="")
                        {
                            int fileid = uploadFiles(true, trackPath, coverPath);
                            writeSongInfo(fileid);
                            isComplete.set(true);
                            close();
                        }
                        else
                            container.getChildren().add(new Label("Поля не заполнены"));
                        break;
                }
            }
        }
    }
    private File chooseFile(String str)
    {
        File f = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(str);
        f = fileChooser.showOpenDialog(getScene().getWindow());
        System.out.println(f.getAbsolutePath());
        return f;
    }
    private int uploadFiles(boolean isMusic, String musicPath, String imgPath)
    {
        FTPClient ftp = new FTPClient();
        
        int newTrackId = 0;
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
            newTrackId = Integer.parseInt(UUID.randomUUID().toString().replaceAll("-", "").substring(28), 16);
            FileInputStream music = new FileInputStream(musicPath);
            FileInputStream cover = new FileInputStream(imgPath);
            ftp.changeWorkingDirectory("www/somespace.ru/player/tracks/");
            ftp.makeDirectory(""+newTrackId);
            ftp.changeWorkingDirectory(""+newTrackId);
            ftp.storeFile("track.mp3", music);
            ftp.storeFile("preview.png", cover);
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
        return newTrackId;
    }
    private void writeSongInfo(int newTrackId)
    {
        try
        {
            Statement stmt = DB.createStatement();
            String sql = "INSERT INTO `tracks` (`file_id`, `title`, `artist`) VALUES("+newTrackId+",'"+title.getText()+"', '"+artist.getText()+"')";
            stmt.executeUpdate(sql);
            sql = "SELECT `id` FROM `tracks` ORDER BY `id` DESC LIMIT 0, 1;"; 
            ResultSet set = stmt.executeQuery(sql);
            set.next();
            long songid = set.getLong("id");
            sql = "INSERT INTO `tracks_in_playlists` VALUES ("+songid+", "+playlistid+");";
            stmt.executeUpdate(sql);
            stmt.close();
        }
        catch(SQLException ex)
        {
            ex.printStackTrace(System.err);
        }
        
    }
    private void setStyle()
    {
        setWidth(300);
        setHeight(300);
        container.setPadding(new Insets(10));
        container.setSpacing(10);
        
        scene.getStylesheets().add(getClass().getResource("css/auth.css").toExternalForm());
    
        title.getStyleClass().add("textfield");
        artist.getStyleClass().add("textfield");
        chooseFile.getStyleClass().add("submit");
        chooseCover.getStyleClass().add("submit");
        submit.getStyleClass().add("button");        
    }
}
