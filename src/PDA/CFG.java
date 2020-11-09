package PDA;

import jdk.nashorn.internal.ir.Terminal;
import jdk.nashorn.internal.runtime.regexp.RegExp;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.*;

public class CFG {
    private Set<String> V;
    private Set<String> T;
    private Set<String> P;
    private Set<Map<String, List<String>>> P1;
    private String S;

    public String getS() {
        return S;
    }
    public Set<String> getV() {
        return V;
    }

    public Set<String> getT() {
        return T;
    }

    public Set<String> getP() {
        return P;
    }

    private String V_re = "[A-Z](_[0-9]*)?(,[A-Z](_[0-9]*)?)*";
    private String T_re = ".*(,.*)*";
    private String S_re = "[A-Z](_[0-9]*)?";
    private String P_re = "([A-Z](_[0-9]*)?->.*(|.*)*(,[A-Z](_[0-9]*)?->.*(|.*)*)*)";
    CFG(){
        V = new HashSet<>();
        T = new HashSet<>();
        P = new HashSet<>();
        P1 = new HashSet<>();
        S = null;

    }

    public void read(String fileName) throws FileNotFoundException {
        File file = new File(fileName);
        Scanner in = new Scanner(file);

        while(in.hasNext()){
            String temp = in.next();
            if(temp.equals("#V#")){
                String[] line = in.next().split(",");
                for(String s:line){
                    V.add(s);
                }

            } else if(temp.equals("#T#")){
                String[] line = in.next().split(",");
                for(String s:line){
                    T.add(s);
                }
            } else if(temp.equals("#S#")){
                S = in.next().charAt(0)+"";
            } else if(temp.equals("#P#")){
                while (in.hasNextLine()){
                while (in.hasNextLine()){
                    String t = in.nextLine();
                    t = t.replace(" ","");
                    if(!t.equals("")){
//                        System.out.println(P);
                        P.add(t);
                    }
                }
                }
            }
        }
        changeP2();
        valid();
    }

    public void Start(CFG cfg){
        removeEmpty(cfg);
        vertify(cfg);
        removeUnitP(cfg);
        vertify(cfg);
        reduceCFG(cfg);
        vertify(cfg);
    }
    public void valid(){
        boolean allValid = true;
        //判断V的合法性
        System.out.println("#非终结符 合法性检测！");
        for (String s:V){
            if(!Pattern.matches(V_re,s)){
                allValid = false;
                System.out.println(s+"该字符不符合非终结符规范，请重试");
            }
        }
        System.out.println("#终结符 合法性检测！");
        for (String s:T){
            if(Pattern.matches("[A-Z]",s)|| !Pattern.matches(T_re,s) ){
                allValid = false;
                System.out.println(s+"该字符不符合终结符规范，请重试");
            }
        }
        System.out.println("#开始符号合法性检测！");
        if(!Pattern.matches(S_re,S)){
            allValid = false;
            System.out.println(S+"该字符不符合初始符号规范,请重试");
        }
        System.out.println("#产生式 合法性检测！");
        if(allValid){
            System.out.println("所有输入合法！接下来开始了");
        }
        if(!allValid){
            System.exit(2);
        }
    }

    /**
     * 改变产生式的存储结构
     * from A->AB|aBC|
     * to A:AB  A:abc
     */
    public void changeP(){
        for (String t:P){
            String s = t.charAt(0)+"";
            String[] sub = t.substring(3).split("\\|");
            Map<String, List<String>> map = new HashMap<>();
            List<String> str = new ArrayList<>();
            for(String right:sub){
                str.add(right);
            }
            map.put(s,str);
            P1.add(map);
        }
    }

    /**
     * make A->B|c  to  A->B A->c
     */
    public void changeP2(){
        Set<String> p2 = new HashSet<>();
        for (String s:P){
            String[] fege = s.substring(3).split("\\|");
            for (String f:fege){
                StringBuffer sb = new StringBuffer(s.charAt(0)+"");
                sb.append("->");
                sb.append(f);
                p2.add(sb.toString());
            }
        }
        P = p2;
    }

    /**
     * 简化cfg分为：
     * 1、去除无用符号 reduceCFG
     * 2、去除空产生式 removeEmpty
     * 3、去除单一产生式 removeUnitP
     * @param cfg
     */
    public void simplify(CFG cfg){
        removeEmpty(cfg);
        removeUnitP(cfg);
        reduceCFG(cfg);
    }
    public void  reduceCFG(CFG cfg){
        reduceCFG1(cfg);
        reduceCFG2(cfg);
        reduceCFG3(cfg);
    }

    public static void outReduce(CFG cfg){
        
    }

    /**
     * 消去无用符号第一步：
     * 1、计算生成符号集合
     * @param cfg
     */
    public void reduceCFG1(CFG cfg){
        Set<String> N0 = new HashSet<>();
        Set<String> N1 = new HashSet<>();

        for (String s:P){
            String f = s.substring(3);
            boolean flag2 = true;
            for(char a:f.toCharArray()){
                if(a>='A'&&a<='Z'){
                    flag2 = false;
                    break;
                }
            }
            if(flag2){
                N1.add(s.charAt(0)+"");
            }
        }


        while(!SetisEqual(N0,N1)){
            N0 = N1;
            Set<String> temp = new HashSet<>();
            for (String s:P){
                boolean flag = true;
                String right = s.substring(3);
                for (char c: right.toCharArray()){
                    String str = c+"";
                    boolean isinN = N0.contains(str);
                    boolean hasBig = Pattern.matches(".*[A-Z]+.*",str);
                    if((!isinN) && hasBig){
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    temp.add(s.charAt(0)+"");
                }
            }
            temp.addAll(N0);
            N1 = temp;
            V = N1;
        }
        //删除相关产生式

        Set<String> none = new HashSet<>();
        for(String p:P){
            String left = p.charAt(0)+"";
            String[] fege = p.substring(3).split("\\|");
            if(!V.contains(left)){
                none.add(p);
            }
            for(String f:fege){
                for(char c:f.toCharArray()){
                    String ch = c+"";
                    if((c>='A'&&c<='Z')&&!V.contains(ch)){
                        none.add(p);
                    }
                }
            }
        }
        P.removeAll(none);
    }
    /**
     * 消去无用符号第二步：
     * 2、计算可达符号集合
     * @param cfg
     */
    public void reduceCFG2(CFG cfg){
        Set<String> N0 = new HashSet<>();
        Set<String> N1 = new HashSet<>();
        N0.add(this.S);
        for (String p:P){
            if(this.S.equals(p.charAt(0)+"")){
                String f = p.substring(3);
                for(char ch:f.toCharArray()){
                    N1.add(ch+"");
                }
            }
        }
        N1.addAll(N0);
        while(!SetisEqual(N0,N1)){
            N0.clear();
            N0.addAll(N1);
            for (String left:N0){
                for (String p:P){
                    if(left.equals(p.charAt(0)+"")){
                        String f = p.substring(3);
                        for(char ch:f.toCharArray()){
                            N1.add(ch+"");
                        }
                    }
                }
            }
        }
        Set<String> Vjoin = new HashSet<>();
        Set<String> Tjoin = new HashSet<>();

        Vjoin.addAll(N1);
        Vjoin.retainAll(V);
        Tjoin.addAll(N1);
        Tjoin.retainAll(T);
        N0 = Vjoin;

//        V_s = N0;
//        T_s = Tjoin;
        V.clear();
        T.clear();
        V.addAll(N0);
        T.addAll(Tjoin);

    }
    /**
     * 消去无用符号第三步：
     * 3、消去 相关产生式
     * @param cfg
     */
    public void reduceCFG3(CFG cfg){
        Set<String> none = new HashSet<>();
        for(String p:P){
            String left = p.charAt(0)+"";
            String[] fege = p.substring(3).split("\\|");
            if(!V.contains(left)){
                none.add(p);
            }
            for(String f:fege){
                for(char c:f.toCharArray()){
                    String ch = c+"";
                    if(!V.contains(ch) && !T.contains(ch)){
                       none.add(p);
                    }
                }
            }
        }
        P.removeAll(none);
    }
    /**
     *
     * 4、打印查验产生式
     * @param cfg
     */
    public void reduceCFG4(CFG cfg){

    }
    public boolean SetisEqual(Set<String> s1,Set<String> s2){
        boolean res = true;
        if(s1.size()!=s2.size()) return false;
        for (String s:s1){
            if(!s2.contains(s)){
                res = false;
                break;
            }
        }
        return res;
    }



    /**
     * 移除空产生式分为2种情况：
     * 1.ε∈L(G)  那么 将S->ε 加入。
     * 2.ε不属于L(G)
     *
     * 先判断 ε是否属于L，通过判断S∈W{}？求取W //能够推出 ε的非终结符集合
     *
     * @param cfg
     */
    public void removeEmpty(CFG cfg){
        Set<String> w = findW(cfg);

        removeEmptyRE(cfg,0);
        deleteEmpty(cfg);
//        if(w.contains(S)){
//            P.add(S+"->#");
//        }

    }
    //递归求解
    public void removeEmptyRE(CFG cfg,int cnts){
        if(cnts == P.size()) return;

        Set<String> W = findW(cfg);
        List<String> P_list = new ArrayList<>(P);
        ListIterator pir = P_list.listIterator();
        int cnt = 0;
        while(pir.hasNext()){
            cnt++;
            String p = (String)pir.next();
            String right = p.substring(3);
            int[] mark = new int[p.length()];
            for(int i =0;i<right.length();i++){
                char ch = right.charAt(i);
                if(W.contains(ch+"")){
                    mark[i] =1;
                    StringBuffer p_copy = new StringBuffer(p);
                    p_copy.replace(i+3,i+4,"");
                    pir.add(p_copy.toString());
                }
            }
        }
        P.addAll(P_list);
        removeEmptyRE(cfg,cnt);
    }

    /**
     * 删除右端只有#
     * @param cfg
     */
    public void deleteEmpty(CFG cfg){
        Iterator<String> pl = P.iterator();
        while (pl.hasNext()){
            String p = pl.next();
            String right = p.substring(3);
            if(right.length() == 0){
                pl.remove();
            }
            String re = "^#+$";
            if(Pattern.matches(re,right)){
                pl.remove();
            }
        }

    }


    /**
     * 找到可以推出空的非终结符集合
     * @param cfg
     * @return
     */
    public Set<String> findW(CFG cfg){
        Set<String> res = new HashSet<>();
        for (String p:P){
            String right = p.substring(3);
            if(right.equals("#")){
                res.add(p.charAt(0)+"");
            }
        }
        Set<String> pre = new HashSet<>();
        while(!(SetisEqual(pre,res))){
            pre.addAll(res);
            for (String p:P){
                String right = p.substring(3);
                boolean flag = true;
                for(char ch:right.toCharArray()){
                    if(!res.contains(ch+"")){
                        flag = false;
                        break;
                    }
                }
                if(flag){
                    res.add(p.charAt(0)+"");
                }
            }
        }
        return res;
    }

    /**
     * 去除单产生式
     * @param cfg
     */
    public void removeUnitP(CFG cfg){
        Set<String> new_P = new HashSet<>();
        for(String v:V){
            Set<String> N0 = new HashSet<>();
            Set<String> N1 = new HashSet<>();
            N0.add(v);
            for (String p:P){
                String right = p.substring(3);
                if(v.equals(p.charAt(0)+"") && right.length()==1 && Pattern.matches("[A-Z]",right) ){
                    N1.add(right);
                }
            }
            N1.addAll(N0);

            while(!SetisEqual(N0,N1)){
                N0.clear();
                N0.addAll(N1);
                for (String p:P){
                    String right = p.substring(3);
                    if(N0.contains(p.charAt(0)+"") && right.length()==1 &&Pattern.matches("[A-Z]",right)){
                        N1.add(right);
                    }
                }
                N1.addAll(N0);
            }

            for(String nt:N0){
                for(String p:P){
                    String right = p.substring(3);
                    if(p.charAt(0) == nt.charAt(0)){
                        boolean flag = (right.length()==1) && right.charAt(0)>='A'&&right.charAt(0)<='Z';
                        if(!flag){
                            String t = v+"->"+right;
                            new_P.add(t);
                        }
                    }
                }
            }
        }
        P.clear();
        P.addAll(new_P);
    }

    /**
     * 再次确定有哪些 V T
     * @param c
     */

    public void vertify(CFG c){
        Set<String> Vset = new HashSet<>();
        Set<String> Tset = new HashSet<>();
        for (String p:P){
            String left = p.charAt(0)+"";
            String right = p.substring(3);
            Vset.add(left);
            for(char ch:right.toCharArray()){
                if(!(ch>='A'&&ch<='Z')){
                    Tset.add(ch+"");
                }
            }
        }
        T.clear();
        T.addAll(Tset);
        V.clear();
        V.addAll(Vset);

//        for (String v:Vset){
//            System.out.print(v+" ");
//        }
//        System.out.println("");
//        for (String t:Tset){
//            System.out.print(t+" ");
//        }
//        System.out.println(" ");
    }

    public void  printCFG(){
        System.out.println("简化完了");
        for (String v:V){
            StringBuffer sb = new StringBuffer("");
            for(String p:P){
                if(p.charAt(0) == v.charAt(0)){
                    sb.append(p.substring(3));
                    sb.append("|");
                }
            }
            System.out.println(v+"->"+sb.toString());
        }
        System.out.println("_____________________");
    }

    public static void main(String[] args) throws FileNotFoundException{
        CFG c = new CFG();
        c.read("./src/resource/Grammar2.txt");
        String P_re = "^#+$";
        c.removeEmpty(c);
        c.removeUnitP(c);
        c.reduceCFG(c);
        c.printCFG();
        c.vertify(c);


    }

}
