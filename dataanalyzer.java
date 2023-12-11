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
    static ArrayList<Integer> alreadyTracked = new ArrayList();

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
        findDiameter(nodes.get(0), 0);
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

    /*
     * Prints the important data out.
     */
    public static void printScore(){
        numberOfNodes = nodes.size();
        System.out.print(numberOfNodes + " nodes, ");
        System.out.print(numberOfEdges + " edges, ");
        System.out.print("undirected\n");
        System.out.println("Number of Components: 1");
        System.out.println("Density: " + (2.0 * numberOfEdges) / (numberOfNodes * (numberOfNodes - 1.0)));
    }

    public static Integer findDiameter(Node domain, int maxDepth){
        for (int i = 0; i < domain.neighbors.size(); i++) {
            if(!(alreadyTracked.contains(domain.neighbors[i]))){
                if (findDiameter(domain.neighbors[i], maxDepth + 1) > maxDepth) {
                    maxDepth = findDiameter(nodes.get(domain.neighbors[i]), maxDepth + 1);
                }
            }
        }
        return maxDepth;
    }
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
