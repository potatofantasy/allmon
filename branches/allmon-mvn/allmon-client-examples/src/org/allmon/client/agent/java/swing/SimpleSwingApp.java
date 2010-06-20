package org.allmon.client.agent.java.swing;

import java.awt.BorderLayout;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class SimpleSwingApp {

	public static void main(String[] args) {
		JFrame frame = new JFrame("SimpleSwingApp");
		final JLabel label = new JLabel("Hello World");
		frame.getContentPane().add(label);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		
		//
		JPanel converterPanel = new JPanel();
	    converterPanel.setLayout(new GridLayout(2, 2));
	    JButton button = new JButton();
		converterPanel.add(button);
		
	    frame.getContentPane().add(converterPanel, BorderLayout.CENTER);

	}

}
