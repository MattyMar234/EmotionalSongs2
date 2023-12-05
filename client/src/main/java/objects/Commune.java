package objects;

public class Commune {

    protected String name;
    public String [] cap ;

    public Commune(String name, String [] cap) {
        this.name = name;
        this.cap = cap;
    }

    @Override
    public String toString() {
        return new String("commune: " + name);
    }

    
    /** 
     * @param capToTest
     * @return boolean
     */
    public boolean testCap(String capToTest) {
        for(String c : cap) {
            if(c.equals(capToTest)) {
                return true;
            }
        }
        return false;
    }


    
    /** 
     * @return String
     */
    public String getName() {
        return name;
    }


    
    /** 
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    
    /** 
     * @param index
     * @return String
     */
    public String getCap(int index) {
        if(index < cap.length) {
            return cap[index];
        }

        return "00000";
    }

    
    /** 
     * @param newCap
     * @param index
     */
    public void setCap(String newCap, int index) {
        if(index < cap.length) {
            this.cap[index] = newCap;
        }
    }
}
