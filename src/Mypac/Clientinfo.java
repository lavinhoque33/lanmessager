package Mypac;

import java.io.Serializable;

/**
 * Created by MusiCLife on 12/20/2015.
 */
public class Clientinfo implements Serializable{
    public String name;
    public String password;

    public Clientinfo()
    {
        name = "";
        password = "";
    }
}
