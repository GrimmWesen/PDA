package UI;
import java.awt.Color;
import java.awt.Container;

import javax.swing.*;

public class Windows {
    public void init(){
        JFrame frm = new JFrame();
        frm.setBounds(100, 100, 920, 580);  // 功能与上面相同
        frm.setTitle("自动机作业");
        Container c = frm.getContentPane(); // frm中包含一个内容窗格， 需要获取内容窗格，再设置背景颜色，直接设置frm的背景颜色会被内容窗格挡住
        c.setBackground(Color.lightGray);
//        frm.setLayout(null);                // 如过不设置为null默认，按钮会充满整个内容框，挡住背景颜色
//        JButton btn = new JButton("确定");
//		btn.setBounds(100, 100, 100, 40);   // 功能与上面相同
//        frm.add(btn);       				// 添加了按钮会把背景颜色挡住，可以通过面板来调节

        JTextArea jta = new JTextArea("", 10, 30);
        JScrollPane jsp = new JScrollPane(jta);
        jsp.setBounds(20, 70, 160, 100);
        jta.setLineWrap(true);
        frm.add(jta);
        frm.add(jsp);
        frm.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frm.setVisible(true);
    }
    public static void main(String[] args) {
        new Windows().init();
    }
}