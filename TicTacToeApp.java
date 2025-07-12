import javax.swing.*;
import java.awt.*;
import java.net.URL;
import javax.sound.sampled.*;
import java.io.*;

public class TicTacToeApp extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final Settings settings = new Settings();
    private Clip clip; // Optional: for music/sound control

    public TicTacToeApp() {
        initUI();
        setVisible(true);
    }

    private void initUI() {
        setTitle("Tic-Tac-Toe");
        setSize(800, 800);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setAppIcon("resources/my_icon.png");

        mainPanel.add(new WelcomePanel(this), "Welcome");
        mainPanel.add(new GamePanel(this), "Game");
        mainPanel.add(new SettingsPanel(this), "Settings");

        add(mainPanel);
        showScreen("Welcome");
    }

    /**
     * Sets the window icon using an image file in the resources folder.
     * @param iconPath relative path to the icon image
     */
    private void setAppIcon(String iconPath) {
        URL iconURL = getClass().getClassLoader().getResource(iconPath);
        if (iconURL != null) {
            ImageIcon appIcon = new ImageIcon(iconURL);
            setIconImage(appIcon.getImage());
        } else {
            System.err.println("Application icon not found: " + iconPath);
        }
    }

    /**
     * Switches the current screen using CardLayout.
     * @param name name of the panel to show
     */
    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);
    }

    /**
     * Returns the current settings object used across screens.
     * @return Settings instance
     */
    public Settings getSettings() {
        return settings;
    }
}
