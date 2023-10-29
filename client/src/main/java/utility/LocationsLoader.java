package utility;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import application.Main;



public class LocationsLoader extends Thread {

    private ArrayList<Region> regions = new ArrayList<Region>();
    private String path = Main.LocationsPath;
    private UtilityInterface function;

    public LocationsLoader(UtilityInterface function) {
        super();
        this.function = function;
        start();
    }

    public ArrayList<Region> getLocations() {
        return regions;
    }


    @Override
    public void run() {
        LoadData();
        function.execute(regions);
    }

    /** 
    * Cerca in quale regione è situate la provincia che si sta cercando.
    * @param Name nome della regione.
    * @return ritorna l'oggetto che rappresenta tale regione. Se il nome passato non è valido, il risultato sarà NULL.
    */
    public Province FindProvince(String Name) {
        for(Region region : regions) {

            Province province = region.findProvince(Name);

            if(province != null) {
                return province;
            }
        }
        return null;
    }

    
    /** 
    * Carica i dati
    * @return Restituisce TRUE se l'operazione di caricamento va a buon fine altrimenti FALSE.
    */
    public boolean LoadData() 
    {
        try {  
            JsonParser jsonFileReader = new JsonParser(path);
            JSONObject [] structure = jsonFileReader.ReadJsonFile_as_ArrayOfJsonObject();

            for(JSONObject jsonData : structure) 
            {
                String name = (String)jsonData.get("nome");
                JSONArray caps = ((JSONArray)jsonData.get("cap"));
                String [] cap = new String[caps.size()];

                for(int i = 0; i < caps.size(); i++) {
                    cap[i] = (String) caps.get(i);
                }
                
                Commune common = new Commune(name, cap);
                Region region = null;
                Province province = null;

                String regionName   = (String)((JSONObject)jsonData.get("regione")).get("nome");
                String provinceName = (String)((JSONObject)jsonData.get("provincia")).get("nome");
    
                

                //cerco la regione e se non è presente la creo
                for(Region r : regions) {
                    if(r.getName().equals(regionName)) {
                        region = r;
                    }
                }

                if(region == null) {
                    region = new Region(regionName);
                    regions.add(region);
                    //System.out.println("new region: " + regionName);
                }


                //cerco la provincia e se non è presente la creo
                province = region.findProvince(provinceName);
                
                if(province == null) {
                    province = new Province(provinceName);
                    region.add(province);
                    //System.out.println("new province: " + provinceName);
                }

                province.add(common);
                //System.out.println("add " + common.getName() + " in " + regionName + ", " + provinceName);

            }

        } catch (FileNotFoundException e) {
            System.out.println("Json File not found");
            e.printStackTrace();

        } catch (IOException e) {
            System.out.println("Reading Error");
            e.printStackTrace();

        } catch (Exception e) {
            System.err.println(e);
            e.printStackTrace();
            
        } finally {
            System.out.println("reading completed");    
        }
        return true;
    }
    
}
