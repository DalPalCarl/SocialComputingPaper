import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Iterator;  


class rankpage {
    
    private static final String FILENAME = "polblogs.csv";
    private static final float d = 0.85f;
    private static final float MARGIN = 0.01f;

    static int numberOfNodes = 0;
    static ArrayList sinks = new ArrayList<>();
    static float damping;
    static boolean flag;
    static Dictionary<String, PageRank> pages = new Hashtable<String, PageRank>();

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
                parseInput(code[0], code[2]);
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
       
        //First, we set all scores to be the same
        //We also want to set our global damping variable, since we have
        //calculated the total number of nodes after parsing the input

        flag = true;
        initializeScore();
        damping = (1.0f - d)/numberOfNodes;

        //Then, we want to curate a list of sinks (nodes with no out degrees)
        //So we don't have to keep calculating them in the future
        for(Enumeration<String> E = pages.keys(); E.hasMoreElements();){
            String i = E.nextElement();
            for(int j = 0; j < pages.get(i).edgeTo.size(); j++){
                if(pages.get(pages.get(i).edgeTo.get(j)) == null){
                    sinks.add(j);
                }
            }
        }

        

        //Here's where the magic happens: We want to walk through each iteration
        //to update the scores.  The algorithm converges when either 1) we go through
        // 100 iterations or 2) the difference between the old score and the new score 
        //has an error margin less than 0.01

        for(Enumeration<String> E = pages.keys(); E.hasMoreElements();){
            String i = E.nextElement();
            //pages.get(i).printScores(i);
        }

        int step = 1;
        while(step <= 100 && flag){
            System.out.println("Step " + step + ":\n");
            iterate();
            for(Enumeration<String> E = pages.keys(); E.hasMoreElements();){
                String i = E.nextElement();
                pages.get(i).printScores(i);
            }
            step++;
        }
        
        
        System.out.println(damping);
        System.out.println(sinks.toString());
    }

    public static void parseInput(String node1, String node2){

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
        for(Enumeration<String> E = pages.keys(); E.hasMoreElements();){
            String i = E.nextElement();
            float f = 1.0f / numberOfNodes;
            pages.get(i).SetInitialScore(f);
        }
    }

    public static void iterate(){
        
        handleSinks();

        //We then iterate through the list of keys to get their list of nodes they are pointed to
        //taking into account their size as well
        for(Enumeration<String> E = pages.keys(); E.hasMoreElements();){
            String i = E.nextElement();
            ArrayList outDegreeNodes = pages.get(i).edgeTo;
            int arraySize = outDegreeNodes.size();

            if(sinks.contains(i)){
                continue;
            }

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
        for(Enumeration<String> K = pages.keys(); K.hasMoreElements();){
            String i = K.nextElement();
            PageRank node = pages.get(i);
            float temp = damping + (d * node.newValue);

            //Here, we check if the difference is greater than the margin of error
            // to determine if we need to stop prematurely.
            if(Math.abs(node.oldValue - temp) >= MARGIN){
                isOverMargin = true;
            }
            node.oldValue = temp;
        }
        flag = isOverMargin;
    }

    public static void handleSinks(){
        //"For each sink, set its updated rank to be the sink's previous
        //rank divided by the number of total pages and sum all of the sink's
        //updated ranks"

        float sum = 0.0f;
        for(int i = 0; i < sinks.size(); i++){
            
        }
        
        //For every page in the graph, set the newrank to be the sum of the
        //sinks' updated ranks

        /* 
        for(Enumeration<String> F = pages.keys(); F.hasMoreElements();){
            String i = F.nextElement();
            pages.get(i).newValue = sum;
        }
        */
    }
}

class PageRank {
    float oldValue;
    float newValue;
    ArrayList edgeTo = new ArrayList();

    public PageRank(String pointsTo){
        newValue = 0.0f;
        edgeTo.add(pointsTo);
    }

    public void AddNode(String node){
        edgeTo.add(node);
    }

    public void SetInitialScore(float n){
        oldValue = n;
    }

    public void printPageRank(String key){
        System.out.println("Key: " + key + " -- {oldValue : " + oldValue +
         " | newValue : " + newValue + " | Edges to : " + edgeTo.toString() + "}");
    }

    public void printScores(String key){
        System.out.println("Key: " + key + " -- " + oldValue);
    }
}
