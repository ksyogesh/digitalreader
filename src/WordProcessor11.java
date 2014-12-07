import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JSplitPane;


public class WordProcessor11 extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WordProcessor11 frame = new WordProcessor11();
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
	public WordProcessor11() {
		setTitle("DIGITAL READER-RTF1");
		//setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(0, 0, 450, 400);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
				new WordProcessor1(),new WordProcessor1());
		splitPane.setDividerSize(8);
		splitPane.setDividerLocation(200);
		splitPane.setResizeWeight(0.5);
		splitPane.setContinuousLayout(false);
		//splitPane.setOneTouchExpandable(true);

		getContentPane().add(splitPane, BorderLayout.CENTER);
		contentPane.add(splitPane, BorderLayout.CENTER);
	}

}
