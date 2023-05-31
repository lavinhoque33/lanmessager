package Mypac;

import java.io.Serializable;

/**
 * Created by MusiCLife on 12/20/2015.
 */
public class Mypaclogin implements Serializable{
    public String name;
    public String password;
    public boolean login = false;
    public boolean signup = false;
    public boolean serverresponse;
    public boolean signupresponse;

    public Mypaclogin()
    {
        name = "";
        password = "";
    }
}
