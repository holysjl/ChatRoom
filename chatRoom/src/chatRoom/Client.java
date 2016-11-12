package chatRoom;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.Socket;
import java.util.Date;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.LineBorder;

public class Client extends JFrame{
	private File file;
	private FileOutputStream fop;
	private String content;
	private byte[] contentInBytes;
	private String ipAddress;
	private int port;
	private String name;
	private Socket server;
	private BufferedReader fromServer;
	private PrintWriter toServer;
	private InetAddress groupAddress;
	private MulticastSocket groupSocket;
	private String type ="no"; 
	private JPanel panel_user;
	private JPanel panel_userInfo;
	private JPanel panel_control;
	private JPanel panel_input;
	private JPanel panel_message;
	private JPanel panel_label;
	private JLabel label_ip;
	private JLabel label_port;
	private JLabel label_name;
	private JTextField text_ip;
	private JTextField text_port;
	private JTextField text_name;
	private JTextArea text_message;
	private JTextField text_input;
	private JScrollPane scrollPane;
	private JButton send;
	private JButton singleChat;
	private JButton groupChat;
	private JButton exitSingle;
	private JButton exitGroup;
	
	public Client(){
		setLayout(new BorderLayout());
		//������Ϣ��
		label_ip = new JLabel("IP:");
		label_ip.setHorizontalAlignment(SwingConstants.RIGHT);
		label_port = new JLabel("PORT:");
		label_name = new JLabel("NAME:");
		text_ip = new JTextField();
		text_port = new JTextField();
		text_name = new JTextField();
		text_input = new JTextField();
		panel_userInfo = new JPanel();
		panel_userInfo.setLayout(new GridLayout(3, 1));
		panel_userInfo.add(text_ip);
		panel_userInfo.add(text_port);
		panel_userInfo.add(text_name);
		panel_label = new JPanel();
		panel_label.setLayout(new GridLayout(3, 1));
		panel_label.add(label_ip);
		panel_label.add(label_port);
		panel_label.add(label_name);
		//���ư�ť��
		singleChat = new JButton("Single Chat");
		exitSingle = new JButton("Exit Single Chat");
		groupChat = new JButton("Group Chat");
		exitGroup = new JButton("Exit Group Chat");
		singleChat.addActionListener(new buttonAction());
		exitSingle.addActionListener(new buttonAction());
		groupChat.addActionListener(new buttonAction());
		exitGroup.addActionListener(new buttonAction());
		panel_control = new JPanel();
		panel_control.setLayout(new GridLayout(4, 1));
		panel_control.add(singleChat);
		panel_control.add(exitSingle);
		panel_control.add(groupChat);
		panel_control.add(exitGroup);
		panel_user = new JPanel();
		panel_user.setLayout(new BorderLayout());
		panel_user.add(panel_label,BorderLayout.WEST);
		panel_user.add(panel_userInfo,BorderLayout.CENTER);
		panel_user.add(panel_control, BorderLayout.EAST);
		text_message = new JTextArea();
		text_message.setBorder(new LineBorder(new java.awt.Color(127,157,185), 1, false));
		text_message.setEditable(false);
		text_message.setLineWrap(true);
		scrollPane = new JScrollPane(text_message);
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		send = new JButton("Send");
		send.addActionListener(new buttonAction());
		//������
		panel_input = new JPanel();
		panel_input.setLayout(new BorderLayout());
		panel_input.add(text_input,BorderLayout.CENTER);
		panel_input.add(send, BorderLayout.EAST);
		panel_message = new JPanel();
		panel_message.setLayout(new BorderLayout());
		panel_message.add(scrollPane,BorderLayout.CENTER);
		panel_message.add(panel_input, BorderLayout.SOUTH);
		add(panel_user,BorderLayout.NORTH);
		add(panel_message,BorderLayout.CENTER);
		setSize(600,400);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);    
	}
	public static void main(String[] args) {
		Client c = new Client();
	}
	//������
	public class buttonAction implements ActionListener{
		public void actionPerformed(ActionEvent event){
			if (event.getActionCommand() == "Single Chat"){
				try {
					if (type.equals("group")){
						JOptionPane.showMessageDialog(null,"����Ⱥ������");
						return;
					}
					if (type.equals("single")){
						JOptionPane.showMessageDialog(null,"���е�����������");
						return;
					}
					ipAddress = text_ip.getText();
					port = Integer.parseInt(text_port.getText());
					name = text_name.getText();
					server = new Socket(ipAddress,port);
					fromServer = new BufferedReader(new InputStreamReader(server.getInputStream()));
					toServer = new PrintWriter(server.getOutputStream());
					toServer.println(name);
					toServer.flush();
					type="single";
					String tmp = "Succeed to connect!\n";
					text_message.append(tmp);
					//�����¼�浵
					java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
					file = new File(format2.format(new Date())+".txt");
					fop = new FileOutputStream(file);
					fop.write(tmp.getBytes());
					//������Ϣ��ȡ�߳�
					Thread dialog = new Thread(new readMessage_single());
					dialog.start();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,"����ʧ��");
				}
			}
			if (event.getActionCommand() == "Exit Single Chat"){
				try {
					if (server.isClosed()) throw new Exception();
					server.close();	
					type = "no";
				
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,"�����ڿ��˳��ĵ�������");
				}
			}
			if (event.getActionCommand()=="Group Chat"){
				try {
					if (type.equals("group")){
						JOptionPane.showMessageDialog(null,"����Ⱥ������");
						return;
					}
					if (type.equals("single")){
						JOptionPane.showMessageDialog(null,"���е�����������");
						return;
					}
					ipAddress = text_ip.getText();
					port = Integer.parseInt(text_port.getText());
					name = text_name.getText();
					groupAddress = InetAddress.getByName("224.0.0.4");
					groupSocket = new MulticastSocket(port);
					groupSocket.joinGroup(groupAddress);
					type = "group";
					//�������û��㲥�����û��ļ���
					String tmp = new Date()+" "+text_name.getText()+" has joined the group!\n";
					byte[] buffer = tmp.getBytes();
					DatagramPacket dPacket = new DatagramPacket(buffer, buffer.length,groupAddress,port);
					groupSocket.send(dPacket);
					//�����¼�浵
					java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
					file = new File(format2.format(new Date())+".txt");
					fop = new FileOutputStream(file);
					contentInBytes = tmp.getBytes();
					fop.write(contentInBytes);
					//����������Ϣ�߳�
					Thread dialog = new Thread(new readMessage_group());
					dialog.start();
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,"����ʧ��");
				}
				
			}
			if (event.getActionCommand()=="Exit Group Chat"){
				try {
					groupSocket.leaveGroup(groupAddress);
					groupSocket.close();
					type = "no";
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,"�����ڿ��˳���Ⱥ��");
				}
			}
			
			if (event.getActionCommand() == "Send"){
				try {
					if (type.equals("single")){
						//�洢��¼
						content = new Date()+" "+text_name.getText()+": "+text_input.getText()+'\n';
						contentInBytes = content.getBytes();
						fop.write(contentInBytes);
						//������Ϣ
						toServer.println(new Date()+" "+text_name.getText()+": "+text_input.getText());
						toServer.flush();	
						text_message.append(content);
						text_input.setText("");
					}
					if (type.equals("group")){
						String tmp = new Date()+" "+text_name.getText()+":"+text_input.getText()+'\n';
						byte[] buffer = tmp.getBytes();
						DatagramPacket dPacket = new DatagramPacket(buffer, buffer.length,groupAddress,port);
						groupSocket.send(dPacket);
						text_input.setText("");
						//�洢��¼
						fop.write(buffer);
					}
					if (type.equals("no"))
						throw new Exception();
					
				} catch (Exception e) {
					JOptionPane.showMessageDialog(null,"��ǰ�����ӣ��޷�������Ϣ");
				}
			}	
		}
	}
	//���������ȡ��Ϣ�߳�
	public class readMessage_single implements Runnable{
		public void run(){
			String message;
			
			while (true){
				try {
					message = fromServer.readLine();	
					if (message != null){
						//�洢��¼
						content = message + '\n';
						contentInBytes = content.getBytes();
						fop.write(contentInBytes);
						//�ѽ��յ�����Ϣ��ʾ��������
						text_message.append(content);
					}
					server.sendUrgentData(0xFF);//�жϷ������Ƿ�ر�����
					if (server.isClosed())//�жϱ����Ƿ�Ͽ�����
						throw new Exception();
				}
				catch (Exception e) {
					String tmp = new Date()+" Disconnect!\n";
					text_message.append(tmp);
					try {
						contentInBytes = tmp.getBytes();
						fop.write(contentInBytes);
						fop.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					break;
				}
			} 
		}
	}
	//�鲥�����ȡ��Ϣ�߳�
	public class readMessage_group implements Runnable {
		public void run() {
			String message;
			byte[] buffer = new byte[81920];
			while(true){
				try{
					DatagramPacket dPacket = new DatagramPacket(buffer, buffer.length);
					groupSocket.receive(dPacket);
					message = new String(dPacket.getData(), 0 ,dPacket.getLength());
					text_message.append(message);	
					//�洢��¼
					fop.write(buffer);
				}
				catch(Exception e){
					String tmp = new Date()+" Disconnect!\n";
					text_message.append(tmp);
					try {
						contentInBytes = tmp.getBytes();
						fop.write(contentInBytes);
						fop.close();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					break;
				}
			}
		}
	}
}
