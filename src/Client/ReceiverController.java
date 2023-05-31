package Client;

import Mypac.Mypac;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Hashtable;
import java.util.List;

public class ReceiverController{

    public static Hashtable<String,Boolean> blocklist = new Hashtable<>();
    public static ObservableList<String> observableList = FXCollections.observableArrayList();
    public static String loghistory = "";
    public static String chathistory = "";



    public Button disconnectButton;

    @FXML
    public Button clientsend;

    @FXML
    public Button sendfilebutton;

    @FXML
    public Button blockbutton;

    @FXML
    public Button logclear;



    @FXML
    public Button blocklistbutton;

    @FXML
    public Button chatclear;


    ClientMain clientMain;
    Socket socket;
    public static int newnumber;

    void setClientMain(ClientMain clientMain, Socket socket)
    {
        this.socket = socket;
        this.clientMain = clientMain;
    }

    public void refreshlist()
    {
        chatterlist.setItems(Client.observableList);
    }

    @FXML
    public ListView<String> chatterlist;

    @FXML
    public TextArea writeArea;

    @FXML
    public TextArea logArea;

    @FXML
    public TextArea messageBox;

    @FXML
    public void disconnect(ActionEvent event) {
        try {
            if (socket.isClosed()) clientMain.showConnectionPage();
            else{
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do You really want to close the chat??", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    output.writeObject(new Mypac(ClientMain.windowname,"<ExitCode>")); //To remove the name from the server
                    socket.close();
                    ReceiverController.loghistory = "";
                    Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION, "Do You want to save your chat history??", ButtonType.YES, ButtonType.NO);
                    alert2.showAndWait();

                    if (alert2.getResult() == ButtonType.YES) {
                        chathistory = messageBox.getText();
                        File history = new File(clientMain.windowname+" ChatHistory.txt");
                        Files.write(history.toPath(),chathistory.getBytes());
                        chathistory = "";
                        Alert alert3 = new Alert(Alert.AlertType.INFORMATION, "Chat history saved.", ButtonType.OK);
                        alert3.showAndWait();
                    }
                    else if (alert2.getResult() == ButtonType.NO) {
                        chathistory = "";
                    }
                    clientMain.showConnectionPage();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @FXML
    public void blistaction(ActionEvent event) {
        try {
            loghistory = logArea.getText();
            chathistory = messageBox.getText();
            newnumber = ClientController.connectionnumber;
            clientMain.showBlockerPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void sendfaction(ActionEvent event) {
        try {
            String receivername = chatterlist.getSelectionModel().getSelectedItem();
            if(receivername.compareTo(ClientMain.windowname)==0)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Wrong client");
                alert.setHeaderText("Wrong client");
                alert.setContentText("You cannot send file to yourself buddy :v");
                alert.showAndWait();
            }
            else if(blocklist.get(receivername)== true)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Blocked client");
                alert.setHeaderText("Blocked client");
                alert.setContentText("This user blocked u buddy :v");
                alert.showAndWait();
            }
            else
            {
                FileChooser fileChooser = new FileChooser();
                fileChooser.setTitle("Open Resource File");
                fileChooser.getExtensionFilters().addAll(
                        new FileChooser.ExtensionFilter("Text Files", "*.txt"),
                        new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"),
                        new FileChooser.ExtensionFilter("Audio Files", "*.wav", "*.mp3", "*.aac"),
                        new FileChooser.ExtensionFilter("All Files", "*.*"));
                List<File> filelist = fileChooser.showOpenMultipleDialog(clientMain.stage);
                if(filelist != null)
                {
                    for(File selectedFile:filelist)
                    {
                        Mypac mypac = new Mypac();
                        mypac.nofile = false;
                        mypac.file = Files.readAllBytes(selectedFile.toPath());
                        mypac.filename = selectedFile.getName();
                        mypac.nomessage = true;
                        mypac.receiver = chatterlist.getSelectionModel().getSelectedItem();
                        mypac.sender = ClientMain.windowname;
                        ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                        output.writeObject(mypac);
                        output.flush();
                    }
                }
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION, "File(s) has been sent :)", ButtonType.OK);
                alert2.showAndWait();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void lclearaction(ActionEvent event) {
        logArea.clear();
    }

    @FXML
    public void cclearaction(ActionEvent event) {
        messageBox.clear();
    }

    @FXML
    public void sendaction(ActionEvent event) {
        try {
            String receivername = chatterlist.getSelectionModel().getSelectedItem();
            if(receivername.compareTo(ClientMain.windowname)==0)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Wrong client");
                alert.setHeaderText("Wrong client");
                alert.setContentText("You cannot send message to yourself buddy :v");
                alert.showAndWait();
            }
            else if(blocklist.get(receivername)== true)
            {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Blocked client");
                alert.setHeaderText("Blocked client");
                alert.setContentText("This user blocked u buddy :v");
                alert.showAndWait();
            }
            else
            {
                String message = writeArea.getText();
                writeArea.clear();
                String sender = ClientMain.windowname;
                String[] nothings = new String[1];
                messageBox.appendText("[me("+receivername + ")] : " + message + "\n");
                Mypac mypac = new Mypac(sender,receivername,message,nothings,"<NULL>","<NULL");
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                output.writeObject(mypac);
                output.flush();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void blockaction(ActionEvent event) {
        try {
            if(chatterlist.getSelectionModel().getSelectedItem().compareTo(ClientMain.windowname)== 0)
            {
                Alert alert3 = new Alert(Alert.AlertType.ERROR);
                alert3.setTitle("Wrong client");
                alert3.setHeaderText("Wrong client");
                alert3.setContentText("You cannot block yourself buddy :v");
                alert3.showAndWait();
            }
            else {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do You really want to block this user??", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();

                if (alert.getResult() == ButtonType.YES) {
                    Mypac mypac = new Mypac();
                    mypac.requestmode = "<Block>";
                    mypac.sender = ClientMain.windowname;
                    mypac.receiver = chatterlist.getSelectionModel().getSelectedItem();
                    mypac.nomessage = true;
                    observableList.add(mypac.receiver);
                    ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                    output.writeObject(mypac);
                    output.flush();
                    Alert alert2 = new Alert(Alert.AlertType.INFORMATION, mypac.receiver + " has been blocked.", ButtonType.OK);
                    alert2.showAndWait();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }



}
