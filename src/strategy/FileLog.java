package strategy;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.DefaultListModel;

import command.addShapeCommand;
import command.removeShapeCommand;
import command.selectShapeCommand;
import command.toBackCommand;
import command.toFrontCommand;
import command.updateCircleCommand;
import command.updateLineCommand;
import command.updatePointCommand;
import command.updateRectangleCommand;
import command.updateSquareCommand;
import controller.Controller;
import dialog.dialogLogParser;
import frame.Frame;
//import hexagon.Hexagon;
import model.Model;
import shapes.Circle;
import shapes.Line;
import shapes.Point;
import shapes.Rectangle;
import shapes.Shape;
import shapes.Square;

//Class that is responsible to save and parse log of executed commands.
public class FileLog implements FileHandler {
	private BufferedWriter writer;
	private BufferedReader reader;
	private Frame frame;
	private Model model;
	private Controller controller;
	private dialogLogParser logParser;
	
	public FileLog(Frame frame, Model model, Controller controller) {
		this.frame = frame;
		this.model = model; 
		this.controller = controller;
	}
	
	//Save forwarded file as log of commands.
	@Override
	public void save(File file) {
		try {
			writer = new BufferedWriter(new FileWriter(file + ".log"));
			DefaultListModel<String> list = frame.getList();
			for (int i = 0; i < frame.getList().size(); i++) {
				writer.write(list.getElementAt(i));
				writer.newLine();
			}
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
		try {
			writer.close();
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}

	//Open forwarded log file and execute it command by command in interaction with user.
	@Override
	public void open(File file) {
		try {
			reader = new BufferedReader(new FileReader(file));
			logParser = new dialogLogParser();
			logParser.setFileLog(this);
			logParser.addCommand(reader.readLine());
			logParser.setVisible(true);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	//Read one line from log file and parse it.
	public void readLine(String command) {
		try {
			String[] commands1 = command.split("->");
			switch(commands1[0]) {
				case "Added":
					Shape shape = parseShape(commands1[1].split(":")[0], commands1[1].split(":")[1]);
					controller.executeCommand(new addShapeCommand(shape, model));
					frame.getList().addElement("Added->" + shape.toString());
					break;
				case "Updated":
					Shape oldShape = parseShape(commands1[1].split(":")[0], commands1[1].split(":")[1]);
					int index = model.getIndexOf(oldShape);
					if (oldShape instanceof Point) {
						Point newPoint = parsePoint(commands1[2].split(":")[1]);
						controller.executeCommand(new updatePointCommand((Point) model.getByIndex(index), newPoint));
						frame.getList().addElement("Updated->" + oldShape.toString() + "->" + newPoint.toString());
					}
					else if (oldShape instanceof Line) {
						Line newLine = parseLine(commands1[2].split(":")[1]);
						controller.executeCommand(new updateLineCommand((Line) model.getByIndex(index), newLine));
						frame.getList().addElement("Updated->" + oldShape.toString() + "->" + newLine.toString());
					}
					else if (oldShape instanceof Rectangle) {
						Rectangle newRectangle = parseRectangle(commands1[2].split(":")[1]);
						controller.executeCommand(new updateRectangleCommand((Rectangle) model.getByIndex(index), newRectangle));
						frame.getList().addElement("Updated->" + oldShape.toString() + "->" + newRectangle.toString());
					}
					else if (oldShape instanceof Square) {
						Square newSquare = parseSquare(commands1[2].split(":")[1]);
						controller.executeCommand(new updateSquareCommand((Square) model.getByIndex(index), newSquare));
						frame.getList().addElement("Updated->" + oldShape.toString() + "->" + newSquare.toString());
					}
					else if (oldShape instanceof Circle) {
						Circle newCircle = parseCircle(commands1[2].split(":")[1]);
						controller.executeCommand(new updateCircleCommand((Circle) model.getByIndex(index), newCircle));
						frame.getList().addElement("Updated->" + oldShape.toString() + "->" + newCircle.toString());
					}
					break;
				case "Deleted":
					Shape deletedShape = parseShape(commands1[1].split(":")[0], commands1[1].split(":")[1]);
					controller.executeCommand(new removeShapeCommand(deletedShape, model)); 
					frame.getList().addElement("Deleted->" + deletedShape.toString());
					controller.handleSelect("Deleted", "parser");
					break;
				case "Moved to front":
					Shape shapeMovedToFront = parseShape(commands1[1].split(":")[0], commands1[1].split(":")[1]);
					controller.executeCommand(new toFrontCommand(model, shapeMovedToFront));
					frame.getList().addElement("Moved to front->" + shapeMovedToFront.toString());
					break;
				case "Moved to back":
					Shape shapeMovedToBack = parseShape(commands1[1].split(":")[0], commands1[1].split(":")[1]);
					controller.executeCommand(new toBackCommand(model, shapeMovedToBack));
					frame.getList().addElement("Moved to back->" + shapeMovedToBack.toString());
					break;
				case "Selected":
					Shape selectedShape = parseShape(commands1[1].split(":")[0], commands1[1].split(":")[1]);
					int index1 = model.getIndexOf(selectedShape);
					controller.executeCommand(new selectShapeCommand(model.getByIndex(index1), true));
					frame.getList().addElement("Selected->" + selectedShape.toString());
					controller.handleSelect("Selected", "parser");
					break;
				case "Unselected":
					Shape unselectedShape = parseShape(commands1[1].split(":")[0], commands1[1].split(":")[1]);
					int index2 = model.getIndexOf(unselectedShape);
					controller.executeCommand(new selectShapeCommand(model.getByIndex(index2), false));
					frame.getList().addElement("Unselected->" + unselectedShape.toString());
					controller.handleSelect("Unselected", "parser");
					break;
			}
		
			String line = reader.readLine();
			if (line != null) logParser.addCommand(line);
			else {
				logParser.closeDialog();
				return;
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	//Determine which type of shape need to be parsed and call appropriate method.
	private Shape parseShape(String shape, String shapeParameters) {
		if (shape.equals("Point")) return parsePoint(shapeParameters);
		else if (shape.equals("Line")) return parseLine(shapeParameters);
		else if (shape.equals("Circle")) return parseCircle(shapeParameters);
		else if (shape.equals("Rectangle")) return parseRectangle(shapeParameters);
		else return parseSquare(shapeParameters);
	}

	//Method that parse Point from log file.
	private Point parsePoint(String string) {
		String [] pointParts = string.split(";"); 		
		String s = pointParts[2].split("=")[1].substring(1, pointParts[2].split("=")[1].length() - 1);
		String [] colors = s.split(",");
		return new Point(Integer.parseInt(pointParts[0].split("=")[1]), Integer.parseInt(pointParts[1].split("=")[1]), 
				new Color(Integer.parseInt(colors[0].split("-")[1]), Integer.parseInt(colors[1].split("-")[1]), 
						Integer.parseInt(colors[2].split("-")[1])));
	}
	
	//Method that parse Circle from log file.
	private Circle parseCircle(String string) {
		String [] circleParts = string.split(";"); 	
		int radius = Integer.parseInt(circleParts[0].split("=")[1]);
		int x = Integer.parseInt(circleParts[1].split("=")[1]);
		int y = Integer.parseInt(circleParts[2].split("=")[1]);
		String s = circleParts[3].split("=")[1].substring(1, circleParts[3].split("=")[1].length() - 1);
		String [] edgeColors = s.split(",");
		String s1 = circleParts[4].split("=")[1].substring(1, circleParts[4].split("=")[1].length() - 1);
		String [] interiorColors = s1.split(",");
		return new Circle(new Point(x, y), radius, new Color(Integer.parseInt(edgeColors[0].split("-")[1]), 
				Integer.parseInt(edgeColors[1].split("-")[1]), Integer.parseInt(edgeColors[2].split("-")[1])), 
				new Color(Integer.parseInt(interiorColors[0].split("-")[1]), Integer.parseInt(interiorColors[1].split("-")[1]), 
						Integer.parseInt(interiorColors[2].split("-")[1])));
	}
	
	//Method that parse Square from log file.
	private Square parseSquare(String string) {
		String [] squareParts = string.split(";"); 	
		int x = Integer.parseInt(squareParts[0].split("=")[1]);
		int y = Integer.parseInt(squareParts[1].split("=")[1]);
		int side = Integer.parseInt(squareParts[2].split("=")[1]);
		String s = squareParts[3].split("=")[1].substring(1, squareParts[3].split("=")[1].length() - 1);
		String [] edgeColors = s.split(",");
		String s1 = squareParts[4].split("=")[1].substring(1, squareParts[4].split("=")[1].length() - 1);
		String [] interiorColors = s1.split(",");
		return new Square(new Point(x, y), side, new Color(Integer.parseInt(edgeColors[0].split("-")[1]), 
				Integer.parseInt(edgeColors[1].split("-")[1]), Integer.parseInt(edgeColors[2].split("-")[1])), 
				new Color(Integer.parseInt(interiorColors[0].split("-")[1]), Integer.parseInt(interiorColors[1].split("-")[1]), 
						Integer.parseInt(interiorColors[2].split("-")[1])));
	}

	//Method that parse Rectangle from log file.
	private Rectangle parseRectangle(String string) {
		String [] rectangleParts = string.split(";"); 	
		int x = Integer.parseInt(rectangleParts[0].split("=")[1]);
		int y = Integer.parseInt(rectangleParts[1].split("=")[1]);
		int height = Integer.parseInt(rectangleParts[2].split("=")[1]);
		int width = Integer.parseInt(rectangleParts[3].split("=")[1]);
		String s = rectangleParts[4].split("=")[1].substring(1, rectangleParts[4].split("=")[1].length() - 1);
		String [] edgeColors = s.split(",");
		String s1 = rectangleParts[5].split("=")[1].substring(1, rectangleParts[5].split("=")[1].length() - 1);
		String [] interiorColors = s1.split(",");
		return new Rectangle(new Point(x, y), width, height, new Color(Integer.parseInt(edgeColors[0].split("-")[1]), 
				Integer.parseInt(edgeColors[1].split("-")[1]), Integer.parseInt(edgeColors[2].split("-")[1])), 
				new Color(Integer.parseInt(interiorColors[0].split("-")[1]), Integer.parseInt(interiorColors[1].split("-")[1]), 
						Integer.parseInt(interiorColors[2].split("-")[1])));
	}

	//Method that parse Line from log file.
	private Line parseLine(String string) {
		String [] lineParts = string.split(";"); 	
		int xStart = Integer.parseInt(lineParts[0].split("=")[1]);
		int yStart = Integer.parseInt(lineParts[1].split("=")[1]);
		int xEnd = Integer.parseInt(lineParts[2].split("=")[1]);
		int yEnd = Integer.parseInt(lineParts[3].split("=")[1]);
		String s = lineParts[4].split("=")[1].substring(1, lineParts[4].split("=")[1].length() - 1);
		String [] edgeColors = s.split(",");
		return new Line(new Point(xStart, yStart), new Point(xEnd, yEnd), new Color(Integer.parseInt(edgeColors[0].split("-")[1]), Integer.parseInt(edgeColors[1].split("-")[1]), Integer.parseInt(edgeColors[2].split("-")[1])));
	}
}