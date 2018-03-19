package terminacion;

/**
 *
 * @author George Alfred
 */
import java.util.Vector;
import java.net.Socket;
import java.net.ServerSocket;
import java.io.ObjectOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Terminacion{

    private final Vector<Socket> sockets;
    private final Vector<ServerSocket> serversockets;
    private final Vector<DataOutputStream> dataoutputs;
    private final Vector<ObjectOutputStream> objectoutputs;
	
    public Terminacion(){
        sockets = new Vector<Socket>();
        serversockets = new Vector<ServerSocket>();
        dataoutputs = new Vector<DataOutputStream>();
        objectoutputs = new Vector<ObjectOutputStream>();
    }
    
    public void addSocket(Socket s){
        sockets.add(s);
    }

    public void addServerSocket(ServerSocket ss){
        serversockets.add(ss);
    }
	
    public void addDataOutput(DataOutputStream dos){
        dataoutputs.add(dos);
    }
	
    public void addObjectOutput(ObjectOutputStream oos){
        objectoutputs.add(oos);
    }
	
    public void cerrarTodo(){
        try{
            for(int i=0; i<sockets.size(); i++){
                sockets.get(i).close();
            }
            for(int i=0; i<serversockets.size(); i++){
                serversockets.get(i).close();
            }
            for(int i=0; i<dataoutputs.size(); i++){
                dataoutputs.get(i).close();
            }
            for(int i=0; i<objectoutputs.size(); i++){
                objectoutputs.get(i).close();
            }
        }catch(IOException e){}
		
    }
}
