

public class JarMain {
    public static void main(String[] args) {
        try {
            server.App.main(args);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
