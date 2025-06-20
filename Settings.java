import java.sql.*;

public class Settings {
    private String mode = "Singleplayer";
    private int boardSize = 3;
    private boolean musicEnabled = true;

    public Settings() {
        loadSettings();
    }

    public void loadSettings() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:settings.db")) {
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE IF NOT EXISTS settings (mode TEXT, boardSize INTEGER, music INTEGER)");
            ResultSet rs = stmt.executeQuery("SELECT * FROM settings");
            if (rs.next()) {
                mode = rs.getString("mode");
                boardSize = rs.getInt("boardSize");
                musicEnabled = rs.getInt("music") == 1;
            } else {
                saveSettings();
            }
        } catch (SQLException e) {
            System.err.println("Failed to load settings: " + e.getMessage());
        }
    }

    public void saveSettings() {
        try (Connection conn = DriverManager.getConnection("jdbc:sqlite:settings.db")) {
            Statement stmt = conn.createStatement();
            stmt.execute("DELETE FROM settings");
            PreparedStatement ps = conn.prepareStatement("INSERT INTO settings VALUES (?, ?, ?)");
            ps.setString(1, mode);
            ps.setInt(2, boardSize);
            ps.setInt(3, musicEnabled ? 1 : 0);
            ps.execute();
        } catch (SQLException e) {
            System.err.println("Failed to save settings: " + e.getMessage());
        }
    }

    public void reset() {
        mode = "Singleplayer";
        boardSize = 3;
        musicEnabled = true;
        saveSettings();
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public int getBoardSize() {
        return boardSize;
    }

    public void setBoardSize(int boardSize) {
        this.boardSize = boardSize;
    }

    public boolean isMusicEnabled() {
        return musicEnabled;
    }

    public void setMusicEnabled(boolean musicEnabled) {
        this.musicEnabled = musicEnabled;
    }
}
