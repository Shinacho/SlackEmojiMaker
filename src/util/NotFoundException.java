
package util;


/**
 * 指定されたオブジェクトが存在しなかったことを通知する例外です.
 * <br>
 *
 * <br>
 * @version 1.0.0 - 2013/04/20_17:58:50<br>
 * @author Shinacho<br>
 */
public abstract class NotFoundException extends ContentsIOException {

	private static final long serialVersionUID = -951498720683908364L;

	/**
	 * 新しい NotFoundException のインスタンスを作成.
	 */
	public NotFoundException() {
	}

	/**
	 * 新しい NotFoundException のインスタンスを作成.
	 * @param msg この例外のメッセージ.<br>
	 */
	public NotFoundException(String msg) {
		super(msg);
	}

	/**
	 * Throwableをラップする例外を作成します.
	 * @param thrwbl 投げられた例外を送信します。<br>
	 */
	public NotFoundException(Throwable thrwbl) {
		super(thrwbl);
	}
}
