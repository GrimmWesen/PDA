package Tuling;

import java.util.Scanner;

public class Tuling1 {
    public String build_Str(int x,int y){
        String res = "";
        StringBuffer sb = new StringBuffer(res);
        int i = 0;
        while(i<y){
            sb.append("1");
            i++;
        }
        sb.append("0");
        int j = 0;
        while(j<x){
            sb.append("1");
            j++;
        }
        sb.append("010");
        return sb.toString();
    }
    public String Exp(String line) {
        StringBuffer sb = new StringBuffer(line);

        int index = 0;
        int state = 0;
        String temp = new String(sb);
        while (state != 17) //17设为终点状态
        {
            if(!temp.equals(sb.toString())){
                System.out.println(sb.toString());
                temp = new String(sb.toString());
            }
            switch (state) {
                case 0:
                    if (sb.charAt(index) == '0') {
                        state = 17;
                        index++;
                        ;
                    } else if (sb.charAt(index) == '1') {
                        state = 1;
                        sb.setCharAt(index,'a');;
                        index++;
                        ;
                    } else {
                        state = 17;
                    }
                    break;
                case 1:
                    if (sb.charAt(index) == '0') {
                        state = 100;
                        index++;
                    } else if (sb.charAt(index) == '1') {
                        index++;
                    } else {
                        state = 17;
                    }
                    break;
                case 2:
                    if (sb.charAt(index) == '0') {
                        state = 10;
                        index--;
                    } else if (sb.charAt(index) == '1') {
                        state = 3;
                        sb.setCharAt(index,'b');;
                        index++;
                        ;
                    } else {
                        state = 17;
                    }
                    break;
                case 3:
                    if (sb.charAt(index) == '0') {
                        state = 4;
                        index++;
                    } else if (sb.charAt(index) == '1') {
                        index++;
                    } else {
                        state = 17;
                    }
                    break;
                case 4:
                    if (sb.charAt(index) == '0') {
                        state = 9;
                        index--;
                    } else if (sb.charAt(index) == '1') {
                        state = 5;
                        sb.setCharAt(index,'c');;
                        index++;
                        ;
                    } else {
                        state = 17;
                    }
                    break;
                case 5:
                    if (sb.charAt(index) == '0') {
                        state = 6;
                        index++;
                        ;
                    } else if (sb.charAt(index) == '1') {
                        index++;
                        ;
                    } else {
                        state = 17;
                    }
                    break;
                case 6:
                    if (index == sb.length()) {
//                        s = new turing_node;
//                        s -> data = '1';
//                        s -> pro = tail;
//                        s -> next = NULL;
//                        tail -> next = s;
//                        tail = s;
//                        read_write = tail;
                        sb.append("1");

                        state = 7;
                        index++;

                    } else if (sb.charAt(index) == '0') {
                        state = 7;
                        sb.setCharAt(index,'1');;
                        index++;

                    } else if (sb.charAt(index) == '1') {
                        index++;

                    } else {
                        state = 17;
                    }
                    break;
                case 7:
                    if (index == sb.length()) {
//                        s = new turing_node;
//                        s -> data = '0';
//                        s -> pro = tail;
//                        s -> next = NULL;
//                        tail -> next = s;
//                        tail = s;
                        sb.append("0");
                        state = 8;
                        index--;
                    } else {
                        state = 17;
                    }
                    break;
                case 8:
                    if (sb.charAt(index) == '0' || sb.charAt(index) == '1') {
                        index--;
                    } else if (sb.charAt(index) == 'c') {
                        state = 4;
                        index++;
                        ;
                    } else {
                        state = 17;
                    }
                    break;
                case 9:
                    if (sb.charAt(index) == '0' || sb.charAt(index) == '1') {
                        index--;
                    } else if (sb.charAt(index) == 'c') {
                        sb.setCharAt(index,'1');;
                        index--;
                    } else if (sb.charAt(index) == 'b') {
                        state = 2;
                        index++;
                        ;
                    } else {
                        state = 17;
                    }
                    break;
                case 10:
                    if (sb.charAt(index) == '0') {
                        state = 11;
                        index++;
                        ;
                    } else if (sb.charAt(index) == 'b') {
                        sb.setCharAt(index,'1');;
                        index--;
                    } else {
                        state = 17;
                    }
                    break;
                case 11:
                    if (sb.charAt(index) == '0') {
                        state = 12;
                        index++;
                    } else if (sb.charAt(index) == '1') {
                        index++;
                    } else {
                        state = 17;
                    }
                    break;
                case 12:
                    if (sb.charAt(index) == '0') {
                        state = 13;
                        sb.setCharAt(index,'1');;
                        index++;
                        ;
                    } else if (sb.charAt(index) == '1') {
                        sb.setCharAt(index,'d');;
                        index++;
                        ;
                    } else {
                        state = 17;
                    }
                    break;
                case 13:
                    if (sb.charAt(index) == '0') {
                        state = 14;
                        index--;
                        sb = new StringBuffer(sb.substring(0,sb.length()-1));

                    } else if (sb.charAt(index) == '1') {
                        index++;
                        ;
                    } else {
                        state = 17;
                    }
                    break;
                case 14:
                    if (sb.charAt(index) == '1') {
                        state = 15;
                        sb.setCharAt(index,'0');;
                        index--;
                    } else {
                        state = 17;
                    }
                    break;
                case 15:
                    if (sb.charAt(index) == '0') {
                        state = 16;
                        index--;
                    } else if (sb.charAt(index) == '1') {
                        index--;
                    } else if (sb.charAt(index) == 'd') {
                        state = 13;
                        sb.setCharAt(index,'1');;
                        index++;
                        ;
                    } else {
                        state = 17;
                    }
                    break;
                case 16:
                    if (sb.charAt(index) == '0' || sb.charAt(index) == '1') {
                        index--;
                    } else if (sb.charAt(index) == 'a') {
                        state = 0;
                        index++;
                        ;
                    } else {
                        state = 17;
                    }
                    break;
                case 100:
                    if (sb.charAt(index) == '0'){
                        state = 101;
                        index++;
                    }
                    else{
                        state = 2;
                    }
                    break;
                case 101:
                    if (sb.charAt(index) == '1'){
                        sb.setCharAt(index,'0');
                        state = 17;
                    }
                    else{
                        state = 17;
                    }
                    break;

                default:
                    break;
            }
        }

        return sb.toString();
    }
    public int  getRes(String line){
        int res = 0;
        int len = line.length();
        len = len -2;
        while(true){
            if(line.charAt(len) =='0'){
                break;
            }
            res++;
            len--;
        }
        return res;
    }

    public static void main(String[] args) {
        Tuling1 tl = new Tuling1();
        System.out.println("欢迎使用图灵机的幂指数运算x的y次方！");
        System.out.println("请输入x 和 y，用逗号分隔===");
        Scanner scanner = new Scanner(System.in);
        String str = scanner.next();
        String[] ch = str.split(",");
        int x = Integer.parseInt(ch[0]);
        int y = Integer.parseInt(ch[1]);
        if(x==0 && y==0){
            System.out.println("0的0次方无意义，请重试!");
            System.exit(2);
        }
        String line = tl.build_Str(x,y);
        System.out.println("初始纸条如下：");
        System.out.println(line);
        String l1 = tl.Exp(line);
        System.out.println("最终纸条如下：");
        System.out.println(l1);
        System.out.print("x^y = ");
        System.out.println(tl.getRes(l1));
    }

}


