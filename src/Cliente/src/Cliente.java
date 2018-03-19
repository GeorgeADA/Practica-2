package cliente;

/**
 *
 * @author George Alfred
 */

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.JTextArea;
import javax.swing.JButton;
import javax.swing.JScrollPane;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import java.awt.event.*;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.net.InetAddress;
import java.io.IOException;
import java.io.DataOutputStream;
import java.io.ObjectOutputStream;
import mensaje.Mensaje;
import terminacion.Terminacion;

public class Cliente extends JFrame{

    static Cliente fr;
    private JTextField nick,ip,mensaje;
    private JLabel aceptacion;
    private JTextArea chat;
    private JButton enviar, guardar;
    private JScrollPane scroll;
    private Socket cliente;
    private boolean activo1 = false, activo2 = false, activo3 = false;	
    private ObjectOutputStream salida;
    private Terminacion flujos;
	
    public Cliente(){	
        super("CHAT FCC");
        initComponents();
    }
	
    public void actualizar(String texto){
        chat.append(texto+"\n");
    }
	
    public void eventos(){
        
        mensaje.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){
                if( activo3 == false){
                    mensaje.setText("");
                    activo3 = true;
                }
            }
        });
		
        ip.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(activo2 == false){
                    ip.setText("");
                    activo2 = true;
                }
            }
        });
		
        nick.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e){
                if(activo1 == false){
                    nick.setText("");
                    activo1 = true;
                }
            }
        });
		
        mensaje.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    try {
                        salida = new ObjectOutputStream(cliente.getOutputStream());
						
                        Mensaje mens = new  Mensaje();
                        mens.setIp(InetAddress.getLocalHost().getHostAddress()); // indico la ip del cliente (de este proceso)
                        mens.setNick(nick.getText());
                        mens.setMensaje(mensaje.getText());
					
                        salida.writeObject(mens);
                        flujos.addObjectOutput(salida);
                        mensaje.setText("");
            
                    }catch(UnknownHostException e1){
                        JOptionPane.showMessageDialog(null,"Cliente: Referencia a Host no resulta");
                    }catch(IOException e2){
                        JOptionPane.showMessageDialog(null,"Cliente: Error en las comunicaciones");
                    }
                }
            }
        });
		
        enviar.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
		try {
                    salida = new ObjectOutputStream(cliente.getOutputStream());
                    
                    Mensaje mens = new  Mensaje();
                    mens.setIp(InetAddress.getLocalHost().getHostAddress()); // indico la ip del cliente (de este proceso)
                    mens.setNick(nick.getText());
                    mens.setMensaje(mensaje.getText());
    			
                    salida.writeObject(mens);
                    flujos.addObjectOutput(salida);
                    mensaje.setText(" ");
                }catch(UnknownHostException e1){
                    JOptionPane.showMessageDialog(null,"Cliente: Referencia a Host no resulta");
                }catch(IOException e2){
                    JOptionPane.showMessageDialog(null,"Cliente: Error en las comunicaciones");
                }
            }
        });
		
        guardar.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
    			
                if( activo1 == true && nick.getText()!="" && activo2 == true && ip.getText()!=""  ){		
                    try{
                        cliente = new Socket(ip.getText(), 11435);
                        ObjectOutputStream salida = new ObjectOutputStream(cliente.getOutputStream());
						
                        Mensaje mensaje = new Mensaje();
                        mensaje.setIp(InetAddress.getLocalHost().getHostAddress()); // saco la ip del cliente 
                        mensaje.setNick(nick.getText());
						
                        salida.writeObject(mensaje);
						
                        HiloCliente hilo = new HiloCliente(cliente,fr);
                        hilo.start();
						
                        flujos.addSocket(cliente);
                        flujos.addObjectOutput(salida);
                    }catch(UnknownHostException e1){
                        JOptionPane.showMessageDialog(null,"Cliente: Referencia a Host no resulta");
                        System.out.println(e1);
                    }catch(IOException e2){
                        JOptionPane.showMessageDialog(null,"Cliente: Error en las comunicaciones");
                        e2.printStackTrace();
                    }
                    nick.setVisible(false);
                    ip.setVisible(false);
                    guardar.setVisible(false);
                    aceptacion.setText("BIENVENIDO "+nick.getText());
                    aceptacion.setVisible(true);
                }
                else{
                    JOptionPane.showMessageDialog(null,"Verifique que la informaci\u00f3n haya sido llenada");
                }
            }
        });		
    }

    @SuppressWarnings("unchecked")
    private void initComponents(){
        setLayout(null);
        setResizable(false);
        setBounds(100,100,350,450);		
		
        aceptacion = new JLabel();
        aceptacion.setBounds(15,30,200,30);
        aceptacion.setVisible(false);
		
        nick = new JTextField("Escribe tu nick ");
        nick.setBounds(5,5,100,30);
	
        ip = new JTextField("Escribe la IP del servidor");
        ip.setBounds(125,5,200,30);
		
        guardar = new JButton("Guardar datos");
        guardar.setBounds(100,40,150,25);
		
        chat = new JTextArea();
        chat.setEditable(false);
        scroll = new JScrollPane(chat);
        scroll.setBounds(12,70,320,250);
		
        mensaje = new JTextField("Escribe tu mensaje");
        mensaje.setBounds(12,360,220,50);
		
        enviar = new JButton("Enviar");
        enviar.setBounds(243,375,80,30);
		
        add(aceptacion);
        add(nick);
        add(ip);
        add(guardar);
        add(mensaje);
        add(scroll);
        add(enviar);
		
        eventos();
        setVisible(true);
    	
        flujos = new Terminacion();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                flujos.cerrarTodo();
                System.exit(0);
            }
        });
    }
    
    public static void main(String[] args) {
        fr = new Cliente();
    }

}




