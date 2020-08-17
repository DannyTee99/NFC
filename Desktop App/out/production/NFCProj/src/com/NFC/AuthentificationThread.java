package com.NFC;


import java.util.Hashtable;

public class AuthentificationThread extends Thread{

    public boolean done=false;
    public boolean validUser=false;

    public String user="";
    public String pw= "";

    private Hashtable<String, String> userdata= new Hashtable();

    public AuthentificationThread(String u, String p)
    {
        this.user=u;
        this.pw=p;
        done=false;
        validUser=false;

    }

    @Override
    public void run() {
       validUser=true;
        done=true;

    }

    }




