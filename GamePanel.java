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
import javax.sound.sampled.*;

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
    private JLabel statusLabel; // Removed final to allow initialization in createStatsPanel
    private final int size = 3; // Hardcoded 3x3 as requested

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

    public GamePanel(TicTacToeApp app) {
        initializeFonts();
        setupMainLayout();
        setupTopPanel(app);
        setupGameBoard(app);
        setupStyling();

        resetGame();
        playBackgroundMusic("path/to/your/music.wav");
    }

    private void initializeFonts() {
        try {
            // Create custom fonts with fallback to system fonts
            pixelFont = new Font("Monospaced", Font.BOLD, 14);
            headerFont = new Font("Dialog", Font.BOLD, 16);
            buttonFont = new Font("Dialog", Font.BOLD, 28);
        } catch (Exception e) {
            // Fallback fonts
            pixelFont = new Font(Font.MONOSPACED, Font.BOLD, 14);
            headerFont = new Font(Font.DIALOG, Font.BOLD, 16);
            buttonFont = new Font(Font.DIALOG, Font.BOLD, 28);
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

        // Control buttons panel
        JPanel controlPanel = createControlPanel(app);
        topPanel.add(controlPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 8, 0));
        statsPanel.setBackground(BACKGROUND_DARK);

        // Status card
        statusLabel = createStatCard("X's Turn", NEON_BLUE);
        statsPanel.add(statusLabel);

        // Timer card
        timerLabel = createStatCard("\u23F1 0s", NEON_GREEN);
        statsPanel.add(timerLabel);

        // Spots taken card
        spotsTakenLabel = createStatCard("\u25A0 0/9", NEON_YELLOW);
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

        // Home button with Balatro styling
        JButton homeButton = createStyledButton("HOME", RETRO_PURPLE);
        homeButton.addActionListener(e -> {
            stopGameTimer();
            stopMusic();
            app.showScreen("Welcome");
        });
        controlPanel.add(homeButton);

        // Music toggle button
        musicToggleButton = createStyledButton("\u266B", NEON_PINK);
        musicToggleButton.addActionListener(e -> toggleMusic());
        controlPanel.add(musicToggleButton);

        return controlPanel;
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
        JPanel boardPanel = new JPanel(new GridLayout(size, size, 3, 3));
        boardPanel.setBackground(BACKGROUND_DARK);
        boardPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEON_BLUE, 3),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        buttons = new JButton[size][size];

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
        button.setPreferredSize(new Dimension(120, 120));

        // Enhanced hover effects
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.getText().isEmpty()) {
                    button.setBackground(RETRO_PURPLE.darker());
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(NEON_PINK, 2),
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
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
        secondsElapsed = 0;
        updateTimerDisplay();
        if (gameTimer != null) {
            gameTimer.stop();
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
            musicToggleButton.setText("\u266A");
            musicMuted = false;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Synthwave beats unavailable: " + e.getMessage());
            musicToggleButton.setEnabled(false);
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
            musicToggleButton.setText("\u266A");
            musicMuted = false;
        } else {
            backgroundMusicClip.stop();
            musicToggleButton.setText("\u266B");
            musicMuted = true;
        }
    }

    private void makeMove(int i, int j, TicTacToeApp app) {
        if (!buttons[i][j].getText().isEmpty()) return;

        String symbol = playerX ? "X" : "O";
        buttons[i][j].setText(symbol);

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
        spotsTakenLabel.setText("\u25A0 " + spotsTaken + "/9");

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
        } else if (spotsTaken == size * size) {
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
        List<Point> empty = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
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
        for (int i = 0; i < size; i++) {
            if (checkRow(i, symbol)) return true;
        }

        // Check columns
        for (int j = 0; j < size; j++) {
            if (checkColumn(j, symbol)) return true;
        }

        // Check diagonals
        return checkDiagonals(symbol);
    }

    private boolean checkRow(int row, String symbol) {
        for (int j = 0; j < size; j++) {
            if (!buttons[row][j].getText().equals(symbol)) return false;
        }
        return true;
    }

    private boolean checkColumn(int col, String symbol) {
        for (int i = 0; i < size; i++) {
            if (!buttons[i][col].getText().equals(symbol)) return false;
        }
        return true;
    }

    private boolean checkDiagonals(String symbol) {
        boolean diag1 = true;
        boolean diag2 = true;

        for (int i = 0; i < size; i++) {
            if (!buttons[i][i].getText().equals(symbol)) {
                diag1 = false;
            }
            if (!buttons[i][size - 1 - i].getText().equals(symbol)) {
                diag2 = false;
            }
        }
        return diag1 || diag2;
    }

    private void disableBoard() {
        for (JButton[] row : buttons) {
            for (JButton btn : row) {
                btn.setEnabled(false);
                if (!btn.getText().isEmpty()) {
                    btn.setBackground(CARD_BACKGROUND.darker());
                }
            }
        }
    }

    public void resetGame() {
        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                buttons[i][j].setText("");
                buttons[i][j].setEnabled(true);
                buttons[i][j].setBackground(CARD_BACKGROUND);
                buttons[i][j].setForeground(PIXEL_WHITE);
                buttons[i][j].setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(RETRO_PURPLE, 2),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                ));
            }
        }
        playerX = true;
        statusLabel.setText("X's Turn");
        statusLabel.setForeground(NEON_BLUE);
        spotsTaken = 0;
        spotsTakenLabel.setText("\u25A0 0/9");
        startGameTimer();
    }
}