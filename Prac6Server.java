
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.ReentrantLock;


public class Prac6Server {
    public static void main(String[] args) throws IOException{
        ServerSocket server =  new ServerSocket(10000);
        Account acc = new Account(1000);
        ReentrantLock re = new ReentrantLock();
        System.out.println("Server started");
        while(true){
            Socket socket = null;
            try{
                socket = server.accept();
                System.out.println("A new Client accepted");

                DataInputStream din = new DataInputStream(socket.getInputStream()); //takes input from client socket
                DataOutputStream dout = new DataOutputStream(socket.getOutputStream());

                System.out.println("Assigning new thread to the client");
                Thread t = new ClientHandler(socket,din,dout,acc,re);
                t.start();
            }
            catch(IOException i){
                socket.close();
                System.out.println(i);
            }
        }  
    }
}

class ClientHandler extends Thread implements Runnable{
    final DataInputStream din; 
    final DataOutputStream dout; 
    final Socket socket; 
    final private Account acc; 
    final private ReentrantLock re;
    public ClientHandler(Socket socket, DataInputStream din, DataOutputStream dout, Account acc, ReentrantLock re)  
    { 
       this.socket = socket;
       this.din = din;
       this.dout = dout;
       this.acc = acc;
       this.re = re;
    } 
  
    @Override
    public void run()  
    { 
        String received; 
        String toreturn; 
        while(true)  
        { 
            try
            { 
                dout.writeUTF("Enter the choice:\n 1)Deposit money\n 2)Withdraw money\n 3)Check Balance\n");
                received = din.readUTF();  
                boolean ans = re.tryLock();
                if(ans){
                    try{
                        System.out.println("if part");
                        if(received.equals("exit")){
                            System.out.println("Client " + this.socket + " sends exit..."); 
                            System.out.println("Closing this connection."); 
                            this.socket.close(); 
                            System.out.println("Connection closed"); 
                            break; 
                        }
                        switch(received) {
                            case "1": acc.deposit(din.readUTF());
                                      dout.writeUTF("Deposited"); 
                                      break; 
                            case "2": acc.withdraw(din.readUTF());
                                      dout.writeUTF("Withdrawn"); 
                                      break; 
                            default: toreturn = acc.checkBalance();
                                     dout.writeUTF(toreturn); 
                                     break; 
                        }
                        //Thread.sleep(1500);
                    }
                    catch(InterruptedException e2){
                        System.out.println(e2);
                    }
                    finally{
                        re.unlock();
                    }
                }
                else{
                    //try{ 
                        System.out.println("in else part");
//                        Thread.sleep(1000); 
                    //} 
//                    catch(InterruptedException e){
//                      e.printStackTrace(); 
//                    }
                }
            }
            catch(IOException e1){ 
                System.out.println(e1);
            }
        } 
        try
        { 
            this.din.close(); 
            this.dout.close(); 
              
        }
        catch(IOException e){ 
            e.printStackTrace(); 
        } 
    } 
}

class Account{
    private int balance;
    public Account(int balance){
        this.balance = balance;
    }
    public String checkBalance(){
        return balance+"";
    }
    
    public void deposit(String amount) throws InterruptedException{
        this.balance += Integer.valueOf(amount);
        Thread.sleep(1500);
    }
    
    public void withdraw(String amount) throws InterruptedException{
        this.balance -= Integer.valueOf(amount);
        Thread.sleep(1500);
    }
}