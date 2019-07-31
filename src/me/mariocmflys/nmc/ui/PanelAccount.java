package me.mariocmflys.nmc.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import me.mariocmflys.nmc.Instance;

@SuppressWarnings("serial")
public class PanelAccount extends JPanel {
	private JTextField txtUsername;
	private JTextField txtUuid;
	
	public PanelAccount(MainWindow mainWindow) {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				ColumnSpec.decode("default:grow"),},
			new RowSpec[] {
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				RowSpec.decode("default:grow"),}));
		
		JLabel lblUser = new JLabel("User");
		add(lblUser, "2, 2, right, default");
		
		txtUsername = new JTextField();
		txtUsername.setText(Instance.player.getUsername());
		txtUsername.setEditable(false);
		add(txtUsername, "4, 2, fill, default");
		txtUsername.setColumns(10);
		
		JLabel lblUuid = new JLabel("UUID");
		add(lblUuid, "2, 4, right, default");
		
		txtUuid = new JTextField();
		txtUuid.setText(Instance.player.getUUID());
		txtUuid.setEditable(false);
		add(txtUuid, "4, 4, fill, default");
		txtUuid.setColumns(10);
		
		JButton btnSignOut = new JButton("Sign Out");
		btnSignOut.setBackground(Appearance.color_button);
		btnSignOut.setForeground(Appearance.color_text);
		btnSignOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Instance.signOut();
				
				mainWindow.setVisible(false);
				mainWindow.dispose();
				LoginWindow l = new LoginWindow();
				l.setVisible(true);
				
			}
		});
		
		JLabel lblCreation = new JLabel("Created");
		add(lblCreation, "2, 6");
		
		JLabel lblAccAge = new JLabel("LEGACY: SIGN IN AGAIN TO VIEW");
		
		if(Instance.config.has("user_created")) {
			String date = new java.text.SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(new java.util.Date(Instance.config.getLong("user_created")));
			lblAccAge.setText(date);
		}
		add(lblAccAge, "4, 6");
		add(btnSignOut, "4, 8");
		

	}

}
