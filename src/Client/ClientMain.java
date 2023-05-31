package Client;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import Mypac.Mypac;
import javax.lang.model.type.NullType;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;

class Client implements Runnable
{
    ClientMain clientMain;
    Socket socket;
    Thread thread;
    int port;
    String name;
    static ObservableList<String> observableList = FXCollections.observableArrayList();

    Client(int port, String name, ClientMain clientMain)
    {
        this.clientMain = clientMain;
        this.name = name;
        this.port = port;
        thread = new Thread(this);
        thread.start();
    }

    public void run() {
        try {
            socket = new Socket("127.0.0.1", port);
            clientMain.socket = socket;
            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
            outputStream.writeObject(new Mypac(name));
            outputStream.flush();
            while(!socket.isClosed()){
                try {
                    ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                    Mypac mypac = (Mypac) inputStream.readObject();
                    if(mypac.requestmode.compareTo("<Block>")==0)
                        ReceiverController.blocklist.put(mypac.sender,true);
                    else if(mypac.requestmode.compareTo("<Unblock>")==0)
                        ReceiverController.blocklist.put(mypac.sender,false);
                    else if(!mypac.nofile)
                    {
                        Platform.runLater(new Runnable(){
                            @Override
                            public void run() {
                                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Incoming file from "+mypac.sender +":\n\t\t\t"+mypac.filename+ "\n Receive?? ", ButtonType.YES, ButtonType.NO);
                                alert.showAndWait();
                                if (alert.getResult() == ButtonType.YES){
                                    File selectedFile = new File(mypac.filename);
                                    try {
                                        Files.write(selectedFile.toPath(), mypac.file);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        });

                    }
                    else
                    {
                        if(!mypac.nolist)
                        {
                            Platform.runLater(() -> observableList.clear());
                            for(String i:mypac.clientlist) {
                                System.out.println(i);
                                ReceiverController.blocklist.put(i,false);
                                Platform.runLater(() -> observableList.add(i));
                            }
                            Thread.sleep(500);
                            clientMain.receiverController.chatterlist.setItems(observableList);
                        }
                        if(mypac.nolog == false)
                            clientMain.receiverController.logArea.appendText(mypac.log+ "\n");
                        if(mypac.nomessage == false)
                            clientMain.receiverController.messageBox.appendText("[" +mypac.sender +"]"+ ": " + mypac.message + "\n");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}


public class ClientMain extends Application {

    Stage stage;
    ReceiverController receiverController;
    blockcontroller blockController;
    Client client;
    Socket socket;
    Parent parent;
    static String windowname;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        showConnectionPage();
;
    }

    public void showConnectionPage() throws Exception
    {
        FXMLLoader connectorScreen = new FXMLLoader(getClass().getResource("ClientConnector.fxml"));

        Parent root = connectorScreen.load();

        ClientController clientController = (ClientController) connectorScreen.getController();
        clientController.setClientMain(this);
        Scene scene = new Scene(root);
        stage.setTitle("Client");
        stage.setScene(scene);
        stage.centerOnScreen();
        stage.show();
    }

    public void showReceiverPage() throws Exception
    {
        FXMLLoader receiverScreen = new FXMLLoader(getClass().getResource("Receiver.fxml"));

        Parent root = receiverScreen.load();
        parent = root;

        ReceiverController receiverController = (ReceiverController) receiverScreen.getController();
        this.receiverController = receiverController;
        receiverController.setClientMain(this, socket);

        stage.setScene(new Scene(root));
        stage.setTitle("ChatBox :" + windowname);
        stage.centerOnScreen();
        stage.show();
        File history = new File(windowname + " ChatHistory.txt");
        if(history.exists())
        {
            byte[] hist = Files.readAllBytes(history.toPath());
            if(hist.length!=0)
                ReceiverController.chathistory = new String(hist);
        }
        if(ReceiverController.newnumber == ClientController.connectionnumber)
        {
            receiverController.logArea.setText(ReceiverController.loghistory);
            receiverController.messageBox.setText(ReceiverController.chathistory);
            receiverController.refreshlist();
            System.out.println(ClientController.connectionnumber);
        }
        else
        {
            if(!ReceiverController.chathistory.isEmpty()) {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do You want to recover your chat history??", ButtonType.YES, ButtonType.NO);
                alert.showAndWait();
                if (alert.getResult() == ButtonType.YES) {
                    receiverController.messageBox.setText(ReceiverController.chathistory);
                    receiverController.refreshlist();
                    System.out.println(ClientController.connectionnumber);
                } else if (alert.getResult() == ButtonType.NO) {
                    Thread.sleep(500);
                    Alert alert2 = new Alert(Alert.AlertType.CONFIRMATION, "Do You want to clear your previous chat history??", ButtonType.YES, ButtonType.NO);
                    alert2.showAndWait();
                    if (alert2.getResult() == ButtonType.YES) {
                        ReceiverController.chathistory = "";
                        Files.write(history.toPath(),"".getBytes());
                    }
                }
            }
        }

    }

    public void showBlockerPage() throws Exception
    {
        FXMLLoader blockScreen = new FXMLLoader(getClass().getResource("blockwindow.fxml"));

        Parent root = blockScreen.load();
        parent = root;

        blockcontroller blockController = (blockcontroller) blockScreen.getController();
        this.blockController = blockController;
        blockController.setClientMain(this, socket);
        stage.setScene(new Scene(root));
        stage.setTitle("BlockWindow :" + windowname);
        stage.centerOnScreen();
        stage.show();
        blockController.showlist();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
