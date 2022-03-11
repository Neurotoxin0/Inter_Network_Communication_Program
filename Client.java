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
                InetAddress address = InetAddress.getByName(raw[0]);
                int port = Integer.parseInt(raw[1]);

                ClientMenu(address, port, false);
            }
            catch (ArrayIndexOutOfBoundsException e) { JOptionPane.showMessageDialog(frame, "Invalid Server Detail"); }
            catch (UnknownHostException e) { JOptionPane.showMessageDialog(frame, "Unknown Host"); }
            catch (IOException e) { JOptionPane.showMessageDialog(frame, "IO Exception"); e.printStackTrace(); }
        });

        // Listener for UDP button
        UDPConnectButton.addActionListener(actionEvent ->
        {
            try
            {
                String [] raw = split_address_port(ServerDetail.getText());
                InetAddress address = InetAddress.getByName(raw[0]);
                int port = Integer.parseInt(raw[1]);

                ClientMenu(address, port, true);
            }
            catch (ArrayIndexOutOfBoundsException e) { JOptionPane.showMessageDialog(frame, "Invalid Server Detail"); }
            catch (UnknownHostException e) { JOptionPane.showMessageDialog(frame, "Unknown Host"); }
            catch (IOException e) { JOptionPane.showMessageDialog(frame, "IO Exception"); e.printStackTrace(); }
        });
    }

    private void ClientMenu(InetAddress address, int port, boolean udpMode) throws IOException, IllegalArgumentException // throw exceptions to upper layer for processing
    {
        // TCP panel
        JPanel ClientMenu = new JPanel();
        Layer.add(ClientMenu, "Client");

        JLabel Label = new JLabel("Message");
        Label.setFont(new Font(Font.SERIF, Font.PLAIN, 20));
        ClientMenu.add(Label);

        JTextField Message = new JTextField(15);
        ClientMenu.add(Message);

        JButton SubmitButton = new JButton("Submit");
        ClientMenu.add(SubmitButton);

		JTextArea textArea = new JTextArea();
        JScrollPane pane = new JScrollPane(textArea);
        pane.setPreferredSize(new Dimension(450, 200));
        ClientMenu.add(pane);

        Back(ClientMenu);  // add back to menu button

        ClientMenu.repaint();
        cl.show(Layer,"Client");


        SubmitButton.addActionListener(actionEvent ->
        {
            try
            {
				if(udpMode)
                {
					byte[] msg_in = new byte[16];
					byte[] msg_out;

					// Create client socket
					DatagramSocket socket = new DatagramSocket();
					socket.setSoTimeout(1000);

					// Create hello msg
					String msg = Message.getText() + "\n";
					msg_out = msg.getBytes();

					// Create out datagram & send hello msg
					DatagramPacket packet_out = new DatagramPacket(msg_out, msg_out.length, address, port);
					socket.send(packet_out);
                    textArea.append("--------------------------------------------------\n");
					textArea.append("Packet sent to: " + address + ":"  + port + "; with the message: " + msg + "\n");

					// Read from server
					DatagramPacket packet_in = new DatagramPacket(msg_in, msg_in.length);
					socket.receive(packet_in);
					msg = new String(packet_in.getData()).trim();

					// Close socket
					socket.close();
					textArea.append("Message Received: " + msg + "\n");
                    textArea.append("--------------------------------------------------\n");
				}
                else // TCP mode
                {
					// Open socket
					Socket socket = new Socket(address, port);
					socket.setSoTimeout(1000);
					
					// Create out stream & send msg
					BufferedWriter msg_out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					msg_out.write(Message.getText() + "\n");
					msg_out.flush();
                    textArea.append("--------------------------------------------------\n");
					textArea.append("Packet sent to: " + address + ":"  + port + "; with the message: " + Message.getText() + "\n");
					
					// Read from server
					BufferedReader msg_in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					String msg = msg_in.readLine();
                    textArea.append("Message Received: " + msg + "\n");
                    textArea.append("--------------------------------------------------\n");

					msg_in.close();
					socket.close();
				}
            }
            catch (ArrayIndexOutOfBoundsException e) { throw new ArrayIndexOutOfBoundsException(); }
            catch (IllegalArgumentException e) { JOptionPane.showMessageDialog(frame, "Illegal Port Number"); }
            catch (SocketException e) { JOptionPane.showMessageDialog(frame, "Server Unreachable"); }
            catch (SocketTimeoutException e) { JOptionPane.showMessageDialog(frame, "Connection Timeout");}

            catch (Exception e) // if socket has been reset -> retry close socket and back to Connect Layer
            {
                JOptionPane.showMessageDialog(frame, "Connection Failure");
                e.printStackTrace();
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
