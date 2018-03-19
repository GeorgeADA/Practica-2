package cliente;

/**
 *
 * @author George Alfred
 */
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultListModel;
import javax.swing.JOptionPane;
import mensaje.Mensaje;
import terminacion.Terminacion;


public class HiloCliente extends Thread{
    
    private final Socket socket;
    private final Cliente cliente;
    private DataInputStream entrada;

    public HiloCliente(Socket socketCliente, Cliente c) {
        socket = socketCliente;
        cliente = c;
    }
    
    @Override
    public void run(){
        while (true) {            
            try {
                entrada = new DataInputStream(socket.getInputStream());
                cliente.actualizar(entrada.readUTF());
            } catch(IOException ex){
                JOptionPane.showMessageDialog(null,"HiloCliente: Error en las comunicaciones");
                System.exit(0);
            }
        }
    }
    
    
    
}

