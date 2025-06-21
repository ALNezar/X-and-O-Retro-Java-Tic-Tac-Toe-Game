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
    private static final Color BACKGROUND_DARK = new Color(15, 12, 28);
    private static final Color CARD_BACKGROUND = new Color(33, 29, 55);
    private static final Color NEON_BLUE = new Color(0, 255, 255);
    private static final Color NEON_PINK = new Color(255, 20, 147);
    private static final Color NEON_GREEN = new Color(0, 255, 127);
    private static final Color NEON_YELLOW = new Color(255, 255, 0);
    private static final Color NEON_ORANGE = new Color(255, 165, 0);
    private static final Color PIXEL_WHITE = new Color(255, 255, 255);
    private static final Color RETRO_PURPLE = new Color(138, 43, 226);

    private Font pixelFont;
    private Font headerFont;
    private Font buttonFont;

    private JButton[][] buttons;
    private boolean playerX = true;
    private JLabel statusLabel;
    private int size;

    private int spotsTaken = 0;
    private JLabel spotsTakenLabel;
    private int humanWins = 0;
    private JLabel humanWinsLabel;
    private int botWins = 0;
    private JLabel botWinsLabel;

    private Timer gameTimer;
    private int secondsElapsed = 0;
    private JLabel timerLabel;

    private Clip backgroundMusicClip;
    private JButton musicToggleButton;
    private boolean musicMuted = false;

    private JButton restartButton;

    private Clip humanMoveSoundClip;
    private Clip botMoveSoundClip;
    private Clip hoverSoundClip;

    public GamePanel(TicTacToeApp app) {
        this.size = app.getSettings().getBoardSize();

        initializeFonts();
        loadGameSoundClips();
        loadButtonHoverSound();
        setupMainLayout();
        setupTopPanel(app);
        setupGameBoard(app);
        setupBottomPanel();
        setupStyling();

        resetGame();
        playBackgroundMusic("resources/music.wav");
    }

    private void initializeFonts() {
        try {
            pixelFont = new Font(Font.MONOSPACED, Font.BOLD, 14);
            headerFont = new Font(Font.DIALOG, Font.BOLD, 16);
            if (size > 3) {
                buttonFont = new Font(Font.DIALOG, Font.BOLD, 20);
            } else {
                buttonFont = new Font(Font.DIALOG, Font.BOLD, 28);
            }

        } catch (Exception e) {
            System.err.println("Error loading custom fonts (using fallback): " + e.getMessage());
            pixelFont = new Font(Font.MONOSPACED, Font.BOLD, 14);
            headerFont = new Font(Font.DIALOG, Font.BOLD, 16);
            if (size > 3) {
                buttonFont = new Font(Font.DIALOG, Font.BOLD, 20);
            } else {
                buttonFont = new Font(Font.DIALOG, Font.BOLD, 28);
            }
        }
    }

    private void loadGameSoundClips() {
        try {
            File humanMoveFile = new File("resources/humanMove.wav");
            if (humanMoveFile.exists()) {
                AudioInputStream humanAudioStream = AudioSystem.getAudioInputStream(humanMoveFile);
                humanMoveSoundClip = AudioSystem.getClip();
                humanMoveSoundClip.open(humanAudioStream);
            } else {
                System.err.println("Human move sound file not found: " + humanMoveFile.getAbsolutePath());
            }

            File botMoveFile = new File("resources/botMove.wav");
            if (botMoveFile.exists()) {
                AudioInputStream botAudioStream = AudioSystem.getAudioInputStream(botMoveFile);
                botMoveSoundClip = AudioSystem.getClip();
                botMoveSoundClip.open(botAudioStream);
            } else {
                System.err.println("Bot move sound file not found: " + botMoveFile.getAbsolutePath());
            }

        } catch (UnsupportedAudioFileException e) {
            System.err.println("UNSUPPORTED AUDIO FORMAT. Convert to WAV. Error: " + e.getMessage());
            e.printStackTrace();
        } catch (IOException e) {
            System.err.println("I/O Error loading move sound file: " + e.getMessage());
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            System.err.println("Audio line unavailable for move sound playback: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("Error loading move sounds: " + e.getMessage());
            e.printStackTrace();
        }
    }

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

        JPanel statsPanel = createStatsPanel();
        topPanel.add(statsPanel, BorderLayout.CENTER);

        JPanel controlPanel = createControlPanel(app);
        topPanel.add(controlPanel, BorderLayout.EAST);

        add(topPanel, BorderLayout.NORTH);
    }

    private JPanel createStatsPanel() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 5, 8, 0));
        statsPanel.setBackground(BACKGROUND_DARK);

        statusLabel = createStatCard("X's Turn", NEON_BLUE);
        statsPanel.add(statusLabel);

        timerLabel = createStatCard("\u23F1 0s", NEON_GREEN);
        statsPanel.add(timerLabel);

        spotsTakenLabel = createStatCard("\u25A0 0/" + (size * size), NEON_YELLOW);
        statsPanel.add(spotsTakenLabel);

        humanWinsLabel = createStatCard("\u2605 0", NEON_PINK);
        statsPanel.add(humanWinsLabel);

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

        Border innerBorder = BorderFactory.createLineBorder(accentColor, 2);
        Border outerBorder = BorderFactory.createEmptyBorder(5, 8, 5, 8);
        card.setBorder(BorderFactory.createCompoundBorder(outerBorder, innerBorder));

        return card;
    }

    private JPanel createControlPanel(TicTacToeApp app) {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlPanel.setBackground(BACKGROUND_DARK);

        JButton homeButton = createStyledButton("HOME", RETRO_PURPLE);
        homeButton.addActionListener(e -> {

            app.showScreen("Welcome");
        });
        controlPanel.add(homeButton);

        musicToggleButton = createStyledButton("\u266B", NEON_PINK);
        musicToggleButton.addActionListener(e -> toggleMusic());
        controlPanel.add(musicToggleButton);

        return controlPanel;
    }

    private void setupBottomPanel() {
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 10));
        bottomPanel.setBackground(BACKGROUND_DARK);

        restartButton = createStyledButton("RESTART GAME", NEON_GREEN);
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

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(accentColor.darker());
                if (hoverSoundClip != null) {
                    hoverSoundClip.stop();
                    hoverSoundClip.setFramePosition(0);
                    hoverSoundClip.start();
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

        JLabel boardTitle = new JLabel("Tic-tac-toe", JLabel.CENTER);
        boardTitle.setFont(new Font("Dialog", Font.BOLD, 24));
        boardTitle.setForeground(NEON_BLUE);
        boardTitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 15, 0));
        boardContainer.add(boardTitle, BorderLayout.NORTH);

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
        int preferredButtonSize = 120;
        if (size == 4) preferredButtonSize = 90;
        else if (size == 5) preferredButtonSize = 70;
        else if (size == 6) preferredButtonSize = 55;
        button.setPreferredSize(new Dimension(preferredButtonSize, preferredButtonSize));


        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (button.getText().isEmpty()) {
                    button.setBackground(RETRO_PURPLE.darker());
                    button.setBorder(BorderFactory.createCompoundBorder(
                            BorderFactory.createLineBorder(NEON_PINK, 2),
                            BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                    if (hoverSoundClip != null) {
                        hoverSoundClip.stop();
                        hoverSoundClip.setFramePosition(0);
                        hoverSoundClip.start();
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
            musicToggleButton.setText("\u266B");
            musicMuted = false;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("MUSIC NOT WORKING: " + e.getMessage());
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
            musicToggleButton.setText("\u266B");
            musicMuted = false;
        } else {
            backgroundMusicClip.stop();
            musicToggleButton.setText("\u266B");
            musicMuted = true;
        }
    }

    public void closeGamePanelAudio() {
        stopMusic();
        if (humanMoveSoundClip != null) {
            humanMoveSoundClip.stop();
            humanMoveSoundClip.close();
            humanMoveSoundClip = null;
        }
        if (botMoveSoundClip != null) {
            botMoveSoundClip.stop();
            botMoveSoundClip.close();
            botMoveSoundClip = null;
        }
        if (hoverSoundClip != null) {
            hoverSoundClip.stop();
            hoverSoundClip.close();
            hoverSoundClip = null;
        }
        System.out.println("GamePanel audio resources closed.");
    }


    private void makeMove(int i, int j, TicTacToeApp app) {
        if (!buttons[i][j].getText().isEmpty()) return;

        String symbol = playerX ? "X" : "O";
        buttons[i][j].setText(symbol);

        if (playerX && humanMoveSoundClip != null) {
            humanMoveSoundClip.stop();
            humanMoveSoundClip.setFramePosition(0);
            humanMoveSoundClip.start();
        }

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
        if (botMoveSoundClip != null) {
            botMoveSoundClip.stop();
            botMoveSoundClip.setFramePosition(0);
            botMoveSoundClip.start();
        }

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
        for (int i = 0; i < size; i++) {
            if (checkRow(i, symbol)) return true;
        }

        for (int j = 0; j < size; j++) {
            if (checkColumn(j, symbol)) return true;
        }

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
        spotsTaken = 0;

        statusLabel.setText("X's Turn");
        statusLabel.setForeground(NEON_BLUE);
        spotsTakenLabel.setText("\u25A0 0/" + (size * size));

        stopGameTimer();
        startGameTimer();
    }
}