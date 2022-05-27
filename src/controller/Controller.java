package controller;

import java.awt.Color;
import java.awt.event.MouseEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Stack;
import javax.swing.DefaultListModel;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

import command.addShapeCommand;
import command.command;
import command.removeShapeCommand;
import command.selectShapeCommand;
import command.toBackCommand;
import command.toFrontCommand;
import command.updateCircleCommand;
import command.updateLineCommand;
import command.updatePointCommand;
import command.updateRectangleCommand;
import command.updateSquareCommand;
import dialog.dialogCircle;
import dialog.dialogLine;
import dialog.dialogPoint;
import dialog.dialogRectangle;
import dialog.dialogSquare;
import frame.Frame;
import model.Model;
import observer.Observer;
import shapes.Circle;
import shapes.Square;
import strategy.FileDraw;
import strategy.FileLog;
import strategy.FileManager;
import strategy.FilePicture;
import view.View;
import shapes.Line;
import shapes.Point;
import shapes.Rectangle;
import shapes.Shape;

/*Class that represent controller in MVC architectural pattern
 * Call DrawingView to catch the click event
 */
public class Controller {
	private Model model;
	private Frame frame;
	private Point initialPointOfLine;
	private Color edgeColor = Color.BLACK;
	private Color interiorColor = Color.WHITE;
	private Color choosenEdgeColor;
	private Color choosenInteriorColor;
	private PropertyChangeSupport propertyChangeSupport;
	private int counterOfSelectedShapes = 0;
	private FileManager fileManager;
	private DefaultListModel<String> log;
	private Stack<String> undoCommandsLog;
	private Stack<command> commands;
	private Stack<command> undoCommands;
	
	public Controller(Model model, Frame frame) {
		this.model = model;
		this.frame = frame;
		initialPointOfLine = null;
		propertyChangeSupport = new PropertyChangeSupport(this);
		log = frame.getList();
		commands = new Stack<>();
		undoCommands = new Stack<>();
		undoCommandsLog = new Stack<String>();
	}
	
	//Add listener that will listen to the changes in this class
	public void addPropertyChangedListener(PropertyChangeListener propertyChangeListener) {
		propertyChangeSupport.addPropertyChangeListener(propertyChangeListener);
	}
	
	//User selects the edge color and returns the color selected by the user
	public Color btnEdgeColorClicked() {
		choosenEdgeColor = JColorChooser.showDialog(null, "Colors pallete", edgeColor);
		if (choosenEdgeColor != null) {
			if (choosenEdgeColor.equals(Color.WHITE)) {
				JOptionPane.showMessageDialog(null, "Background is white :D");
				return null;
			}
			edgeColor = choosenEdgeColor;
			return edgeColor;
		}
		return choosenEdgeColor;
	}
	
	//User selects the area color and returns the color selected by the user
	public Color btnInteriorColorClicked() {
		choosenInteriorColor = JColorChooser.showDialog(null, "Colors pallete", interiorColor);
		if (choosenInteriorColor != null) {
			interiorColor = choosenInteriorColor;
			return interiorColor;
		}
		return choosenEdgeColor;
	}
	
	//Call when user click add new point 
	public void btnAddPointClicked(MouseEvent click) {
		Point point = new Point(click.getX(), click.getY(), edgeColor);
		executeCommand(new addShapeCommand(point, model));
		log.addElement("Added->" + point.toString());
	}
	
	//Call when user click add new line
	public void btnAddLineClicked(MouseEvent click) {
		if(initialPointOfLine == null) initialPointOfLine = new Point(click.getX(), click.getY(), edgeColor);
		else {
			Line line = new Line(initialPointOfLine, new Point(click.getX(), click.getY()), edgeColor);
			executeCommand(new addShapeCommand(line, model));
			log.addElement("Added->" + line.toString());
			initialPointOfLine = null;
		}
	}
	
	//Call when user click add new Square
	public void btnAddSquareClicked(MouseEvent click) {
		dialogSquare dlgSquare = new dialogSquare();
		dlgSquare.write(click.getX(),click.getY(), frame.getView().getWidth(), frame.getView().getHeight());
		dlgSquare.deleteButtons();
		dlgSquare.setVisible(true);
		if (dlgSquare.isConfirmed()) {
			Square square = new Square(new Point(click.getX(), click.getY()), dlgSquare.getSideLength(), edgeColor, interiorColor);
			executeCommand(new addShapeCommand(square, model));
			log.addElement("Added->" + square.toString());
		}
	}
	
	//Call when user click add new Rectangle
	public void btnAddRectangleClicked(MouseEvent click) {
		dialogRectangle dlgRectangle = new dialogRectangle();
		dlgRectangle.write(click.getX(), click.getY(), frame.getView().getWidth(), frame.getView().getHeight());
		dlgRectangle.deleteButtons();
		dlgRectangle.setVisible(true);
		if (dlgRectangle.isConfirmed()) {
			Rectangle rectangle = new Rectangle(new Point(click.getX(), click.getY()), dlgRectangle.getRectangleWidth(), dlgRectangle.getRectangleHeight(), edgeColor, interiorColor);
			executeCommand(new addShapeCommand(rectangle, model));
			log.addElement("Added->" + rectangle.toString());
		}
	}
	
	//Call when user click add new Circle
	public void btnAddCircleClicked(MouseEvent click) {
		dialogCircle dlgCircle = new dialogCircle();
		dlgCircle.write(click.getX(), click.getY(), frame.getView().getWidth(), frame.getView().getHeight());
		dlgCircle.deleteButtons();
		dlgCircle.setVisible(true);
		if (dlgCircle.isConfirmed()) {
			Circle circle = new Circle(new Point(click.getX(), click.getY()), dlgCircle.getRadiusLength(), edgeColor, interiorColor);
			executeCommand(new addShapeCommand(circle, model));
			log.addElement("Added->" + circle.toString());
		}
	}
	
	//Call when user selected/ unselected some shape on a draw
	public void btnSelectShapeClicked(MouseEvent click) {
		Iterator <Shape> iterator = model.getAll().iterator();	
		ArrayList<Integer> tempListOfShapes = new ArrayList<>();
		
		while(iterator.hasNext()) {
			Shape shapeForSelection = iterator.next();
			if(shapeForSelection.containsClick(click.getX(), click.getY())) tempListOfShapes.add(model.getIndexOf(shapeForSelection));
		}
		
		if (!tempListOfShapes.isEmpty()) {
			Shape shape = model.getByIndex(Collections.max(tempListOfShapes));

			if (!shape.isSelected()) {
				++counterOfSelectedShapes;
				executeCommand(new selectShapeCommand(shape, true));
				log.addElement("Selected->" + shape.toString());
			}
			else {
				--counterOfSelectedShapes;
				executeCommand(new selectShapeCommand(shape, false));
				log.addElement("Unselected->" + shape.toString());
			}
			
			handleSelectButtons();
		}
		
		frame.getView().repaint();	
	}
	
	//Count how many shapes are selected, used to define undo and redo
	public void handleSelect(String s, String command) {
		if (command.equals("redo")) {
			if (s.equals("Selected")) ++counterOfSelectedShapes;
			else --counterOfSelectedShapes;
			handleSelectButtons();
		} else if (command.equals("undo")) {
			if (s.equals("Selected")) --counterOfSelectedShapes;
			else ++counterOfSelectedShapes;
			handleSelectButtons();
		} else if (command.equals("parser")) {
			if (s.equals("Selected")) ++counterOfSelectedShapes;
			else --counterOfSelectedShapes;
		}
	}
	
	//Handle buttons state depend on number of selected shapes.
	public void handleSelectButtons() {
		if (counterOfSelectedShapes == 0) propertyChangeSupport.firePropertyChange("shape unselected", false, true);
		else if (counterOfSelectedShapes == 1)  {
			propertyChangeSupport.firePropertyChange("update/move turn on", false, true);
			propertyChangeSupport.firePropertyChange("shape selected", false, true);
		}  
		else if (counterOfSelectedShapes > 1) propertyChangeSupport.firePropertyChange("update/move turn off", false, true);
	}
	
	//Call when user choose update shape
	public void updateShapeClicked() {
		Shape shape = getSelectedShape();
		if (shape instanceof Point) btnUpdatePointClicked((Point) shape);
		else if (shape instanceof Line) btnUpdateLineClicked((Line) shape);
		else if (shape instanceof Rectangle) btnUpdateRectangleClicked((Rectangle) shape);
		else if (shape instanceof Square) btnUpdateSquareClicked((Square) shape);
		else if (shape instanceof Circle) btnUpdateCircleClicked((Circle) shape);
		//else if (shape instanceof HexagonAdapter) btnUpdateHexagonClicked((HexagonAdapter) shape);
	}

	//Call when user want update Point on draw
	public void btnUpdatePointClicked(Point oldPoint) {
		dialogPoint dlgPoint = new dialogPoint();
		dlgPoint.write(oldPoint, frame.getView().getWidth(), frame.getView().getHeight());
		dlgPoint.setVisible(true);
		if(dlgPoint.isConfirmed()) {
			Point newPoint = new Point(dlgPoint.getXcoordinate(), dlgPoint.getYcoordinate(), dlgPoint.getColor());
			executeCommand(new updatePointCommand(oldPoint, newPoint));
			log.addElement("Updated->" + oldPoint.toString() + "->" + newPoint.toString());
		}
	}
	
	//Call when user want update Line on draw
	public void btnUpdateLineClicked(Line oldLine) {
		dialogLine dlgLine = new dialogLine();
		dlgLine.write(oldLine);
		dlgLine.setVisible(true);
		if(dlgLine.isConfirmed()) {
			Line newLine =  new Line(new Point(dlgLine.getXcoordinateInitial(), dlgLine.getYcoordinateInitial()), new Point(dlgLine.getXcoordinateLast(), dlgLine.getYcoordinateLast()), dlgLine.getColor());
			executeCommand(new updateLineCommand(oldLine, newLine));
			log.addElement("Updated->" + oldLine.toString() + "->" + newLine.toString());
		}
	}
	
	//Call when user want update Rectangle on draw
	public void btnUpdateRectangleClicked(Rectangle oldRectangle) {
		dialogRectangle dlgRectangle = new dialogRectangle();
		dlgRectangle.fillUp(oldRectangle, frame.getView().getWidth(), frame.getView().getHeight());
		dlgRectangle.setVisible(true);
		if(dlgRectangle.isConfirmed()) {
			Rectangle newRectangle = new Rectangle(new Point(dlgRectangle.getXcoordinate(), dlgRectangle.getYcoordinate()), dlgRectangle.getRectangleWidth(), dlgRectangle.getRectangleHeight(), dlgRectangle.getEdgeColor(), dlgRectangle.getInteriorColor());
			executeCommand(new updateRectangleCommand(oldRectangle, newRectangle));
			log.addElement("Updated->" + oldRectangle.toString() + "->" + newRectangle.toString());
		}
	}

	//Call when user want update Square on draw
	public void btnUpdateSquareClicked(Square oldSquare) {
		dialogSquare dlgSquare = new dialogSquare();
		dlgSquare.fillUp(oldSquare, frame.getView().getWidth(), frame.getView().getHeight());
		dlgSquare.setVisible(true);
		if(dlgSquare.isConfirmed()) {
			Square newSquare = new Square(new Point(dlgSquare.getXcoordinate(), dlgSquare.getYcoordinate()), dlgSquare.getSideLength(), dlgSquare.getEdgeColor(), dlgSquare.getInteriorColor());
			executeCommand(new updateSquareCommand(oldSquare, newSquare));
			log.addElement("Updated->" + oldSquare.toString() + "->" + newSquare.toString());
		}
	}
	
	//Call when user want update Circle on draw
	public void btnUpdateCircleClicked(Circle oldCircle) {
		dialogCircle dlgCircle = new dialogCircle();
		dlgCircle.fillUp(oldCircle, frame.getView().getWidth(), frame.getView().getHeight());
		dlgCircle.setVisible(true);
		if(dlgCircle.isConfirmed()) {
			Circle newCircle = new Circle(new Point(dlgCircle.getXcoordinateOfCenter(), dlgCircle.getYcoordinateOfCenter()), dlgCircle.getRadiusLength(), dlgCircle.getEdgeColor(), dlgCircle.getInteriorColor());
			executeCommand(new updateCircleCommand(oldCircle, newCircle));
			log.addElement("Updated->" + oldCircle.toString() + "->" + newCircle.toString());
		}
	}
	
	//Call to bring shape upwards if the shape is not at the top position
	public void toFront() {
		Shape shape = getSelectedShape();
		if (model.getIndexOf(shape) == model.getAll().size() - 1) JOptionPane.showMessageDialog(null, "Shape is already on top!");
		else {
			executeCommand(new toFrontCommand(model, shape));
			log.addElement("Moved to front->" + shape.toString());
		}
	}

	//Call to bring shape down if the shape is not in the final position
	public void toBack() {
		Shape shape = getSelectedShape();
		if (model.getIndexOf(shape) == 0) JOptionPane.showMessageDialog(null, "Shape is already on bottom!");
		else {
			executeCommand(new toBackCommand(model, shape));
			log.addElement("Moved to back->" + shape.toString());
		}
	}

	//Return shape selected 
	public Shape getSelectedShape() {
		Iterator<Shape> iterator = model.getAll().iterator();
		while(iterator.hasNext()) {
			Shape shapeForModification = iterator.next();
			if(shapeForModification.isSelected()) return shapeForModification;
		}
		return null;
	}

	//Call when user want delete shape
	public void btnDeleteShapeClicked() {
		if(JOptionPane.showConfirmDialog(null, "Are you sure that you want to delete selected shape?", "Warning!", JOptionPane.YES_NO_OPTION) == 0) {	
			Iterator<Shape> it = model.getAll().iterator();
			ArrayList<Shape> shapesForDeletion = new ArrayList<Shape>();
			
			while (it.hasNext()) {
				Shape shape = it.next();
				if(shape.isSelected()) {
					shapesForDeletion.add(shape);
					counterOfSelectedShapes--;
					log.addElement("Deleted->" + shape.toString());
				}	
			}
			
			executeCommand(new removeShapeCommand(shapesForDeletion, model));
			handleSelectButtons();
		}
	}
	
	//Method that execute some command
	public void executeCommand(command command) {
		command.execute();
		commands.push(command);
	
		if (!undoCommands.isEmpty()) {
			undoCommands.removeAllElements();
			propertyChangeSupport.firePropertyChange("redo turn off", false, true);
		}
		
		if (model.getAll().isEmpty()) propertyChangeSupport.firePropertyChange("shape don't exist", false, true);
		else if (model.getAll().size() == 1) propertyChangeSupport.firePropertyChange("shape exist", false, true);
		
		if (commands.isEmpty()) propertyChangeSupport.firePropertyChange("draw is empty", false, true);
		else if (commands.size() == 1) propertyChangeSupport.firePropertyChange("draw is not empty", false, true);
		frame.getView().repaint();
	}
	
	//Method that unexecute (undo) some command
	public void undo() {
		commands.peek().unexecute();
		if (commands.peek() instanceof removeShapeCommand) {
			int i = ((removeShapeCommand) commands.peek()).getSize();
			for (int j = 0; j < i; j++) undoCommandsLog.add(log.remove(log.size() - 1));
		}
		else undoCommandsLog.add(log.remove(log.size() - 1));
		if (commands.peek() instanceof selectShapeCommand) handleSelect((log.get(log.size() - 1)).split("->")[0], "undo");
		undoCommands.push(commands.pop());
		if (log.isEmpty()) propertyChangeSupport.firePropertyChange("log turn off", false, true);
		if (undoCommands.size() == 1) propertyChangeSupport.firePropertyChange("redo turn on", false, true);
		if (commands.isEmpty()) propertyChangeSupport.firePropertyChange("draw is empty", false, true);
		frame.getView().repaint();
	}
	
	//Method that execute previously unexecuted command
	public void redo() {		
		undoCommands.peek().execute();
		if (undoCommands.peek() instanceof removeShapeCommand) {
			int i = ((removeShapeCommand) undoCommands.peek()).getSize();
			for (int j = 0; j < i; j++) log.addElement(undoCommandsLog.pop());
		}
		else log.addElement(undoCommandsLog.pop());
		if (undoCommands.peek() instanceof selectShapeCommand) handleSelect((log.get(log.size() - 1)).split("->")[0], "redo");
		commands.push((undoCommands.pop()));
		if (undoCommands.isEmpty()) propertyChangeSupport.firePropertyChange("redo turn off", false, true);
		if (commands.size() == 1) {
			propertyChangeSupport.firePropertyChange("shape exist", false, true);
			propertyChangeSupport.firePropertyChange("draw is not empty", false, true);
			propertyChangeSupport.firePropertyChange("log turn on", false, true);
		}
		frame.getView().repaint();
	}
	
	//Displayed when choosing to save drawing 
	public void save() {
		JFileChooser chooser = new JFileChooser();
		chooser.setFileSelectionMode(JFileChooser.SAVE_DIALOG);
	    chooser.setFileSelectionMode(JFileChooser.FILES_ONLY); 
		chooser.enableInputMethods(false);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileHidingEnabled(false);
		chooser.setEnabled(true);
		chooser.setDialogTitle("Save");
		chooser.setAcceptAllFileFilterUsed(false);
		if (!model.getAll().isEmpty()) {
			chooser.setFileFilter(new FileNameExtensionFilter("Serialized draw", "ser"));
			chooser.setFileFilter(new FileNameExtensionFilter("Picture", "jpeg"));
		}
		if (!commands.isEmpty()) chooser.setFileFilter(new FileNameExtensionFilter("Commands log", "log"));
		if(chooser.showSaveDialog(null) == JFileChooser.APPROVE_OPTION) {
			if (chooser.getFileFilter().getDescription().equals("Serialized draw")) fileManager = new FileManager(new FileDraw(model));
			else if (chooser.getFileFilter().getDescription().equals("Commands log")) fileManager = new FileManager(new FileLog(frame, model, this));
			else fileManager = new FileManager(new FilePicture(frame));
			fileManager.save(chooser.getSelectedFile());
		}
		chooser.setVisible(false);
	}

	//Displayed when choosing to open file
	public void open() {
		JFileChooser chooser = new JFileChooser();
		chooser.enableInputMethods(true);
		chooser.setMultiSelectionEnabled(false);
		chooser.setFileHidingEnabled(false);
		chooser.setEnabled(true);
		chooser.setAcceptAllFileFilterUsed(false);
		chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		chooser.setFileSelectionMode(JFileChooser.OPEN_DIALOG);
		chooser.setFileFilter(new FileNameExtensionFilter("Serialized draw", "ser"));
		chooser.setFileFilter(new FileNameExtensionFilter("Commands log", "log"));
		if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			model.removeAll();
			log.removeAllElements();
			undoCommands.clear();
			undoCommandsLog.clear();
			commands.clear();
			frame.getView().repaint();
			if (chooser.getFileFilter().getDescription().equals("Serialized draw")) {
				fileManager = new FileManager(new FileDraw(model));
				propertyChangeSupport.firePropertyChange("serialized draw opened", false, true);
			}
			else if (chooser.getFileFilter().getDescription().equals("Commands log")) fileManager = new FileManager(new FileLog(frame, model, this));
			fileManager.open(chooser.getSelectedFile());
		}	
		chooser.setVisible(false);
	}

	//Create a new drawing
	public void newDraw() {
		if(JOptionPane.showConfirmDialog(null, "Are you sure that you want to start new draw?", "Warning!", JOptionPane.YES_NO_OPTION) == 0) {	
			model.removeAll();
			log.removeAllElements();
			undoCommands.clear();
			undoCommandsLog.clear();
			commands.clear();
			propertyChangeSupport.firePropertyChange("draw is empty", false, true);
			frame.getView().repaint();
		}
	}
}