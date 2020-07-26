
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

public class VectorClock {
    static HashMap<String,ArrayList<ArrayList<Integer>>> hmap = new LinkedHashMap<>();
    static HashMap<String,ArrayList<Integer>> flag_map = new LinkedHashMap<>();
//    static int np = 3; //number of processes
//    static String ts[][] = {
//            {"E","S2","S1","R1","R2","R2","S1","E"}, //Process P0
//            {"E","S0","R0","R2","S2","R0","E"},      //Process P1
//            {"S1","S0","S0","R0","E","R1","E"}};     //Process P2
    static int np = 4; //number of processes
    static String ts[][] = {
            {"E","S1","E","S3","E"},
            {"R0","E","S2a","S2b","E"},
            {"S3","E","R1b","R1a","E","S3","R3","E"},
            {"E","R2","E","R0","R2","E","S2","E"}};
    static ArrayList<Integer> events[][]=new ArrayList[np][];
    static int flag[][]=new int[np][];
    public static void main(String[] args) {
        for(int i=0;i<np;i++){
            events[i] = new ArrayList[ts[i].length];
            flag[i]=new int[ts[i].length];
            for(int j=0;j< events[i].length;j++){
                events[i][j] = new ArrayList<>();
                for(int k=0;k<np;k++)
                    events[i][j].add(0);
            }
            for(int j=0;j<ts[i].length;j++)
                flag[i][j]=0;
        }
        func(0,0);
        System.out.println("");
        for(int i=0;i< np ;i++){
            System.out.println("Process: "+i);
            for(int j=0;j< events[i].length;j++){
                System.out.print("[");
                for(int k=0;k<np;k++)
                    System.out.print(events[i][j].get(k)+" ");
                System.out.print("]");
            }
            System.out.println("");
        }
    }
    
    private static void func(int k,int z){ //initially, k=0 and z=0
        for(int i=k;i<np;i++){
            for(int j=z;j<ts[i].length;j++){
                System.out.println("");
                //System.out.println("i: "+i+" j: "+j);
                if(ts[i][j].equals("E") && flag[i][j]==0 ){
                    if(j==0)
                       events[i][j].set(i,1);
                    else{
                       for(int l=0;l<events[i][j].size();l++)
                            events[i][j].set(l, events[i][j-1].get(l));
                       events[i][j].set(i,events[i][j-1].get(i)+1);
                    }
                    flag[i][j]=1;
                    System.out.println("Process "+i+": ");
                    for(int g=0;g< events[i][j].size();g++)
                        System.out.print(events[i][j].get(g)+" ");
                }
                else if(ts[i][j].contains("S") && flag[i][j]==0 ){
                    if(j==0)
                       events[i][j].set(i,1);
                    else{
                       for(int l=0;l<events[i][j].size();l++)
                            events[i][j].set(l, events[i][j-1].get(l));
                       events[i][j].set(i,events[i][j-1].get(i)+1);
                    }
                    String key = i+ts[i][j];
                    if(hmap.containsKey(key)){
                       ArrayList<ArrayList<Integer>> aal = hmap.get(key);
                       aal.add(events[i][j]);
                       hmap.replace(key, aal);
                       ArrayList<Integer> flag_aal = flag_map.get(key);
                       flag_aal.add(0);
                       flag_map.replace(key, flag_aal);
                    }
                    else{
                       hmap.put(key,new ArrayList<>());
                       hmap.get(key).add(events[i][j]);
                       flag_map.put(key,new ArrayList<>());
                       flag_map.get(key).add(0);
                   }
                   flag[i][j]=1;
                   System.out.println("Process "+i+": ");
                   for(int g=0;g< events[i][j].size();g++)
                        System.out.print(events[i][j].get(g)+" ");
                }
                else if(ts[i][j].contains("R") && flag[i][j]==0 ){
                    char num = ts[i][j].charAt(1);
                    String key = null;
                    if(ts[i][j].length()==3)
                        key = num+"S"+i+ ts[i][j].charAt(2);
                    else
                        key = num+"S"+i;
                    int idx=-1;
                    if(j!=0){
                        for(int l=0;l<events[i][j].size();l++)
                            events[i][j].set(l, events[i][j-1].get(l));
                    }
                    if(hmap.containsKey(key)){
                        if(ts[i][j].length()==3 && j!=0){
                            int real_j = j;
                            while(ts[i][real_j-1].contains("R") && ts[i][real_j-1].length()==3){
                                real_j--;
                            }
                            real_j--;
                            ArrayList<Integer> treasure = events[i][real_j];
                            for(int l=0;l<events[i][j].size();l++)
                                events[i][j].set(l, events[i][real_j].get(l));
                            events[i][j].set(i,events[i][real_j].get(i)+1);
                            for(int p=0;p<flag_map.get(key).size();p++){
                                if(flag_map.get(key).get(p)==0){
                                    idx = p;
                                    //System.out.println("j: "+j+" idx:"+idx);
                                    break;
                                }
                            }
                            if(idx==-1){
                                func(Character.getNumericValue(num),0);
                                return;
                            }
                            else{
                                for(int l=0;l<events[i][j].size();l++){
                                    events[i][j].set(l, Math.max(events[i][j].get(l),hmap.get(key).get(idx).get(l)));
                                }
                                flag_map.get(key).set(idx,1);
                                flag[i][j]=1;
                            }
                            if(ts[i][j].length()==3 && ts[i][j-1].length()==3 && checkVectors(events[i][j],events[i][j-1]))
                                System.out.println("Causal vioaltion");
                            else if(ts[i][j].length()==3 && ts[i][j-1].length()==3 && !checkVectors(events[i][j],events[i][j-1]))
                                System.out.println("No Causal violation");
                        }
                        else{
                            if(j==0)
                                events[i][j].set(i,1);
                            else
                                events[i][j].set(i,events[i][j-1].get(i)+1);
                            for(int p=0;p<flag_map.get(key).size();p++){
                                if(flag_map.get(key).get(p)==0){
                                    idx = p;
                                    //System.out.println("j: "+j+" idx:"+idx);
                                    break;
                                }
                            }
                            if(idx==-1){
                                func(Character.getNumericValue(num),0);
                                return;
                            }
                            else{
                                for(int l=0;l<events[i][j].size();l++){
                                    events[i][j].set(l, Math.max(events[i][j].get(l),hmap.get(key).get(idx).get(l)));
                                }
                                flag_map.get(key).set(idx,1);
                                flag[i][j]=1;
                            }
                        }
                    }
                    else{
                        func(Character.getNumericValue(num),0);
                        if(hmap.containsKey(key)){
                            events[i][j].set(i,events[i][j-1].get(i)+1);
                            for(int p=0;p<flag_map.get(key).size();p++){
                                if(flag_map.get(key).get(p)==0){
                                    idx = p;
                                    break;
                                }
                            }
                            if(idx==-1){
                                func(Character.getNumericValue(num),0);
                                return;
                            }
                            else{
                                for(int l=0;l<events[i][j].size();l++){
                                    events[i][j].set(l, Math.max(events[i][j].get(l),hmap.get(key).get(idx).get(l)));
                                }
                                flag_map.get(key).set(idx,1);
                                flag[i][j]=1;
                            }
                        }
                    }
                    System.out.println("Process "+i+": ");
                    for(int g=0;g<events[i][j].size();g++)
                        System.out.print(events[i][j].get(g)+" ");
                }
            }
        }
    }
    
    private static boolean checkVectors(ArrayList<Integer> a, ArrayList<Integer> b){
        int i=0;
        for(i=0;i<a.size();i++){
           if(a.get(i)<=b.get(i)){
               continue;
           }else{
               return false;
           }
        }
        if(i==a.size())
            return true;
        return false;
    }
}
