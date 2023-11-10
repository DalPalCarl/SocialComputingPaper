import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;
import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;  


class rankpage {
    
    
    public static void main(string[] args) {
        Dictionary pages = new Hashtable();
        // input CSV data file
        String line = "";  
        String splitBy = ",";  
        //parsing a CSV file into BufferedReader class constructor  
        BufferedReader br = new BufferedReader(new FileReader("polblogs.csv"));  
        while ((line = br.readLine()) != null)    {  
            String[] code = line.split(splitBy);    // use comma as separator

            // If the outgoing link is already present in the hashTable.
            if (pages.keys().contains(code[0])){
                ArrayList temp = pages.get(code[0]);
                temp.add(code[3]);
            } 
            // If the outgoing link is not present
            else{
            ArrayList<String> news = new ArrayList<String>();
            news.add(code[3]);
            pages.put(code[0], code[3]);
            }
        
    }
    // DO some math(Use damping factor)
        
            // use dictionary
        System.out.println();

        // ( (1 - d)/ n ) + d * (sum of each page P's Pagerank: current page rank / outbound links)

        // output
    }
}

public class PageRank {

}





