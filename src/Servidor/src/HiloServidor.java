package servidor;

/**
 *
 * @author George Alfred
 */
import java.io.ObjectInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.io.IOException;
import java.util.Vector;
import javax.swing.JOptionPane;
import mensaje.Mensaje;
import terminacion.Terminacion;

public class HiloServidor extends Thread{
    
    private Socket cliente;
    private Servidor server;
    private ObjectInputStream entrada;
    private DataOutputStream salida;
    private static Vector<HiloServidor> usuarios = new Vector<HiloServidor>();
    private String id,ip,nick;
    private Terminacion flujos;
    
    public HiloServidor(String id, Socket socket, Mensaje m, Servidor servidor, Terminacion flujos){
        this.id = id;
        cliente = socket;
        ip = m.getIp();
        nick = m.getNick();
        server = servidor;
        usuarios.add(this);
        this.flujos = flujos;
        
        for(int i = 0; i < usuarios.size(); i++){ // Envia mensaje a todos clientes de que un nuevo cliente ha iniciado sesión
            usuarios.get(i).enviarMensajeClientes(">>Servidor: "+nick+" se ha unido al chat");
        }
    }
    
    public void run(){
        while (true) {            
            try {
                entrada = new ObjectInputStream(cliente.getInputStream());
                Mensaje mensaje = new Mensaje();
                mensaje = (Mensaje)entrada.readObject(); // 	Cuando un cliente envie un mensaje al servidor y este lo lea, el servidor lo reenviará a todos los clientes (para simular un chat) 
                for (int i = 0; i < usuarios.size(); i++) {
                    usuarios.get(i).enviarMensajeClientes(">>"+mensaje.getNick()+": "+mensaje.getMensaje());
                }
                server.actualizar(mensaje.getIp()+":("+mensaje.getNick()+") ha enviado un mensaje");
                server.actualizar("He enviado mensaje a todos los clientes");
            }catch(Exception e){// Habrá una excepción cuando se desconecte un cliente
                break;
            }
        }
        usuarios.removeElement(this);
        server.actualizar(ip+":("+nick+") se ha desconectado.");
        server.eliminarTabla(id);
        
        try {
            cliente.close(); // cierra la comunicación con ese cliente que se ha desconecatado
        }catch(IOException e){
            JOptionPane.showMessageDialog(null,"HiloServidor: Error en las comunicaciones");
            e.printStackTrace();
            System.exit(0);
        }
    }
   
    public void enviarMensajeClientes(String mensaje){
        try{		
            salida = new DataOutputStream(cliente.getOutputStream());
            salida.writeUTF(mensaje);
            flujos.addDataOutput(salida);		
        }catch(UnknownHostException e1){
            JOptionPane.showMessageDialog(null,"HiloServidor: Referencia a Host no resulta");
            System.out.println(e1);
        }catch(IOException e2){
            JOptionPane.showMessageDialog(null,"HiloSservidor: Error en las comunicaciones");
            e2.printStackTrace();
        }
    }
	
}
