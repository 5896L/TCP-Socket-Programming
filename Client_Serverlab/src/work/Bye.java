package work;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Calendar;

public class Bye extends JFrame {
    public Bye() throws Exception {
        setComponent();
        ByeFrame();
        Timer timer;
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

    public void ByeFrame() {

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
        setTitle("ATM--结束界面");
        // 清除默认的布局管理器
        getContentPane().setLayout(null);
        // 设置窗口显示
        setVisible(true);
    }
    // 定义控件

    JLabel byeLabel, timeLabel;
    // 控件集合
    ArrayList<JComponent> listComponent = new ArrayList<>();

    // 页面控件布置
    public void setComponent() {
        // 标签
        byeLabel = new JLabel("期待与您的下次相遇！", 2);

        timeLabel = new JLabel();

        // 设置坐标与宽高

        byeLabel.setBounds(180, 150, 300, 60);
        timeLabel.setBounds(5, 320, 305, 40);

        // 存入集合

        listComponent.add(timeLabel);
        // titleLabel不存入集合单独设置
        byeLabel.setFont(new Font("黑体", Font.BOLD, 24));

        // 放到页面上

        getContentPane().add(byeLabel);
        getContentPane().add(timeLabel);

        // 整体设置字体
        for (JComponent jComponent : listComponent) {
            jComponent.setFont(new Font("宋体", Font.BOLD, 20));
        }
    }
}
