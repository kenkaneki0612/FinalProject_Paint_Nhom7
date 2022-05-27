package command;

import model.Model;
import shapes.Shape;

//Class add new shape
public class addShapeCommand implements command {
	private Shape shape;
	private Model model;
	
	public addShapeCommand(Shape shape, Model model) {
		this.shape = shape;
		this.model = model;
	}
	
	@Override
	public void execute() {
		model.add(shape);
	}

	@Override
	public void unexecute() {	
		model.remove(shape);
	}
}