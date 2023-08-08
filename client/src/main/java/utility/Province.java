package utility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Questa classe è utilizzata per rappresentare le informazioni di una provincia, ai fini della realizzazione di un Account.
 */
public class Province {

    protected HashMap<String, Commune> commons = new HashMap<String, Commune>();
    protected String name;

    public Province(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new String("provincia: " + name);
    }

    public Commune findCommons(String name) {
        return commons.get(name);
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void add(Commune common) {
        commons.put(common.getName(), common);
    }

    public ArrayList<Commune> getCommonsList() {
        ArrayList<Commune> c = new ArrayList<>();

        for(String name : commons.keySet()) {
            c.add((Commune)commons.get(name));
        }

        return c;
    }
    
}
