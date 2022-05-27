package view;

import java.awt.Graphics;
import java.util.Iterator;
import javax.swing.JPanel;
import controller.Controller;
import model.Model;
import shapes.Shape;

	//Represent view in MVC architectural pattern.
public class View extends JPanel {
	private static final long serialVersionUID = 1L;
	private Model model;
	
	public View() {}

	//When DrawingModel change, paint changes triggered by DrawingController to draw.
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		if (model != null) {
			Iterator<Shape> it = model.getAll().iterator();
			while (it.hasNext()) it.next().draw(g);
		}
	}
	
	public void setModel(Model model) {
		this.model = model;
	}
}