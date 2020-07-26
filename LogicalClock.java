
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class LogicalClock {
    static HashMap<String,ArrayList<Integer>> hmap = new LinkedHashMap<>();
    static HashMap<String,ArrayList<Integer>> flag_map = new LinkedHashMap<>();
    static int np = 4; //number of processes
    static int error_flag=0;
//    static String ts[][] = {{""},
//            {"","E","S2","S3","S4","E"},
//            {"","R1","E","S3","E"},
//            {"","S4","E","R1","R2","E","S4","R4","E"},
//            {"","E","R3","E","R1","R3","E","S3","E"}};
    static String ts[][] = {{""},
            {"","E","S2","S3","R4","E"},
            {"","R1","E","S3","E"},
            {"","S4","E","R1","R2","E","S4","R4","E"},
            {"","E","R3","E","S1","R3","E","S3","E"}};
//        static String ts[][] = {{""},
//            {"","E","S2","E","S4","E"},
//            {"","R1","E","S3a","S3b","E"},
//            {"","S4","E","R2b","R2a","E","S4","R4","E"},
//            {"","E","R3","E","S1","R3","E","S3","E"}};
    static int events[][]= new int[np+1][];
    public static void main(String[] args) {
        for(int i=1;i<np+1;i++){
            events[i] = new int[ts[i].length];
        }
        func(1,1);
        if(error_flag==0){
            for(int i=1;i<np+1;i++){
                for(int j=1;j<ts[i].length;j++){
                    System.out.print(events[i][j]+" ");
                }
                System.out.println("");
            }
        }
    }
    
    private static void func(int k,int z){ //initially, k=1 and z=1
        for(int i=k;i<np+1;i++){
            for(int j=z;j<ts[i].length;j++){
                System.out.println(i+" "+j);
                if(ts[i][j].equals("E") && events[i][j]==0){
                    events[i][j] = events[i][j-1]+1;
                    System.out.println(events[i][j]);
                }
                else if(ts[i][j].contains("S") && events[i][j]==0){
                    events[i][j] = events[i][j-1]+1;
                    String skey = i+ts[i][j];
                    if(hmap.containsKey(skey)){
                        ArrayList<Integer> list =hmap.get(skey);
                        list.add(events[i][j]);
                        hmap.replace(skey, list);
                        ArrayList<Integer> flaglist = flag_map.get(skey);
                        flaglist.add(0);
                        flag_map.replace(skey, flaglist);
                    }
                    else{
                        hmap.put(skey,new ArrayList<>());
                        hmap.get(skey).add(events[i][j]);
                        flag_map.put(skey,new ArrayList<>());
                        flag_map.get(skey).add(0);
                    }
                    System.out.println(events[i][j]);
                }
                else if(ts[i][j].contains("R") && events[i][j]==0){
                    char num = ts[i][j].charAt(1);
                    String key = null;
                    if(ts[i][j].length()==3)
                        key = num+"S"+i+ ts[i][j].charAt(2);
                    else
                        key = num+"S"+i;
                    int idx=0;
                    if(hmap.containsKey(key)){
                        if(ts[i][j].length()==3){
                            error_flag=checkOrder();
                            return;
                        }
                        for(int p=0;p<flag_map.get(key).size();p++){
                            if(flag_map.get(key).get(p)==0){
                                idx = p;
                                break;
                            }
                        }
                        events[i][j] = Math.max(events[i][j-1],hmap.get(key).get(idx))+1;
                        flag_map.get(key).set(idx,1);
                        System.out.println("r if: "+events[i][j]);
                    }
                    else{
                        func(Character.getNumericValue(num),1);
                        if(hmap.containsKey(key)){
                            for(int p=0;p<flag_map.get(key).size();p++){
                                if(flag_map.get(key).get(p)==0){
                                    idx = p;
                                    break;
                                }
                            }
                            events[i][j] = Math.max(events[i][j-1],hmap.get(key).get(idx))+1;
                            flag_map.get(key).set(idx,1);
                            System.out.println("r else: "+i+" "+j+" "+events[i][j]);
                        }
                    }
                }
                //if(i!=1 && i!=2 && j==ts[i].length-1)
                       // return;
            }
        }
    }
    
    private static int checkOrder(){
        for(Map.Entry elem:hmap.entrySet()){
            for(Map.Entry elem2:hmap.entrySet()){
                String a = (String)elem.getKey();
                String b = (String)elem2.getKey();
                if(!a.equals(b) && a.charAt(0)==b.charAt(0) && a.charAt(1)==b.charAt(1) && a.charAt(2)==b.charAt(2)){
                    if(a.charAt(3) < b.charAt(3)){
                        System.out.println("error");
                        return 1;
                    }
                }
            }
        }
        return 0;
    }
}
