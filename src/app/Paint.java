package app;

import controller.Controller;
import frame.Frame;
import model.Model;

//Main
public class Paint {

	public static void main(String[] args) {
		Frame frame = new Frame();
		frame.setVisible(true);
		frame.setTitle("Paint application using design pattern");
		Model model = new Model();
		frame.getView().setModel(model);
		frame.setController(new Controller(model, frame));
	}
}