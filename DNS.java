/* Comment block
   CS656-003 Group M2
   Venkat Sravanth(vl73), NaveenBhalaji (na445), Ayush Garg(ag2323), Dhrumil Shah(das98), Neel Patel(njp62), Orville Cole(ooc3)*/
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.net.BindException;
import java.net.InetAddress;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Inet6Address;
public class DNS{
public static void printCharArray(char[] a){
    for(char b:a)
    System.out.print(b);
  }
public static char[] bytesToCharArray(byte byteArray[],int length) {
    int j=0;
	while(j<length && byteArray[j] != 13)
	j++;
	length=j;
	char charArray[] = new char[length];
    for(int i = 0; i < charArray.length; i++)
        charArray[i] = (char) (((int) byteArray[i]) & 0xFF);
    return charArray;
  }
  public static int byteArrToInt(byte[] byteArr) {
	int numInt = 0;
	for(int i=0;i<byteArr.length;i++)
		numInt=10*numInt + (((int)byteArr[i])-48);
    return numInt;
  }
  //main function
  public static void main(String args[]) throws IOException{
    if(args.length < 1) {
      System.err.println("Please enter port number");
      System.exit(1);
    }
    int portNumber = byteArrToInt(args[0].getBytes());
    if(portNumber > 65535 || portNumber <= 1024){
      System.err.println("Enter a valid port number");
      System.exit(1);
    }
    System.out.println("DNS Server listening on socket : " + portNumber );
    ServerSocket server = null;
    Socket clientSocket = null;
    InputStream inputstream = null;
    OutputStream outputstream = null;
    do{ 
    	try{
        server = new ServerSocket();
        InetSocketAddress socketaddress = new InetSocketAddress(portNumber);
        server.bind(socketaddress,5);
        clientSocket = server.accept();
        inputstream = clientSocket.getInputStream();
        outputstream = clientSocket.getOutputStream();
        byte[] data = new byte[1024];
        int bytesread=inputstream.read(data,0,1024);
        char[] chars= bytesToCharArray(data,bytesread);
        System.out.println("Incoming client connection from " + clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort() + " to me " + clientSocket.getLocalAddress().getHostAddress() + ":" + server.getLocalPort());
        System.out.print("REQ : ");
        printCharArray(chars);
        System.out.print("\n");
        InetAddress inetAddress[] =InetAddress.getAllByName(new String(chars));
        outputstream.write("REQ : ".getBytes());
        outputstream.write(data,0,bytesread);
        for(InetAddress i:inetAddress) {
        	outputstream.write("IP: ".getBytes());
            outputstream.write(i.getHostAddress().getBytes());
            outputstream.write("\n".getBytes());
        }
        double diff = 5000, tmp;
        InetAddress preferredIP = null;
        long start, end;
        Socket s = new Socket();
        for (int i=0; i<inetAddress.length; i++) {
        if(inetAddress[i] instanceof Inet6Address) continue;
            else
            {
              try{
            	 InetSocketAddress pref = new InetSocketAddress(inetAddress[i], 80);
            	 start= System.nanoTime();
            	 s.connect(pref);
            	 end= System.nanoTime();
            	 s.close();
            	 tmp = (end-start)/1000000;
            	 if(tmp < diff){
            	 	diff = tmp;
            	 	preferredIP = inetAddress[i];}}
              catch(IOException e){
                 continue;
              }}}
        if(preferredIP==null)preferredIP = inetAddress[0];
        outputstream.write("\nPreffered IP : ".getBytes());
        outputstream.write(preferredIP.getHostAddress().getBytes());
        outputstream.write("\n".getBytes());
      }
      	catch (BindException b){
          System.err.println("Address already in use, try some another port number");
          b.printStackTrace();
          System.exit(1);
        }catch (SocketException se){
          System.err.println("Something went wrong while creating or accessing socket, try again");
          se.printStackTrace();
        }catch (NullPointerException n){
          System.err.println("Something went wrong, try again");
          n.printStackTrace();
        }catch (UnknownHostException e){
          outputstream.write("\n NO IP ADDRESSESS FOUND \n".getBytes());
      	}catch (IOException e){
          System.err.println("Exception caught when trying to listen for a connection");
          e.printStackTrace();
        }finally{
          if(null != outputstream)outputstream.close();
          if(null != inputstream)inputstream.close();
          if(null != clientSocket)clientSocket.close();
          if(null != server)server.close();
        }}while(true);
  }}
