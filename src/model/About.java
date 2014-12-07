package model;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;
import javax.swing.JList;
import javax.swing.AbstractListModel;
import javax.swing.UIManager;

public class About extends JDialog {

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		UIManager.LookAndFeelInfo plafinfo[] = UIManager
				.getInstalledLookAndFeels();
		boolean nimbusfound = false;
		int nimbusindex = 0;

		for (int look = 0; look < plafinfo.length; look++) {
			if (plafinfo[look].getClassName().toLowerCase().contains("nimbus")) {
				nimbusfound = true;
				nimbusindex = look;
			}
		}

		try {

			if (nimbusfound) {
				UIManager.setLookAndFeel(plafinfo[nimbusindex].getClassName());
			} else

				UIManager.setLookAndFeel(UIManager
						.getCrossPlatformLookAndFeelClassName());

		} catch (Exception e) {
		}
		JFrame.setDefaultLookAndFeelDecorated(true);
		JDialog.setDefaultLookAndFeelDecorated(true);
		try {
			About dialog = new About();
			dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
			dialog.setVisible(true);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Create the dialog.
	 */
	public About() {
		setIconImage(Toolkit.getDefaultToolkit().getImage(
				"logo.png"));
		setTitle("About");
		setBounds(100, 100, 467, 387);
		getContentPane().setLayout(null);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setBounds(176, 0, 105, 163);
		lblNewLabel.setIcon(new ImageIcon(
				"logo.png"));
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblNewLabel);

		JLabel lblDigitalReaderV = new JLabel("Digital Reader v0.1 beta");
		lblDigitalReaderV.setBounds(146, 164, 167, 28);
		lblDigitalReaderV.setHorizontalAlignment(SwingConstants.CENTER);
		getContentPane().add(lblDigitalReaderV);

		JList list = new JList();
		list.setBounds(186, 204, 100, 100);
		list.setModel(new AbstractListModel() {
			String[] values = new String[] { "     Team", "Chetan.H.A",
					"Gowtham.A.M", "Pavan.P.Naik", "Yogesh.K.S" };

			public int getSize() {
				return values.length;
			}

			public Object getElementAt(int index) {
				return values[index];
			}
		});
		getContentPane().add(list);
	}

}
