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

    public void D(GNF gnf){
        Map<String,List<String>> map = new HashMap<>();
        for(String v:V_order){
            List<String> list = new ArrayList<>();
            for (String p:P){
                if(p.substring(0,p.indexOf(">")-1).equals(v)){
                    list.add(p.substring(p.indexOf(">")+1));
                }
            }
            map.put(v,list);
        }
        int length = V_order.size();
        List<String> V_oreder_copy = new ArrayList<>();
        V_oreder_copy.addAll(V_order);
        for(int i=0;i<length;i++){
            String left = V_oreder_copy.get(i);
            List<String> right1 = map.get(left);
            List<String> newR = new ArrayList<>();
            List<String> delR = new ArrayList<>();
            // C-> ?
            for (int j = length-1; j > i; j--) {
                String left2 = V_oreder_copy.get(j);
                List<String> list = map.get(left);
                List<String> newList = null;
                boolean flag = false;
                for (String p:list){
                    List<String> rightList = CNF.splitRight(p);
                    String rightOne = rightList.get(0);
                    if(left2.equals(rightOne)){
                        flag = true;
                        newList = removeLeft(left,list);
                        break;
                    }
                }
                if (flag){
                    map.put(left,newList);
                    list.clear();
                    list.addAll(newList);
                }
            }

            for (String s:right1){
                List<String> rightList = CNF.splitRight(s);
                String rightOne = rightList.get(0);
                for (int j = length-1; j > i; j--){
                    String r1 = V_oreder_copy.get(j);
                    if(rightOne.equals(r1)){
                        delR.add(s);
                        for(String s2:map.get(r1)){
                            String temp = s.replaceFirst(rightOne,s2);
                            newR.add(temp);
                        }

                    }
                }
            }
            right1.addAll(newR);
            right1.removeAll(delR);
        }

        for (String s:V_oreder_copy){
            System.out.print(s+"->");
            for(String s2:map.get(s)){
                System.out.print(s2+" | ");
            }
            System.out.println("");
        }
    }
    public List<String> removeLeft(String v,List<String> origin){
        List<String> newSet = new ArrayList<>();
        List<String> vP_without = new ArrayList<>();
        List<String> vP_left = new ArrayList<>();

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
            String newp = newV+"->"+noV;
            String newp2 = newV+"->"+noV+newV;
            P.add(newp);
            P.add(newp2);
        }
        return newSet;
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
            else{

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
            StringBuffer sb = new StringBuffer("");
            for(String p:P){
                String left = p.substring(0,p.indexOf(">")-1);
                if(left.equals(v)){
                    sb.append(p.substring(p.indexOf(">")+1));
                    sb.append("|");
                }
            }
            System.out.println(v+"->"+sb.toString());
        }
    }

    public void toGNF(GNF gnf){
        daihuan(gnf);
        removeLeft(gnf);
        backIN(gnf);
    }

    public static void main(String[] args) throws FileNotFoundException {
        CFG c = new CFG();
        c.read("./src/resource/Grammar8.txt");
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
        gnf.printGNF(gnf);
        System.out.println("------------------------------------");
        gnf.D(gnf);

        //先消除一次左递归
//        gnf.removeLeft(gnf);
//        gnf.printGNF(gnf);

//        System.out.println("--------------进行代换----------------- ");
//        gnf.daihuan(gnf);
//        gnf.printGNF(gnf);
//        System.out.println("--------------接下来消除左递归-----------------");
//        gnf.removeLeft(gnf);
//        gnf.printGNF(gnf);
//
//        gnf.backIN(gnf);
//        gnf.printGNF(gnf);
//        for(String v:gnf.V_order){
//            System.out.print(v+" ");
//        }


    }


}
