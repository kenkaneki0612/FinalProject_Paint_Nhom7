package command;

import shapes.Point;

//Class update point
public class updatePointCommand implements command {
	private Point oldState;
	private Point newState;
	private Point originalState;
	
	public updatePointCommand(Point oldState, Point newState) {
		this.oldState = oldState;
		this.newState = newState;
	}
	
	//Update point
	@Override
	public void execute() {
		originalState = oldState.clone();
		oldState.moveTo(newState.getXcoordinate(), newState.getYcoordinate());
		oldState.setColor(newState.getColor());
	}

	//Undo point
	@Override
	public void unexecute() {
		oldState.moveTo(originalState.getXcoordinate(), originalState.getYcoordinate());
		oldState.setColor(originalState.getColor());
	}
}