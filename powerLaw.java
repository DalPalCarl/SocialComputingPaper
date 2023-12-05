import java.util.HashMap;
import java.util.Random;

/*
 * Authors: Dallas Carlson, Elliot Fackler
 */
class powerLaw {

    static int nodes = 0;
    static int edges = 0;
    static HashMap<Integer,int[]> hm1 = new HashMap<>();    // hashmap 1: keeps track of each nodes' neighbors
    static HashMap<Integer, Integer> hm2 = new HashMap<>(); // hashmap 2: tracks the number of connections each node has
    static HashMap<Integer, Integer> hm3 = new HashMap<>(); // hashmap 3: tracks the number of nodes each degree count has
    static int[][] randomEdges = new int[1][2];

    /*
    * Pseudocode:
    * 
    * Use 2 dictionaries
    * 
    * 
    */



/* 2 Hashmaps - One to keep track of the list of neighboring nodes, One to track num of edges */

    public static void step(){

        /* Here we will add a new node to the graph.  We accomplish this by:
         *    - looping through the existing nodes, comparing degrees to find the highest
         *      based on the probability p = (degree(u))/ |E| + |V|
         * 
         * 
         * 
         * Considerations:
         *  - When more than one node has the same degree value
         */

        //Initialize the graph with one node
        if (nodes == 0){
            int[] temp = new int[1];
            hm1.put(nodes, temp);
            hm2.put(nodes,0);
            nodes += 1;
            return;
        }

        else if (nodes == 1){
            //creates a graph with two nodes, whose connections are to each other
            int[] temp = new int[1];
            int[][] tempEdge = new int[1][2];
            temp[0] = 0;
            tempEdge[0][0] = 0;
            tempEdge[0][1] = 1;
            hm1.put(nodes,temp);
            hm2.put(nodes, 1);
            hm2.put(nodes-1, 1);

            //we then append this edge to randomEdges
            randomEdges = tempEdge;
            nodes++;
            edges++;
            return;
        }
        else{
            // This chunk of code selects a random edge, and a random point in that edge, in the randomEdges array
            Random random = new Random();
            int chosen_node = randomEdges[random.nextInt(randomEdges.length)][random.nextInt(2)];

            //we then create a new array that copies the contents of the current array, as well as adding our new edge
            //between our new node and the randomly chosen one above
            int[][] newEdgesList = new int[edges+1][2];
            int index = 0;
            for(int[] i : randomEdges){ //iterate through the current array, adding each edge to our new array
                newEdgesList[index][0] = i[0];
                newEdgesList[index][1] = i[1];
                index++;
            }
            newEdgesList[edges][0] = chosen_node;
            newEdgesList[edges][1] = nodes;

            //finally we update randomEdges with our new edge
            randomEdges = newEdgesList;
            


            //Similarly, we want to update the value in hm1 of the chosen node and the new node to include each other
            int length = hm2.get(chosen_node);
            int[] hm1_list = new int[length+1];
            for(int i = 0; i < length + 1; i++){
    
                if(i == length || length == 0){// i == length: index of the node to add
                                                // i == 0: hm1 of the new node; includes only our chosen node, since it is its first connection
                    hm1_list[i] = nodes;
                }
                else{
                    hm1_list[i] = hm1.get(chosen_node)[i];
                }
                
            }

            // here we initialize the array for the new node's hm1 value, and update our chosen node's value
            int[] temp = new int[1];
            temp[0] = chosen_node;

            hm1.put(chosen_node, hm1_list);
            hm1.put(nodes, temp);

            //we update the degree count of each node
            hm2.put(chosen_node, hm2.get(chosen_node) + 1);
            hm2.put(nodes, 1);

            nodes += 1;
            edges += 1;
        }
        }
        

    public static void main(String[] args) {
        int steps = 25000;
        int iter = steps;
        while (iter > 0){
            step();
            iter -= 1;
        }

        //this takes the maximum degree count of our graph; that way we can iterate through only the degree counts that are necessary
        int maximum = 0;
        for(int i : hm2.values()){
            if(i > maximum){
                maximum = i;
            }
        }
        for(int i = 1; i < maximum; i++){
            int numberofnodes = 0;
            for(int j : hm2.values()){
                if(j == i){
                    numberofnodes++; // counts the number of nodes with degree number of i
                }
            }
            hm3.put(i, numberofnodes);
        }

        System.out.println("degree,numnodes");
        for(int i : hm3.keySet()){
            if(hm3.get(i) != 0){
                System.out.println(i + "," + hm3.get(i)); // outputs the log
            }
        }
    }
}