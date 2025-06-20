import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.sound.sampled.*; // Import all necessary sound classes

public class GamePanel extends JPanel {
    // Balatro-inspired color palette
    private static final Color BACKGROUND_DARK = new Color(15, 12, 28);
    private static final Color CARD_BACKGROUND = new Color(33, 29, 55);
    private static final Color NEON_BLUE = new Color(0, 255, 255);
    private static final Color NEON_PINK = new Color(255, 20, 147);
    private static final Color NEON_GREEN = new Color(0, 255, 127);
    private static final Color NEON_YELLOW = new Color(255, 255, 0);
    private static final Color NEON_ORANGE = new Color(255, 165, 0);
    private static final Color PIXEL_WHITE = new Color(255, 255, 255);
    private static final Color RETRO_PURPLE = new Color(138, 43, 226);

    // Custom fonts for retro feel
    private Font pixelFont;
    private Font headerFont;
    private Font buttonFont;

    private JButton[][] buttons;
    private boolean playerX = true;
    private JLabel statusLabel;
    private int size; // REMOVED HARDCODED 3; NOW SET FROM SETTINGS

    // Enhanced game statistics with Balatro styling
    private int spotsTaken = 0;
    private JLabel spotsTakenLabel;
    private int humanWins = 0;
    private JLabel humanWinsLabel;
    private int botWins = 0;
    private JLabel botWinsLabel;

    private Timer gameTimer;
    private int secondsElapsed = 0;
    private JLabel timerLabel;

    // Music controls
    private Clip backgroundMusicClip;
    private JButton musicToggleButton;
    private boolean musicMuted = false;

    // Add restart button field
    private JButton restartButton;

    // Audio clips for move sounds and button hover
    private Clip humanMoveSoundClip;
    private Clip botMoveSoundClip;
    private Clip hoverSoundClip; // New clip for button hover sound

    public GamePanel(TicTacToeApp app) {
        // --- FIX START: Get board size from settings ---
        this.size = app.getSettings().getBoardSize();
        // --- FIX END ---

        initializeFonts();
        loadGameSoundClips(); // Load human and bot move sounds
        loadButtonHoverSound(); // Load button hover sound
        setupMainLayout();
        setupTopPanel(app);
        setupGameBoard(app); // This method now uses the 'size' from settings
        setupBottomPanel(); // New method for the bottom panel
        setupStyling();

        // Call resetGame to initialize the game state and timer
        resetGame();
        // Ensure you have a valid path for your music file here
        playBackgroundMusic("resources/music.wav");
    }

    private void initializeFonts() {
        try {
            // Using system default fonts as placeholders for demonstration
            // If you have custom font files, load them like this:
            // pixelFont = Font.createFont(Font.TRUETYPE_FONT, new File("path/to/your/pixel_font.ttf")).deriveFont(14f);
            // headerFont = Font.createFont(Font.TRUETYPE_FONT, new File("path/to/your/header_font.ttf")).deriveFont(16f);
            // buttonFont = Font.createFont(Font.TRUETYPE_FONT, new File("path/to/your/button_font.ttf")).deriveFont(28f);
            // GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
            // ge.registerFont(pixelFont);
            // ge.registerFont(headerFont);
            // ge.registerFont(buttonFont);

            // Fallback fonts
            pixelFont = new Font(Font.MONOSPACED, Font.BOLD, 14);
            headerFont = new Font(Font.DIALOG, Font.BOLD, 16);
            // Adjusted button font size dynamically based on board size
            // Smaller font for larger boards to prevent text overflow
            if (size > 3) {
                buttonFont = new Font(Font.DIALOG, Font.BOLD, 20); // Smaller for 4x4, 5x5, 6x6
            } else {
                buttonFont = new Font(Font.DIALOG, Font.BOLD, 28); // Original for 3x3
            }

        } catch (Exception e) { // Catching FontFormatException | IOException if loading from files
            System.err.println("Error loading custom fonts (using fallback): " + e.getMessage());
            // Fallback fonts
            pixelFont = new Font(Font.MONOSPACED, Font.BOLD, 14);
            headerFont = new Font(Font.DIALOG, Font.BOLD, 16);
            if (size > 3) {
                buttonFont = new Font(Font.DIALOG, Font.BOLD, 20);
            } else {
                buttonFont = new Font(Font.DIALOG, Font.BOLD, 28);
            }
        }
    }

    /**
     * Loads the audio files for human and bot moves into Clip objects.
     * These clips are then ready for playback during the game.
     * Includes error handling for file loading issues.
     */
    private void loadGameSoundClips() {
        try {
            // Load human move sound (resources/humanMove.ogg)
            // *** IMPORTANT: The Java Sound API (javax.sound.sampled) primarily supports WAV files. ***
            // *** To ensure these sounds play correctly, please convert 'humanMove.ogg' and 'botMove.ogg' ***
            // *** to 'humanMove.wav' and 'botMove.wav' respectively.                     ***
            // *** Using OGG files directly may lead to 'UnsupportedAudioFileException'. ***
            File humanMoveFile = new File("resources/humanMove.ogg"); // CONSIDER CHANGING TO .WAV
            if (humanMoveFile.exists()) {
                AudioInputStream humanAudioStream = AudioSystem.getAudioInputStream(humanMoveFile);
                humanMoveSoundClip = AudioSystem.getClip();
                humanMoveSoundClip.open(humanAudioStream);
            } else {
                System.err.println("Human move sound file not found: " + humanMoveFile.getAbsolutePath());
            }

            // Load bot move sound (resources/botMove.ogg)
            File botMoveFile = new File("resources/botMove.ogg"); // CONSIDER CHANGING TO .WAV
            if (botMoveFile.exists()) {
                AudioInputStream botAudioStream = AudioSystem.getAudioInputStream(botMoveFile);
                botMoveSoundClip = AudioSystem.getClip();
                botMoveSoundClip.open(botAudioStream);
            } else {
                System.err.println("Bot move sound file not found: " + botMoveFile.getAbsolutePath());
            }

        } catch (UnsupportedAudioFileException e) {
            System.err.println("UNSUPPORTED AUDIO FORMAT FOR MOVE SOUNDS. Please convert OGG files to WAV. Error: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("I/O Error loading move sound file: " + e.getMessage());
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable for move sound playback: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("An unexpected error occurred while loading move sounds: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Loads the audio file for general button hover effects.
     */
    private void loadButtonHoverSound() {
        try {
            File hoverFile = new File("resources/button.wav");
            if (hoverFile.exists()) {
                AudioInputStream hoverAudioStream = AudioSystem.getAudioInputStream(hoverFile);
                hoverSoundClip = AudioSystem.getClip();
                hoverSoundClip.open(hoverAudioStream);
            } else {
                System.err.println("Button hover sound file not found: " + hoverFile.getAbsolutePath());
            }
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading button hover sound: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void setupMainLayout() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_DARK);
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
    }

    private void setupTopPanel(TicTacToeApp app) {
        JPanel topPanel = new JPanel(new BorderLayout(10, 0));
        topPanel.setBackground(BACKGROUND_DARK);

        // Game statistics panel with Balatro-style cards
        JPanel statsPanel = createStatsPanel();
        topPanel.add(statsPanel, BorderLayout.CENTER);

        // Control buttons panel (now only Home and Music)
        JPanel controlPanel = createControlPanel(app);
        topPanel.add(controlPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 8, 0));
        statsPanel.setBackground(BACKGROUND_DARK);

        // Status card - Initialized here, updated in resetGame
        statusLabel = createStatCard("X's Turn", NEON_BLUE);
        statsPanel.add(statusLabel);

        // Timer card - Initialized here, updated in resetGame
        timerLabel = createStatCard("\u23F1 0s", NEON_GREEN);
        statsPanel.add(timerLabel);

        // Spots taken card - Initialized here, updated in resetGame
        // Update total spots dynamically
        spotsTakenLabel = createStatCard("\u25A0 0/" + (size * size), NEON_YELLOW);
        statsPanel.add(spotsTakenLabel);

        // Human wins card
        humanWinsLabel = createStatCard("\u2605 0", NEON_PINK);
        statsPanel.add(humanWinsLabel);

        // Bot wins card
        botWinsLabel = createStatCard("\u2699 0", NEON_ORANGE);
        statsPanel.add(botWinsLabel);

        return statsPanel;
    }

    private JLabel createStatCard(String text, Color accentColor) {
        JLabel card = new JLabel(text, JLabel.CENTER);
        card.setFont(pixelFont);
        card.setForeground(PIXEL_WHITE);
        card.setOpaque(true);
        card.setBackground(CARD_BACKGROUND);

        // Create neon-style border
        Border innerBorder = BorderFactory.createLineBorder(accentColor, 2);
        Border outerBorder = BorderFactory.createEmptyBorder(5, 8, 5, 8);
        card.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

        return card;
    }

    private JPanel createControlPanel(TicTacToeApp app) {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlPanel.setBackground(BACKGROUND_DARK);

        // Home button
        JButton homeButton = createStyledButton("HOME", RETRO_PURPLE);
        homeButton.addActionListener(e -> {
            stopGameTimer();
            stopMusic();
            closeGamePanelAudio(); // Close all audio clips when leaving GamePanel
            app.showScreen("Welcome");
        });
        controlPanel.add(homeButton);

        // Music toggle button
        musicToggleButton = createStyledButton("\u266B", NEON_PINK);
        musicToggleButton.addActionListener(e -> toggleMusic());
        controlPanel.add(musicToggleButton);

        return controlPanel;
    }

    private void setupBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10)); // Centered, some vertical gap
        bottomPanel.setBackground(BACKGROUND_DARK);

        // Restart button - now in the bottom panel
        restartButton = createStyledButton("RESTART GAME", NEON_GREEN); // Slightly more descriptive text
        restartButton.addActionListener(e -> resetGame());
        bottomPanel.add(restartButton);

        add(bottomPanel, BorderLayout.SOUTH);
    }


    private JButton createStyledButton(String text, Color accentColor) {
        JButton button = new JButton(text);
        button.setFont(headerFont);
        button.setForeground(PIXEL_WHITE);
        button.setBackground(CARD_BACKGROUND);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                BorderFactory.createEmptyBorder(8, 16, 8, 16)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(accentColor.darker());
                // Play button hover sound
                if (hoverSoundClip != null) {
                    hoverSoundClip.stop(); // Stop if already playing
                    hoverSoundClip.setFramePosition(0); // Rewind to start
                    hoverSoundClip.start(); // Play the sound
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(CARD_BACKGROUND);
            }
        });

        return button;
    }

    private void setupGameBoard(TicTacToeApp app) {
        JPanel boardContainer = new JPanel(new BorderLayout());
        boardContainer.setBackground(BACKGROUND_DARK);

        // Title for the game board
        JLabel boardTitle = new JLabel("Tic-tac-toe", JLabel.CENTER);
        boardTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        boardTitle.setForeground(NEON_BLUE);
        boardTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        boardContainer.add(boardTitle, BorderLayout.NORTH);

        // Game board with enhanced styling
        JPanel boardPanel = new JPanel(new GridLayout(size, size, 3, 3)); // Uses 'size' variable
        boardPanel.setBackground(BACKGROUND_DARK);
        boardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEON_BLUE, 3),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        buttons = new JButton[size][size]; // Uses 'size' variable

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buttons[i][j] = createGameButton();
                int row = i, col = j;
                buttons[i][j].addActionListener(e -> makeMove(row, col, app));
                boardPanel.add(buttons[i][j]);
            }
        }

        boardContainer.add(boardPanel, BorderLayout.CENTER);
        add(boardContainer, BorderLayout.CENTER);
    }

    private JButton createGameButton() {
        JButton button = new JButton();
        button.setFont(buttonFont);
        button.setBackground(CARD_BACKGROUND);
        button.setForeground(PIXEL_WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(RETRO_PURPLE, 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        // Dynamically adjust preferred size based on board size
        int preferredButtonSize = 120; // Default for 3x3
        if (size == 4) preferredButtonSize = 90;
        else if (size == 5) preferredButtonSize = 70;
        else if (size == 6) preferredButtonSize = 55; // Smaller for larger boards
        button.setPreferredSize(new Dimension(preferredButtonSize, preferredButtonSize));


        // Enhanced hover effects with sound for game board buttons
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.getText().isEmpty()) {
                    button.setBackground(RETRO_PURPLE.darker());
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(NEON_PINK, 2),
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                    // Play button hover sound
                    if (hoverSoundClip != null) {
                        hoverSoundClip.stop(); // Stop if already playing
                        hoverSoundClip.setFramePosition(0); // Rewind to start
                        hoverSoundClip.start(); // Play the sound
                    }
                }
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (button.getText().isEmpty()) {
                    button.setBackground(CARD_BACKGROUND);
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(RETRO_PURPLE, 2),
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                }
            }
        });

        return button;
    }

    private void setupStyling() {
        // Apply additional styling to the main panel
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEON_BLUE, 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
    }

    private void startGameTimer() {
        secondsElapsed = 0; // Ensure timer starts from 0 on game start/restart
        updateTimerDisplay();
        if (gameTimer != null) {
            gameTimer.stop(); // Stop any existing timer
        }
        gameTimer = new Timer(1000, e -> {
            secondsElapsed++;
            updateTimerDisplay();
        });
        gameTimer.start();
    }

    private void updateTimerDisplay() {
        timerLabel.setText("\u23F1 " + secondsElapsed + "s");
    }

    private void stopGameTimer() {
        if (gameTimer != null) {
            gameTimer.stop();
        }
    }

    private void playBackgroundMusic(String filePath) {
        try {
            File audioFile = new File(filePath);
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(audioFile);
            backgroundMusicClip = AudioSystem.getClip();
            backgroundMusicClip.open(audioStream);
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicToggleButton.setText("\u266A"); // Music playing symbol
            musicMuted = false;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("MUSIC NOT WORKING: " + e.getMessage());
            musicToggleButton.setEnabled(false); // Disable button if music can't load
        }
    }

    private void stopMusic() {
        if (backgroundMusicClip != null && backgroundMusicClip.isRunning()) {
            backgroundMusicClip.stop();
            backgroundMusicClip.close();
        }
    }

    private void toggleMusic() {
        if (backgroundMusicClip == null) return;

        if (musicMuted) {
            backgroundMusicClip.start();
            backgroundMusicClip.loop(Clip.LOOP_CONTINUOUSLY);
            musicToggleButton.setText("\u266A"); // Music playing symbol
            musicMuted = false;
        } else {
            backgroundMusicClip.stop();
            musicToggleButton.setText("\u266B"); // Music muted symbol
            musicMuted = true;
        }
    }

    /**
     * Closes all audio clips managed by this GamePanel to release system resources.
     * This should be called when the panel is no longer active (e.g., when switching
     * back to the Welcome screen).
     */
    public void closeGamePanelAudio() {
        stopMusic(); // Ensure background music is stopped and closed
        if (humanMoveSoundClip != null) {
            humanMoveSoundClip.stop();
            humanMoveSoundClip.close();
            humanMoveSoundClip = null; // Dereference to allow garbage collection
        }
        if (botMoveSoundClip != null) {
            botMoveSoundClip.stop();
            botMoveSoundClip.close();
            botMoveSoundClip = null; // Dereference to allow garbage collection
        }
        if (hoverSoundClip != null) {
            hoverSoundClip.stop();
            hoverSoundClip.close();
            hoverSoundClip = null; // Dereference to allow garbage collection
        }
        System.out.println("GamePanel audio resources closed.");
    }


    private void makeMove(int i, int j, TicTacToeApp app) {
        if (!buttons[i][j].getText().isEmpty()) return;

        String symbol = playerX ? "X" : "O";
        buttons[i][j].setText(symbol);

        // Play human move sound if it's the human's turn (playerX)
        if (playerX && humanMoveSoundClip != null) {
            humanMoveSoundClip.stop();
            humanMoveSoundClip.setFramePosition(0);
            humanMoveSoundClip.start();
        }

        // Style the move with player-specific colors
        if (playerX) {
            buttons[i][j].setForeground(NEON_BLUE);
            buttons[i][j].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(NEON_BLUE, 3),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
        } else {
            buttons[i][j].setForeground(NEON_PINK);
            buttons[i][j].setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(NEON_PINK, 3),
                    BorderFactory.createEmptyBorder(10, 10, 10, 10)
            ));
        }

        spotsTaken++;
        // Update spotsTakenLabel to reflect current board size
        spotsTakenLabel.setText("\u25A0 " + spotsTaken + "/" + (size * size));


        if (checkWin(symbol)) {
            statusLabel.setText(symbol + " WINS!");
            statusLabel.setForeground(NEON_GREEN);
            disableBoard();
            stopGameTimer();

            if (symbol.equals("X")) {
                humanWins++;
                humanWinsLabel.setText("\u2605 " + humanWins);
            } else {
                botWins++;
                botWinsLabel.setText("\u2699 " + botWins);
            }
        } else if (spotsTaken == size * size) { // Uses 'size' variable
            statusLabel.setText("DRAW GAME!");
            statusLabel.setForeground(NEON_YELLOW);
            disableBoard();
            stopGameTimer();
        } else {
            playerX = !playerX;
            statusLabel.setText((playerX ? "X" : "O") + "'s Turn");
            statusLabel.setForeground(playerX ? NEON_BLUE : NEON_PINK);

            if (app.getSettings().getMode().equals("Singleplayer") && !playerX) {
                Timer botDelay = new Timer(800, e -> {
                    botMove(app);
                    ((Timer)e.getSource()).stop();
                });
                botDelay.setRepeats(false);
                botDelay.start();
            }
        }
    }

    private void botMove(TicTacToeApp app) {
        // Play bot move sound
        if (botMoveSoundClip != null) {
            botMoveSoundClip.stop();
            botMoveSoundClip.setFramePosition(0);
            botMoveSoundClip.start();
        }

        List<Point> empty = new ArrayList<>();
        for (int i = 0; i < size; i++) { // Uses 'size' variable
            for (int j = 0; j < size; j++) { // Uses 'size' variable
                if (buttons[i][j].getText().isEmpty()) {
                    empty.add(new Point(i, j));
                }
            }
        }

        if (!empty.isEmpty()) {
            Point move = empty.get(new Random().nextInt(empty.size()));
            makeMove(move.x, move.y, app);
        }
    }

    private boolean checkWin(String symbol) {
        // Check rows
        for (int i = 0; i < size; i++) { // Uses 'size' variable
            if (checkRow(i, symbol)) return true;
        }

        // Check columns
        for (int j = 0; j < size; j++) { // Uses 'size' variable
            if (checkColumn(j, symbol)) return true;
        }

        // Check diagonals
        return checkDiagonals(symbol);
    }

    private boolean checkRow(int row, String symbol) {
        for (int j = 0; j < size; j++) { // Uses 'size' variable
            if (!buttons[row][j].getText().equals(symbol)) return false;
        }
        return true;
    }

    private boolean checkColumn(int col, String symbol) {
        for (int i = 0; i < size; i++) { // Uses 'size' variable
            if (!buttons[i][col].getText().equals(symbol)) return false;
        }
        return true;
    }

    private boolean checkDiagonals(String symbol) {
        boolean diag1 = true;
        boolean diag2 = true;

        for (int i = 0; i < size; i++) { // Uses 'size' variable
            if (!buttons[i][i].getText().equals(symbol)) {
                diag1 = false;
            }
            if (!buttons[i][size - 1 - i].getText().equals(symbol)) { // Uses 'size' variable
                diag2 = false;
            }
        }
        return diag1 || diag2;
    }

    private void disableBoard() {
        for (JButton[] row : buttons) {
            for (JButton btn : row) {
                btn.setEnabled(false);
            }
        }
    }

    public void resetGame() {
        // Reset board buttons to initial state
        for (int i = 0; i < size; i++) { // Uses 'size' variable
            for (int j = 0; j < size; j++) { // Uses 'size' variable
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
                buttons[i][j].setBackground(CARD_BACKGROUND);
                buttons[i][j].setForeground(PIXEL_WHITE); // Default text color
                buttons[i][j].setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(RETRO_PURPLE, 2),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        }
        // Reset game state variables
        playerX = true;
        spotsTaken = 0;

        // Update display labels
        statusLabel.setText("X's Turn");
        statusLabel.setForeground(NEON_BLUE);
        // Update spotsTakenLabel to reflect current board size
        spotsTakenLabel.setText("\u25A0 0/" + (size * size));

        // Reset timer
        stopGameTimer(); // Stop any currently running timer
        startGameTimer(); // Start a fresh timer from 0
    }
}