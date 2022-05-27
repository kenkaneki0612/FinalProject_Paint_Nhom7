package model;

import java.io.Serializable;
import java.util.ArrayList;
import shapes.Shape;

//Represent model in MVC architectural pattern. Contains application data.
public class Model implements Serializable {
	private static final long serialVersionUID = 1L;
	private ArrayList<Shape> shapes;
	
	public Model() {
		shapes = new ArrayList<Shape>();
	}
	
	//Add new shape.
	public void add(Shape shape) {
		shapes.add(shape);
	}
	
	//Add new shape to specified index.
	public void addToIndex(int index, Shape shape) {
		shapes.add(index, shape);
	}
	
	//Add multiple shapes to list of shapes.
	public void addMultiple(ArrayList<Shape> shapes) {
		this.shapes.addAll(shapes);
	}
	
	//Remove shape from list of shapes.
	public void remove(Shape shape) {
		shapes.remove(shape);
	}
	
	//Remove shape at specified index.
	public void removeAtIndex(int index) {
		shapes.remove(index);
	}
	
	//Remove multiple shapes from list of shapes.
	public void removeMultiple(ArrayList<Shape> shapes) {
		this.shapes.removeAll(shapes);
	}

	//Remove all shapes from list of shapes.
	public void removeAll() {
		shapes.clear();
	}
	
	public Shape getByIndex(int index) {
		return shapes.get(index);
	}
	
	public int getIndexOf(Shape shape) {
		return shapes.indexOf(shape);
	}
	
	public ArrayList<Shape> getAll() {
		return shapes;
	}
}