package org.smultron.framework;

import org.smultron.framework.MullbarRand.DumbnessFactor;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class MullbarGUI {
	protected JDialog mainDialog;
	protected JPanel mainPanel;
	private final String title = "Smultron";
	protected JButton startButton;

	public MullbarGUI(MullbarScript script) {
		script.isRunning = false;
		mainDialog = new JDialog();
		mainPanel = new JPanel();
		mainDialog.setModal(true);
		mainDialog.setModalityType(Dialog.ModalityType.APPLICATION_MODAL);
		mainDialog.setLocationRelativeTo(null);
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.PAGE_AXIS));
		mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
		mainDialog.getContentPane().add(mainPanel);

		JPanel speedPanel = new JPanel();
		speedPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
		JLabel speedLabel = new JLabel("Speed multiplier?");
		speedPanel.add(speedLabel);
		JComboBox<DumbnessFactor> speed = new JComboBox<>(DumbnessFactor.values());
		speedPanel.add(speed);
		mainPanel.add(speedPanel);
		mainDialog.pack();

		// Add from other guis here

		// Start button
		startButton = new JButton("Start script");
		startButton.addActionListener(e -> {
			script.isRunning = true;
			MullbarRand.setDumbnessFactor((DumbnessFactor) speed.getSelectedItem());
			close();
		});

		mainPanel.add(startButton);
		mainDialog.add(mainPanel);
		mainDialog.pack();
	}

	public void open() {
		mainDialog.setVisible(true);
	}

	public void close() {
		mainDialog.setVisible(false);
		mainDialog.dispose();
	}

	public void setTitle(final String title) {
		mainDialog.setTitle(this.title + title);
	}
}
