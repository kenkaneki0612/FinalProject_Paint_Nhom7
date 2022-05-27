package strategy;

import java.io.File;

//Class that contains appropriate FileHandler and call his methods.
public class FileManager implements FileHandler {
	private FileHandler fileHandler;
	
	public FileManager(FileHandler fileHandler) {
		this.fileHandler = fileHandler;
	}
	
	//Call method to save forwarded file.
	@Override
	public void save(File file) {
		fileHandler.save(file);
	}

	//Call method to open forwarded file.
	@Override
	public void open(File file) {
		fileHandler.open(file);
	}
}