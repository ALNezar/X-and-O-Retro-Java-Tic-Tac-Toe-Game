import javax.swing.*;
import java.awt.*;
import java.net.URL; // Added import for URL
import javax.sound.sampled.*;
import java.io.*;

public class TicTacToeApp extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final Settings settings = new Settings();
    private Clip clip; // This clip might be for background music if implemented later

    public TicTacToeApp() {
        initUI();
        setVisible(true);
    }

    private void initUI() {
        setTitle("Tic-Tac-Toe");
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // --- NEW CODE TO SET APPLICATION ICON ---
        setAppIcon("resources/my_icon.png"); // Call the method to set the icon
        // ----------------------------------------

        mainPanel.add(new WelcomePanel(this), "Welcome");
        mainPanel.add(new GamePanel(this), "Game");
        mainPanel.add(new SettingsPanel(this), "Settings");

        add(mainPanel);
        showScreen("Welcome");
    }

    /**
     * Sets the icon for the JFrame.
     * The icon file should be placed in the 'resources' folder in your classpath.
     * @param iconPath The path to the icon file (e.g., "resources/my_icon.png").
     */
    private void setAppIcon(String iconPath) {
        URL iconURL = getClass().getClassLoader().getResource(iconPath);
        if (iconURL != null) {
            ImageIcon appIcon = new ImageIcon(iconURL);
            setIconImage(appIcon.getImage()); // Set the icon for the JFrame
        } else {
            System.err.println("Application icon not found: " + iconPath);
            // Optionally, you can set a default text/pixel icon if the image is missing:
            // setIconImage(new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB)); // A tiny transparent icon
        }
    }

    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);
    }

    public Settings getSettings() {
        return settings;
    }
}