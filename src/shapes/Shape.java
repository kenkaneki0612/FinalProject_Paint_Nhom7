package shapes;

import java.awt.Color;
import java.awt.Graphics;
import java.io.Serializable;

	//Abstract class which all shapes extend. Allows that other elements of application are not depend on all shapes individually but on this class.
public abstract class Shape implements Movable, Cloneable, Serializable, Comparable<Shape> {
	private static final long serialVersionUID = 1L;
	private boolean selected;
    private Color color = Color.BLACK;

    public Shape() {}

    //Abstract method that must implements all shapes to draw itself.
    public abstract void draw(Graphics graphics);

    //Abstract method that must implements all shapes to select itself.
    public abstract void selected(Graphics graphics);

    //Abstract method that must implements all shapes to determine if is selected.
    public abstract boolean containsClick(int xCoordinate, int yCoordinate);

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
    
    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }
}