import java.io.*;
import java.net.*;
import java.util.*;
public class Server{
    static Vector<ClientHandler> users=new Vector<>();
    public static void main(String[] args){
        try{
            ServerSocket ss=new ServerSocket(27015);
            Socket s;
            while(true){
                System.out.println("Waiting for clients");
                s=ss.accept();
                DataInputStream din=new DataInputStream(s.getInputStream());
                DataOutputStream dout=new DataOutputStream(s.getOutputStream());
                String name=din.readUTF();
                System.out.println(name+" Joined the room");
                dout.writeUTF("Welcome to JCHAT "+ name+"#JCHAT");
                ClientHandler clt=new ClientHandler(name,s,din,dout);
                users.add(clt);
                Thread t=new Thread(clt);
                t.start();
            }
        }
        catch(IOException E){
            E.printStackTrace();
        }
    }
}
class ClientHandler implements Runnable{
    String name;
    Socket s;
    DataInputStream din;
    DataOutputStream dout;
    boolean online;
    public ClientHandler(String name,Socket s,DataInputStream din,DataOutputStream dout){
        this.name=name;
        this.s=s;
        this.din=din;
        this.dout=dout;
        this.online=true;
    }
    @Override
    public void run(){
        String response;
        while(true){
            try{
                response=din.readUTF();
                StringTokenizer st=new StringTokenizer(response,"#");
                if(st.countTokens()>1){
                    String body=st.nextToken();
                    for(ClientHandler clt:Server.users){
                        if(clt!=this){
                            clt.dout.writeUTF(response);
                        }
                    }
                    if(body.equals("logged out")){
                        this.dout.writeUTF(response);
                        Server.users.remove(this);
                        break;
                    }
                }
            }
            catch(IOException E){
                E.printStackTrace();
            }
        }   
        try{
            this.s.close();
            this.din.close();
            this.dout.close();
        }
        catch(IOException E){
            E.printStackTrace();
        }
    }
}