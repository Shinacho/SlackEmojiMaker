
package util;

import java.io.File;

/**
 *
 * @vesion 1.0.0 - 2021/11/04_16:50:38<br>
 * @author Shinacho<br>
 */
public class FileNotFoundException extends NotFoundException {

	public FileNotFoundException() {
	}

	public FileNotFoundException(File f) {
		this(f.getName() + " is not found");
	}

	public FileNotFoundException(String val) {
		super(val);
	}

	public FileNotFoundException(Exception ex) {
		super(ex);
	}
}
