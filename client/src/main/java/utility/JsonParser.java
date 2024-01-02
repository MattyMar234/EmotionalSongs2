package utility;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;


/**
 * Questa classe è utilizzata per gestire la lettura/scrittura su file.json
 */
public class JsonParser 
{
    /*Fields*/

    /**
    * Percorso del file su cui si opera
    */
    protected String FilePath;
    protected String FileData;


    /*Constructor*/
    /**
    * Genera un oggetto di tipo JsonParser.
    * @param Path Percorso del file.
    */
    public JsonParser(String Path)  {
        this.FilePath = Path;
    }

    /**
    * legge il file.json e restituisce un oggetto che rappresenta il contenuto. E' volutamente privato perché viene utilizzato solo dalla classe
    * @return restituisce un oggetto Object che rappresenta la struttura del contenuto de file
    */
    private Object OpenFile() throws IOException, ParseException 
    {
        FileReader reader = new FileReader(this.FilePath);
        JSONParser parser = new JSONParser();
        Object p = parser.parse(reader);
        
        return p;
    }

    /**
    * Effettua la scrittura dei dati sul file json. Tale operazione consiste nella sovrascrittuta del contenuto del file
    * @param data sono i dati che bisogna scrivere sul file. Può solo essere creato con instanze di JSONArray e JSONObject.
    */
    public void WriteJsonFile(Object data) throws IOException
    {
        FileWriter file = new FileWriter(this.FilePath);
        String DataStructure = "";
        JSONObject [] dataList;

        //inizializzo l'array
        if(data == null) {
            return;
        } 

        if(data instanceof JSONArray) 
        {
            JSONArray jsonarray = ((JSONArray) data);
            dataList = new JSONObject[jsonarray.size()];

            

            for(int  i = 0; i < dataList.length; i++) {
                dataList[i] = (JSONObject) jsonarray.get(i);
            }

            file.write("[\n");
            for(int k = 0; k < dataList.length; k++) 
            {
                String s = new String();
                s = "\t" + ((JSONObject)jsonarray.get(k)).toJSONString() + (k == dataList.length - 1 ? "\n" : ",\n"); 
                
                for(int  i = 0; i < s.length(); i++) {
                    file.append(s.charAt(i));
                }

                
            }
            file.write("]");
            file.close();

        }
        else if(data instanceof JSONObject) {
            dataList = new JSONObject[1];
            dataList[0] = ((JSONObject) data);

            file.write("[\n\t" + dataList[0].toJSONString() + "\n]");
            file.close();
        }
    }


    /**
    * Legge il file.json e restituisce un oggetto che rappresenta il contenuto solo se tale oggetto è rapresentabile come JSONObject
    * @return restituisce un oggetto JSONObject solo se la struttura del file è rapresentabile come JSONObject altrimenti restituisce NULL.
    */
    public JSONObject ReadJsonFile_as_JsonObject() throws ParseException, IOException 
    { 
        Object obj = OpenFile();

        if(obj instanceof JSONObject) {
            JSONObject object = (JSONObject) obj;
            return object;
        }

        return null;
    }
    
    /**
    * Legge il file.json e restituisce un oggetto che rappresenta il contenuto solo se tale oggetto è rapresentabile come JSONArray
    * @return restituisce un oggetto JSONArray solo se la struttura del file è rapresentabile come JSONArray altrimenti restituisce NULL.
    */
    public JSONArray ReadJsonFile_as_JsonArray() throws ParseException, IOException 
    {
        Object obj = OpenFile();

        if(obj instanceof JSONArray) {
            JSONArray objects = (JSONArray) obj;
            return objects;
        }
        
        return null;
    }

    /**
    * legge il file.json e restituisce un Array di JSONObject solo se il contenuto del file è rapresentabile come JSONArray
    * @return restituisce un Array di JSONObject solo se la struttura del file è rapresentabile come JSONArray altrimenti restituisce NULL.
    */
    public JSONObject [] ReadJsonFile_as_ArrayOfJsonObject() throws ParseException, IOException 
    {
        JSONParser parser = new JSONParser();
        Object obj = OpenFile();
        JSONObject output [];

        if(obj instanceof JSONArray) {
            JSONArray objects = (JSONArray) obj;

            output = new JSONObject [objects.size()];
            
            for(int  i = 0; i < output.length; i++) {
                output[i] = (JSONObject) objects.get(i);
            }

            return output;
        }
        return null;
    }
}
