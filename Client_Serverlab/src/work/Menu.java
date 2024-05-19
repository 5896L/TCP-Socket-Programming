package work;

//登录界面类
//bug：注册账号，修改密码后需要重新登陆才能登录，因为用户集合未刷新

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.io.*;
import java.net.*;
import javax.swing.JFrame;
import javax.swing.WindowConstants;

public class Menu extends JFrame {

    public void setDefaultCloseOperation(int operation) {
        // TODO Auto-generated method stub
        super.setDefaultCloseOperation(operation);
    }

    Timer timer;
    Link link;

    public Menu(Link link) throws Exception {
        this.link = link;
        // 先放置控件
        setComponent();
        // 放入事件监听函数
        setActionListen();
        // 最后布置页面（先布置页面会覆盖控件布置）
        MenuFrame();

        timer = new Timer(0, E -> {
            StringBuffer sBuffer = new StringBuffer();
            // 时分秒
            Calendar cal = Calendar.getInstance();
            int year = cal.get(Calendar.YEAR);
            // 这样获取的月份是从0开始的
            int month = cal.get(Calendar.MONTH) + 1;
            int day = cal.get(Calendar.DAY_OF_MONTH);
            int hour = cal.get(Calendar.HOUR_OF_DAY);
            int minute = cal.get(Calendar.MINUTE);
            int second = cal.get(Calendar.SECOND);

            sBuffer.append(String.format("%04d", year)).append("年").append(month).append("月").append(day).append("日 ")
                    .append(String.format("%02d", hour)).append(":").append(String.format("%02d", minute)).append(":")
                    .append(String.format("%02d", second));
            timeLabel.setText(sBuffer.toString());
        });

        timer.start();
    }

    // 页面属性设置
    public void MenuFrame() {
        setDefaultCloseOperation(1);
        ImageIcon bg = new ImageIcon("src//work//LOGINBACK.jpg");
        Image image = bg.getImage();
        Image newiImage = image.getScaledInstance(600, 400, DO_NOTHING_ON_CLOSE);
        bg.setImage(newiImage);
        JLabel label = new JLabel(bg);
        label.setSize(600, 400);
        getLayeredPane().add(label, new Integer(Integer.MIN_VALUE));

        // 设置窗口的参数
        setBounds(500, 300, 600, 400);

        // 2.把窗口面板设为内容面板并设为透明、流动布局。
        JPanel pan = (JPanel) getContentPane();
        pan.setOpaque(false);

        // 禁用重新调整窗口大小的功能
        setResizable(false);
        // 关闭窗口的右上角的叉的同时关闭程序
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        // 设置窗口上当前程序的名字
        setTitle("ATM--功能界面");
        // 清除默认的布局管理器
        getContentPane().setLayout(null);
        // 设置窗口显示
        setVisible(true);
    }

    // 定义控件
    JButton balanceButton, withdrawlButton, byeButton;
    JLabel idLabel, pwdLabel, titleLabel, timeLabel;

    // 控件集合
    ArrayList<JComponent> listComponent = new ArrayList<>();

    // 页面控件布置
    public void setComponent() {

        titleLabel = new JLabel("请选择服务!");
        timeLabel = new JLabel();

        // 设置坐标与宽高
        titleLabel.setBounds(210, 50, 200, 60);
        timeLabel.setBounds(5, 320, 305, 40);

        // 存入集合
        listComponent.add(timeLabel);
        // titleLabel不存入集合单独设置
        titleLabel.setFont(new Font("黑体", Font.BOLD, 28));

        getContentPane().add(titleLabel);
        getContentPane().add(timeLabel);

        // 按钮
        balanceButton = new JButton("查询余额");
        withdrawlButton = new JButton("用户取款");
        byeButton = new JButton("用户退出");
        // 设置大小
        balanceButton.setBounds(100, 120, 120, 70);
        withdrawlButton.setBounds(100, 220, 120, 70);
        byeButton.setBounds(360, 120, 120, 70);

        // 存入集合
        listComponent.add(balanceButton);
        listComponent.add(withdrawlButton);
        listComponent.add(byeButton);

        // 放置于页面
        getContentPane().add(balanceButton);
        getContentPane().add(withdrawlButton);
        getContentPane().add(byeButton);

        // 整体设置字体
        for (JComponent jComponent : listComponent) {
            jComponent.setFont(new Font("宋体", Font.BOLD, 20));
        }
    }

    // 事件监听
    public void setActionListen() {

        this.getRootPane().setDefaultButton(balanceButton);

        // 登录按钮监听
        balanceButton.addActionListener(e -> {
            try {
                String s = link.balance_q();
                new Balance(s);
            } catch (Exception E) {
                E.printStackTrace();
            }
        });

        withdrawlButton.addActionListener(e -> {
            try {
                new Withdrawl(link);
            } catch (Exception E) {
                E.printStackTrace();
            }
        });

        byeButton.addActionListener(e -> {
            try {
                link.bye();
            } catch (Exception E) {
                E.printStackTrace();
            }
        });
    }
}
