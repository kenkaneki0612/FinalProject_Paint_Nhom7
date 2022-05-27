package command;

import shapes.Shape;

//Class select shape
public class selectShapeCommand implements command {
	private Shape shape;
	private boolean selectedState;
	
	public selectShapeCommand(Shape shape, boolean selectedState) {
		this.shape = shape;
		this.selectedState = selectedState;
	}
		
	@Override
	public void execute() {
		shape.setSelected(selectedState);
	}
	
	@Override
	public void unexecute() {	
		if (shape.isSelected()) shape.setSelected(false);
		else shape.setSelected(true);
	}
}