/**
 Group 8
 // Yang Xu 500890631
 // Ruoling Yu 500976267
 // Xinyu Ma 500943173
 // Vince De Vera 500550779
 // Raynor Elgie 500964140
 **/

import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;


public class Server
{
    private static final JFrame frame = new JFrame("Time Server");;
    private static final JPanel Layer = new JPanel(new CardLayout());
    private final CardLayout cl=(CardLayout)(Layer.getLayout());

    public static void main(String[] args)
    {
        Server server = new Server();
        server.frame.setSize(800,300);
        server.frame.add(Layer);
        server.frame.setVisible(true);
        server.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private Server() { Start_Server(); }

    private void Start_Server()
    {
        JPanel Server = new JPanel();
        Layer.add(Server, "Server");

        JLabel Label = new JLabel("Time Server Service Port");
        Label.setFont(new Font(Font.SERIF, Font.BOLD, 24));
        Server.add(Label);

        JTextField ServerDetail = new JTextField(5);
        Server.add(ServerDetail);

        JButton TCPServerButton = new JButton("TCP Server");
        JButton UDPServerButton = new JButton("UDP Server");
        Server.add(TCPServerButton);
        Server.add(UDPServerButton);

        cl.show(Layer,"Server");

        // Listener for TCP button
        TCPServerButton.addActionListener(actionEvent ->
        {
            try
            {
                int port = Integer.parseInt(ServerDetail.getText());
                TCP(port);
            }
            catch (IllegalArgumentException e) { JOptionPane.showMessageDialog(frame, "Illegal Port Number"); }
            catch (IOException e)   // nearly impossible: failed to launch server(start socket)
            {
                JOptionPane.showMessageDialog(frame, "IO Exception");
                e.printStackTrace();
            }
        });

        // Listener for UDP button
        UDPServerButton.addActionListener(actionEvent ->
        {
            try
            {
                int port = Integer.parseInt(ServerDetail.getText());
                UDP(port);
            }
            catch (IllegalArgumentException e) { JOptionPane.showMessageDialog(frame, "Illegal Port Number"); }
            catch (IOException e)
            {
                JOptionPane.showMessageDialog(frame, "IO Exception");
                e.printStackTrace();
            }
        });
    }

    private void TCP(int port) throws IOException, IllegalArgumentException // throw exceptions to upper layer for processing
    {
        // Open socket
        ServerSocket socket = new ServerSocket(port);
        JOptionPane.showMessageDialog(frame, "TCP Server initialized; Listen on port: " + port + "\n");

        // TCP panel
        JPanel TCP = new JPanel();
        Layer.add(TCP, "TCP");

        JLabel Label = new JLabel("127.0.0.1:" + port);
        Label.setFont(new Font(Font.SERIF, Font.BOLD, 24));
        TCP.add(Label);

        JTextArea textArea = new JTextArea();
        //textArea.setBounds();  
        JScrollPane pane = new JScrollPane(textArea);
        pane.setPreferredSize(new Dimension(450, 110));
        TCP.add(pane);
        
        cl.show(Layer,"TCP");

        TCP.repaint();
        

        new Thread() {
            public void run() {
              try {
                while (true) {
                    Socket connection = socket.accept();
                    SocketAddress client_address = connection.getRemoteSocketAddress();
                    textArea.append("Client connected: " + client_address + "\n");
                    BufferedReader msg_in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

                    String time = get_time(msg_in.readLine());  // get time/date/error msg
                    // Send msg
                    DataOutputStream msg_out = new DataOutputStream(connection.getOutputStream());
                    msg_out.writeBytes(time + "\n");
                    textArea.append("Message Sent: " + time + "\n");
                }
              } catch(IOException e) {
                e.printStackTrace();
              }
            }
        }.start();

    }

    private void UDP(int port) throws IOException, IllegalArgumentException
    {
        byte[] msg_in = new byte[16];
        byte[] msg_out;

        // Open socket
        DatagramSocket socket = new DatagramSocket(port);
        JOptionPane.showMessageDialog(frame,"UDP Server initialized; Listen on port: " + port + "\n");

        // UDP panel
        JPanel UDP = new JPanel();
        Layer.add(UDP, "UDP");

        JLabel Label = new JLabel("127.0.0.1:" + port);
        Label.setFont(new Font(Font.SERIF, Font.BOLD, 24));
        UDP.add(Label);

        cl.show(Layer,"UDP");

        while(true)
        {
            // Create incoming datagram & await receiving
            DatagramPacket datagram = new DatagramPacket(msg_in, msg_in.length);
            socket.receive(datagram);

            // Output client info & received message
            InetAddress client_address = datagram.getAddress();
            int client_port = datagram.getPort();
            String client_msg = new String(datagram.getData()).trim();
            JOptionPane.showMessageDialog(frame,"Client Connected - " + client_address + ":" + client_port + "\nReceived message: " + client_msg);

            // Get time & process msg_out
            String time = get_time(client_msg);
            msg_out = (time + "\n").getBytes();

            // Create out datagram & send msg
            DatagramPacket packet_out = new DatagramPacket(msg_out, msg_out.length, client_address, client_port);
            socket.send(packet_out);
            JOptionPane.showMessageDialog(frame,"Message sent: " + time);
        }
    }

    private String get_time(String format)
    {
        Calendar calendar = Calendar.getInstance();

        if (format.equalsIgnoreCase("time"))
        {
            String time = calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE) + ":" + calendar.get(Calendar.SECOND);
            return time;
        }
        else if (format.equalsIgnoreCase("date"))
        {
            String date = calendar.get(Calendar.MONTH)+1 + "/" + calendar.get(Calendar.DAY_OF_MONTH) + "/" + calendar.get(Calendar.YEAR);
            return date;
        }
        else { return "Invalid Format"; }
    }
}
