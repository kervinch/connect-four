package connectfour.ui;

import java.awt.Color;
import java.awt.Graphics;

public abstract class Counter {

	private static int x_counter = 50;
	private static int y_counter = 50;
	
	public static void draw(Graphics g, int x, int y, Color c) {
		g.setColor(c);
		g.fillOval(x, y, x_counter, y_counter);
	}

	public static void setX_counter(int x_counter) {
		Counter.x_counter = x_counter;
	}

	public static void setY_counter(int y_counter) {
		Counter.y_counter = y_counter;
	}
}
