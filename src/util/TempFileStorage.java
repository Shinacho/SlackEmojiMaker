
package util;

import java.util.ArrayList;
import java.util.List;


/**
 * 一時ファイルを管理します.
 * <br>
 * 一時ファイルの読み込み・書き出しにはテキストリーダを使用します。<br>
 * 一時ファイルは、ゲームの終了時にすべて削除される点に注意してください。<br>
 * <br>
 *
 * @version 1.0.0 - 2015/01/04<br>
 * @author Shinacho<br>
 * <br>
 */
public final class TempFileStorage {

	private static final TempFileStorage INSTANCE = new TempFileStorage();
	private List<TempFile> list = new ArrayList<>();

	private TempFileStorage() {
	}

	public static TempFileStorage getInstance() {
		return INSTANCE;
	}

	public TempFile create() throws ContentsIOException {
		TempFile file = new TempFile();
		list.add(file);
		return file;
	}

	private String prefix = "sem_";
	private String suffix = ".tmp";

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void deleteAll() {
		for (TempFile file : list) {
			if (file.exists()) {
				file.delete();
			}
		}
	}

}
