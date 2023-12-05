package objects;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Questa classe è utilizzata per rappresentare le informazioni di una regione, ai fini della realizzazione di un Account.
 */
public class Region 
{
    protected HashMap<String, Province> Provinces = new HashMap<String, Province>();
    protected String name;

    public Region(String name) {
        this.name = name;
    }

    public void add(Province province) {
        this.Provinces.put(province.getName(), province);
    }

    public Province findProvince(String name) {
        return Provinces.get(name);
    }

    public Commune findCommons(String name1, String name2) {

        Province p = Provinces.get(name1);
       
        if(p != null) {
            return p.findCommons(name2);
        }
        return null;
    }

    public String getName() {
        return name;
    }

    public ArrayList<Province> getProvincesList() {
        ArrayList<Province> c = new ArrayList<>();

        for(String name : Provinces.keySet()) {
            c.add((Province)Provinces.get(name));
        }

        return c;
    }


    
}
