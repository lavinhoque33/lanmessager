package Client;


import Mypac.Mypac;
import Mypac.Mypaclogin;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientController {

    public static int connectionnumber=0;

    public Button exitButton;

    public Button signinbutton;

    public Button signupbutton;
    ClientMain clientMain;

    @FXML
    public TextField nameField;

    @FXML
    public TextField passwordfield;

    @FXML

    void setClientMain(ClientMain clientMain)
    {
        this.clientMain = clientMain;
    }

    @FXML
    void signinaction(ActionEvent event) throws Exception {

        ClientMain.windowname = nameField.getText();
        connectionnumber++;
        Mypaclogin mypaclogin = new Mypaclogin();
        mypaclogin.name = nameField.getText();
        mypaclogin.password = passwordfield.getText();
        mypaclogin.login = true;
        Socket socket = new Socket("127.0.0.1", 33333);
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        output.writeObject(mypaclogin);
        Mypaclogin mypaclogin1;
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        Object object = inputStream.readObject();
        mypaclogin1 = (Mypaclogin) object;
        System.out.println(mypaclogin1.serverresponse);
        socket.close();
        if(mypaclogin1.serverresponse == true)
        {
            Client client = new Client(33333, nameField.getText(), clientMain);
            clientMain.showReceiverPage();
        }
        else if(mypaclogin1.serverresponse == false)
        {
            Alert alert3 = new Alert(Alert.AlertType.ERROR, "Wrong username or password.Did u forget to signup?", ButtonType.OK);
            alert3.showAndWait();
        }


    }

    @FXML
    void signupaction(ActionEvent event) throws Exception {

        ClientMain.windowname = nameField.getText();
        connectionnumber++;
        Mypaclogin mypaclogin = new Mypaclogin();
        mypaclogin.name = nameField.getText();
        mypaclogin.password = passwordfield.getText();
        mypaclogin.signup = true;
        Socket socket = new Socket("127.0.0.1", 33333);
        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
        output.writeObject(mypaclogin);
        Mypaclogin mypaclogin1;
        ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
        Object object = inputStream.readObject();
        mypaclogin1 = (Mypaclogin) object;
        socket.close();
        if(mypaclogin1.signupresponse == true)
        {
            Alert alert3 = new Alert(Alert.AlertType.INFORMATION, "You are signed up.Login Now.", ButtonType.OK);
            alert3.showAndWait();
        }

    }

    public void closeClient(ActionEvent event) {
        System.exit(0);
    }
}
