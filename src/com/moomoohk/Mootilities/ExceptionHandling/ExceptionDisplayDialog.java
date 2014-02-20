package com.moomoohk.Mootilities.ExceptionHandling;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

/**
 * This class is meant to make exception handling more user friendly.
 * 
 * @author Meshulam Silk (moomoohk@ymail.com)
 * @since Dec 24, 2013
 */
public class ExceptionDisplayDialog extends JDialog
{
	private static final long serialVersionUID = 1L;

	/**
	 * Creates a new window which will display the stack trace of a given Exception when made visible.
	 * 
	 * @param parent
	 *            Since this is a dialog it's recommended that a parent component is attached to it. Null for no parent component.
	 * @param e
	 *            The Exception to display.
	 */
	public ExceptionDisplayDialog(Component parent, Exception e)
	{
		setSize(450, 300);
		setLocationRelativeTo(parent);
		setResizable(false);
		getContentPane().setLayout(null);

		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setBounds(10, 38, 430, 191);
		getContentPane().add(scrollPane);

		JTextArea textArea = new JTextArea();
		textArea.setEditable(false);
		scrollPane.setViewportView(textArea);

		StringBuilder stackTrace = new StringBuilder(e.getClass().getSimpleName() + ": " + e.getMessage());
		for (StackTraceElement ste : e.getStackTrace())
			stackTrace.append("\n\tat " + ste.toString());
		textArea.setText(stackTrace.toString().trim());
		textArea.setCaretPosition(0);

		JButton btnClose = new JButton("Close");
		btnClose.setBounds(10, 240, 430, 30);
		getContentPane().add(btnClose);

		JLabel lblAnExceptionHas = new JLabel("An exception has been thrown:");
		lblAnExceptionHas.setBounds(10, 10, 424, 16);
		getContentPane().add(lblAnExceptionHas);

		setModal(true);

		btnClose.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent paramActionEvent)
			{
				ExceptionDisplayDialog.this.dispose();
			}
		});
	}
}