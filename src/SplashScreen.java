import java.awt.*;
import javax.swing.*;

public class SplashScreen extends JWindow {
	private int duration;

	public SplashScreen(int d) {
		duration = d;
	}

	// A simple little method to show a title screen in the center
	// of the screen for the amount of time given in the constructor
	public void showSplash() {
		JPanel content = (JPanel) getContentPane();
		content.setBackground(Color.white);

		// Set the window's bounds, centering the window
		int width = 300;
		int height = 300;
		Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
		int x = (screen.width - width) / 2;
		int y = (screen.height - height) / 2;
		setBounds(x, y, width, height);

		// Build the splash screen
		JLabel label = new JLabel(new ImageIcon("digi.jpg"));
		JLabel copyrt = new JLabel("DIGITAL READER", JLabel.CENTER);
		copyrt.setFont(new Font("Sans-Serif", Font.BOLD, 12));
		content.add(label, BorderLayout.CENTER);
		content.add(copyrt, BorderLayout.SOUTH);
		// Color oraRed = new Color(156, 20, 20, 255);
		// content.setBorder(BorderFactory.createLineBorder(oraRed, 10));

		// Display it
		setVisible(true);

		// Wait a little while, maybe while loading resources
		try {
			Thread.sleep(duration);
		} catch (Exception e) {
		}

		setVisible(false);
		digireader tester = new digireader();
		tester.setVisible(true);
	}

	public void showSplashAndExit() {
		showSplash();
		// System.exit(0);
	}

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
			javax.swing.UIManager.setLookAndFeel("com.birosoft.liquid.LiquidLookAndFeel");

		    // if you want decorations for frames and dialogs you can put this two lines
		    //
			 JFrame.setDefaultLookAndFeelDecorated(true);	// to decorate frames 
			// JDialog.setDefaultLookAndFeelDecorated(true);	// to decorate dialogs 
		    //
		    // or put this one line
		    //
			 com.birosoft.liquid.LiquidLookAndFeel.setLiquidDecorations(true);
			//
			// or if you want to use Apple's Panther window decoration
			//
			 com.birosoft.liquid.LiquidLookAndFeel.setLiquidDecorations(true, "panther");

	    } catch (Exception e) {}
		// Throw a nice little title page up on the screen first
		SplashScreen splash = new SplashScreen(4000);

		// Normally, we'd call splash.showSplash() and get on with the program.
		// But, since this is only a test...
		splash.showSplashAndExit();
	}
}