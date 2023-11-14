import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;  


class rankpage {
    
    private static final String FILENAME = "polblogs.csv";
    private static final float D = 0.85f;

    static int numberOfNodes = 0;
    static Enumeration<Integer> keyList;
    static Dictionary<Integer, PageRank> pages = new Hashtable<Integer, PageRank>();

    /**
     * @param args
     */
    public static void main(String[] args) {
        
        String line = "";  
        String splitBy = ",";
        BufferedReader reader = null;
        try{
            reader = new BufferedReader(new FileReader(FILENAME));
            while ((line = reader.readLine()) != null)    {  
                String[] code = line.split(splitBy);    // use comma as separator
                parseInput(Integer.parseInt(code[0]), Integer.parseInt(code[2]));
            }

        
        } catch (IOException e){
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e){
                e.printStackTrace();
            }
        }

        initializeScore();

            // ( (1 - d)/ n ) + d * (sum of each page P's Pagerank: current page rank / outbound links)

            // output
    }

    public static void parseInput(int node1, int node2){

        // If the outgoing link is not present in the hashTable.
        if (pages.get(node1) == null){
            PageRank valueData = new PageRank(node2);
            pages.put(node1, valueData);
        } 
        // If the outgoing link is present
        else{
            pages.get(node1).AddNode(node2);
        }
    }

    public static void initializeScore(){
        keyList = pages.keys();
        numberOfNodes = pages.size();
        while(keyList.hasMoreElements()){
            int i = keyList.nextElement();
            pages.get(i).SetInitialScore(numberOfNodes);
        }
    }

    public static void iterate(){
        while(keyList.hasMoreElements()){
            int i = keyList.nextElement();
            int[] outDegreeNodes = pages.get(i).edgeTo;
            for(int j = 0; j< outDegreeNodes.length; j++){
                float scoreOfNode = pages.get(outDegreeNodes[j]).oldValue;
                pages.get(i).newValue += scoreOfNode;
            }
        }
    }
}

class PageRank {
    float oldValue;
    float newValue;
    ArrayList edgeTo;

    public PageRank(int pointsTo){
        newValue = 0.0f;
        edgeTo = new ArrayList();
        edgeTo.add(pointsTo);
    }

    public void AddNode(int node){
        edgeTo.add(node);
    }

    public void SetInitialScore(int n){
        oldValue = 1/n;
    }
}
