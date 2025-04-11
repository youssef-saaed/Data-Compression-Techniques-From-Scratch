package gui;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Font;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

public class MainPanel extends JPanel 
{
    public GUINode tree;
    public Integer nodeWidth;
    public Integer nodeHeight;
    public Integer sepX;
    public Integer sepY;
    public Integer offsetX;
    public Integer offsetY;
    public Integer treeWidth;
    public Integer treeHeight;
    public JScrollPane scrollPane;
    public String encoded = "";

    public MainPanel()
    {
        nodeWidth = 100;
        nodeHeight = 100;
        sepX = 20;
        sepY = 70;
        offsetX = 50;
        offsetY = 100;
        tree = null;
        treeWidth = 0;
        treeHeight = 0;
        scrollPane = null;
    }
    
    public void paint(Graphics g)
    {
        Graphics2D g2D = (Graphics2D) g;
        
        // Painting background (Sky Blue)
        g2D.setPaint(Color.getHSBColor(0.575f, 0.18f, 1f)); 
        g2D.fillRect(0, 0, this.getWidth(), this.getHeight());

        // Write the encoded string in the top left corner
        g2D.setPaint(Color.BLACK);
        g2D.setFont(new Font("sans-serif", Font.BOLD, 32));
        g2D.drawString(encoded, 30, 70);

        // Determine tree x-offset for tree to be centered
        if (treeWidth < this.getWidth())
        {
            offsetX = (this.getWidth() - treeWidth) / 2;
        }
        else 
        {
            offsetX = 10;
        }

        if (tree != null)
        {
            drawTree(g2D, tree);
        }

        // Update the prefered size of the panel based on width and height of the tree to show scroll bars as needed
        this.setPreferredSize(new Dimension(treeWidth + 2 * offsetX, treeHeight + offsetY));
        if (scrollPane != null)
        {
            scrollPane.updateUI();
        }
    }

    public void drawTree(Graphics2D g, GUINode subtree)
    {
        Color color;
        if (subtree.highlighted)
        {
            color = Color.RED;
        }
        else if (subtree.nyt)
        {
            color = Color.GRAY;
        }
        else
        {
            color = Color.BLUE;
        }

        Integer x = subtree.x + offsetX;
        Integer y = subtree.y + offsetY;

        GUIComponents.drawNode(g, x, y, subtree.symbol, subtree.frequency, subtree.nodeNumber, color);
        if (subtree.left != null)
        {
            GUIComponents.drawLeftArrow(g, x, offsetX + subtree.left.x, y, offsetY + subtree.left.y);
            GUIComponents.drawRightArrow(g, x, offsetX + subtree.right.x, y, offsetY + subtree.right.y);
            drawTree(g, subtree.left);
            drawTree(g, subtree.right);
        }
    }
}
