import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Queue;


class dataanalyzer {

    private static final String FILENAME = "nodes_1000.csv";

    static int numberOfEdges = 0;
    static int numberOfNodes = 0;
    static int next_id = 0;
    static int diameter = 0;
    static HashMap<Integer,Node> nodes = new HashMap<>();    // nodes: keeps track of each nodes' neighbors
    static HashMap<String,Integer> hm_ids = new HashMap<>(); // hm_ids: keeps track of which strings have which integer ids associated
    static ArrayList<Integer> top5Friends = new ArrayList<>(); // A list of 5 node IDs which represent
    static ArrayList<Float> coefficientClusters = new ArrayList<>(); // A list of clustering scores
    static int sumOfPaths = 0;
    static HashMap<Integer,Float> coefficientClusteringScores = new HashMap<>();    // Index, Score
    static ArrayList<Integer> top5CoefficientClusteringScores = new ArrayList<>();    // Index

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

        numberOfNodes = nodes.size(); // Initialize numberOfNodes after we calculate the nodes of our graph

        ArrayList<ArrayList<Integer>> shortestPaths = generatePaths();
        diameter = findDiameter(shortestPaths);
        calculateClusteringCoefficient();
        calculateFriends(shortestPaths);
        topFiveCoefficientClusteringScores();
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

        System.out.println("Top 5 clustering coefficients:");
        System.out.println("1 (" + top5CoefficientClusteringScores.get(0) + ", " + nodes.get(top5CoefficientClusteringScores.get(0)).og_url + " , " + coefficientClusteringScores.get(top5CoefficientClusteringScores.get(0)) + ")");
        System.out.println("2 (" + top5CoefficientClusteringScores.get(1) + ", " + nodes.get(top5CoefficientClusteringScores.get(1)).og_url + " , " + coefficientClusteringScores.get(top5CoefficientClusteringScores.get(1)) + ")");
        System.out.println("3 (" + top5CoefficientClusteringScores.get(2) + ", " + nodes.get(top5CoefficientClusteringScores.get(2)).og_url + " , " + coefficientClusteringScores.get(top5CoefficientClusteringScores.get(2)) + ")");
        System.out.println("4 (" + top5CoefficientClusteringScores.get(3) + ", " + nodes.get(top5CoefficientClusteringScores.get(3)).og_url + " , " + coefficientClusteringScores.get(top5CoefficientClusteringScores.get(3)) + ")");
        System.out.println("5 (" + top5CoefficientClusteringScores.get(4) + ", " + nodes.get(top5CoefficientClusteringScores.get(4)).og_url + " , " + coefficientClusteringScores.get(top5CoefficientClusteringScores.get(4)) + ")");
        System.out.println();
        System.out.println("Top 5 Friends:");
        System.out.println("1 (" + top5Friends.get(0) + ", " + nodes.get(top5Friends.get(0)).og_url + " , " + nodes.get(top5Friends.get(0)).friends + ")");
        System.out.println("2 (" + top5Friends.get(1) + ", " + nodes.get(top5Friends.get(1)).og_url + " , " + nodes.get(top5Friends.get(1)).friends + ")");
        System.out.println("3 (" + top5Friends.get(2) + ", " + nodes.get(top5Friends.get(2)).og_url + " , " + nodes.get(top5Friends.get(2)).friends + ")");
        System.out.println("4 (" + top5Friends.get(3) + ", " + nodes.get(top5Friends.get(3)).og_url + " , " + nodes.get(top5Friends.get(3)).friends + ")");
        System.out.println("5 (" + top5Friends.get(4) + ", " + nodes.get(top5Friends.get(4)).og_url + " , " + nodes.get(top5Friends.get(4)).friends + ")");
    
    
    
    }

    // We find the diameter by finding the maximum size of any of our
    // shortest paths we generated
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

    // Rank the top five clustering scores
    public static void topFiveCoefficientClusteringScores() {
        while(top5CoefficientClusteringScores.size() < 5){
            int node = -1;
            float maxFriend = -1.0f;

            for(int i = 0; i < nodes.size(); i++){
                if(coefficientClusteringScores.get(i) > maxFriend && !top5CoefficientClusteringScores.contains(i)){
                    maxFriend = coefficientClusteringScores.get(i);
                    node = i;
                }
            }

            top5CoefficientClusteringScores.add(node);
        }
    }

    // We find the clustering coefficient
    public static void calculateClusteringCoefficient() {
        for (int i = 0; i < nodes.size(); i++) {
            ArrayList<Integer> neighbors = nodes.get(i).neighbors;
            float denom = 1.0f; 
            float numer = 0.0f;
            float score = 0.0f;
            if (neighbors.size() == 1) {
                coefficientClusters.add(score);
            } else {
                denom = (neighbors.size() * (neighbors.size() - 1) / 2);
                ArrayList<Integer> alreadyChecked = new ArrayList<>();
                for (int j : neighbors) {
                    ArrayList<Integer> secondaryNeighbors = nodes.get(j).neighbors;
                    alreadyChecked.add(j);
                    for (int q : neighbors) {
                        if (secondaryNeighbors.contains(q) && !alreadyChecked.contains(q)) {
                            numer = numer + 1.0f;
                        }
                        }
                    }
                }
                score = numer / denom;
                coefficientClusteringScores.put(i, score);
            }
        }

    // We must generate a list of the shortest paths between any two nodes in our
    // graph using a Breadth-First-Search algorithm
    public static ArrayList<ArrayList<Integer>> generatePaths(){
        ArrayList<ArrayList<Integer>> pathsList = new ArrayList<>();
        HashMap<Integer,ArrayList<Integer>> alreadyVisited = new HashMap<>();
        for(int i = 0; i < numberOfNodes; i++){
            ArrayList<Integer> temp = new ArrayList<>();
            alreadyVisited.put(i, temp);
        }

        for(int i = 0; i < numberOfNodes; i++){
            for(int j = 0; j < numberOfNodes; j++){
                if(i != j && !alreadyVisited.get(i).contains(j) && !alreadyVisited.get(j).contains(i)){
                    ArrayList<Integer> path = bfs(i, j);
                    pathsList.add(path);
                    alreadyVisited.get(i).add(j);
                }
            }
        }

        return pathsList;
    }

    //Here is the BFS algorithm function, which calls two helper functions
    public static ArrayList<Integer> bfs(int start, int end){
        ArrayList<Integer> prev = bfs_solve(start);

        return bfs_reconstruct(start, end, prev);
    }


    // Helper function 1: generate a list of size n (numberOfNodes)
    //    -- Each index represents the node ID in our nodes HashMap
    //    -- Each entry will store the index of whichever node points to it in our
    //       supposed shortest path
    public static ArrayList<Integer> bfs_solve(int start){
        //Use a queue to keep track of which nodes still need to be searched
        Queue<Integer> q = new PriorityQueue<Integer>();
        q.add(start);

        ArrayList<Boolean> visited = new ArrayList<>();
        ArrayList<Integer> prev = new ArrayList<>();

        //Initializing each ArrayList
        for(int i = 0; i < numberOfNodes; i++){
            visited.add(false);
            prev.add(null);
        }
        visited.set(start, true); //Our starting node is obviously already visited, so set it as so

        while(!q.isEmpty()){
            //for each node in our queue, we get its neighbors, check if any of them have been visited,
            //add them to our queue to be searched later, and set its neighbor to point at our current node
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
        //prev will contain our shortest path, so we send it back for our second helper function
        return prev;
    }

    // Helper function 2: Construct an ArrayList containing our final path from start to end
    //    -- We work backwards from the end of the list, tracing the indices back to start
    public static ArrayList<Integer> bfs_reconstruct(int start, int end, ArrayList<Integer> prev){
        ArrayList<Integer> path = new ArrayList<>();
        for(int at = end; prev.get(at) != null; at = prev.get(at)){
            path.add(at);
        }

        //If our path points back to the start successfully, then return the path
        if(prev.get(path.get(path.size()-1)) == start){
            return path;
        }

        //If not, then we return an empty list
        ArrayList<Integer> empty = new ArrayList<>();
        return empty;
    }

    // Here we take our list of shortest paths, iterate through each node ID and see how many
    // times a given node acts as a bridge in a path.  This number will be the number of friends.
    public static void calculateFriends(ArrayList<ArrayList<Integer>> list){
        for(int i = 0; i < nodes.size(); i++){
            Node node_to_find = nodes.get(i);

            //Iterate through list of paths
            for(ArrayList<Integer> path : list){

                //We don't want to count if the node is pointing to itself (not a bridge)
                if(path.contains(i) && path.get(0) != i){
                    node_to_find.friends += 1.0f;
                }
            }

        }
    }

    // Similar to finding the diameter, we calculate the node with the maximum friend number.
    // Since we are looking for 5 numbers, we can repeat this process 5 times, removing nodes that
    // are already in our top 5 list
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
