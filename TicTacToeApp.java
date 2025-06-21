import javax.swing.*;
import java.awt.*;
import javax.sound.sampled.*;
import java.io.*;

public class TicTacToeApp extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final Settings settings = new Settings();
    private Clip clip;

    public TicTacToeApp() {
        initUI();
        setVisible(true);
    }

    private void initUI() {
        setTitle("Tic-Tac-Toe");
        setSize(600, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        mainPanel.add(new WelcomePanel(this), "Welcome");
        mainPanel.add(new GamePanel(this), "Game");
        mainPanel.add(new SettingsPanel(this), "Settings");

        add(mainPanel);
        showScreen("Welcome");
    }

    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);
    }



    public Settings getSettings() {
        return settings;
    }
}