import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;
import java.io.File; // Required for loading audio files
import java.io.IOException; // Required for audio file operations
import java.net.URL; // Required for loading resources
import javax.sound.sampled.AudioInputStream; // Required for audio file operations
import javax.sound.sampled.AudioSystem; // Required for audio file operations
import javax.sound.sampled.Clip; // Required for playing short audio clips
import javax.sound.sampled.LineUnavailableException; // Exception for audio line issues
import javax.sound.sampled.UnsupportedAudioFileException; // Exception for unsupported audio formats

public class WelcomePanel extends JPanel {
    // Balatro-inspired color palette, consistent with GamePanel
    private static final Color BACKGROUND_DARK = new Color(15, 12, 28);
    private static final Color CARD_BACKGROUND = new Color(33, 29, 55);
    private static final Color NEON_BLUE = new Color(0, 255, 255);
    private static final Color NEON_PINK = new Color(255, 20, 147);
    private static final Color RETRO_PURPLE = new Color(138, 43, 226);
    private static final Color PIXEL_WHITE = new Color(255, 255, 255);
    private static final Color PIXEL_GREY = new Color(180, 180, 180);

    // Custom fonts, consistent with GamePanel
    private Font titleFont;
    private Font buttonFont;
    private Font footerFont;

    // Animation components
    private Timer animationTimer;
    private float pulsePhase = 0;
    private final Random random = new Random();
    private final Point2D.Float[] starfield;

    // Audio clips for sound effects
    private Clip hoverSoundClip;
    private Clip clickSoundClip;

    // UI Components for the title and icon
    private JLabel titleLabel;
    private JLabel gameIconLabel;

    public WelcomePanel(TicTacToeApp app) {
        initializeFonts();
        this.starfield = createStarfield(400); // Generate star positions once
        loadSoundClips(); // Load audio files at initialization

        setupMainLayout();
        setupUI(app);
        startAnimation();
    }

    private void initializeFonts() {
        try {
            // Attempt to load a custom pixel font
            // Ensure 'resources/PressStart2P-Regular.ttf' is correctly placed in your project's resources
            URL fontUrl = getClass().getClassLoader().getResource("resources/PressStart2P-Regular.ttf");
            if (fontUrl != null) {
                Font customFont = Font.createFont(Font.TRUETYPE_FONT, fontUrl.openStream());
                GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
                ge.registerFont(customFont); // Register the font with the graphics environment

                titleFont = customFont.deriveFont(Font.BOLD, 36f);
                buttonFont = customFont.deriveFont(Font.PLAIN, 18f);
                footerFont = customFont.deriveFont(Font.PLAIN, 12f);
            } else {
                System.err.println("Custom font not found: resources/PressStart2P-Regular.ttf. Using fallback fonts.");
                // Fallback fonts if custom font loading fails
                titleFont = new Font(Font.MONOSPACED, Font.BOLD, 36);
                buttonFont = new Font(Font.DIALOG, Font.BOLD, 18);
                footerFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
            }
        } catch (IOException | FontFormatException e) {
            System.err.println("Error loading custom font: " + e.getMessage());
            e.printStackTrace();
            // Fallback fonts if custom font loading fails (e.g., font file corrupted)
            titleFont = new Font(Font.MONOSPACED, Font.BOLD, 36);
            buttonFont = new Font(Font.DIALOG, Font.BOLD, 18);
            footerFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        }
    }

    private Clip loadClip(String filePath) {
        try {
            URL url = getClass().getClassLoader().getResource(filePath);
            if (url == null) {
                System.err.println("Sound file not found: " + filePath);
                return null;
            }
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(url);
            Clip clip = AudioSystem.getClip();
            clip.open(audioStream);
            return clip;
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.err.println("Error loading sound file " + filePath + ": " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    private void loadSoundClips() {
        hoverSoundClip = loadClip("resources/button.wav");
        clickSoundClip = loadClip("resources/generic1.wav");
    }

    private void setupMainLayout() {
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_DARK);
        // Add a subtle border to the whole screen
        setBorder(BorderFactory.createLineBorder(NEON_BLUE.darker(), 1));
    }

    private void setupUI(TicTacToeApp app) {
        // Main content panel that holds logo and buttons
        JPanel contentPanel = new JPanel(new BorderLayout(0, 50));
        contentPanel.setOpaque(false); // Make it transparent to see the animated background
        contentPanel.setBorder(BorderFactory.createEmptyBorder(60, 0, 60, 0));

        // 1. Logo Panel (now includes icon and text)
        JPanel logoPanel = createLogoPanel();
        contentPanel.add(logoPanel, BorderLayout.NORTH);

        // 2. Button Panel
        JPanel buttonPanel = createButtonPanel(app);
        contentPanel.add(buttonPanel, BorderLayout.CENTER);

        // 3. Footer
        JLabel footerLabel = createFooter();
        contentPanel.add(footerLabel, BorderLayout.SOUTH);

        add(contentPanel, BorderLayout.CENTER);
    }

    private JPanel createLogoPanel() {
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0)); // Center elements with 10px horizontal gap
        logoPanel.setPreferredSize(new Dimension(0, 100)); // Give it some vertical space

        // Create and add the icon label
        ImageIcon gameIcon = loadScaledIcon("resources/my_icon.png", 64, 64); // Scale icon
        gameIconLabel = new JLabel();
        if (gameIcon != null) {
            gameIconLabel.setIcon(gameIcon);
        } else {
            gameIconLabel.setText("X O"); // Fallback text if icon not found
            gameIconLabel.setFont(titleFont.deriveFont(48f));
            gameIconLabel.setForeground(NEON_BLUE);
        }
        logoPanel.add(gameIconLabel);


        // Create and add the title text label
        titleLabel = new JLabel("Tic-tac-toe", SwingConstants.CENTER);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(PIXEL_WHITE); // Initial color
        logoPanel.add(titleLabel);

        return logoPanel;
    }

    private ImageIcon loadScaledIcon(String path, int width, int height) {
        URL url = getClass().getClassLoader().getResource(path);
        if (url != null) {
            ImageIcon originalIcon = new ImageIcon(url);
            Image scaledImage = originalIcon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(scaledImage);
        } else {
            System.err.println("Icon not found: " + path);
            return null;
        }
    }

    private JPanel createButtonPanel(TicTacToeApp app) {
        JPanel buttonPanel = new JPanel();
        // Use GridBagLayout for easy vertical centering of fixed-size elements
        buttonPanel.setLayout(new GridBagLayout());
        buttonPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();

        // Play Button
        JButton playButton = createStyledButton("PLAY GAME", NEON_PINK);
        playButton.addActionListener(e -> {
            // Play click sound when button is activated
            if (clickSoundClip != null) {
                if (clickSoundClip.isRunning()) clickSoundClip.stop();
                clickSoundClip.setFramePosition(0); // Rewind to start
                clickSoundClip.start(); // Play the sound
            }
            stopAnimation();
            app.showScreen("Game");
        });

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0); // Margin below the button
        buttonPanel.add(playButton, gbc);

        // Settings Button
        JButton settingsButton = createStyledButton("SETTINGS", RETRO_PURPLE);
        settingsButton.addActionListener(e -> {
            // Play click sound when button is activated
            if (clickSoundClip != null) {
                if (clickSoundClip.isRunning()) clickSoundClip.stop();
                clickSoundClip.setFramePosition(0); // Rewind to start
                clickSoundClip.start(); // Play the sound
            }
            stopAnimation();
            app.showScreen("Settings");
        });

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 0, 0); // No margin for the last button
        buttonPanel.add(settingsButton, gbc);

        return buttonPanel;
    }

    private JLabel createFooter() {
        JLabel footer = new JLabel("THE X & O GAME", JLabel.CENTER);
        footer.setFont(footerFont);
        footer.setForeground(PIXEL_GREY);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        return footer;
    }

    private JButton createStyledButton(String text, Color accentColor) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
        button.setForeground(PIXEL_WHITE);
        button.setBackground(CARD_BACKGROUND);
        button.setOpaque(true); // Needed for background color to show
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Use the signature compound border style
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(accentColor, 2),
                BorderFactory.createEmptyBorder(12, 40, 12, 40)
        ));

        // Add consistent hover effects and sound
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(accentColor.darker());
                // Play hover sound
                if (hoverSoundClip != null) {
                    if (hoverSoundClip.isRunning()) hoverSoundClip.stop();
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

    // --- ANIMATION & CUSTOM PAINTING ---

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // This will paint the BACKGROUND_DARK
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw the animated starfield background
        drawStarfield(g2d);

        g2d.dispose();
    }

    private void drawStarfield(Graphics2D g2d) {
        // Draw each star
        for (int i = 0; i < starfield.length; i++) {
            Point2D.Float star = starfield[i];
            float shimmer = (float) (Math.sin(pulsePhase * 2.0 + i * 0.5) + 1.0) / 2.0f; // 0.0 to 1.0

            // Pulsate size and color
            int size = (int)(shimmer * 2) + 1;
            g2d.setColor(new Color(255, 255, 255, (int)(100 + shimmer * 155)));

            // Slight parallax movement
            float x = (star.x * getWidth() + (float)(20 * Math.sin(pulsePhase * 0.1 + i * 0.1))) % getWidth();
            float y = (star.y * getHeight() + (float)(20 * Math.cos(pulsePhase * 0.08 + i * 0.15))) % getHeight();
            if (x < 0) x += getWidth();
            if (y < 0) y += getHeight();

            g2d.fillOval((int)x, (int)y, size, size);
        }
    }

    private Point2D.Float[] createStarfield(int count) {
        Point2D.Float[] stars = new Point2D.Float[count];
        for (int i = 0; i < count; i++) {
            stars[i] = new Point2D.Float(random.nextFloat(), random.nextFloat());
        }
        return stars;
    }

    private void startAnimation() {
        if (animationTimer != null && animationTimer.isRunning()) {
            return;
        }
        animationTimer = new Timer(33, e -> { // ~30 FPS
            pulsePhase += 0.04f;
            // Update the pulsating glow effect for the title text
            float glow = (float) (Math.sin(pulsePhase * 2.5) + 1.0) / 2.0f; // Varies between 0.0 and 1.0
            if (titleLabel != null) {
                // Blend between PIXEL_WHITE and NEON_BLUE based on glow
                int r = (int)(PIXEL_WHITE.getRed() * (1 - glow) + NEON_BLUE.getRed() * glow);
                int g = (int)(PIXEL_WHITE.getGreen() * (1 - glow) + NEON_BLUE.getGreen() * glow);
                int b = (int)(PIXEL_WHITE.getBlue() * (1 - glow) + NEON_BLUE.getBlue() * glow);
                titleLabel.setForeground(new Color(r, g, b));
            }
            repaint(); // Repaint the panel to update starfield animation
        });
        animationTimer.start();
    }

    private void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
}