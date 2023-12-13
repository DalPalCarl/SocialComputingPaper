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

    private static final String FILENAME = "nodes_100.csv";

    static int numberOfEdges = 0;
    static int numberOfNodes = 0;
    static int next_id = 0;
    static int diameter = 0;
    static HashMap<Integer,Node> nodes = new HashMap<>();    // nodes: keeps track of each nodes' neighbors
    static HashMap<String,Integer> hm_ids = new HashMap<>(); // hm_ids: keeps track of which strings have which integer ids associated
    static ArrayList<Integer> alreadyVisited = new ArrayList<>();

    static int sumOfPaths = 0;

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
        alreadyVisited.add(0);
        diameter = calc_diameter(0, 0);
        printScore();
        generatePaths();
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

    public static void printScore(){
        numberOfNodes = nodes.size();
        System.out.print(numberOfNodes + " nodes, ");
        System.out.print(numberOfEdges + " edges, ");
        System.out.print("directed\n");
        System.out.println("Number of Components: 1");
        System.out.println("Density: " + (2.0 * numberOfEdges) / (numberOfNodes * (numberOfNodes - 1.0)));
        System.out.println("Diameter: " + diameter);
    }

    // We calculate diameter by recursively searching the depth of the graph,
    // since everything stems from the main domain.
    public static int calc_diameter(int link_id, int maxDepth){

        boolean noMoreNodes = true; // Set a flag to check if there are no more nodes that aren't already visited
        Node sampleNode = nodes.get(link_id);

        //iterate through each node's list of neighbors
        for(int i = 0; i < sampleNode.neighbors.size(); i++){
            if(!alreadyVisited.contains(sampleNode.neighbors.get(i))){//not already visited
                noMoreNodes = false;
                alreadyVisited.add(sampleNode.neighbors.get(i));
                return calc_diameter(sampleNode.neighbors.get(i), maxDepth+1);
            }
        }
        if(noMoreNodes){
            return maxDepth;
        }
        else{
            return 0;
        }
    }

    public static ArrayList<ArrayList<Integer>> generatePaths(){
        ArrayList<ArrayList<Integer>> pathsList = new ArrayList<>();

        for(int i = 0; i < numberOfNodes; i++){
            for(int j = 0; j < numberOfNodes; j++){
                if(i != j){
                    ArrayList<Integer> temp = new ArrayList<>();
                    ArrayList<Integer> path = findPath(temp, i, j);
                    System.out.print("[");
                    for(int z = 0; z < path.size(); z++){
                        System.out.print(path.get(z));
                    }
                    System.out.print("]");
                    sumOfPaths += path.size();
                    pathsList.add(path);
                }
            }
        }

        return pathsList;
    }

    public static ArrayList<Integer> findPath(ArrayList<Integer> path, int start, int end){
        Node sampleNode = nodes.get(start);
        if(sampleNode.neighbors.contains(end)){
            path.add(end);
            return path;    
        }
        else{
            //iterate through each node's list of neighbors
            for(int i = 0; i < sampleNode.neighbors.size(); i++){
                int newPoint = sampleNode.neighbors.get(i);
                path.add(newPoint);
                return findPath(path, newPoint, end);
            }
        }
        return null;
    }
}

class Node {
    String og_url;
    float friends = 0.0f;
    ArrayList<Integer> neighbors;

    public Node(String link){
        og_url = link;
        neighbors = new ArrayList<>();    
    }

    public void AddNeighbor(int url_id){
        neighbors.add(url_id);
    }

    public void AddFriend(){
        friends += 1.0;
    }
}