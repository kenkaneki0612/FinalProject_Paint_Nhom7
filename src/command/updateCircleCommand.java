package command;

import shapes.Circle;

//Class update Circle 
public class updateCircleCommand implements command {
	private Circle oldState;
	private Circle newState;
	private Circle originalState;
	
	public updateCircleCommand(Circle oldState, Circle newState) {
		this.oldState = oldState;
		this.newState = newState;
	}
	
	//Update Circle
	@Override
	public void execute() {
		originalState = oldState.clone();
		oldState.setRadius(newState.getRadius());
		oldState.setCenter(newState.getCenter().clone());
		oldState.setInteriorColor(newState.getInteriorColor());
		oldState.setColor(newState.getColor());
	}

	//Undo Circle
	@Override
	public void unexecute() {
		oldState.setRadius(originalState.getRadius());
		oldState.setCenter(originalState.getCenter());
		oldState.setInteriorColor(originalState.getInteriorColor());
		oldState.setColor(originalState.getColor());
	}
}