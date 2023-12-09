import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;  


class dataanalyzer {

    private static final String FILENAME = "nodes_1000.csv";

    static int numberOfEdges = 0;
    static int numberOfNodes = 0;
    static int next_id = 0;
    static HashMap<Integer,Node> nodes = new HashMap<>();    // nodes: keeps track of each nodes' neighbors
    static HashMap<String,Integer> hm_ids = new HashMap<>(); // hm_ids: keeps track of which strings have which integer ids associated

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
                parseInput(code[0], code[1]);
                numberOfEdges = numberOfEdges + 1;
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

        //Here's where the magic happens: We want to walk through each iteration
        //to update the scores.  The algorithm converges when either 1) we go through
        // 100 iterations or 2) the difference between the old score and the new score 
        //has an error margin less than 0.01

        printScore();
        /*
        while(step <= 100){
            System.out.println("Step " + step + ":\n");
            iterate();

            for(Enumeration<Integer> E = pages.keys(); E.hasMoreElements();){
                int i = E.nextElement();
                pages.get(i).printPageRank(i);
            }
            step++;
        }
        */
    }

    public static void parseInput(String url1, String url2){

        // If the link is not present in the hashTable.

        if (hm_ids.get(url1) == null){
            hm_ids.put(url1, next_id);
            //If there is no id for a link, then it does not exist in our nodes hm either
            Node valueData = new Node(url1);
            nodes.put(next_id, valueData);
            next_id += 1;
        }

        //In case we need to add the second link as well
        if(hm_ids.get(url2) == null){
            hm_ids.put(url2, next_id);
            //If there is no id for a link, then it does not exist in our nodes hm either
            Node valueData = new Node(url2);
            nodes.put(next_id, valueData);
            next_id += 1;
        }

        // If both links are present in hm_ids, both links are neighbors to each other
        int link1 = hm_ids.get(url1);
        int link2 = hm_ids.get(url2);

        nodes.get(link1).AddNeighbor(link2);
        nodes.get(link2).AddNeighbor(link1);
    }

    // Search for diameter and average path length at same time.

    public static void printScore(){
        numberOfNodes = nodes.size();
        System.out.print(numberOfNodes + " nodes, ");
        System.out.print(numberOfEdges + " edges, ");
        System.out.print("undirected\n");
        System.out.println("Number of Components: 1");
        System.out.println("Density: " + (2.0 * numberOfEdges) / (numberOfNodes * (numberOfNodes - 1.0)));
    }
/*
    public static void iterate(){

        //First, we need to reset each node's new score to 0, so that we can accumulate the next
        //iteration's score properly

        for(Enumeration<Integer> F = pages.keys(); F.hasMoreElements();){
            int i = F.nextElement();
            pages.get(i).newValue = 0.0f;
        }
        
        //handleSinks();

        //We then iterate through the list of keys to get their list of nodes they are pointed to
        //taking into account their size as well
        for(Enumeration<Integer> E = pages.keys(); E.hasMoreElements();){
            int i = E.nextElement();
            ArrayList outDegreeNodes = pages.get(i).edgeTo;
            int arraySize = outDegreeNodes.size();

            //We iterate through the list of nodes B that the current node A is pointing to.
            //For each node B in this list, distribute A's score divided by the number of nodes B in the array
            for(int j = 0; j < arraySize; j++){
                float scoreOfNode = pages.get(i).oldValue;
                pages.get(outDegreeNodes.get(j)).newValue += scoreOfNode/arraySize;
            }
        }

        //After we calculate all of this, we take each key and update their current score to
        //the new score, applying our damping factor to this
        boolean isOverMargin = false;
        for(Enumeration<Integer> K = pages.keys(); K.hasMoreElements();){
            int i = K.nextElement();
            PageRank node = pages.get(i);
            float temp = Float.valueOf(String.format("%.2f", damping + (d * node.newValue)));
            if(Math.abs(node.oldValue - temp) >= MARGIN){
                isOverMargin = true;
            }
            node.oldValue = temp;
        }
    }
    */
}

class Node {
    String og_url;
    ArrayList<Integer> neighbors;

    public Node(String link){
        og_url = link;
        neighbors = new ArrayList<>();    }

    public void AddNeighbor(int url_id){
        neighbors.add(url_id);
    }
}
