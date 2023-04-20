import dabase.Database;

public class Server {
    public static void main(String[] args) throws Exception {
        System.out.println("Running....");

        Database db = Database.getInstance();
        db.createTable();
    }
}
