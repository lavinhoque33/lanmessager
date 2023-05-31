package Server;

import Mypac.Mypac;
import Mypac.Mypaclogin;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import Mypac.Clientinfo;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Set;

class receiver implements Runnable {
    Socket socket;
    ServerSide serverSide;
    String name;
    Thread th;

    receiver(Socket socket, ServerSide serverSide, String name) {
        this.socket = socket;
        this.serverSide = serverSide;
        this.name = name;
        th = new Thread(this);
        th.start();
    }

    public void run() {
        Mypac mp = new Mypac();
        mp.log = name + " has joined";
        serverSide.logwindow.setText(mp.log + "\n");
        Connector.sendstatus(mp,name);
        while (!socket.isClosed()) {
            try {
                ObjectInputStream input = new ObjectInputStream(socket.getInputStream());
                Object object = input.readObject();
                if(object instanceof Mypac)
                {
                    Mypac mypac = (Mypac) object;

                    if (mypac.requestmode.compareTo("<ExitCode>") == 0) {
                        String s = name + " has left";
                        Mypac mypac3 = new Mypac();
                        mypac3.log = s;
                        serverSide.logwindow.appendText(s + "\n");
                        System.out.println(name + "removing");
                        ServerSide.relationList.remove(name);
                        Platform.runLater(() -> Connector.observableList.remove(name));

                        serverSide.clientList.setItems(Connector.observableList);

                        socket.close();
                        ServerSide.clientnumbers-=1;
                        Connector.sendstatus(mypac3,name);
                    }
                    else if (mypac.requestmode.compareTo("<Block>") == 0) {
                        Socket sock = ServerSide.relationList.get(mypac.receiver);
                        ObjectOutputStream outputStream = new ObjectOutputStream(sock.getOutputStream());
                        outputStream.writeObject(mypac);
                        outputStream.flush();
                    }
                    else if (mypac.requestmode.compareTo("<Unblock>") == 0) {
                        Socket sock = ServerSide.relationList.get(mypac.receiver);
                        ObjectOutputStream outputStream = new ObjectOutputStream(sock.getOutputStream());
                        outputStream.writeObject(mypac);
                        outputStream.flush();
                    }
                    else if(!mypac.nofile)
                    {
                        Socket sock = ServerSide.relationList.get(mypac.receiver);
                        ObjectOutputStream outputStream = new ObjectOutputStream(sock.getOutputStream());
                        outputStream.writeObject(mypac);
                        outputStream.flush();
                    }

                    else {
                        Socket sock = ServerSide.relationList.get(mypac.receiver);
                        ObjectOutputStream outputStream = new ObjectOutputStream(sock.getOutputStream());
                        outputStream.writeObject(mypac);
                        outputStream.flush();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

class Connector implements Runnable
{
    ServerSide serverSide;
    ServerSocket serverSocket;
    int port;
    Thread thread;
    static ObservableList<String> observableList = FXCollections.observableArrayList();

    Connector(int port, ServerSide serverSide)
    {
        this.serverSide = serverSide;
        this.port = port;
        thread = new Thread(this);
        thread.start();
    }

    public static void sendstatus(Mypac mypac,String nam)
    {
        Set<String> allnames = ServerSide.relationList.keySet();
        String[] names = new String[ServerSide.clientnumbers];
        int i=0;
        Iterator<String> itr3 = allnames.iterator();
        while (itr3.hasNext()) {
            names[i] = itr3.next();
            i++;
        }
        mypac.clientlist = names;
        String log = mypac.log;

        Iterator<String> itr = allnames.iterator();
        while (itr.hasNext()) {
            try {
                String name = itr.next();
                if(name.compareTo(nam)== 0)
                {
                    mypac.log = "";
                    mypac.nolog = true;
                    mypac.nomessage = true;
                    mypac.nolist = false;
                }
                else
                {
                    mypac.log = log;
                    mypac.nolog = false;
                    mypac.nomessage = true;
                    mypac.nolist = false;
                }
                Socket sock = ServerSide.relationList.get(name);
                ObjectOutputStream outputStream = new ObjectOutputStream(sock.getOutputStream());
                outputStream.writeObject(mypac);
                outputStream.flush();
            } catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            while(true) {
                Socket socket = serverSocket.accept();

                ObjectInputStream inputStream = new ObjectInputStream(socket.getInputStream());
                Object object = inputStream.readObject();
                if(object instanceof Mypaclogin)
                {
                    Mypaclogin mypaclogin = (Mypaclogin) object;
                    if(mypaclogin.signup)
                    {
                        Clientinfo clientinfo = new Clientinfo();
                        clientinfo.name = mypaclogin.name;
                        clientinfo.password = mypaclogin.password;
                        ServerSide.credentialsList.put(mypaclogin.name,clientinfo);
                        Mypaclogin mypaclogin1 = new Mypaclogin();
                        mypaclogin1.signupresponse = true;
                        ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                        outputStream.writeObject(mypaclogin1);
                    }
                    else if(mypaclogin.login)
                    {

                        if(!ServerSide.credentialsList.containsKey(mypaclogin.name))
                        {
                            System.out.println("logging in trying");
                            Mypaclogin mypaclogin1 = new Mypaclogin();
                            mypaclogin1.serverresponse = false;
                            ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                            outputStream.writeObject(mypaclogin1);
                        }
                        else
                        {
                            String password = ServerSide.credentialsList.get(mypaclogin.name).password;
                            if(mypaclogin.password.compareTo(password)==0)
                            {
                                Mypaclogin mypaclogin1 = new Mypaclogin();
                                mypaclogin1.serverresponse = true;
                                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                                outputStream.writeObject(mypaclogin1);
                                socket.close();
                                Socket sock = serverSocket.accept();
                                ServerSide.clientnumbers += 1;
                                String clientName = mypaclogin.name;
                                ServerSide.relationList.put(clientName, sock);
                                Platform.runLater(() -> observableList.add(clientName));
                                serverSide.clientList.setItems(observableList);
                                new receiver(sock, serverSide,clientName);
                            }
                            else
                            {
                                Mypaclogin mypaclogin1 = new Mypaclogin();
                                mypaclogin1.serverresponse = false;
                                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                                outputStream.writeObject(mypaclogin1);
                            }
                        }
                    }
                }


            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

public class ServerMain extends Application {
    static Stage stage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        stage = primaryStage;
        FXMLLoader serverFXML = new FXMLLoader(getClass().getResource("ServerSide.fxml"));
        Parent Root = serverFXML.load();
        ServerSide serverSide = serverFXML.getController();

        new Connector(33333, serverSide);

        primaryStage.setTitle("Server Window");
        primaryStage.setScene(new Scene(Root));
        primaryStage.show();
        File clientinfo = new File("ClientInfo.bin");
        if(clientinfo.exists())
        {
            FileInputStream fileIn = new FileInputStream(clientinfo);
            ObjectInputStream in = new ObjectInputStream(fileIn);
            ServerSide.credentialsList = (Hashtable)in.readObject(  );
        }

    }

    public static void main(String[] args) {
        launch(args);
    }
}
