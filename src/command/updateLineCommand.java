package command;

import shapes.Line;

//Class update line
public class updateLineCommand implements command {
	private Line oldState;
	private Line newState;
	private Line originalState;
	
	public updateLineCommand(Line oldState, Line newState) {
		this.oldState = oldState;
		this.newState = newState;
	}
	
	//Update line
	@Override
	public void execute() {
		originalState = oldState.clone();
		oldState.setInitial(newState.getInitial().clone());
		oldState.setLast(newState.getLast().clone());
		oldState.setColor(newState.getColor());
	}

	//Undo line
	@Override
	public void unexecute() {
		oldState.setInitial(originalState.getInitial());
		oldState.setLast(originalState.getLast());
		oldState.setColor(originalState.getColor());
	}
}