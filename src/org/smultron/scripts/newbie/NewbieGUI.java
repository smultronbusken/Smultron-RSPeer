package org.smultron.scripts.newbie;

import org.smultron.framework.MullbarGUI;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * TODO: for future use
 */
public class NewbieGUI extends MullbarGUI
{
    private final JTextField nameField;

    public NewbieGUI(Newbie script) {
        super(script);
	setTitle("Newbie");

	JPanel namePanel = new JPanel();
	namePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	JLabel nameLabel = new JLabel("Optional name for fresh accounts (It will take one of the recommended names, if this name is already taken):");
	namePanel.add(nameLabel);
	mainPanel.add(namePanel);

	JPanel nameInputPanel = new JPanel();
	nameInputPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
	nameField = new JTextField(50);
	nameInputPanel.add(nameField);
	mainPanel.add(nameInputPanel);

	startButton.addActionListener(new ActionListener() {
	    @Override
	    public void actionPerformed(ActionEvent e) {
	        script.accountName = nameField.getText();
	    }
	});
	mainDialog.pack();
    }
}