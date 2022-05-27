package strategy;

import java.awt.Robot;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import frame.Frame;

//Class that is responsible to save draw as jpeg.
public class FilePicture implements FileHandler {
	private Frame frame;
	
	public FilePicture(Frame frame) {
		this.frame = frame;
	}
	
	//Save currently draw as picture. 
	@Override
	public void save(File file) {
		 BufferedImage imagebuffer = null;
		    try {
		        imagebuffer = new Robot().createScreenCapture(frame.getView().getBounds());
		        frame.getView().paint(imagebuffer.createGraphics());
		        ImageIO.write(imagebuffer,"jpeg", new File(file + ".jpeg"));
		    } catch (Exception e) {
		        System.out.println(e.getMessage());
		    }
	}

	//Not implemented.
	@Override
	public void open(File file) {}
}