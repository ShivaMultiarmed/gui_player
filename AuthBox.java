package gui_player;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import javafx.beans.Observable;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.event.EventHandler;
import javafx.event.EventTarget;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

class AuthBox extends Stage implements EventHandler<MouseEvent> {
    
    private String authType; 
    public SimpleLongProperty userid = new SimpleLongProperty(0);
    
    private final Scene scene;
    private final VBox root;
    
    private final Button submit, log, sign;
    private final Label label;
    private final TextField name;
    private final PasswordField password;
    
    private final Label msg;
    
    private Connection c;
    
    public AuthBox(Connection connection)
    {
        root = new VBox();
        this.scene = new Scene(root);
        this.setScene(scene);
        this.show();
        
        log = new Button("Войти");
        sign = new Button("Регистрироваться");
        log.setId("login");
        sign.setId("signup");
        
        root.getChildren().add(log);
        root.getChildren().add(sign);
        log.getStyleClass().add("button");
        sign.getStyleClass().add("button");
        
        root.addEventFilter(MouseEvent.MOUSE_CLICKED, this);
        root.addEventHandler(MouseEvent.MOUSE_CLICKED, this);
        
        submit = new Button();
        submit.setId("submit");
        submit.getStyleClass().add("button");
        label = new Label();
        msg = new Label();
        label.getStyleClass().add("titleLabel");
        msg.getStyleClass().add("msgLabel");
        
        name = new TextField();
        password = new PasswordField();
 
        c = connection;
        
        this.setStyle();
        
    }
    @Override
    public void handle(MouseEvent e)
    {
        Node target = (Node)e.getTarget();
        System.out.println((Node)e.getTarget());
        if (userid.get() == 0)
        {
            userid.set(-1); // authentification started
            if (target.getId()!=null)
                setAuth(target.getId());
            else 
                setAuth(target.getParent().getId());
        }
        
        else if (userid.get()<0)
        {
            if ("submit".equals(target.getId()) || ((Text)target).getText() == submit.getText())
                auth(authType);
        }
        
        /*switch (target.getId())
        {}*/
    }
    private void setAuth(String type)
    {
        root.getChildren().clear();
        if (type.equals("login"))
        {
            this.authType = "login";
            submit.setText("Войти");
            label.setText("Войти");
            name.setText("Ivan");
            password.setText("qwerty");
        }
        else
        {
            this.authType = "signup";
            submit.setText("Зарегистрироваться");
            label.setText("Регистрация");
        }
        
        this.root.getChildren().addAll(label, name, password, submit, msg);
        
    }
    
    private void auth(String type)
    {
        int id = 0;
        String nick = null, pass = null;
        if (name.getText().equals("") || password.getText().equals(""))
                userid.set(-3);
        else
        {
            try
            {
                Statement stmt = c.createStatement();
                ResultSet set = stmt.executeQuery("SELECT * FROM `users` WHERE `name` = '"+name.getText()+"';");

                if (set.next())
                {
                    if (authType.equals("login"))
                    {
                        id = set.getInt("id");
                        nick = set.getString("name");
                        pass = set.getString("password");
                        
                        if (pass.equals(password.getText()))
                            userid.set(id); // auth success
                        else
                            userid.set(-2); // password is incorrect
                    }
                    else if (authType.equals("signup"))
                    {
                        userid.set(-4);
                    }
                    
                }
                else if (id == 0)
                {
                    if (authType.equals("login"))
                    {
                        userid.set(-5); // user is not found
                    }
                    else if (authType.equals("signup"))
                    {
                        Statement registration = c.createStatement();
                        Statement newDefaultMusic = c.createStatement();
                        
                        registration.executeUpdate("INSERT INTO `users` (`name`, `password`) VALUES ('" + name.getText() + "', '" + password.getText() + "');");
                        
                        ResultSet s = c.createStatement().executeQuery("SELECT id FROM `users` WHERE `name` = '" +name.getText()+ "';");
                        s.next();
                        long uid = s.getLong("id");
                        newDefaultMusic.executeUpdate("INSERT INTO `playlists` (userid) VALUES("+uid+");");
                        userid.set(uid);
                        s.close();
                        
                        registration.close();
                        newDefaultMusic.close();
                        this.close();
                    }
                }
                    
                
                set.close();

                stmt.close();
            }
            catch (SQLException ex)
            {
                ex.printStackTrace(System.err);
            }
        } 
        
        switch(userid.getValue().intValue())
        {
            case -5:
                msg.setText("Пользователь не существует");
                break;
            case -2:
                msg.setText("Пароль неверный.");
                break;
            case -3:
                msg.setText("Пустые поля.");
                break;
            case -4:
                msg.setText("Такой пользователь уже существует.");
                break;
        }
    }
    private void setStyle()
    {
        this.setWidth(300);
        this.setHeight(300);
        root.setSpacing(10);
        root.setPadding(new Insets(10));
        
        name.getStyleClass().add("textfield");
        password.getStyleClass().add("textfield");
        
        log.getStyleClass().add("button");
        sign.getStyleClass().add("button");
        submit.getStyleClass().add("button");
        
        scene.getStylesheets().add(getClass().getResource("css/auth.css").toExternalForm());
    }
}
