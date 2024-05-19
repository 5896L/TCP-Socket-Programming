package work;

//登录界面类
//bug：注册账号，修改密码后需要重新登陆才能登录，因为用户集合未刷新

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;

public class Withdrawl extends JFrame {
    Timer timer;
    Link link;

    public Withdrawl(Link link1) throws Exception {
        this.link = link1;
        // 先放置控件
        setComponent();
        // 放入事件监听函数
        setActionListen();
        // 最后布置页面（先布置页面会覆盖控件布置）
        WithdrawlFrame();

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
    public void WithdrawlFrame() {

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
        setTitle("ATM--用户取款界面");
        // 清除默认的布局管理器
        getContentPane().setLayout(null);
        // 设置窗口显示
        setVisible(true);
    }

    // 定义控件
    JButton oneButton, twoButton, threeButton, one1Button, two2Button, three3Button, restartButton, okButton;
    JLabel tipLabel, titleLabel, timeLabel;
    JTextField amountText;

    // 控件集合
    ArrayList<JComponent> listComponent = new ArrayList<>();

    // 页面控件布置
    public void setComponent() {

        amountText = new JTextField();
        amountText.setBounds(185, 150, 200, 45);
        listComponent.add(amountText);
        getContentPane().add(amountText);
        // 标签
        tipLabel = new JLabel("手动输入取款金额(元)");
        titleLabel = new JLabel("请选择取款金额!");
        timeLabel = new JLabel();

        tipLabel.setBounds(180, 100, 250, 60);
        titleLabel.setBounds(180, 30, 250, 60);
        timeLabel.setBounds(5, 320, 305, 40);

        // 存入集合
        listComponent.add(tipLabel);
        listComponent.add(timeLabel);
        // titleLabel不存入集合单独设置
        titleLabel.setFont(new Font("黑体", Font.BOLD, 28));

        // 放到页面上
        getContentPane().add(tipLabel);
        getContentPane().add(titleLabel);
        getContentPane().add(timeLabel);

        // 按钮
        oneButton = new JButton("100元");
        twoButton = new JButton("500元");
        threeButton = new JButton("900元");

        one1Button = new JButton("300元");
        two2Button = new JButton("700元");
        three3Button = new JButton("1000元");

        okButton = new JButton("确认");
        restartButton = new JButton("重置");

        // 设置大小
        oneButton.setBounds(60, 100, 100, 40);
        twoButton.setBounds(60, 160, 100, 40);
        threeButton.setBounds(60, 220, 100, 40);

        one1Button.setBounds(400, 100, 100, 40);
        two2Button.setBounds(400, 160, 100, 40);
        three3Button.setBounds(400, 220, 100, 40);

        okButton.setBounds(180, 280, 80, 40);
        restartButton.setBounds(310, 280, 80, 40);
        // 存入集合
        listComponent.add(oneButton);
        listComponent.add(twoButton);
        listComponent.add(threeButton);
        listComponent.add(one1Button);
        listComponent.add(two2Button);
        listComponent.add(three3Button);

        listComponent.add(okButton);
        listComponent.add(restartButton);
        // 放置于页面
        getContentPane().add(oneButton);
        getContentPane().add(twoButton);
        getContentPane().add(threeButton);
        getContentPane().add(one1Button);
        getContentPane().add(two2Button);
        getContentPane().add(three3Button);
        getContentPane().add(okButton);
        getContentPane().add(restartButton);

        // 整体设置字体
        for (JComponent jComponent : listComponent) {
            jComponent.setFont(new Font("宋体", Font.BOLD, 20));
        }
    }

    // 事件监听
    public void setActionListen() {

        okButton.addActionListener(e -> {
            try {
                String money = amountText.getText();

                int flag = link.withdrwal(money);
                if (flag == 1) {
                    try {
                        JOptionPane.showMessageDialog(getContentPane(), "取款成功!");
                        new Balance(link.balance_q());

                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                } else if (flag == 2) {
                    try {
                        JOptionPane.showMessageDialog(getContentPane(), "余额不足！");
                        new Balance(link.balance_q());
                    } catch (Exception E) {
                        E.printStackTrace();
                    }
                } else if (flag == 3) {
                    JOptionPane.showMessageDialog(getContentPane(), "未知错误！请联系ATM管理员");
                }
            } catch (Exception E) {
                E.printStackTrace();
            }
        });

        restartButton.addActionListener(e -> {
            amountText.setText("");
        });
        oneButton.addActionListener(e -> {
            amountText.setText("100");
        });
        twoButton.addActionListener(e -> {
            amountText.setText("500");
        });
        threeButton.addActionListener(e -> {
            amountText.setText("900");
        });

        one1Button.addActionListener(e -> {
            amountText.setText("300");
        });
        two2Button.addActionListener(e -> {
            amountText.setText("700");
        });
        three3Button.addActionListener(e -> {
            amountText.setText("1000");
        });
    }
}
