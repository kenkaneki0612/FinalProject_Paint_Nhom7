package command;

import shapes.Rectangle;

//Class update Rectangle
public class updateRectangleCommand implements command {
	private Rectangle oldState;
	private Rectangle newState;
	private Rectangle originalState;
	
	public updateRectangleCommand(Rectangle oldState, Rectangle newState) {
		this.oldState = oldState;
		this.newState = newState;
	}
	
	//Update rectangle.
	@Override
	public void execute() {
		originalState = oldState.clone();
		oldState.setUpLeft(newState.getUpLeft().clone());
		oldState.setWidth(newState.getWidth());
		oldState.setSide(newState.getSide());
		oldState.setColor(newState.getColor());
		oldState.setInteriorColor(newState.getInteriorColor());
	}

	//Undo Rectangle
	@Override
	public void unexecute() {
		oldState.setUpLeft(originalState.getUpLeft());
		oldState.setWidth(originalState.getWidth());
		oldState.setSide(originalState.getSide());
		oldState.setColor(originalState.getColor());
		oldState.setInteriorColor(originalState.getInteriorColor());
	}
}