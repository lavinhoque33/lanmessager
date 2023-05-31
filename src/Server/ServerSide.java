package Server;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;
import Mypac.Clientinfo;

public class ServerSide {
    public static int clientnumbers;


    public static Hashtable<String, Socket> relationList = new Hashtable<>();

    public static Hashtable<String, Clientinfo> credentialsList = new Hashtable<>();

    @FXML
    public ListView<String> clientList;


    @FXML
    //public TextField messageBox;
    public TextArea logwindow;


    @FXML
    public Label warningLabelEmpty;

    @FXML
    public Button exitButton;




    public void closeServer(ActionEvent event) throws Exception{
        File clientinfo = new File("ClientInfo.bin");
        FileOutputStream fileOut = new FileOutputStream(clientinfo);
        ObjectOutputStream out = new ObjectOutputStream(fileOut);
        out.writeObject(credentialsList);
        System.exit(0);
    }
}
