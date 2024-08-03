
package util;

/**
 * ゲームコンテンツのI/Oに関する例外です.
 * <br>
 * 通常は、java.io.IOExceptionをラップします。<br>
 *
 * <br>
 * @version 1.0.0 - 2013/04/20_18:58:59<br>
 * @author Shinacho<br>
 */
public class ContentsIOException extends RuntimeException {

	private static final long serialVersionUID = -8593840664351731828L;

	/**
	 * 新しい ContentsIOException のインスタンスを作成.
	 */
	public ContentsIOException() {
	}

	/**
	 * 新しい ContentsIOException のインスタンスを作成.
	 * @param string この例外のメッセージ.<br>
	 */
	public ContentsIOException(String string) {
		super(string);
	}

	/**
	 * Throwableをラップする例外を作成します.
	 * @param thrwbl 投げられた例外を指定します。<br>
	 */
	public ContentsIOException(Throwable thrwbl) {
		super(thrwbl);
	}
}
