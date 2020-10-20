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

    /**
     * 代换
     * @param
     */
    public void daihuan( GNF gnf){
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
        System.out.println("代换后");
        for(String p:P){
            System.out.println(p);
        }

    }
    public String daihuanReg(String p,List<String> pList){
        String left = p.substring(0,p.indexOf(">")-1);
        List<String> rightList = CNF.splitRight(p.substring(p.indexOf(">")+1));
        String right = rightList.get(0);
        if(V_order.indexOf(left) >= V_order.indexOf(right)){
            pList.add(p);
            return p;
        }
        else {
            for(String p1:P){
                if(p1.substring(0,p.indexOf(">")-1).equals(right)){
                    String p1Right = p1.substring(p.indexOf(">")+1);
                    String p2 = p.replaceFirst(right,p1Right);
                    daihuanReg(p2,pList);
                }
            }
        }

        return p;
    }
    public void removeLeftRe(GNF gnf){

    }

    public static void main(String[] args) throws FileNotFoundException {
        CFG c = new CFG();
        c.read("./src/resource/Grammar7.txt");
        c.removeEmpty(c);
        c.removeUnitP(c);
        c.reduceCFG(c);
        c.printCFG();
        c.vertify(c);
        System.out.println("_____________________");
        CNF cnf = new CNF(c);
        cnf.toCNF(c);
        cnf.printCNF(cnf);
        GNF gnf = new GNF(cnf);
        gnf.orederP(gnf);
        gnf.daihuan(gnf);


    }


}
