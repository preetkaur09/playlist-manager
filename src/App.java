import database.UserManager;
import view.UserAuthGUI;

import javax.swing.*;

public class App {
    public static void main(String[] args) {
        UserManager userManager = new UserManager(); // Create UserManager instance
        SwingUtilities.invokeLater(() -> {
            new UserAuthGUI(userManager).setVisible(true); // Show user authentication GUI
        });
    }
}
