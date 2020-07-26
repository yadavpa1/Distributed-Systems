
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.util.Scanner;


public class Prac6Client {
    public static void main(String[] args) throws IOException {
        try
        { 
            Scanner sc = new Scanner(System.in); 
            InetAddress ip = InetAddress.getByName("localhost"); 
            Socket socket = new Socket(ip, 10000); 
            System.out.println("Client connected");
      
            DataInputStream dis = new DataInputStream(socket.getInputStream()); 
            DataOutputStream dout = new DataOutputStream(socket.getOutputStream()); 
            
            while (true)  
            { 
                System.out.println(dis.readUTF()); 
                String tosend = sc.nextLine(); 
                dout.writeUTF(tosend); 
                   
                if(tosend.equals("exit")) 
                { 
                    System.out.println("Closing this connection: " + socket); 
                    socket.close(); 
                    System.out.println("Connection closed"); 
                    break; 
                }
                else if(tosend.equals("1")){
                    System.out.println("Enter the amount to be deposited");
                    dout.writeUTF(sc.nextLine());
                    String received = dis.readUTF(); 
                    System.out.println(received); 
                }
                else if(tosend.equals("2")){
                    System.out.println("Enter the amount to withdraw");
                    dout.writeUTF(sc.nextLine());
                    String received = dis.readUTF(); 
                    System.out.println(received); 
                }
                else if(tosend.equals("3")){
                    String received = dis.readUTF(); 
                    System.out.println("Account Balance: "+received); 
                }
            } 
            sc.close(); 
            dis.close(); 
            dout.close(); 
        }catch(Exception e){ 
            e.printStackTrace(); 
        }
    }
}
