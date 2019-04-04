package me.mariocmflys.nmc.ui;

import javax.swing.JPanel;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import com.jgoodies.forms.layout.FormSpecs;
import javax.swing.JLabel;
import javax.swing.JRadioButton;

@SuppressWarnings("serial")
public class PanelSettings extends JPanel {
	public PanelSettings() {
		setLayout(new FormLayout(new ColumnSpec[] {
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,
				FormSpecs.RELATED_GAP_COLSPEC,
				FormSpecs.DEFAULT_COLSPEC,},
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
				FormSpecs.DEFAULT_ROWSPEC,}));
		
		JLabel lblLauncherCloseAction = new JLabel("Close Button:");
		add(lblLauncherCloseAction, "2, 2");
		
		JRadioButton rdbtnDoNothing = new JRadioButton("Does nothing");
		add(rdbtnDoNothing, "4, 2");
		
		JRadioButton rdbtnMinimizesLauncher = new JRadioButton("Minimizes Launcher");
		add(rdbtnMinimizesLauncher, "4, 4");
		
		JRadioButton rdbtnHidesLauncher = new JRadioButton("Hides launcher");
		add(rdbtnHidesLauncher, "4, 6");
		
		JRadioButton rdbtnExitsLauncher = new JRadioButton("Exits launcher");
		add(rdbtnExitsLauncher, "4, 8");
		
	}
}
