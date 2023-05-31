package Mypac;

import java.io.File;
import java.io.Serializable;

/**
 * Created by MusiCLife on 12/16/2015.
 */
public class Mypac implements Serializable{
    public String sender;
    public String receiver;
    public String message;
    public String[] clientlist;
    public String log;
    public String requestmode;
    public boolean nomessage=false;
    public boolean nolog = true;
    public boolean nolist = true;
    public boolean nofile = true;
    public byte[] file;
    public String filename = "";

    public Mypac(String sender,String receiver,String message,String[] clientlist,String log,String requestmode)
    {
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.clientlist = clientlist;
        this.log = log;
        this.requestmode = requestmode;
    }

    public Mypac(String sender,String requestmode)
    {
        this.sender = sender;
        receiver = new String();
        message = new String();
        clientlist = new String[1];
        clientlist[0] = new String();
        log = new String();
        this.requestmode = requestmode;
    }

    public Mypac(String sender)
    {
        this.sender = sender;
        receiver = new String();
        message = new String();
        clientlist = new String[1];
        clientlist[0] = new String();
        log = new String();
        requestmode = new String();
    }

    public Mypac()
    {
        sender = new String();
        receiver = new String();
        message = new String();
        clientlist = new String[1];
        clientlist[0] = new String();
        log = new String();
        requestmode = new String();
    }

    public Mypac(String[] clientlist)
    {
        sender = new String();
        receiver = new String();
        message = new String();
        this.clientlist = clientlist;
        log = new String();
        requestmode = new String();
        nomessage = true;
    }

    public Mypac(Mypac mypac,String[] clientlist)
    {
        sender = mypac.sender;
        receiver = mypac.receiver;
        message = mypac.message;
        this.clientlist = clientlist;
        log = new String();
        requestmode = new String();
    }

}
