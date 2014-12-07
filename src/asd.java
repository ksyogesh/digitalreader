import javax.swing.JPanel;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.SwingConstants;
import javax.swing.ImageIcon;


public class asd extends JPanel {

	/**
	 * Create the panel.
	 */
	public asd() {
		setLayout(null);
		
		JLabel lblNewLabel = new JLabel("WELCOME TO DIGITAL READER");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setFont(new Font("Century Schoolbook", Font.BOLD, 20));
		lblNewLabel.setBounds(0, 0, 1272, 29);
		add(lblNewLabel);
		
		JLabel lblNewLabel_1 = new JLabel("");
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_1.setIcon(new ImageIcon("flip1.jpg"));
		lblNewLabel_1.setBounds(0, 43, 1272, 359);
		add(lblNewLabel_1);
		
		JLabel lblNewLabel_2 = new JLabel("Click on any of the buttons to get prompted to that window");
		lblNewLabel_2.setFont(new Font("Dialog", Font.BOLD, 12));
		lblNewLabel_2.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel_2.setBounds(0, 414, 1272, 14);
		add(lblNewLabel_2);

	}

}
