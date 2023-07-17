package utility;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Questa classe è utilizzata per rappresentare le informazioni di una provincia, ai fini della realizzazione di un Account.
 */
public class Province {

    protected HashMap<String, Common> commons = new HashMap<String, Common>();
    protected String name;

    public Province(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new String("provincia: " + name);
    }

    public Common findCommons(String name) {
        return commons.get(name);
    }

    public String getName() {
        return this.name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public void add(Common common) {
        commons.put(common.getName(), common);
    }

    public ArrayList<Common> getCommonsList() {
        ArrayList<Common> c = new ArrayList<>();

        for(String name : commons.keySet()) {
            c.add((Common)commons.get(name));
        }

        return c;
    }
    
}
