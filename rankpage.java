import java.io.BufferedReader;  
import java.io.FileReader;  
import java.io.IOException;  


class rankpage {
    
    public static void main(string[] args){

        // input CSV data file
        String line = "";  
        String splitBy = ",";  
        //parsing a CSV file into BufferedReader class constructor  
        BufferedReader br = new BufferedReader(new FileReader("polblogs.csv"));  
        while ((line = br.readLine()) != null)    {  
        String[] code = line.split(splitBy);    // use comma as separator 
        // DO some math
        // use dictionary


        // output
    }
    }
}





