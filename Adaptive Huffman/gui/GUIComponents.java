package gui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;

public class GUIComponents
{
    public static void drawNode(Graphics2D g, Integer x, Integer y, Character symbol, Integer frequency, Integer nodeNumber, Color color)
    {
        // Drawing tree node with its information written inside it
        g.setPaint(color);
        g.fillOval(x, y, 100, 100);
        g.setPaint(Color.WHITE);
        g.setFont(new Font("sans-serif", Font.BOLD, 8));
        g.drawString("Symbol: " + symbol, x + 30, y + 30);
        g.drawString("Frequency: " + frequency, x + 20, y + 50);
        g.drawString("Node number: " + nodeNumber, x + 15, y + 70);
    }

    public static void drawArrow(Graphics2D g, Integer x1, Integer x2, Integer y1, Integer y2)
    {
        // Draw a line between two points
        g.setPaint(Color.BLACK);
        g.setStroke(new BasicStroke(3));
        g.drawLine(x1 + 50, y1 + 100, x2 + 50, y2);
    }

    public static void drawLeftArrow(Graphics2D g, Integer x1, Integer x2, Integer y1, Integer y2)
    {
        // Draw a line with 0 on its left
        drawArrow(g, x1, x2, y1, y2);
        g.drawString("0", (x1 + x2) / 2 + 30, (y1 + y2) / 2 + 50);
    }

    public static void drawRightArrow(Graphics2D g, Integer x1, Integer x2, Integer y1, Integer y2)
    {
        // Draw a line with 1 on its right
        drawArrow(g, x1, x2, y1, y2);
        g.drawString("1", (x1 + x2) / 2 + 70, (y1 + y2) / 2 + 50);
    }
}