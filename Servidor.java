package servidor;

/**
 *
 * @author George Alfred
 */
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
import javax.swing.JTextArea;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.*;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.lang.ClassNotFoundException;
import mensaje.Mensaje;
import terminacion.Terminacion;

public final class Servidor extends JFrame{

    private JPanel lugar_tabla;
    private JTextArea control;
    private JTable table;
    private DefaultTableModel dtm;
    private JScrollPane scrolltable, scrolltext;
    private ServerSocket server_socket;
    private Terminacion flujos;
	
    public Servidor(){
        super("SERVIDOR FCC");
        initComponents();
		
        try{
            server_socket = new ServerSocket(11435);
            control.append("El servidor ha iniciado");
            int posicion=0;
            while (true) {                
                Mensaje mensaje;
                Socket cliente = server_socket.accept();

                ObjectInputStream entrada = new ObjectInputStream(cliente.getInputStream());
                mensaje = (Mensaje)entrada.readObject();
			
                actualizar("Cliente conectado / IP: " + mensaje.getIp());
                agregarTabla(String.valueOf(posicion), mensaje.getIp(),mensaje.getNick());
    
                HiloServidor hilo = new HiloServidor(String.valueOf(posicion), cliente, mensaje, this, flujos);
                hilo.start();
				
                flujos.addSocket(cliente);
                posicion++;
            }
        }catch(UnknownHostException e1){
            JOptionPane.showMessageDialog(null,"Servidor: Referencia a Host no resulta");
            System.exit(0);
        }catch(IOException e2){
            JOptionPane.showMessageDialog(null,"Servidor: Error en las comunicaciones");
            System.exit(0);
        }catch(ClassNotFoundException e3){
            JOptionPane.showMessageDialog(null,"Servidor: Error");
            System.exit(0);
        }

    }
	
    public void actualizar(String texto){
        control.append("\n"+texto);
    }
	
    public void agregarTabla(String id, String ip, String nick){
        dtm.addRow(new Object[]{id,ip,nick});	
    }
	
    public void eliminarTabla(String id){
        String fila;
        for(int i=0; i<table.getRowCount(); i++){
            fila = (String)table.getValueAt(i, 0);
            if( fila.equals(id) ){
                dtm.removeRow(i);
                break;
            }
        }
    }
	
    @SuppressWarnings("unchecked")
    private void initComponents(){
        getContentPane().setLayout(null);
        setResizable(false);
        setBounds(0,0,600,650);	

        control = new JTextArea();
        control.setEditable(false);
        scrolltext = new JScrollPane(control);
        scrolltext.setBounds(10,10,570,400);
        add(scrolltext);
		
	lugar_tabla = new JPanel(new FlowLayout());
        lugar_tabla.setBounds(10,420,570,180);
        lugar_tabla.setBackground(Color.WHITE);
		
        String[] columnas = {"ID USUARIO","IP","NICK"};
        Object[][] datos = new Object[0][0];
		
        dtm = new DefaultTableModel(datos,columnas);
        table = new JTable(dtm);
        table.setPreferredScrollableViewportSize(new Dimension(560, 170));
        table.setFillsViewportHeight(true);
        scrolltable = new JScrollPane( table, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        lugar_tabla.add(scrolltable);	
        add(lugar_tabla);
		
		
        flujos = new Terminacion();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                flujos.cerrarTodo();
                System.exit(0);
            }
        });
        setVisible(true);    
    }
    
    public static void main(String[] args) {	
        Servidor fr = new Servidor();
    }

}
