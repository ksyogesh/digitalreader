import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.JMenuBar;

import org.jpedal.examples.jpaneldemo.JPanelDemo;

import java.awt.Button;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import javax.swing.SwingConstants;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.ImageIcon;
import java.awt.Toolkit;

public class textreader1 extends JFrame {

	private JPanel contentPane;

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
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					textreader1 frame = new textreader1();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public textreader1() {
		setTitle("DIGITAL READER");
		setIconImage(Toolkit.getDefaultToolkit().getImage("digi1.jpg"));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 507, 358);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JButton btnText = new JButton("       TEXT       ");
		btnText.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if (s == "       TEXT       ") {
					/**********************************************
					 * This is where i am calling the window2 *
					 *********************************************/
					BasicTextEditor tester = new BasicTextEditor();
					tester.setVisible(true);
					setVisible(false);
				}
			}
		});
		menuBar.add(btnText);

		JButton btnPdf = new JButton("        PDF         ");
		btnPdf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if (s == "        PDF         ") {
					/**********************************************
					 * This is where i am calling the window2 *
					 *********************************************/
					JPanelDemo tester = new JPanelDemo();
					tester.setVisible(true);
					setVisible(false);
				}
			}
		});
		menuBar.add(btnPdf);

		JButton btnRtf = new JButton("          RTF         ");
		btnRtf.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if (s == "          RTF         ") {
					/**********************************************
					 * This is where i am calling the window2 *
					 *********************************************/
					WordProcessor tester = new WordProcessor();
					tester.setVisible(true);
					setVisible(false);
				}
			}
		});
		menuBar.add(btnRtf);

		JButton btnHtml = new JButton("           HTML         ");
		btnHtml.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if (s == "           HTML         ") {
					/**********************************************
					 * This is where i am calling the window2 *
					 *********************************************/
					HtmlProcessor tester = new HtmlProcessor();
					tester.setVisible(true);
					setVisible(false);
				}
			}
		});
		menuBar.add(btnHtml);

		JButton btnAbout = new JButton("About");
		btnAbout.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String s = e.getActionCommand();
				if (s == "About") {
					/**********************************************
					 * This is where i am calling the window2 *
					 *********************************************/
					About tester = new About();
					tester.setVisible(true);
					// this.setVisible(false);
				}
			}
		});
		menuBar.add(btnAbout);

		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JLabel lblWelcomeToDigital = new JLabel("WELCOME TO DIGITAL READER");
		lblWelcomeToDigital.setFont(new Font("Century Schoolbook", Font.BOLD,
				20));
		lblWelcomeToDigital.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblWelcomeToDigital, BorderLayout.NORTH);

		JLabel lblClickOnAny = new JLabel(
				"Click on any of the buttons to get prompted to that window");
		lblClickOnAny.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblClickOnAny, BorderLayout.SOUTH);

		JLabel lblNewLabel = new JLabel("");
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		lblNewLabel.setVerticalAlignment(SwingConstants.TOP);
		lblNewLabel.setIcon(new ImageIcon(
				"C:\\Users\\CS_Lab\\Downloads\\flip1.jpg"));
		contentPane.add(lblNewLabel, BorderLayout.CENTER);
	}

}
