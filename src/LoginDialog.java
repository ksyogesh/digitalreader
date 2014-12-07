import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;

import javax.swing.*;
import javax.swing.border.*;
import javax.swing.event.*;

import dl.*;

public class LoginDialog extends JDialog {

	private boolean m_succeeded = false;
	private JTextField m_loginNameBox;
	private JPasswordField m_passwordBox;

	private String m_loginName;
	private String m_password;

	private int m_errCounter = 0;

	public LoginDialog(Frame parent) {
		super(parent, "Login", true);

		JPanel pp = new JPanel(new DialogLayout2());
		pp.setBorder(new CompoundBorder(new EtchedBorder(EtchedBorder.RAISED),
				new EmptyBorder(5, 5, 5, 5)));

		pp.add(new JLabel("User name:"));
		m_loginNameBox = new JTextField(16);
		pp.add(m_loginNameBox);

		pp.add(new JLabel("Password:"));
		m_passwordBox = new JPasswordField(16);
		pp.add(m_passwordBox);

		JPanel p = new JPanel(new DialogLayout2());
		p.setBorder(new EmptyBorder(10, 10, 10, 10));
		p.add(pp);

		ActionListener lst = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				m_loginName = m_loginNameBox.getText();
				m_password = new String(m_passwordBox.getPassword());

				if (!LoginModule.login(m_loginName, m_password)) {
					JOptionPane.showMessageDialog(LoginDialog.this,
							"System cannot login", "Login Error",
							JOptionPane.ERROR_MESSAGE);

					if (++m_errCounter >= LoginModule.MAX_LOGIN_ATTEMPTS) {
						System.out.println("All login attempts failed");
						System.exit(1); //
					} else {
						m_passwordBox.setText("");
						return; // Try one more time
					}
				}

				// If we get here, login was successful
				m_succeeded = true;
				String s = evt.getActionCommand();
				dispose();
				// this.setVisible(false);
				// }
			}
		};

		JButton saveButton = new JButton("Login");
		saveButton.addActionListener(lst);
		getRootPane().setDefaultButton(saveButton);
		getRootPane().registerKeyboardAction(lst,
				KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		p.add(saveButton);

		JButton cancelButton = new JButton("Cancel");
		lst = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				dispose();
			}
		};
		cancelButton.addActionListener(lst);
		getRootPane().registerKeyboardAction(lst,
				KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0),
				JComponent.WHEN_IN_FOCUSED_WINDOW);
		p.add(cancelButton);

		getContentPane().add(p, BorderLayout.CENTER);
		pack();
		setResizable(false);
		setLocationRelativeTo(parent);
	}

	public LoginDialog() {
		// TODO Auto-generated constructor stub
	}

	public boolean succeeded() {
		return m_succeeded;
	}

	public String getLoginName() {
		return m_loginName;
	}

	public String getPassword() {
		return m_password;
	}

	public static void main(String args[]) {
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

		LoginDialog dlg = new LoginDialog(null);
		dlg.show();
		if (!dlg.succeeded()) {
			System.out.println("User cancelled login");
			System.exit(1);
		}
		System.out.println("User " + dlg.getLoginName() + " has logged in");

		// if(s=="Browse"){
		/*******************************************
		 * This is where i call window1 by clicking * the button *
		 *******************************************/
		textreader1 tester = new textreader1();
		tester.setVisible(true);

	}

}

/**
 * Emulator for login module
 */
class LoginModule {

	public static final int MAX_LOGIN_ATTEMPTS = 3;

	public static boolean login(String userName, String password) {
		return userName.equalsIgnoreCase("root")
				&& password.equalsIgnoreCase("password");
	}
}
