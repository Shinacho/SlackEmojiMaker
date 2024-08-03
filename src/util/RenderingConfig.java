
package util;

import java.awt.RenderingHints;
import java.util.Collections;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_6:23:18<br>
 * @author Shinacho<br>
 */
public enum RenderingConfig {

	/**
	 * レンダリングヒントを全く設定しません. 全ての設定は規定値となります。<br>
	 */
	NOT_USE {
		@Override
		public RenderingHints getRenderingHints() {
			if (hints == null) {
				hints = new RenderingHints(Collections.<RenderingHints.Key, Object>emptyMap());
			}
			return hints;
		}
	},
	/**
	 * 速度を優先する設定です.
	 */
	SPEED {
		@Override
		public RenderingHints getRenderingHints() {
			if (hints == null) {
				hints = new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_SPEED);
				hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
				hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_DISABLE);
				hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
				hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
			}
			return hints;
		}
	},
	/**
	 * 描画品質を優先する設定です.
	 */
	QUALITY {
		@Override
		public RenderingHints getRenderingHints() {
			if (hints == null) {
				hints = new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
				hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
				hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}
			return hints;
		}
	},
	/**
	 * 推奨設定です.特に設定しない場合の規定値となります.
	 */
	DEFAULT {
		@Override
		public RenderingHints getRenderingHints() {
			if (hints == null) {
				hints = new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_DEFAULT);
				hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
				hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
				hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				hints.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				hints.put(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
			}
			return hints;
		}
	};
	private static RenderingHints hints;

	public abstract RenderingHints getRenderingHints();
}
