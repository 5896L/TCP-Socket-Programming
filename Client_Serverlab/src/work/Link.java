package work;

import java.io.*;
import java.net.*;

public class Link {
    String sentence;
    String modifiedSentence;//
    Socket clienSocket;
    // BufferedReader input;
    DataOutputStream out;

    BufferedReader input;

    // 10.242.228.142

    public Link() throws Exception {
        clienSocket = new Socket("192.168.0.178", 2525);// 创建一个套接字 clienSocket，并指定连接的服务器的 IP 地址和端口号
        out = new DataOutputStream(clienSocket.getOutputStream());
        // 向服务器端发送数据
        input = new BufferedReader(new InputStreamReader(clienSocket.getInputStream()));
    }

    public String send_receve(String s) throws Exception {
        System.out.println(s);
        out.writeBytes(s + "\n");
        modifiedSentence = String.valueOf(input.readLine());
        System.out.println(modifiedSentence);
        return modifiedSentence;
    }

    public int mylink(String id, String pwd) throws Exception {

        sentence = "HELO " + id;// 用于存储从用户输入读取的数据
        modifiedSentence = send_receve(sentence);
        String temp = modifiedSentence.substring(0, 3);
        // 从服务器读取一行数据，并将其存储在 modifiedSentence 变量中。
        if (temp.equals("500")) {
            sentence = "PASS " + pwd;
            modifiedSentence = send_receve(sentence);
            temp = modifiedSentence.substring(0, 3);
            if (temp.equals("525")) {// !!!!!!!!!!!!更改为对的
                return 1;
            } else {
                return 5;// 密码有误
                // JOptionPane.showMessageDialog(getContentPane(), "密码有误!");
            }
        } else {
            return 4;
            // JOptionPane.showMessageDialog(getContentPane(), "账号有误!");
        }
    }

    String balance_q() throws Exception {

        sentence = "BALA";// 用于存储从用户输入读取的数据
        modifiedSentence = send_receve(sentence);
        String s = modifiedSentence.substring(0, 4);
        if (s.equals("AMNT")) {
            return modifiedSentence.substring(5, modifiedSentence.length()) + "元";
        } else {
            return "Error";
        }
        // JOptionPane.showMessageDialog(getContentPane(), "账号有误!");
    }

    int withdrwal(String money) throws Exception {
        sentence = "WDRA " + money;// 用于存储从用户输入读取的数据

        modifiedSentence = send_receve(sentence);

        String s = modifiedSentence.substring(0, 3);
        if (s.equals("525")) {
            return 1;// 取款成功
        } else if (modifiedSentence.equals("401 ERROR!")) {
            return 2;// 取款失败 余额不足
        } else {
            return 3;// 未知错误
        }
    }

    void bye() throws Exception {
        sentence = "BYE";
        modifiedSentence = send_receve(sentence);
        if (modifiedSentence.equals("BYE")) {
            clienSocket.close();
            new Bye();
        }
    }
}
