package slackemojimaker;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.stream.IntStream;
import javax.swing.ImageIcon;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import util.ARGBColor;
import util.ContentsIOException;
import util.ImageUtil;
import util.RenderingConfig;
import util.StringUtil;
import util.TempFile;
import util.TempFileStorage;

/**
 *
 * @author Shinacho
 */
public class MainFrame extends javax.swing.JFrame {

	/**
	 * Creates new form MainFrame
	 */
	public MainFrame() {
		initComponents();
		int x = Toolkit.getDefaultToolkit().getScreenSize().width / 2 - getWidth() / 2;
		int y = Toolkit.getDefaultToolkit().getScreenSize().height / 2 - getHeight() / 2;
		setLocation(x, y);
		init();
	}

	private void init() {
		setIconImage(new ImageIcon(getClass().getResource("icon.png")).getImage());
		Color fontColor = Color.BLACK;
		Color backColor = Color.WHITE;
		jButton1.setForeground(fontColor);
		jButton2.setForeground(backColor);

		jComboBox1.addItem("256");
		jComboBox1.addItem("128");
		jComboBox1.addItem("64");

		jComboBox1.setSelectedIndex(0);

		jComboBox2.addItem(Font.MONOSPACED);
		jComboBox2.addItem(Font.SANS_SERIF);
		jComboBox2.addItem(Font.SERIF);
		jComboBox2.addItem(Font.DIALOG);
		jComboBox2.addItem(Font.DIALOG_INPUT);

		jComboBox2.setSelectedIndex(0);

		buildImage();
	}

	private BufferedImage image;

	private void buildImage() {
		if (jComboBox1.getSelectedItem() == null) {
			return;
		}
		if (jComboBox2.getSelectedItem() == null) {
			return;
		}
		//image size
		int imageSize = Integer.parseInt(jComboBox1.getSelectedItem().toString());

		//image
		image = ImageUtil.newImage(imageSize, imageSize);

		Graphics2D g = ImageUtil.createGraphics2D(image, RenderingConfig.QUALITY);
		//backColor
		g.setBackground(jButton2.getForeground());
		g.clearRect(0, 0, imageSize, imageSize);

		//色が同じ場合は終わり
		if (!jButton1.getForeground().equals(jButton2.getForeground())) {

			//text
			if (jTextArea1.getText().isEmpty()) {
				g.dispose();
				//set
				jLabel6.setIcon(new ImageIcon(image));
				jButton3.setEnabled(true);
				return;
			}

			String[] text = StringUtil.safeSplit(jTextArea1.getText(), "\n");
			List<String> list = Arrays.stream(text).filter(p -> !"".equals(p)).filter(p -> !p.trim().equals("")).filter(p -> !"　".equals(p)).toList();

			//rows
			int rows = list.size();
			if (rows == 0) {
				g.dispose();
				//set
				jLabel6.setIcon(new ImageIcon(image));
				jButton3.setEnabled(true);
				return;
			}

			//draw
			int fontSize = imageSize / rows;

			int fontMode = jCheckBox3.isSelected()
					? jCheckBox4.isSelected() ? Font.BOLD | Font.ITALIC
					: Font.BOLD : jCheckBox4.isSelected() ? Font.ITALIC : Font.PLAIN;
			Font font = new Font(jComboBox2.getSelectedItem().toString(),
					fontMode,
					fontSize);

			//draw
			for (int line = 0; line < rows; line++) {
				BufferedImage textImage = ImageUtil.newImage(4096, fontSize * 3);
				Graphics2D g2 = ImageUtil.createGraphics2D(textImage, RenderingConfig.QUALITY);
				g2.setFont(font);
				g2.setColor(jButton1.getForeground());

				int x = 0;
				int y = fontSize;
				g2.drawString(list.get(line), x, y);
				g2.dispose();

				//location
				Point start = start(textImage);
				Point end = end(textImage);
				int w = end.x - start.x;
				int h = end.y - start.y;
				if (w == 0 || h == 0) {
					JOptionPane.showConfirmDialog(this, "文字が多すぎますね", "ERROR", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
					jButton3.setEnabled(false);
					return;
				}

				//cut
				BufferedImage l = ImageUtil.trimming(textImage, start.x, start.y, w, h);

				//resize
				{
					int ww = imageSize;
					l = ImageUtil.resize(l, ww, fontSize);
				}
				//draw
				int nx = imageSize / 2 - l.getWidth() / 2;
				int ny = (fontSize * line);
				g.drawImage(l, nx, ny, null);
			}
			g.dispose();

			//ちょっと小さめ
			if (jCheckBox1.isSelected()) {
				final float scale = 0.9f;
				int newSize = (int) (image.getWidth() * scale);
				BufferedImage newImage = ImageUtil.resize(image, newSize, newSize);
				Graphics2D g2 = ImageUtil.createGraphics2D(image, RenderingConfig.QUALITY);
				//imageに上書き
				//backColor
				g2.setBackground(jButton2.getForeground());
				g2.clearRect(0, 0, imageSize, imageSize);
				//text
				int gap = (image.getWidth() - newSize) / 2;
				g2.drawImage(newImage, gap, gap, null);
				g2.dispose();
			}
			//もっと小さめ
			if (jCheckBox2.isSelected()) {
				final float scale = 0.9f;
				int newSize = (int) (image.getWidth() * scale);
				BufferedImage newImage = ImageUtil.resize(image, newSize, newSize);
				Graphics2D g2 = ImageUtil.createGraphics2D(image, RenderingConfig.QUALITY);
				//imageに上書き
				//backColor
				g2.setBackground(jButton2.getForeground());
				g2.clearRect(0, 0, imageSize, imageSize);
				//text
				int gap = (image.getWidth() - newSize) / 2;
				g2.drawImage(newImage, gap, gap, null);
				g2.dispose();
			}
		}

		//set
		jLabel6.setIcon(new ImageIcon(image));

		//size check
		TempFile f = TempFileStorage.getInstance().create();
		try {
			ImageUtil.save(f.getFile(), image);
		} catch (ContentsIOException e) {
			JOptionPane.showConfirmDialog(this, "ファイル名に使用できない文字が含まれているようですね", "ERROR", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
			return;
		}

		try {
			long size = Files.size(f.getFile().toPath());
			final long LIMIT = 128 * 1024;
			if (size >= LIMIT) {
				JOptionPane.showConfirmDialog(this, "128KBを超えているのでSlackの絵文字に使えませんね。\n画像サイズを小さくしてください。", "ERROR", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
				jButton3.setEnabled(false);
				return;
			}
		} catch (IOException ex) {
		}
		jButton3.setEnabled(true);

	}

	private Point start(BufferedImage src) {
		int text = ARGBColor.toARGB(jButton1.getForeground());

		int[][] pix = ImageUtil.getPixel2D(src);

		Point res = new Point();
		for (int y = 0; y < pix.length; y++) {
			if (IntStream.of(pix[y]).anyMatch(p -> p == text)) {
				res.y = y;
				break;
			}
		}
		pix = rotate(pix);
		for (int y = 0; y < pix.length; y++) {
			if (IntStream.of(pix[y]).anyMatch(p -> p == text)) {
				res.x = y;
				break;
			}
		}

		return res;

	}

	private Point end(BufferedImage src) {
		int text = ARGBColor.toARGB(jButton1.getForeground());

		int[][] pix = ImageUtil.getPixel2D(src);

		Point res = new Point();
		for (int y = pix.length - 1; y >= 0; y--) {
			if (IntStream.of(pix[y]).anyMatch(p -> p == text)) {
				res.y = y;
				break;
			}
		}
		pix = rotate(pix);
		for (int y = pix.length - 1; y >= 0; y--) {
			if (IntStream.of(pix[y]).anyMatch(p -> p == text)) {
				res.x = y;
				break;
			}
		}

		return res;

	}

	private int[][] rotate(int[][] p) {
		int n = p.length;
		int m = p[0].length;
		int[][] res = new int[m][n];

		for (int i = 0; i < n; i++) {
			for (int j = 0; j < m; j++) {
				res[j][n - 1 - i] = p[i][j];
			}
		}

		return res;

	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jLabel2 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jLabel3 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox<>();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jButton3 = new javax.swing.JButton();
        jLabel7 = new javax.swing.JLabel();
        jComboBox2 = new javax.swing.JComboBox<>();
        jSeparator1 = new javax.swing.JSeparator();
        jCheckBox1 = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        jCheckBox4 = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("Slack 絵文字 めーかー");
        setResizable(false);
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        jLabel1.setText("てきすと");

        jScrollPane1.setWheelScrollingEnabled(false);

        jTextArea1.setColumns(2);
        jTextArea1.setRows(2);
        jTextArea1.setText("承知\nしました");
        jTextArea1.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                jTextArea1KeyReleased(evt);
            }
        });
        jScrollPane1.setViewportView(jTextArea1);

        jLabel2.setText("文字の色");

        jButton1.setText("■");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel3.setText("背景の色");

        jButton2.setText("■");
        jButton2.setToolTipText("背景を透明にするにはRGBのアルファを0にしてください");
        jButton2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton2ActionPerformed(evt);
            }
        });

        jLabel4.setText("画像サイズ");

        jComboBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox1ActionPerformed(evt);
            }
        });

        jLabel5.setText("プレビュー");

        jLabel6.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);

        jButton3.setText("画像書き出し");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });

        jLabel7.setText("フォント");

        jComboBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboBox2ActionPerformed(evt);
            }
        });

        jCheckBox1.setText("少し小さめ");
        jCheckBox1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox1ActionPerformed(evt);
            }
        });

        jCheckBox2.setText("もっと小さく");
        jCheckBox2.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox2ActionPerformed(evt);
            }
        });

        jCheckBox3.setText("太字");
        jCheckBox3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox3ActionPerformed(evt);
            }
        });

        jCheckBox4.setText("斜体");
        jCheckBox4.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBox4ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jButton3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jSeparator1)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 78, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jCheckBox3)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox4)
                                .addGap(18, 18, 18)
                                .addComponent(jCheckBox1)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(jCheckBox2))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 64, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButton2)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                                    .addGroup(layout.createSequentialGroup()
                                        .addComponent(jButton1)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jComboBox1, 0, 139, Short.MAX_VALUE)
                                    .addComponent(jComboBox2, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jComboBox2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel2)
                            .addComponent(jButton1)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel3)
                            .addComponent(jButton2)
                            .addComponent(jLabel7)))
                    .addComponent(jScrollPane1)
                    .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jCheckBox3)
                    .addComponent(jCheckBox4)
                    .addComponent(jCheckBox1)
                    .addComponent(jCheckBox2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 379, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jButton3, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton2ActionPerformed

		Color c = JColorChooser.showDialog(this, "select color", jButton2.getForeground());

		if (c == null) {
			return;
		}
		jButton2.setForeground(c);
		buildImage();
    }//GEN-LAST:event_jButton2ActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed

		Color c = JColorChooser.showDialog(this, "select color", jButton1.getForeground());
		if (c == null) {
			return;
		}
		jButton1.setForeground(c);
		buildImage();
    }//GEN-LAST:event_jButton1ActionPerformed

    private void jComboBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox1ActionPerformed
		buildImage();
    }//GEN-LAST:event_jComboBox1ActionPerformed

    private void jComboBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboBox2ActionPerformed
		buildImage();
    }//GEN-LAST:event_jComboBox2ActionPerformed

    private void jTextArea1KeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_jTextArea1KeyReleased
		buildImage();
    }//GEN-LAST:event_jTextArea1KeyReleased

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
		TempFileStorage.getInstance().deleteAll();
    }//GEN-LAST:event_formWindowClosing

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
		JFileChooser c = new JFileChooser();
		String name = jTextArea1.getText().replaceAll("\n", "").replaceAll(" ", "").replaceAll("　", "").replaceAll("\t", "");
		c.setSelectedFile(new File(System.getProperty("user.home") + "/Desktop/" + name + ".png"));
		int r = c.showSaveDialog(this);
		if (r != JFileChooser.APPROVE_OPTION) {
			return;
		}
		File tgt = c.getSelectedFile();
		if (tgt.exists()) {
			int rr = JOptionPane.showConfirmDialog(this, "でも「" + tgt.getName() + "」はすでに存在しているんですよね。\n上書きするってことでいいんですかね・・・？", "確認", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
			if (rr != JOptionPane.YES_OPTION) {
				return;
			}
		}

		try {
			ImageUtil.save(tgt, image);
		} catch (ContentsIOException e) {
			JOptionPane.showConfirmDialog(this, "ファイル名に使用できない文字が含まれているようですね", "ERROR", JOptionPane.DEFAULT_OPTION, JOptionPane.ERROR_MESSAGE);
		}
    }//GEN-LAST:event_jButton3ActionPerformed

    private void jCheckBox1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox1ActionPerformed
		buildImage();
    }//GEN-LAST:event_jCheckBox1ActionPerformed

    private void jCheckBox2ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox2ActionPerformed
		buildImage();
    }//GEN-LAST:event_jCheckBox2ActionPerformed

    private void jCheckBox3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox3ActionPerformed
		buildImage();
    }//GEN-LAST:event_jCheckBox3ActionPerformed

    private void jCheckBox4ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBox4ActionPerformed
		buildImage();
    }//GEN-LAST:event_jCheckBox4ActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
		//<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
		/* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(MainFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
		//</editor-fold>

		/* Create and display the form */
		SwingUtilities.invokeLater(() -> {
			new MainFrame().setVisible(true);
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JCheckBox jCheckBox1;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JCheckBox jCheckBox4;
    private javax.swing.JComboBox<String> jComboBox1;
    private javax.swing.JComboBox<String> jComboBox2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables
}
