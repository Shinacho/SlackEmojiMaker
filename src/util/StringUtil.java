
package util;


/**
 * 文字列操作ユーティリティです.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2012/06/16_15:23:39.<br>
 * @author Shinacho<br>
 */
public final class StringUtil {

	/**
	 * ユーティリティクラスです.
	 */
	private StringUtil() {
	}

	public static boolean is半角(char c) {
		// Unicodeブロックをチェック
		Character.UnicodeBlock block = Character.UnicodeBlock.of(c);

		// 半角カタカナや基本ラテン文字などを半角と判定
		if (block == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS
				|| block == Character.UnicodeBlock.BASIC_LATIN
				|| block == Character.UnicodeBlock.LATIN_1_SUPPLEMENT
				|| block == Character.UnicodeBlock.KATAKANA) {
			return c < 0xFF;
		}

		// 上記以外のブロックに含まれる文字は全角と判定
		return false;
	}

	public static String spaceOf(String v) {
		StringBuilder sb = new StringBuilder();
		for (char c : v.toCharArray()) {
			sb.append(is半角(c) ? " " : "　");
		}
		return sb.toString();
	}

	/**
	 * 文字列をlengthの長さになるよう右詰し、空いたスペースに" "を挿入します.
	 *
	 * @param msg 対象文字列.<br>
	 * @param length 操作後の全体の長さ.<br>
	 *
	 * @return 右詰された文字列.<br>
	 */
	public static String toRight(String msg, int length) {
		String res = "";
		for (int i = 0; i < length - msg.length(); i++) {
			res += " ";
		}
		res += msg;
		return res;
	}

	public static String zeroUme(int msg, int length) {
		return zeroUme(msg + "", length);
	}

	/**
	 * 文字列をlengthの長さになるよう右詰し、空いたスペースに"0"を挿入します.
	 *
	 * @param msg 対象文字列.<br>
	 * @param length 操作後の全体の長さ.<br>
	 *
	 * @return 右詰された文字列.<br>
	 */
	public static String zeroUme(String msg, int length) {
		if (msg.length() >= length) {
			return msg;
		}
		String res = "";
		for (int i = 0; i < length - msg.length(); i++) {
			res += "0";
		}
		res += msg;
		return res;
	}

	/**
	 * 指定された文字列からファイル名を抽出します. たとえば/hoge/piyo/fuga/a.cのときa.cを返します。<br>
	 * 文字列の終端が"/"である場合はその文字列自体を返します。<br>
	 * 文字列内に"/"が存在しない場合もその文字列自体を返します。<br>
	 *
	 * @param path ファイル名を抽出するパスを送信します。<br>
	 *
	 * @return パス中からファイル名を抽出して返します。<br>
	 */
	public static String fileName(String path) {
		return path.endsWith("/") ? path : path.substring(path.lastIndexOf('/') + 1, path.length());
	}

	public static int[] parseIntCSV(String value)
			throws NumberFormatException {
		String[] values = value.split(",");
		int[] result = new int[values.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Integer.parseInt(values[i]);
		}
		return result;
	}

	public static int[] parseIntCSV(String value, String separator)
			throws NumberFormatException {
		if (!value.contains(separator)) {
			return new int[]{Integer.parseInt(value)};
		}
		String[] values = value.split(separator);
		int[] result = new int[values.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = Integer.parseInt(values[i]);
		}
		return result;
	}

	public static boolean isDigit(String val) {
		boolean dg = true;
		for (char ch : val.toCharArray()) {
			dg &= (ch <= '9' & ch >= '0');
		}
		return dg;
	}

	public static String[] safeSplit(String val, String sep) {
		if (val == null || val.isEmpty()) {
			return new String[]{};
		}
		return val.contains(sep) ? val.split(sep) : new String[]{val};
	}

}
