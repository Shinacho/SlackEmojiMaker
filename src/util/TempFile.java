
package util;

import java.io.File;
import java.io.IOException;

/**
 * 一時ファイルです.
 * <br>
 *
 * @version 1.0.0 - 2015/01/04<br>
 * @author Shinacho<br>
 * <br>
 */
public final class TempFile{

	private File file;

	protected TempFile() throws ContentsIOException {
		this(TempFileStorage.getInstance().getPrefix(),
				TempFileStorage.getInstance().getSuffix());
	}

	protected TempFile(String prefix, String suffix) throws ContentsIOException {
		try {
			file = File.createTempFile(
					prefix,
					suffix);
		} catch (IOException e) {
			throw new ContentsIOException(e);
		}
	}

	public String getPath() {
		return file.getPath();
	}

	public File getFile() {
		return file;
	}

	public boolean exists() {
		return file.exists();
	}

	public void delete() {
		file.delete();
	}


}
