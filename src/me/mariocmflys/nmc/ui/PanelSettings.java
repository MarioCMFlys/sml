package me.mariocmflys.nmc.ui;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.FormSpecs;
import com.jgoodies.forms.layout.RowSpec;

import me.mariocmflys.nmc.Instance;
import me.mariocmflys.nmc.launcher.Profile;
import me.mariocmflys.nmc.launcher.TunedProfile;

@SuppressWarnings("serial")
public class PanelSettings extends JPanel {
	private JTextField txtAddProfile;
	public PanelSettings(MainWindow mainWindow) {
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
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,
				FormSpecs.RELATED_GAP_ROWSPEC,
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblImportProfile = new JLabel("Import Profile");
		add(lblImportProfile, "2, 2, right, default");
		
		txtAddProfile = new JTextField();
		add(txtAddProfile, "4, 2, fill, default");
		txtAddProfile.setColumns(10);
		
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String url = txtAddProfile.getText();
				
				for(int i = 0; i < mainWindow.profiles.size(); i++) {
					if(mainWindow.profiles.get(i).getSource().equals(url)) {
						txtAddProfile.setText("");
						JOptionPane.showMessageDialog(mainWindow.frame, "This profile has already been imported.", "Import Profile", JOptionPane.ERROR_MESSAGE);
						return;
					}
				}
				
				try {
					File tempFile = new File(Instance.getDataDir() + File.separator + "temp_manifest.json");
					FileUtils.copyURLToFile(new URL(url), tempFile);
					Scanner s = new Scanner(tempFile);
					String text = s.useDelimiter("\\Z").next();
					s.close();
					
					Profile p = new Profile(text);
					String id = p.getID();
					
					File newFile = new File(Instance.getDataDir() + File.separator + "profile" + File.separator + id + File.separator + "manifest.json");
					newFile.getParentFile().mkdirs();
					tempFile.renameTo(newFile);
					
					JSONObject tpJson = new JSONObject();
					tpJson.put("id", id);
					tpJson.put("memory", 1000);
					tpJson.put("dist_url", url);
					TunedProfile tp = new TunedProfile(tpJson);
					tp.attachProfile();
					
					JSONArray profiles = Instance.config.getArray("installed_profiles");
					profiles.put(tpJson);
					Instance.config.remove("installed_profiles");
					Instance.config.set("installed_profiles", profiles);
					Instance.config.save();
					
					mainWindow.profiles.put(mainWindow.profiles.size(), tp);
					mainWindow.listProfiles.updateUI();
					txtAddProfile.setText("");
					JOptionPane.showMessageDialog(mainWindow.frame, "Successfully imported profile", "Import Profile", JOptionPane.INFORMATION_MESSAGE);
					
				} catch (IOException e1) {
					e1.printStackTrace();
					JOptionPane.showMessageDialog(mainWindow.frame, "Failed to import profile, see log for details", "Import Profile", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		add(btnAdd, "4, 4");
		
		ButtonGroup grpCloseAction = new ButtonGroup();
		
		JLabel lblLauncherCloseAction = new JLabel("Close Button:");
		add(lblLauncherCloseAction, "2, 6");
		
		JRadioButton rdbtnDoNothing = new JRadioButton("Does nothing");
		grpCloseAction.add(rdbtnDoNothing);
		add(rdbtnDoNothing, "4, 6");
		
		JRadioButton rdbtnMinimizesLauncher = new JRadioButton("Minimizes Launcher");
		grpCloseAction.add(rdbtnMinimizesLauncher);
		add(rdbtnMinimizesLauncher, "4, 8");
		
		JRadioButton rdbtnHidesLauncher = new JRadioButton("Hides launcher");
		rdbtnHidesLauncher.setSelected(true);
		grpCloseAction.add(rdbtnHidesLauncher);
		add(rdbtnHidesLauncher, "4, 10");
		
		JRadioButton rdbtnExitsLauncher = new JRadioButton("Closes launcher");
		grpCloseAction.add(rdbtnExitsLauncher);
		add(rdbtnExitsLauncher, "4, 12");
	}
}
