package command;

import java.util.ArrayList;
import model.Model;
import shapes.Shape;

//Class remove shape
public class removeShapeCommand implements command {
	private ArrayList<Shape> shapes;
	private Shape shape;
	private Model model;
	
	public removeShapeCommand(ArrayList<Shape> shapes, Model model) {
		this.shapes = shapes;
		this.model = model;
	}
	
	public removeShapeCommand(Shape shape, Model model) {
		this.shape = shape;
		this.model = model;	
	}

	//Remove
	@Override
	public void execute() { 
		if (shapes != null) model.removeMultiple(shapes);
		else model.remove(shape);
	}

	//Redo/ Undo
	@Override
	public void unexecute() {
		if (shapes != null) model.addMultiple(shapes);
		else model.add(shape);
	}
	
	public int getSize() {
		return shapes.size();
	}
}