package Client;

import Mypac.Mypac;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ListView;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class blockcontroller {

    ClientMain clientMain;
    Socket socket;

    @FXML
    public ListView<String> blockArea;

    @FXML
    public Button unblockbutton;


    @FXML
    public Button returnbutton;

    void setClientMain(ClientMain clientMain, Socket socket)
    {
        this.socket = socket;
        this.clientMain = clientMain;
    }


    public void showlist()
    {
        blockArea.setItems(ReceiverController.observableList);
    }

    @FXML
    void returnaction(ActionEvent event) {
        try {
            clientMain.showReceiverPage();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    void unblockaction(ActionEvent event) {
        try {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do You really want to unblock this user??", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();
            if (alert.getResult() == ButtonType.YES) {
                Mypac mypac = new Mypac();
                mypac.requestmode = "<Unblock>";
                mypac.sender = ClientMain.windowname;
                mypac.receiver = blockArea.getSelectionModel().getSelectedItem();
                mypac.nomessage = true;
                ReceiverController.observableList.remove(mypac.receiver);
                blockArea.setItems(ReceiverController.observableList);
                ObjectOutputStream output = new ObjectOutputStream(socket.getOutputStream());
                output.writeObject(mypac);
                output.flush();
                Alert alert2 = new Alert(Alert.AlertType.INFORMATION, mypac.receiver + " has been unblocked.", ButtonType.OK);
                alert2.showAndWait();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
