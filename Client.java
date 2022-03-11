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
import javax.swing.*;


public class Client
{
    private static final JFrame frame = new JFrame("Time Client");
    private static final JPanel Layer = new JPanel(new CardLayout());
    private final CardLayout cl=(CardLayout)(Layer.getLayout());

    public static void main(String[] args)
    {
        Client client = new Client();
        client.frame.setSize(800,300);
        client.frame.add(Layer);
        client.frame.setVisible(true);
        client.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    private Client() { Connect(); }

    private void Connect()
    {
        JPanel Connect = new JPanel();
        Layer.add(Connect, "Connect");

        JLabel Label = new JLabel("Time Server Detail");
        Label.setFont(new Font(Font.SERIF, Font.BOLD, 24));
        Connect.add(Label);

        JTextField ServerDetail = new JTextField(30);
        Connect.add(ServerDetail);

        JButton TCPConnectButton = new JButton("TCP Connect");
        JButton UDPConnectButton = new JButton("UDP Connect");
        Connect.add(TCPConnectButton);
        Connect.add(UDPConnectButton);

        cl.show(Layer,"Connect");

        // Listener for TCP button
        TCPConnectButton.addActionListener(actionEvent ->
        {
            try
            {
                String [] raw = split_address_port(ServerDetail.getText());
                String address = raw[0];
                int port = Integer.parseInt(raw[1]);

                TCP(address, port);
            }
            catch (ArrayIndexOutOfBoundsException e) { JOptionPane.showMessageDialog(frame, "Invalid Server Detail"); }
            catch (IllegalArgumentException e) { JOptionPane.showMessageDialog(frame, "Illegal Port Number"); }
            catch (IOException e) { JOptionPane.showMessageDialog(frame, "Connection Failed"); }
        });

        // Listener for UDP button
        UDPConnectButton.addActionListener(actionEvent ->
        {
            try
            {
                String [] raw = split_address_port(ServerDetail.getText());
                InetAddress address = InetAddress.getByName(raw[0]);
                int port = Integer.parseInt(raw[1]);

                UDP(address, port);
            }
            catch (ArrayIndexOutOfBoundsException e) { JOptionPane.showMessageDialog(frame, "Invalid Server Detail"); }
            catch (IllegalArgumentException e) { JOptionPane.showMessageDialog(frame, "Illegal Port Number"); }
            catch (IOException e) { JOptionPane.showMessageDialog(frame, "Connection Failed"); }
        });
    }

    private void TCP(String address, int port) throws IOException, IllegalArgumentException // throw exceptions to upper layer for processing
    {
        // TCP panel
        JPanel TCP = new JPanel();
        Layer.add(TCP, "TCP");

        JLabel Label = new JLabel("Message");
        Label.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        TCP.add(Label);

        JTextField Message = new JTextField(15);
        TCP.add(Message);

        JButton SubmitButton = new JButton("Submit");
        TCP.add(SubmitButton);

		JTextArea textArea = new JTextArea();
        //textArea.setBounds();  
        JScrollPane pane = new JScrollPane(textArea);
        pane.setPreferredSize(new Dimension(450, 110));
        TCP.add(pane);

        TCP.repaint();

        Back(TCP);  // add back to menu button
        cl.show(Layer,"TCP");

        SubmitButton.addActionListener(actionEvent ->
        {
            Socket socket = null;

            try
            {
                // Open socket
                socket = new Socket(address, port);
                
				
                // Create out stream & send msg
                BufferedWriter msg_out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
                msg_out.write(Message.getText() + "\n");
                msg_out.flush();
                textArea.append("\n--------------------------------------------------\n");
				textArea.append("Packet sent to: " + address + ":"  + port + "; with the message: " + Message.getText() + "\n");
				
                // Read from server
                BufferedReader msg_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                String msg = msg_in.readLine();
				
				textArea.append("Recieved: " + msg + "\n");
                textArea.append("\n--------------------------------------------------\n");

                msg_in.close();
                socket.close();
                
            }
            catch (IllegalArgumentException e) { JOptionPane.showMessageDialog(frame, "Illegal Port Number"); }
            catch (Exception e) // if socket has been reset -> retry close socket and back to Connect Layer
            {
                JOptionPane.showMessageDialog(frame, "Unexpected Socket Failure, Back to Connect");
                try { socket.close(); } catch (IOException ignored) {} // the line is to make sure the socket is closed; if is closed, exception will be caught -> ignored
                cl.show(Layer,"Connect");
            }
        });
    }

    private void UDP(InetAddress address, int port) throws IOException, IllegalArgumentException
    {
        // UDP panel
        JPanel UDP = new JPanel();
        Layer.add(UDP, "UDP");

        JLabel Label = new JLabel("Message");
        Label.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        UDP.add(Label);

        JTextField Message = new JTextField(15);
        UDP.add(Message);

        JButton SubmitButton = new JButton("Submit");
        UDP.add(SubmitButton);

		JTextArea textArea = new JTextArea();
        //textArea.setBounds();  
        JScrollPane pane = new JScrollPane(textArea);
        pane.setPreferredSize(new Dimension(450, 110));
        UDP.add(pane);

        UDP.repaint();

        Back(UDP);
        cl.show(Layer,"UDP");

        SubmitButton.addActionListener(actionEvent ->
        {
            try
            {
                byte[] msg_in = new byte[16];
                byte[] msg_out;

                // Create client socket
                DatagramSocket socket = new DatagramSocket();

                // Create hello msg
                String msg = Message.getText() + "\n";
                msg_out = msg.getBytes();

                // Create out datagram & send hello msg
                DatagramPacket packet_out = new DatagramPacket(msg_out, msg_out.length, address, port);
                socket.send(packet_out);
                textArea.append("\n--------------------------------------------------\n");
				textArea.append("Packet sent to: " + address + ":"  + port + "; with the message: " + msg + "\n");

                // Read from server
                DatagramPacket packet_in = new DatagramPacket(msg_in, msg_in.length);
                socket.receive(packet_in);
                msg = new String(packet_in.getData()).trim();

                // Close socket
                socket.close();
				textArea.append("Recieved: " + msg + "\n");
                textArea.append("\n--------------------------------------------------\n");
                //JOptionPane.showMessageDialog(frame, msg);
            }
            catch (IllegalArgumentException e) { JOptionPane.showMessageDialog(frame, "Illegal Port Number"); }
            catch (Exception e)
            {
                JOptionPane.showMessageDialog(frame, "Unknown Error, Back to Connect");
                cl.show(Layer,"Connect");
            }

        });
    }

    private String[] split_address_port(String detail) { return detail.split(":"); }

    // Add "Back to Menu" Button
    private void Back(JPanel Panel)
    {
        JButton Back = new JButton("Back");
        Back.setFont(new Font(Font.SERIF, Font.BOLD, 18));
        Panel.add(Back);

        Back.addActionListener(actionEvent -> { cl.show(Layer,"Connect"); });
    }
}
