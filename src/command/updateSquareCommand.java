package command;

import shapes.Square;

//Class update Square
public class updateSquareCommand implements command {
	private Square oldState;
	private Square newState;
	private Square originalState;
	
	public updateSquareCommand(Square oldState, Square newState) {
		this.oldState = oldState;
		this.newState = newState;
	}
	
	//Update Square
	@Override
	public void execute() {
		originalState = oldState.clone();
		oldState.setUpLeft(newState.getUpLeft().clone());
		oldState.setSide(newState.getSide());
		oldState.setColor(newState.getColor());
		oldState.setInteriorColor(newState.getInteriorColor());
	}

	//Undo Square
	@Override
	public void unexecute() {
		oldState.setUpLeft(originalState.getUpLeft());
		oldState.setSide(originalState.getSide());
		oldState.setColor(originalState.getColor());
		oldState.setInteriorColor(originalState.getInteriorColor());
	}
}