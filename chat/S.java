package test;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

class S extends JFrame implements ActionListener,Serializable{
	static JTextArea ta = new JTextArea();
	static ArrayList<String> Slist =new ArrayList<>();
	
	JMenuBar mb = new JMenuBar();
	JMenu mm = new JMenu("Main");
	JMenuItem ex = new JMenuItem("サーバー終了");
	JMenuItem lo = new JMenuItem("load");
	JMenuItem sa = new JMenuItem("save");
	
	S(){
		setTitle("(さーばー)");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JScrollPane sp = new JScrollPane(ta);
		ta.setEditable(false);
		
		lo.addActionListener(this);
		sa.addActionListener(this);
		ex.addActionListener(this);
		mm.add(lo);
		mm.add(sa);
		mm.add(ex);
		mb.add(mm);
		
		setJMenuBar(mb);
		
		add(sp);
		setSize(500,600);
		setVisible(true);
	}
	
	public static void main(String[] args) {
		new S();
		try {
			ServerSocket ss = new ServerSocket(5000);
			while(true) {
				Socket soket = ss.accept();
				Thread ab = new Thread(new room(soket));
				ab.start();
			}
		}
		catch(IOException e){
			System.out.println(e);
		}
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO 自動生成されたメソッド・スタブ
		if(e.getSource() == ex) {
//exを押したらサーバー終了
			System.exit(0);
		}else if(e.getSource()==lo) {
			load();
		}else if(e.getSource()==sa) {
			save();
		}
		
	}
	void save() {
//リストを保存する
		try {
			File fl = new File("C:/java/Slist.ser");
				FileOutputStream fs = new FileOutputStream(fl);
				ObjectOutputStream os = new ObjectOutputStream(fs);
				os.writeObject(Slist);
				os.close();
				ta.append("ここまでセーブしました\r\n");
		
		}catch(IOException e) {
			System.out.println(e);
		}
	}

	@SuppressWarnings("unchecked")
	void load() {
//セーブしていたリストを読み込む
		try {
			FileInputStream fs = new FileInputStream("C:/java/Slist.ser");
			ObjectInputStream os = new ObjectInputStream(fs);
			Slist = (ArrayList<String>)os.readObject();
			os.close();
			display();
		}catch(IOException e) {
			System.out.println(e);
		}catch(ClassNotFoundException e) {
			System.out.println(e);
		}
	}
	void display() {
//リストを表示させる
		for(int i=0;(Slist.size())>i;i++) {	
			ta.append(Slist.get(i)+"\r\n"+"\r\n");
		}
	}
}

//各接続先との処理
class room implements Runnable{
	PrintWriter wt;
	BufferedReader rd;
	Socket sk;
	static int co;
	int ListFirstSize;
	
	room(Socket ss){
		co++;
		this.sk=ss;

		try {
			wt = new PrintWriter(sk.getOutputStream());
			rd = new BufferedReader(new InputStreamReader(sk.getInputStream()));
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	void send() {
//サーバーのリストの長さと相手のリストの長さを比べて、更新された分を相手に追加する
		for(int i =ListFirstSize;S.Slist.size()>i;i++) {
			wt.println(S.Slist.get(i));
			wt.flush();
		}

	}
	void read() {
		 try {
			int i=0;
			String mes;
			while((mes=rd.readLine()) != null&&sk.isConnected()==true) {
				if(i==0) {
					//1列目は相手のリストの長さが返ってくる、送るときに使う
					ListFirstSize = Integer.parseInt(mes);
					i=1;
				}else {
					//2行目はリストに入れる
					if(mes.equals("")) {
						
					}else {
						S.ta.append(gettime()+"\r\n"+mes+"\r\n"+"\r\n");
						S.Slist.add(gettime()+"\r\n"+mes);
					}
					send();
					i=0;
				}
			}
			
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
	String gettime() {
//今日の日付を返す
		Date today = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy'/'MM'/'dd'/'hh':'mm'.'ss");
		
		return sdf.format(today);
	}
	
	public void run(){
		
		read();
		while(sk.isConnected()!=true) {
			
		}
		try {
			rd.close();
			wt.close();
			sk.close();
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
		}
	}
	
}
