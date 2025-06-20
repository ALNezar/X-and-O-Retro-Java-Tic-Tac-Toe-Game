import javax.swing.*;
import java.awt.*;

public class SettingsPanel extends JPanel {
    public SettingsPanel(TicTacToeApp app) {
        setLayout(new GridLayout(6, 1));
        add(new JLabel("Settings", JLabel.CENTER));
        setBackground(new Color(224, 255, 255)); // Light Cyan

JLabel title = new JLabel("Settings");
title.setFont(new Font("Comic Sans MS", Font.BOLD, 26));
title.setForeground(new Color(199, 21, 133)); // Medium Violet Red

        JComboBox<String> modeBox = new JComboBox<>(new String[]{"Singleplayer", "Multiplayer"});
        JComboBox<Integer> boardSizeBox = new JComboBox<>();
        for (int i = 3; i <= 6; i++) boardSizeBox.addItem(i);
        JCheckBox musicCheck = new JCheckBox("Enable Music");

        Settings settings = app.getSettings();
        modeBox.setSelectedItem(settings.getMode());
        boardSizeBox.setSelectedItem(settings.getBoardSize());
        musicCheck.setSelected(settings.isMusicEnabled());

        add(new JLabel("Game Mode:"));
        add(modeBox);
        add(new JLabel("Board Size:"));
        add(boardSizeBox);
        add(musicCheck);

        JPanel buttonPanel = new JPanel();
        JButton saveBtn = new JButton("Save");
        JButton resetBtn = new JButton("Reset");
        JButton homeBtn = new JButton("Home");

        saveBtn.addActionListener(e -> {
            settings.setMode((String) modeBox.getSelectedItem());
            settings.setBoardSize((Integer) boardSizeBox.getSelectedItem());
            settings.setMusicEnabled(musicCheck.isSelected());
            settings.saveSettings();
            JOptionPane.showMessageDialog(this, "Settings saved!");
        });

        resetBtn.addActionListener(e -> {
            settings.reset();
            modeBox.setSelectedItem(settings.getMode());
            boardSizeBox.setSelectedItem(settings.getBoardSize());
            musicCheck.setSelected(settings.isMusicEnabled());
        });

        homeBtn.addActionListener(e -> app.showScreen("Welcome"));

        buttonPanel.add(saveBtn);
        buttonPanel.add(resetBtn);
        buttonPanel.add(homeBtn);
        add(buttonPanel);
    }
}