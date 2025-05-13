import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    public static void main(String[] args) {
        String url = "jdbc:mysql://localhost:3306/MP3PlayerConnection"; // Update with your database name
        String user = "root"; // Update with your MySQL username
        String password = "Preet@09"; // Update with your MySQL password

        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            if (conn != null) {
                System.out.println("Connected to the database!");
            }
        } catch (SQLException e) {
            System.out.println("not connected");
            e.printStackTrace();
        }
    }
}
