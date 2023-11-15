import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;  


class rankpage {
    
    private static final String FILENAME = "example.csv";
    private static final float D = 0.85f;

    static int numberOfNodes = 0;
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
        for(Enumeration<Integer> E = pages.keys(); E.hasMoreElements();){
            int i = E.nextElement();
            pages.get(i).printPageRank(i);
        }

        iterate();
        System.out.println("First Iteration:\n");

        for(Enumeration<Integer> E = pages.keys(); E.hasMoreElements();){
            int i = E.nextElement();
            pages.get(i).printPageRank(i);
        }

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
        numberOfNodes = pages.size();
        for(Enumeration<Integer> E = pages.keys(); E.hasMoreElements();){
            int i = E.nextElement();
            float f = 1.0f / numberOfNodes;
            pages.get(i).SetInitialScore(f);
        }
    }

    public static void iterate(){
        for(Enumeration<Integer> E = pages.keys(); E.hasMoreElements();){
            int i = E.nextElement();
            ArrayList outDegreeNodes = pages.get(i).edgeTo;
            int arraySize = outDegreeNodes.size();

            //We iterate through the list of nodes that the current node is pointing to.
            //
            for(int j = 0; j < arraySize; j++){
                Object node = outDegreeNodes.get(j);
                float scoreOfNode = pages.get(node).oldValue;
                pages.get(node).newValue += scoreOfNode/arraySize;
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

    public void SetInitialScore(float n){
        oldValue = n;
    }

    public void printPageRank(int key){
        System.out.println("Key: " + key + " -- {oldValue : " + oldValue +
         " | newValue : " + newValue + " | Edges to : " + edgeTo.toString() + "}");
    }
}
