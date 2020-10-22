package PDA;

import java.io.FileNotFoundException;
import java.util.*;

import PDA.CNF.*;
import PDA.CFG.*;


public class GNF {
    CNF cnf = null;
    Set<String> P = null;
    Set<String> T = null;
    Set<String> V = null;
    List<String> V_order = null;
    String S = null;
    Map<String,List<String>> map = null;

    GNF(CNF cnf){
        this.cnf = cnf;
        this.P = cnf.getP();
        this.T = cnf.getT();
        this.V = cnf.getV();
        this.S = cnf.getS();
    }

    public void orederP(GNF gnf){
        V_order = new ArrayList<>();
        for (String v:V){
            if(v.equals(this.S)){
                continue;
            }
            V_order.add(v);
        }
        Collections.sort(V_order,Collections.reverseOrder());
        V_order.add(this.S);
    }

    public void removeLeft(String v,List<String> origin){
        List<String> newSet = new ArrayList<>();
        List<String> vP_without = new ArrayList<>();
        List<String> vP_left = new ArrayList<>();
        List<String> newv_P = new ArrayList<>();

        for(String right:origin){
            List<String> rightList = CNF.splitRight(right);
            String rightOne = rightList.get(0);
            if(v.equals(rightOne)){
                vP_left.add(right);
            }
            else{
                vP_without.add(right);
            }
        }
        String newV = newV(v);
        V_order.add(newV); //确保新的 V在最末尾
        for (String s:vP_without){
            String newp = s;
            String newp2 = s+newV;
            newSet.add(newp);
            newSet.add(newp2);
        }
        for (String s:vP_left){
            String noV = s.replaceFirst(v,"");//去除右部的第一个V,Axxx--xxx
            String newp = noV;
            String newp2 = noV+newV;
            newv_P.add(newp);
            newv_P.add(newp2);
        }
        map.put(newV,newv_P);
        map.put(v,newSet);
    }

    public void daihuan2(GNF gnf){
        map = new HashMap<>();
        for(String v:V_order){
            List<String> list = new ArrayList<>();
            for (String p:P){
                if(p.substring(0,p.indexOf(">")-1).equals(v)){
                    list.add(p.substring(p.indexOf(">")+1));
                }
            }
            map.put(v,list);
        }
        List<String> V_order_cp = new ArrayList<>();
        V_order_cp.addAll(V_order);
        Collections.reverse(V_order_cp);
        if(hasLeftRe(V_order_cp.get(0),map.get(V_order_cp.get(0)))){ //存在左递归
            removeLeft(V_order_cp.get(0),map.get(V_order_cp.get(0)));
        }
        for(int i =1;i<V_order_cp.size();i++){

            for (int j = 0;j<i;j++){
                List<String> ilist = map.get(V_order_cp.get(i));
                List<String> ilist_c = new ArrayList<>();
                ilist_c.addAll(ilist);
                for(String p:ilist){ //遍历 右部集合
                    List<String> rightList = CNF.splitRight(p);
                    String rightOne = rightList.get(0);
                    if(V_order_cp.get(j).equals(rightOne)){
                        ilist_c.remove(p);
                        List<String> jlist = map.get(V_order_cp.get(j));
                        for (String t:jlist){
                            String newp = p.replaceFirst(rightOne,t); //替换
                            ilist_c.add(newp);
                        }
                        map.put(V_order_cp.get(i),ilist_c);
                    }
                }
            }

            if(hasLeftRe(V_order_cp.get(i),map.get(V_order_cp.get(i)))){
                removeLeft(V_order_cp.get(i),map.get(V_order_cp.get(i)));
            }
        }

    }

    public boolean hasLeftRe(String v,List<String> list){
        boolean res = false;
        for (String p:list){
            List<String> rightList = CNF.splitRight(p);
            String rightOne = rightList.get(0);
            if(v.equals(rightOne)){
                res = true;
                break;
            }
        }
        return res;
    }

    /**
     * 回代 消除非终结符打头
     * @param gnf
     */
    public void Back(GNF gnf){
        int S_index = V_order.indexOf(this.S);
        for(int i =0;i<V_order.size();i++){
            String v = V_order.get(i);
            List<Integer> indexList = FirstisT(v);
            int len = indexList.size();
            if(len == 0){
                continue;
            }
            List<String> right = map.get(v); //Ac | c| b
            List<String> right_copy = new ArrayList<>(); //copy
            right_copy.addAll(right);

            for(Integer in:indexList){
                String p = right.get(in.intValue()); //Ac
                List<String> rightList = CNF.splitRight(p); //Ac---A  c
                String rightOne = rightList.get(0); //A
                List<String> join = map.get(rightOne);//A->cC ...cC

                right_copy.remove(p); //删除 Ac
                for (String s:join){
                    String newP = p.replaceFirst(rightOne,s);
                    right_copy.add(newP);
                }
            }
            map.put(v,right_copy); //更新 右部产生式列表
        }
//        if(S_index == V_order.size()-1) return;
//        for(int i =S_index+1;i<V_order.size();i++){
//            List<String> right = map.get(V_order.get(i)); //A_0 ->BC|CA...BC,CA
//            for(String p:right){
//                List<String> rightList = CNF.splitRight(p); //Ac---A  c
//                String one
//                if()
//            }
//        }
    }

    /**
     * 找出 第一个是非终结符的 下标集合。
     * @param v
     * @return
     */
    public List<Integer> FirstisT(String v){
        List<Integer> res = new ArrayList<>();
        List<String> list = map.get(v);
        for(String p:list){
            List<String> rightList = CNF.splitRight(p);
            String rightOne = rightList.get(0);
            if(V_order.contains(rightOne)){
                res.add(list.indexOf(p));
            }
        }
        return res;
    }


    /**
     * 代换
     * @param
     */
    public void daihuan( GNF gnf){
//        Set<String> Pcopy = new HashSet<>();
//        Pcopy.addAll(P);
        Set<String> Pset = new HashSet<>();
        Set<String> Pdele = new HashSet<>();
        Pset.addAll(P);
        for (String p:P){
            String left = p.substring(0,p.indexOf(">")-1);
            List<String> rightList = CNF.splitRight(p.substring(p.indexOf(">")+1));
            String rightFirst = rightList.get(0);

            if(!V_order.contains(rightFirst)){
                continue;
            }
            int left_index = V_order.indexOf(left);
            int right_index = V_order.indexOf(rightFirst);

            if(left_index<right_index){
                Pdele.add(p);
                List<String> pAdd = new ArrayList<>();
                daihuanReg(p,pAdd);
                Pset.addAll(pAdd);
            }
        }
        P.addAll(Pset);
        P.removeAll(Pdele);

    }
    public String daihuanReg(String p,List<String> pList){
        if(p.contains("BBB")){
            System.out.println("WARN!!!!!");
            System.out.println(p);
            System.exit(1);
        }
        System.out.println(p);
        String left = p.substring(0,p.indexOf(">")-1);
        List<String> rightList = CNF.splitRight(p.substring(p.indexOf(">")+1));
        String right = rightList.get(0);
        if((!V_order.contains(right)) || V_order.indexOf(left) >= V_order.indexOf(right) ){
            pList.add(p);
            return p;
        }
        else {
            for(String p1:P){
                if(p1.substring(0,p1.indexOf(">")-1).equals(right)){
                    String p1Right = p1.substring(p1.indexOf(">")+1);
                    String p2 = p.replaceFirst(right,p1Right);
                    daihuanReg(p2,pList);
                }
            }
        }

        return p;
    }
    public void removeLeft(GNF gnf){
        Set<String> P_copy = new HashSet<>();
        P_copy.addAll(P);

        List<String> V_order_C = new ArrayList<>();
        V_order_C.addAll(V_order);
        for(String v:V_order_C){//按CBAS 最低先
            for (String p:P_copy){
                String pleft = p.substring(0,p.indexOf(">")-1);
                List<String> rightList = CNF.splitRight(p.substring(p.indexOf(">")+1));
                String pRight1 = rightList.get(0);
                if(v.equals(pleft) && pleft.equals(pRight1)){
                    removeLeft1(gnf,v);
                    break;
                }
            }
        }
    }
    public void removeLeft1(GNF gnf,String v){
        List<String> vP_without = new ArrayList<>();
        List<String> vP_left = new ArrayList<>();
        Set<String> newSet = new HashSet<>();
        Set<String> needDele = new HashSet<>();

        for (String p:P){
            String pRight = p.substring(p.indexOf(">")+1);
            List<String> rightList = CNF.splitRight(pRight);
            String pRight1 = rightList.get(0);
            if(p.substring(0,p.indexOf(">")-1).equals(v)){
                needDele.add(p);
                if(v.equals(pRight1)){
                    vP_left.add(pRight);
                }
                else{
                    vP_without.add(pRight);
                }
            }
        }
        String newV = newV(v);
        V_order.add(newV); //确保新的 V在最末尾
        for (String s:vP_without){
            String newp = v+"->"+s;
            String newp2 = v+"->"+s+newV;

            newSet.add(newp);
            newSet.add(newp2);
        }
        for (String s:vP_left){
            String noV = s.replaceFirst(v,"");//去除右部的第一个V,Axxx--xxx
            String newp = newV+"->"+noV;
            String newp2 = newV+"->"+noV+newV;
            newSet.add(newp);
            newSet.add(newp2);
        }
        P.removeAll(needDele);
        P.addAll(newSet);
    }

    /**
     * 回代
     * @param gnf
     */
    public void  backIN(GNF gnf){
        int Sindex = V_order.indexOf(this.S);
        int length = V_order.size();
        for (int i = 0; i < length-1; i++) {
            if(i<=Sindex){
                String vpre = V_order.get(i);
                String vnext = V_order.get(i+1);
                Set<String> vpreSet = new HashSet<>();
                Set<String> vnextSet = new HashSet<>();

                for (String p:P){
                    if(p.substring(0,p.indexOf(">")-1).equals(vpre)){
                        vpreSet.add(p);
                    }
                    else if(p.substring(0,p.indexOf(">")-1).equals(vnext)){
                        vnextSet.add(p);
                    }
                }
                for(String p:vnextSet){
                    String pRight = p.substring(p.indexOf(">")+1);
                    List<String> rightList = CNF.splitRight(pRight);
                    if(rightList.contains(vpre)){
                        P.remove(p);
                        for(String q:vpreSet){
                            String qRight = q.substring(q.indexOf(">")+1);
                            String son = p.replace(vpre,qRight);
                            P.add(son);
                        }
                    }
                }
            }

        }
    }
    public String notUsedV(){
        for(char i='F';i<='Z';i++){
            if(!V.contains(i+"") && !(V.contains(i+"_0"))){
                return i+"";
            }
        }
        return null;
    }
    public String newV(String v){
        StringBuffer sb = new StringBuffer(v);
        if(v.length() == 1){
            return sb.append("_0").toString();
        }
        else{
            int i = v.charAt(2)-'0';
            sb.replace(2,3,(i+1)+"");
            return sb.toString();
        }
    }
    public void printGNF(GNF gnf){
        System.out.println("GNF完了");
        for (String v:V_order){
            System.out.print(v+" -> ");
            for(String s:map.get(v)){
                System.out.print(s+" | ");
            }
            System.out.println("");
        }
    }

    public void toGNF(GNF gnf){
        daihuan(gnf);
        removeLeft(gnf);
        backIN(gnf);
    }

    public static void main(String[] args) throws FileNotFoundException {
        CFG c = new CFG();
        c.read("./src/resource/Grammar9.txt");
        c.simplify(c);
        c.printCFG();
        c.vertify(c);
        System.out.println("_____________________");
        CNF cnf = new CNF(c);
        cnf.toCNF(c);
        cnf.printCNF(cnf);
        System.out.println("-------------------------------");
        GNF gnf = new GNF(cnf);

        gnf.orederP(gnf);
        System.out.println("------------排序完-----------------");
//        gnf.printGNF(gnf);
        System.out.println("------------------------------------");
        gnf.daihuan2(gnf);
        System.out.println("------------消除和排序完-----------------");
        gnf.printGNF(gnf);
        gnf.Back(gnf);
        System.out.println("------------GNF完成-----------------");
        gnf.printGNF(gnf);
//        gnf.D(gnf);

        //先消除一次左递归
//        ll

        System.out.println("--------------进行代换----------------- ");



    }


}
