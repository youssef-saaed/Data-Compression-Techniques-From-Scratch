package gui;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

public class MainFrame extends JFrame
{
    MainPanel panel;
    JScrollPane scrollPane;
    public MainFrame()
    {
        panel = new MainPanel();
        scrollPane = new JScrollPane(panel);
        panel.scrollPane = scrollPane;

        this.setTitle("Adaptive Huffman Simulation");
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        this.add(scrollPane);
        this.pack();

        this.setLocationRelativeTo(null);
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setResizable(false);

        this.setVisible(true);
    }
    public MainPanel getPanel()
    {
        return panel;
    }
}
