package work;

//登录界面类
//bug：注册账号，修改密码后需要重新登陆才能登录，因为用户集合未刷新

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;

public class Balance extends JFrame {

    Timer timer;
    String balance;

    public Balance(String balance) throws Exception {
        this.balance = balance;
        // 先放置控件
        setComponent();
        // 最后布置页面（先布置页面会覆盖控件布置）
        BalanceFrame();

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
    public void BalanceFrame() {

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
        setDefaultCloseOperation(HIDE_ON_CLOSE);
        // 设置窗口上当前程序的名字
        setTitle("ATM--查询账户余额界面");
        // 清除默认的布局管理器
        getContentPane().setLayout(null);
        // 设置窗口显示
        setVisible(true);
    }

    // 定义控件

    JLabel titleLabel, timeLabel, balanceLabel;
    // 控件集合
    ArrayList<JComponent> listComponent = new ArrayList<>();

    // 页面控件布置
    public void setComponent() {

        // 放到页面上
        // 标签
        titleLabel = new JLabel("您的账户余额为", 2);
        timeLabel = new JLabel();
        balanceLabel = new JLabel(balance);
        // 设置坐标与宽高

        titleLabel.setBounds(210, 50, 200, 60);
        balanceLabel.setBounds(210, 100, 200, 60);
        timeLabel.setBounds(5, 320, 305, 40);

        // 存入集合

        listComponent.add(timeLabel);
        listComponent.add(balanceLabel);
        // titleLabel不存入集合单独设置
        titleLabel.setFont(new Font("黑体", Font.BOLD, 24));

        // 放到页面上

        getContentPane().add(titleLabel);
        getContentPane().add(timeLabel);
        getContentPane().add(balanceLabel);

        // 整体设置字体
        for (JComponent jComponent : listComponent) {
            jComponent.setFont(new Font("宋体", Font.BOLD, 20));
        }
    }

}
