import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    // Consistent color palette
    private static final Color BACKGROUND_DARK = new Color(15, 12, 28);
    private static final Color CARD_BACKGROUND = new Color(33, 29, 55);
    private static final Color NEON_BLUE = new Color(0, 255, 255);
    private static final Color NEON_PINK = new Color(255, 20, 147);
    private static final Color RETRO_PURPLE = new Color(138, 43, 226);
    private static final Color PIXEL_WHITE = new Color(255, 255, 255);
    private static final Color PIXEL_GREY = new Color(180, 180, 180);

    private Font titleFont = new Font("Monospaced", Font.BOLD, 36);
    private Font labelFont = new Font("Dialog", Font.BOLD, 18);
    private Font buttonFont = new Font("Dialog", Font.BOLD, 18);

    public SettingsPanel(TicTacToeApp app) {
        setLayout(new BorderLayout(0, 0));
        setBackground(BACKGROUND_DARK);
        setBorder(BorderFactory.createLineBorder(NEON_BLUE.darker(), 1));

        // Title
        JLabel title = new JLabel("Settings", JLabel.CENTER);
        title.setFont(titleFont);
        title.setForeground(PIXEL_WHITE);
        title.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
        add(title, BorderLayout.NORTH);

        // Center card with controls
        JPanel card = new JPanel();
        card.setBackground(CARD_BACKGROUND);
        card.setLayout(new GridBagLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(NEON_PINK, 2),
                BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        card.setMaximumSize(new Dimension(400, 400)); // Prevent oversized panel

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.weightx = 1;

        // Game Mode
        JLabel modeLabel = new JLabel("Game Mode:");
        modeLabel.setFont(labelFont);
        modeLabel.setForeground(PIXEL_WHITE);
        card.add(modeLabel, gbc);

        gbc.gridy = 1;
        JComboBox<String> modeBox = new JComboBox<>(new String[]{"Singleplayer", "Multiplayer"});
        modeBox.setFont(labelFont);
        modeBox.setBackground(BACKGROUND_DARK);
        modeBox.setForeground(PIXEL_WHITE);
        modeBox.setBorder(BorderFactory.createLineBorder(NEON_BLUE, 1));
        card.add(modeBox, gbc);

        // Board Size
        gbc.gridy = 2;
        JLabel boardSizeLabel = new JLabel("Board Size:");
        boardSizeLabel.setFont(labelFont);
        boardSizeLabel.setForeground(PIXEL_WHITE);
        card.add(boardSizeLabel, gbc);

        gbc.gridy = 3;
        JComboBox<Integer> boardSizeBox = new JComboBox<>();
        for (int i = 3; i <= 6; i++) boardSizeBox.addItem(i);
        boardSizeBox.setFont(labelFont);
        boardSizeBox.setBackground(BACKGROUND_DARK);
        boardSizeBox.setForeground(PIXEL_WHITE);
        boardSizeBox.setBorder(BorderFactory.createLineBorder(NEON_BLUE, 1));
        card.add(boardSizeBox, gbc);

        // Music checkbox
        gbc.gridy = 4;
        JCheckBox musicCheck = new JCheckBox("Enable Music");
        musicCheck.setFont(labelFont);
        musicCheck.setBackground(CARD_BACKGROUND);
        musicCheck.setForeground(PIXEL_WHITE);
        musicCheck.setFocusPainted(false);
        card.add(musicCheck, gbc);

        // Apply button
        gbc.gridy = 5;
        JButton applyBtn = createStyledButton("Apply", NEON_BLUE);
        card.add(applyBtn, gbc);

        // Go Back button
        gbc.gridy = 6;
        JButton backBtn = createStyledButton("Back to Game Menu", RETRO_PURPLE);
        card.add(backBtn, gbc);

        // Center the settings card
        JPanel centerPanel = new JPanel();
        centerPanel.setBackground(BACKGROUND_DARK);
        centerPanel.setLayout(new GridBagLayout());
        centerPanel.add(card, new GridBagConstraints());
        add(centerPanel, BorderLayout.CENTER);

        // Footer (optional)
        JLabel footer = new JLabel("THE X & O GAME", JLabel.CENTER);
        footer.setFont(new Font("Monospaced", Font.PLAIN, 12));
        footer.setForeground(PIXEL_GREY);
        footer.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        add(footer, BorderLayout.SOUTH);

        // Populate with settings
        Settings settings = app.getSettings();
        modeBox.setSelectedItem(settings.getMode());
        boardSizeBox.setSelectedItem(settings.getBoardSize());
        musicCheck.setSelected(settings.isMusicEnabled());

        // Button actions
        applyBtn.addActionListener(e -> {
            settings.setMode((String) modeBox.getSelectedItem());
            settings.setBoardSize((Integer) boardSizeBox.getSelectedItem());
            settings.setMusicEnabled(musicCheck.isSelected());
            JOptionPane.showMessageDialog(this, "Settings applied!", "Info", JOptionPane.INFORMATION_MESSAGE);
        });

        backBtn.addActionListener(e -> {
            app.showScreen("Welcome"); // Or "Game" if you want to go directly to the game
        });
    }

    private JButton createStyledButton(String text, Color accentColor) {
        JButton button = new JButton(text);
        button.setFont(buttonFont);
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
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(CARD_BACKGROUND);
            }
        });
        return button;
    }
}