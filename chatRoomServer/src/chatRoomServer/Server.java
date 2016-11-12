package chatRoomServer;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.LineBorder;

public class Server extends JFrame{
	private ServerSocket serverSocket;
	private JScrollPane scrollPane_clients;
	private JPanel panel_serverInfo;
	private JLabel label_serverIp;
	private JLabel label_serverPort;
	private JTextField text_serverIp;
	private JTextField text_serverPort;
	private JTextArea text_statement;
	private File file_record;
	private FileOutputStream fop_record;
	private String content_record;
	private byte[] bytes_record;
	
	public Server(){
		setLayout(new BorderLayout());
		label_serverIp = new JLabel("Server IP:");
		label_serverPort = new JLabel("Server Port:");
		text_serverIp = new JTextField();
		text_serverIp.setEditable(false);
		text_serverPort = new JTextField("2333");
		text_serverPort.setEditable(false);
		panel_serverInfo = new JPanel();
		panel_serverInfo.setLayout(new GridLayout(2,2));
		panel_serverInfo.add(label_serverIp);
		panel_serverInfo.add(text_serverIp);
		panel_serverInfo.add(label_serverPort);
		panel_serverInfo.add(text_serverPort);
		text_statement = new JTextArea("");		
		text_statement.setBorder(new LineBorder(new java.awt.Color(127,157,185), 1, false));
		text_statement.setEditable(false);
		text_statement.setLineWrap(true);
		scrollPane_clients = new JScrollPane(text_statement);
		scrollPane_clients.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane_clients.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		add(panel_serverInfo,BorderLayout.NORTH);
		add(scrollPane_clients, BorderLayout.CENTER);
		setSize(600,400);
		setVisible(true);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);   
		
		startServer();
	}

	public static void main(String[] args) {
		Server server = new Server();
	}
	public void startServer(){//开启服务器
		try{
			serverSocket = new ServerSocket(2333);
			text_serverIp.setText(serverSocket.getInetAddress().toString());
			file_record = new File("record.txt");
			fop_record = new FileOutputStream(file_record);
			while (true){//每次收到一个连接请求，就新开一个聊天界面
				Socket clientSocket = serverSocket.accept();
				clientDialog c = new clientDialog(clientSocket);
				text_statement.append(new Date()+" Connect from IP:"+clientSocket.getInetAddress().toString()+'\n');			
				//在record.txt中记录历史信息
				content_record = new Date()+" Connect from IP:"+clientSocket.getInetAddress().toString()+'\n';
				bytes_record = content_record.getBytes();
				fop_record.write(bytes_record);
			}	
		}
		catch(Exception e){
			e.printStackTrace();
		}
	}
	//聊天界面
	public class clientDialog extends JFrame {
		private Socket client;
		private BufferedReader fromClient;
		private PrintWriter toClient;
		private String clientName;
		private String clientIp;
		private File file_server;
		private FileOutputStream fop_server;
		private String content_server;
		private byte[] bytes_server;
		
		private JTextArea text_message;
		private JTextField text_input;
		private JButton send;
		private JPanel panel_input;
		private JPanel panel_message;
		private JScrollPane scrollPane;
		private JLabel label_clientName;
		private JLabel label_clientIp;
		private JTextField text_clientName;
		private JTextField text_clientIp;
		private JButton disconnect;
		private JPanel panel_clientInfo;
		private JPanel panel_control;
		
		public clientDialog(Socket clientSocket){
			setLayout(new BorderLayout());
			panel_message = new JPanel();
			panel_message.setLayout(new BorderLayout());
			panel_input = new JPanel();
			panel_input.setLayout(new BorderLayout());
			text_message = new JTextArea();
			text_message.setBorder(new LineBorder(new java.awt.Color(127,157,185), 1, false));
			text_message.setEditable(false);
			text_message.setLineWrap(true);
			scrollPane = new JScrollPane(text_message);
			scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
			scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
			text_input = new JTextField();
			send = new JButton("Send");
			send.addActionListener(new buttonAction());
			panel_input.add(text_input, BorderLayout.CENTER);
			panel_input.add(send, BorderLayout.EAST);
			panel_message.add(scrollPane,BorderLayout.CENTER);
			panel_message.add(panel_input, BorderLayout.SOUTH);
			label_clientName = new JLabel("Client Name:");
			label_clientIp = new JLabel("Client IP:");
			text_clientName = new JTextField();
			text_clientIp = new JTextField();
			panel_clientInfo = new JPanel();
			panel_clientInfo.setLayout(new GridLayout(2, 2));
			panel_clientInfo.add(label_clientIp);
			panel_clientInfo.add(text_clientIp);
			panel_clientInfo.add(label_clientName);
			panel_clientInfo.add(text_clientName);
			text_clientName.setEditable(false);
			text_clientIp.setEditable(false);
			disconnect = new JButton("Disconnect");
			disconnect.addActionListener(new buttonAction());
			panel_control = new JPanel();
			panel_control.setLayout(new BorderLayout());
			panel_control.add(panel_clientInfo,BorderLayout.CENTER);
			panel_control.add(disconnect, BorderLayout.EAST);
			add(panel_control,BorderLayout.NORTH);
			add(panel_message,BorderLayout.CENTER);
			setSize(600,400);
			setVisible(true);
			//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			setLocationRelativeTo(null);   
			
			try {
				client = clientSocket;
				fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
				toClient = new PrintWriter(client.getOutputStream());
				clientName = fromClient.readLine();
				clientIp = client.getInetAddress().toString();
				text_clientIp.setText(clientIp);
				text_clientName.setText(clientName);
				content_server = new Date()+" "+clientName+" conntects the server!\n";
				text_message.append(content_server);
				//记录通信内容
				java.text.DateFormat format2 = new java.text.SimpleDateFormat("yyyyMMddhhmmss");
				file_server = new File(format2.format(new Date())+".txt");
				fop_server = new FileOutputStream(file_server);
				bytes_server = content_server.getBytes();
				fop_server.write(bytes_server);
				//开启线程持续接受信息
				Thread dialog= new Thread(new clientThread());
				dialog.start();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		public class clientThread implements Runnable{//接受信息线程
			clientThread(){}
			public void run(){
				String message;			
				while (true){
					try{
						message = fromClient.readLine();
						if (message!=null){
							//存储记录
							content_server = message + '\n';
							bytes_server = content_server.getBytes();
							fop_server.write(bytes_server);
							//把接收到的消息显示到界面上
							text_message.append(message+'\n');
						}
						client.sendUrgentData(0xFF);//判断客户端是否断开连接
						if (client.isClosed())//判断本地是否断开连接
							throw new Exception();
					}
					catch(Exception e){
						content_record = new Date()+" Disconnect from IP:"+clientIp+'\n';
						content_server = new Date()+" Disconnect!\n";
						text_statement.append(content_record);
						text_message.append(content_server);
						try {
							//记录连接信息
							bytes_record = content_record.getBytes();
							fop_record.write(bytes_record);
							//存储通信记录
							bytes_server = content_server.getBytes();
							fop_server.write(bytes_server);
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						break;
					}	
				}		
			}
		}
		//监听器
		public class buttonAction implements ActionListener{
			public void actionPerformed(ActionEvent event){
				if (event.getActionCommand() == "Send"){
					try {
						content_server = new Date()+" Server: "+text_input.getText()+'\n';
						text_message.append(content_server);
						toClient.println(new Date()+" Server: "+text_input.getText());
						toClient.flush();
						text_input.setText("");						
						//存储通信记录
						bytes_server = content_server.getBytes();
						fop_server.write(bytes_server);
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null,"当前无连接，无法发送消息");
					}
				}
				if (event.getActionCommand() == "Disconnect"){
					try {
						client.close();	
					} catch (Exception e) {
						JOptionPane.showMessageDialog(null,"不存在可取消的连接");
					}
				}
			}
		}
		
	}
	
	

}
