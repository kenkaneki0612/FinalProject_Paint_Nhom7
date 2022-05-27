package command;

import model.Model;
import shapes.Shape;

//Class move shape to front
public class toFrontCommand implements command {
	private Model model;
	private Shape shape;
	private int index;

	public toFrontCommand(Model model, Shape shape) {
		this.model = model;
		this.shape = shape;
	}

	//Get index and bring up or return to the old place
	@Override
	public void execute() {
		index =  model.getIndexOf(shape);
		model.removeAtIndex(index);
		model.addToIndex(index + 1, shape);
	}

	@Override
	public void unexecute() {
		model.removeAtIndex(index + 1);
		model.addToIndex(index, shape);
	}
}