package PDA;

import PDA.CFG.*;

import java.io.FileNotFoundException;
import java.util.*;

public class CNF {
    CFG cfg = null;
    Set<String> P = null;
    Set<String> T = null;
    Set<String> V = null;
    String S = null;

    CNF(CFG cfg){
        this.cfg = cfg;
        this.P = cfg.getP();
        this.T = cfg.getT();
        this.V = cfg.getV();
        this.S = cfg.getS();
    }
    CNF(){

    }

    public void toCNF(CFG cfg){
        Set<String> Pset = new HashSet<>();
        for(String p:P){
            String left = p.substring(0,p.indexOf(">")-1);
            String right = p.substring(3);
            List<String> rightAlph = splitRight(right);
            int rightNum = rightAlph.size();

            StringBuffer newP = new StringBuffer(left);
            newP.append("->");


//            String notUsed = notUsedV();
//            V.add(notUsed);

            if(rightNum == 1){
                Pset.add(p);
            }
            else{
                String notUsed = notUsedV();
                V.add(notUsed);
                for(String single:rightAlph){
                    if(T.contains(single)){
                        String newL = newV(notUsed);
                        V.add(newL);
                        newP.append(newL);

                        StringBuffer sb = new StringBuffer(newL);
                        sb.append("->");
                        sb.append(single);
                        Pset.add(sb.toString());
                    }
                    else{
                        newP.append(single);
                    }
                }
                String rightTemp = newP.toString().substring(newP.toString().indexOf(">")+1);
                if(rightNum == 2){
                    Pset.add(newP.toString());
                }
                else if(rightNum >2){
                    String new1 = notUsedV();
                    V.add(new1);
                    List<String> afterright = splitRight(rightTemp);
                    for(int i = 0;i<rightNum-1;i++){
                        String pson = null ;
                        if(i == 0){
                            pson = left+"->"+afterright.get(0)+new1+"_0";
                            V.add(new1+"_0");
                        }
                        else if( i!=rightNum-2){
                            pson = new1+"_"+(i-1)+"->"+afterright.get(i)+new1+"_"+i;
                            V.add(new1+"_"+i);
                        }
                        else if(i == rightNum -2){
                            pson = new1+"_"+(i-1)+"->"+afterright.get(i)+afterright.get(i+1);
                        }
                        Pset.add(pson);
                    }
                }
            }

        }
        System.out.println("CNF完了");
        for (String v:V){
            StringBuffer sb = new StringBuffer("");
            for(String p:Pset){
                String left = p.substring(0,p.indexOf(">")-1);
                if(left.equals(v)){
                    sb.append(p.substring(p.indexOf(">")+1));
                    sb.append("|");
                }
            }
            if(sb.toString().length()>0){
                System.out.println(v+"->"+sb.toString());
            }
        }
//        for (String p:Pset){
//            System.out.println(p);
//        }


    }
    /**
     * make A_0BC_1 to A_0  B C_0 separately
     * @param s
     * @return
     */
    public List<String> splitRight(String s){
        List<String> res = new ArrayList<>();
        char[] arr = s.toCharArray();
        if(s.length() == 1){
            res.add(s);
            return res;
        }
        for (int i = 0; i < arr.length-1 ; i++){
            int t = i;
            StringBuffer sr = new StringBuffer("");
            if(arr[i+1] == '_'){
                i += 2;
            }
            String temp = s.substring(t,i+1);
            sr.append(temp);
            res.add(sr.toString());
        }
        if( arr[arr.length-2]!='_'){
            res.add(arr[arr.length-1]+"");
        }
        return res;
    }

    /**
     * A to A_0  A_0 to A_1
     * @param v
     * @return
     */
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

    public String notUsedV(){
        for(char i='A';i<='Z';i++){
            if(!V.contains(i+"")){
                return i+"";
            }
        }
        return null;
    }

    public static void main(String[] args) throws FileNotFoundException {
        CFG c = new CFG();
        c.read("./src/resource/Grammar6.txt");

        c.removeEmpty(c);
        c.removeUnitP(c);
        c.reduceCFG(c);
        c.printCFG();
        c.vertify(c);
        System.out.println("_____________________");
        CNF cnf = new CNF(c);
        cnf.toCNF(c);


    }

}
