package me.mariocmflys.nmc.ui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import me.mariocmflys.jsoncompat.JSONObject;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import me.mariocmflys.nmc.C;
import me.mariocmflys.nmc.Instance;
import me.mariocmflys.nmc.auth.Mojang;
import me.mariocmflys.nmc.auth.Player;

import javax.swing.JCheckBox;

@SuppressWarnings("serial")
public class LoginWindow extends JFrame {

	private JPanel contentPane;
	private JTextField fieldUsername;
	private JPasswordField fieldPassword;

	public LoginWindow() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setResizable(false);
		setTitle("Simple MC Launcher");
		setBounds(100, 100, 450, 240);
		try {
			setIconImage(ImageIO.read(MainWindow.class.getResource("/icon.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		JFrame loginFrame = this;
		contentPane = new JPanel();
		contentPane.setBackground(Appearance.color_bg);
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new FormLayout(new ColumnSpec[] {
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
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblTitle = new JLabel("Simplified Minecraft Launcher");
		lblTitle.setForeground(Appearance.color_text_emphasis);
		contentPane.add(lblTitle, "4, 2");
		
		JLabel lblSubtitle = new JLabel("Use your Mojang account to sign in");
		contentPane.add(lblSubtitle, "4, 4");
		
		JLabel lblLogin = new JLabel("Login");
		contentPane.add(lblLogin, "2, 6, right, default");
		
		fieldUsername = new JTextField();
		contentPane.add(fieldUsername, "4, 6, fill, default");
		fieldUsername.setColumns(10);
		
		JLabel lblPassword = new JLabel("Password");
		contentPane.add(lblPassword, "2, 8, right, default");
		
		fieldPassword = new JPasswordField();
		contentPane.add(fieldPassword, "4, 8, fill, default");
		fieldPassword.setColumns(10);
		
		JLabel lblVersion = new JLabel(C.VERSION);
		lblVersion.setForeground(Color.GRAY);
		contentPane.add(lblVersion, "2, 14");
		
		JLabel lblErr = new JLabel("");
		lblErr.setForeground(Color.RED);
		contentPane.add(lblErr, "4, 14");
		
		JCheckBox chckbxSave = new JCheckBox("Remember this account");
		chckbxSave.setSelected(true);
		contentPane.add(chckbxSave, "4, 10");
		
		JButton btnSignIn = new JButton("Sign In");
		btnSignIn.setBackground(Appearance.color_button);
		btnSignIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String login = fieldUsername.getText();
				String password = String.valueOf(fieldPassword.getPassword());
				boolean save = chckbxSave.isSelected();
				if(login.equals("") || password.equals("")) {
					lblErr.setText("Username and password required");
					return;
				}
				JSONObject j = Mojang.generateToken(login, password);
				if(j != null) {
					String username = j.getJSONObject("selectedProfile").getString("name");
					String userUUID = j.getJSONObject("selectedProfile").getString("id");
					String clientToken = j.getString("clientToken");
					String accessToken = j.getString("accessToken");
					String userType = "legacy";
					String userProp = "{}";
					
					Instance.player = new Player(username, userUUID, accessToken, userType, userProp);
					
					if(save) {
						Instance.config.set("access_token", accessToken);
						Instance.config.set("client_token", clientToken);
						Instance.config.set("username", username);
						Instance.config.set("uuid", userUUID);
						Instance.config.set("user_type", userType);
						Instance.config.set("user_properties", userProp);
						Instance.config.save();						
					}
					
					loginFrame.setVisible(false);
					loginFrame.dispose();
					MainWindow w = new MainWindow();
					w.setVisible(true);
				}
				else {
					lblErr.setText("Failed to login");
				}
			}
		});
		
		contentPane.add(btnSignIn, "4, 12");
		
		getRootPane().setDefaultButton(btnSignIn);
	}

}
