import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class TicTacToeApp extends JFrame {
    private final CardLayout cardLayout = new CardLayout();
    private final JPanel mainPanel = new JPanel(cardLayout);
    private final Settings settings = new Settings();
    private Clip backgroundMusicClip; // Moved here
    private boolean musicMuted = false; // Moved here

    public TicTacToeApp() {
        initUI();
        loadAndPlayBackgroundMusic("resources/music.wav"); // Play music on app start
        setVisible(true);
    }

    private void initUI() {
        setTitle("Tic-Tac-Toe");
        setSize(600, 600);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Handle closing manually
        setLocationRelativeTo(null);

        mainPanel.add(new WelcomePanel(this), "Welcome");
        mainPanel.add(new GamePanel(this), "Game");
        mainPanel.add(new SettingsPanel(this), "Settings");

        add(mainPanel);
        showScreen("Welcome");

        // Add a WindowListener to stop music when the application is closed
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                stopBackgroundMusic(); // Ensure music is stopped and closed
                // Close any game-specific audio clips if GamePanel is active
                // (though GamePanel's home button handles this when switching)
                // For a clean exit, you might want to iterate through active panels
                // and call their cleanup methods, or rely on garbage collection
                // if clips are properly closed within their respective panels.
                System.out.println("Application closing. Stopping background music.");
                System.exit(0); // Terminate the application
            }
        });
    }

    public void showScreen(String name) {
        cardLayout.show(mainPanel, name);
    }

    public Settings getSettings() {
        return settings;
    }

    // --- Background Music Management Methods ---
    private void loadAndPlayBackgroundMusic(String filePath) {
        try {
            File audioFile = new File(filePath);
            if (!audioFile.exists()) {
                System.err.println("Background music file not found: " + audioFile.getAbsolutePath());
                return;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioStream);
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            System.out.println("Background music started.");
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading/playing background music: " + e.getMessage());
            e.printStackTrace();
            backgroundMusicClip = null; // Ensure clip is null if it failed
        }
    }

    public void stopBackgroundMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
            backgroundMusicClip.close();
            System.out.println("Background music stopped and closed.");
        }
    }

    public void toggleBackgroundMusic() {
        if (backgroundMusicClip == null) {
            System.err.println("Background music clip is not initialized.");
            return;
        }

        if (musicMuted) {
            backgroundMusicClip.start();
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            System.out.println("Background music unmuted.");
            musicMuted = false;
        } else {
            backgroundMusicClip.stop();
            System.out.println("Background music muted.");
            musicMuted = true;
        }
    }

    // This method can be called by panels to get the current music state for button text
    public boolean isMusicMuted() {
        return musicMuted;
    }
}