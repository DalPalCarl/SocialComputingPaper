import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;


class dataanalyzer {

    private static final String FILENAME = "nodes_100.csv";

    static int numberOfEdges = 0;
    static int numberOfNodes = 0;
    static int next_id = 0;
    static int diameter = 0;
    static HashMap<Integer,Node> nodes = new HashMap<>();    // nodes: keeps track of each nodes' neighbors
    static HashMap<String,Integer> hm_ids = new HashMap<>(); // hm_ids: keeps track of which strings have which integer ids associated
    static ArrayList<Integer> top5Friends = new ArrayList<>();
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

        numberOfNodes = nodes.size();

        ArrayList<ArrayList<Integer>> shortestPaths = generatePaths();
        diameter = findDiameter(shortestPaths);
        calculateFriends(shortestPaths);
        topFiveFriends();
        printScore();
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
        System.out.print(numberOfNodes + " nodes, ");
        System.out.print(numberOfEdges + " edges, ");
        System.out.print("directed\n");
        System.out.println("Number of Components: 1");
        System.out.println("Density: " + numberOfEdges / (numberOfNodes * (numberOfNodes - 1.0)));
        System.out.println("Diameter: " + diameter + "\n");

        System.out.println("Top 5 Friends:");
        System.out.println("1 (" + top5Friends.get(0) + ", " + nodes.get(top5Friends.get(0)).og_url + " , " + nodes.get(top5Friends.get(0)).friends + ")");
        System.out.println("2 (" + top5Friends.get(1) + ", " + nodes.get(top5Friends.get(1)).og_url + " , " + nodes.get(top5Friends.get(1)).friends + ")");
        System.out.println("3 (" + top5Friends.get(2) + ", " + nodes.get(top5Friends.get(2)).og_url + " , " + nodes.get(top5Friends.get(2)).friends + ")");
        System.out.println("4 (" + top5Friends.get(3) + ", " + nodes.get(top5Friends.get(3)).og_url + " , " + nodes.get(top5Friends.get(3)).friends + ")");
        System.out.println("5 (" + top5Friends.get(4) + ", " + nodes.get(top5Friends.get(4)).og_url + " , " + nodes.get(top5Friends.get(4)).friends + ")");
    
    
    
    }

    // We calculate diameter by recursively searching the depth of the graph,
    // since everything stems from the main domain.
    public static int findDiameter(ArrayList<ArrayList<Integer>> list){
        int maxLength = 0;
        for(int i = 0; i < list.size(); i++){
            ArrayList<Integer> temp = list.get(i);
            if(temp.size() > maxLength){
                maxLength = temp.size();
            }
        }
        return maxLength;
    }

    public static ArrayList<ArrayList<Integer>> generatePaths(){
        ArrayList<ArrayList<Integer>> pathsList = new ArrayList<>();

        for(int i = 0; i < numberOfNodes; i++){
            for(int j = 0; j < numberOfNodes; j++){
                if(i != j){
                    ArrayList<Integer> path = bfs(i, j);
                    pathsList.add(path);
                }
            }
        }

        return pathsList;
    }

    public static ArrayList<Integer> bfs(int start, int end){
        ArrayList<Integer> prev = bfs_solve(start);

        return bfs_reconstruct(start, end, prev);
    }


    public static ArrayList<Integer> bfs_solve(int start){
        Queue<Integer> q = new PriorityQueue<Integer>();
        q.add(start);

        ArrayList<Boolean> visited = new ArrayList<>();
        ArrayList<Integer> prev = new ArrayList<>();
        for(int i = 0; i < numberOfNodes; i++){
            visited.add(false);
            prev.add(null);
        }
        visited.set(start, true);

        while(!q.isEmpty()){
            int node = q.remove();
            ArrayList<Integer> neighbs = nodes.get(node).neighbors;

            for(int j = 0; j < neighbs.size(); j++){
                if(!visited.get(neighbs.get(j))){
                    q.add(neighbs.get(j));
                    visited.set(neighbs.get(j), true);
                    prev.set(neighbs.get(j), node);
                }
            }
        }

        return prev;
    }

    public static ArrayList<Integer> bfs_reconstruct(int start, int end, ArrayList<Integer> prev){
        ArrayList<Integer> path = new ArrayList<>();
        for(int at = end; prev.get(at) != null; at = prev.get(at)){
            path.add(at);
        }

        if(prev.get(path.get(path.size()-1)) == start){
            return path;
        }
        ArrayList<Integer> empty = new ArrayList<>();
        return empty;
    }

    public static void calculateFriends(ArrayList<ArrayList<Integer>> list){
        for(int i = 0; i < nodes.size(); i++){
            Node node_to_find = nodes.get(i);

            for(ArrayList<Integer> path : list){
                if(path.contains(i)){
                    node_to_find.friends += 1.0f;
                }
            }

        }
    }

    public static void topFiveFriends(){
        while(top5Friends.size() < 5){
            int node = -1;
            float maxFriend = -1.0f;

            for(int i = 0; i < nodes.size(); i++){
                if(nodes.get(i).friends > maxFriend && !top5Friends.contains(i)){
                    maxFriend = nodes.get(i).friends;
                    node = i;
                }
            }

            top5Friends.add(node);

        }
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

    public void Print(int id){
        System.out.println("Node ID: " + id);
        System.out.println("URL: " + og_url);
        System.out.println("Friends (Betweenness Centrality: " + friends);
        System.out.println("List of Neighbors: " + neighbors.toString() + "\n");
    }
}
