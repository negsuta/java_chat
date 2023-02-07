package test;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class C {
	public static void main(String[] args) {
		try {
			Socket sk = new Socket("localhost",5000);
//			BufferedReader rd = new BufferedReader(new InputStreamReader(sk.getInputStream()));
			
			Thread th = new Thread(new UFver1(sk));
			th.start();
		}
		catch(IOException e){
			System.out.println("サーバーと接続できませんでした");
			System.out.println(e);
			new UFver1();
		}
	}

}

//test8のコピー
class UFver1 extends JFrame implements ActionListener,Runnable{
	JTextField ta = new JTextField(20);
	JTextArea ta1 = new JTextArea();

	JButton bt = new JButton("送信");
	ImageIcon icon = new ImageIcon("C:/java/image/リロード.png");
	JButton bt1 = new JButton(icon);
	
	JButton bt3 = new JButton("送信");
	
	ArrayList<String> list = new ArrayList<>();
	
	PrintWriter wt;
	BufferedReader rd;
	Socket sk;
	
	UFver1(){
		
		setTitle("(オフライン用)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane sp = new JScrollPane(ta1);
		ta1.setEditable(false);
		getContentPane().add(sp);
		
		JPanel a = new JPanel();
		a.setLayout(new FlowLayout());
		
		bt1.setSize(2,2);
		
		a.add(ta);
		a.add(bt3);
		a.add(bt1);
		bt3.addActionListener(this);
//		bt1.addActionListener(this);
		
		getContentPane().add(BorderLayout.SOUTH,a);
		
		setSize(550,400);
		setVisible(true);

	}
	
	UFver1(Socket ss){
		sk=ss;
		try {
			rd = new BufferedReader(new InputStreamReader(sk.getInputStream()));
			wt = new PrintWriter(sk.getOutputStream());
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
		setTitle("(クライアント)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JScrollPane sp = new JScrollPane(ta1);
		ta1.setEditable(false);
		getContentPane().add(sp);
		
		JPanel a = new JPanel();
		a.setLayout(new FlowLayout());
		
		bt1.setSize(2,2);
		
		a.add(ta);
		a.add(bt);
		a.add(bt1);
		bt.addActionListener(this);
		bt1.addActionListener(this);
		
		getContentPane().add(BorderLayout.SOUTH,a);
		
		setSize(550,400);
		setVisible(true);

	}
	void read() {
		String mes;
		int i=0;
		try {
			while((mes=rd.readLine()) != null&&sk.isConnected()==true) {
				if(i==0) {
					list.add(mes);
					ta1.append(list.get(list.size()-1)+"\r\n");
					i++;
				}else {
					list.add(mes);
					ta1.append(list.get(list.size()-1)+"\r\n"+"\r\n");
					i=0;
				}
				
			}

		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			System.out.println("サーバーとの接続が切れました");
		}
		
	}
	
	void send() {
		wt.println(list.size()/2);
		wt.println(ta.getText());
		ta.setText("");
		wt.flush();
	}
	
	void textprint() {
		ta1.append(gettime()+"\r\n"+ta.getText()+"\r\n");
		ta.setText("");
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		if(e.getSource()==bt) {
			send();
		}else if(e.getSource()==bt1) {
			wt.println(list.size()/2);
			wt.println("");
			wt.flush();
		}else if(e.getSource()==bt3) {
			textprint();
		}
	}

	String gettime() {
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy'/'MM'/'dd'/'hh':'mm'.'ss");
		
		return sdf.format(today);
	}
	@Override
	public void run() {
		// TODO 自動生成されたメソッド・スタブ
			send();
			read();
	}
}