import java.io.*;
import java.net.*;
import java.util.*;
public class Client{
    static boolean online;
    public static void main(String[] args){
        try{
            Scanner sc=new Scanner(System.in);
            System.out.print("Enter your name:");
            String name=sc.next();
            Socket s=new Socket("localhost",27015);
            DataInputStream din=new DataInputStream(s.getInputStream());
            DataOutputStream dout=new DataOutputStream(s.getOutputStream());
            dout.writeUTF(name);
            Thread sendMessage=new Thread(new Runnable(){
                @Override
                public void run(){
                    while(true){
                        try{
                            String message=sc.nextLine();
                            dout.writeUTF(message + "#" + name);
                            if(message.equals("logged out"))
                                break; 
                        }
                        catch(IOException E){
                            E.printStackTrace();
                        }
                    }
                }
            });
            Thread receiveMessage=new Thread(new Runnable(){
                @Override
                public void run(){
                    while(true){
                        try{
                            String message=din.readUTF();
                            StringTokenizer st=new StringTokenizer(message,"#");
                            String body=st.nextToken();
                            String sender=st.nextToken();
                            if(sender.equals(name)){
                                break;
                            }
                            System.out.println(sender+":"+body);
                        }
                        catch(IOException E){
                            E.printStackTrace();
                        }
                    }
                }          
            });
            sendMessage.start();
            receiveMessage.start();
        }
        catch(IOException E){
            E.printStackTrace();
        }
    }
}