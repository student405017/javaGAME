package aishooter;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public final class GameFrame extends JFrame {
    public GameFrame() {
        super("AI Shooter Challenge");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        setContentPane(new GamePanel());
        pack();
        setLocationRelativeTo(null);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new GameFrame().setVisible(true));
    }
}
