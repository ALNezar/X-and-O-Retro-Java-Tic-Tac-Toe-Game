import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.geom.Point2D;
import java.util.Random;

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

    public WelcomePanel(TicTacToeApp app) {
        initializeFonts();
        this.starfield = createStarfield(400); // Generate star positions once

        setupMainLayout();
        setupUI(app);
        startAnimation();
    }

    private void initializeFonts() {
        try {
            // Create custom fonts with fallback to system fonts
            titleFont = new Font("Monospaced", Font.BOLD, 36);
            buttonFont = new Font("Dialog", Font.BOLD, 18);
            footerFont = new Font("Monospaced", Font.PLAIN, 12);
        } catch (Exception e) {
            // Fallback fonts
            titleFont = new Font(Font.MONOSPACED, Font.BOLD, 36);
            buttonFont = new Font(Font.DIALOG, Font.BOLD, 18);
            footerFont = new Font(Font.MONOSPACED, Font.PLAIN, 12);
        }
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

        // 1. Logo Panel
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
        // The logo panel itself is transparent to allow the main panel's background to show through
        JPanel logoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw the pulsating title text
                drawLogoText(g2d, getWidth() / 2, getHeight() / 2);

                g2d.dispose();
            }
        };
        logoPanel.setOpaque(false);
        logoPanel.setPreferredSize(new Dimension(0, 100)); // Give it some vertical space
        return logoPanel;
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
            stopAnimation();
            app.showScreen("Game");
        });

        gbc.gridy = 0;
        gbc.insets = new Insets(0, 0, 15, 0); // Margin below the button
        buttonPanel.add(playButton, gbc);

        // Settings Button
        JButton settingsButton = createStyledButton("SETTINGS", RETRO_PURPLE);
        settingsButton.addActionListener(e -> {
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

        // Add consistent hover effects
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

    private void drawLogoText(Graphics2D g2d, int centerX, int centerY) {
        String title = "Tic-tac-toe";
        g2d.setFont(titleFont);
        FontMetrics fm = g2d.getFontMetrics();
        int textWidth = fm.stringWidth(title);

        // Create a pulsating glow effect for the text
        float glow = (float) (Math.sin(pulsePhase * 2.5) + 1.0) / 2.0f; // Varies between 0.0 and 1.0

        // Outer glow
        g2d.setColor(new Color(NEON_BLUE.getRed(), NEON_BLUE.getGreen(), NEON_BLUE.getBlue(), (int)(100 * glow)));
        g2d.drawString(title, centerX - textWidth / 2 + 2, centerY + 2);

        // Inner text
        g2d.setColor(PIXEL_WHITE);
        g2d.drawString(title, centerX - textWidth / 2, centerY);
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
            repaint();
        });
        animationTimer.start();
    }

    private void stopAnimation() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
    }
}