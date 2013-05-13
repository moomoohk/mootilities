package com.moomoohk.Mootilities.MooConsole;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.awt.event.WindowFocusListener;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.SpringLayout;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultCaret;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyledDocument;

import com.moomoohk.Mootilities.MooCommands.Command;


/**
 * @author Meshulam Silk <moomoohk@ymail.com>
 * @version 1.0
 * @since 2013-03-08
 */
public class Console extends JFrame
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JScrollPane scrollPane;
	private StyledDocument consoleDoc;
	private SimpleAttributeSet consoleAttributeSet;
	private JTextField input;
	public static final String version = "2.0";
	private ArrayList<String> log;
	private int lastCommandSelector;

	/**
	 * Constructor method.
	 */
	public Console()
	{
		initElements();
		setTitle("MooConsole v" + version);
		setMinimumSize(new Dimension(510, 250));
		getContentPane().setBackground(Color.gray.darker());
		getContentPane().setLayout(getSpringLayout());
		getContentPane().add(this.scrollPane);
		getContentPane().add(this.input);
		addText("Welcome to MooConsole v" + version + "\n", new Color(81, 148, 237));
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		log = new ArrayList<String>();
		lastCommandSelector = -1;
		pack();
		setLocationRelativeTo(null);
		addWindowFocusListener(new WindowFocusListener()
		{
			public void windowLostFocus(WindowEvent arg0)
			{
			}

			public void windowGainedFocus(WindowEvent arg0)
			{
				input.requestFocus();
			}
		});
	}

	/**
	 * Initializes the various GUI elements.
	 */
	private void initElements()
	{
		JPanel nowrapPanel = new JPanel();
		nowrapPanel.setLayout(new BorderLayout(0, 0));
		this.scrollPane = new JScrollPane(nowrapPanel);
		this.scrollPane.setBackground(Color.black);
		this.scrollPane.setVerticalScrollBarPolicy(22);
		this.scrollPane.getVerticalScrollBar().setUnitIncrement(16);
		this.input = new JTextField(40);
		this.input.setBackground(Color.gray);
		this.input.setMaximumSize(new Dimension(400, 15));
		this.input.addKeyListener(new KeyAdapter()
		{
			@Override
			public void keyPressed(KeyEvent arg0)
			{
				if (arg0.getKeyCode() == 38)
				{
					if (log.size() > 0)
					{
						if (lastCommandSelector != log.size() - 1)
							lastCommandSelector++;
						if (lastCommandSelector == log.size())
							lastCommandSelector = log.size() - 1;
						input.setText(log.get(lastCommandSelector));
					}
				}
				else
					if (arg0.getKeyCode() == 40)
					{
						if (lastCommandSelector != -1)
							lastCommandSelector--;
						if (lastCommandSelector == -1)
							input.setText("");
						if (log.size() > 0 && lastCommandSelector >= 0)
							input.setText(log.get(lastCommandSelector));
					}
					else
						lastCommandSelector = -1;
				if (arg0.getKeyCode() == 10)
				{
					if (input.getText().trim().length() != 0 && log.indexOf(input.getText()) != 0)
						log.add(0, input.getText());
					lastCommandSelector = -1;
					if (input.getText().trim().length() == 0)
					{
						input.setText("");
						return;
					}
					try
					{
						Command<?> command = Command.getCommand(Command.parseCommand(input.getText()));
						if (command == null)
						{
							addText("Command not found!\n", Color.red);
							input.setText("");
						}
						else
						{
							command.checkAndExecute(Command.parseParams(input.getText()));
							if (command.getMessage() != null && command.getMessage().trim() != "")
								addText(command.getMessage() + "\n");
							input.setText("");
						}
					}
					catch (NoClassDefFoundError e)
					{
						addText("Problem! Are you sure you have MooCommands installed? Get the latest version here: https://github.com/moomoohk/MooCommands/raw/master/Build/MooCommands.jar\n", Color.red);
						input.setText("");
					}
				}
				if (arg0.getKeyCode() == 27)
					input.setText("");
			}
		});
		JTextPane consoleTextPane = new JTextPane();
		consoleTextPane.setEditable(false);
		consoleTextPane.setBackground(Color.gray.darker().darker().darker());
		this.consoleDoc = consoleTextPane.getStyledDocument();
		this.consoleAttributeSet = new SimpleAttributeSet();
		this.consoleDoc.setParagraphAttributes(0, this.consoleDoc.getLength(), this.consoleAttributeSet, false);
		DefaultCaret caret = (DefaultCaret) consoleTextPane.getCaret();
		caret.setUpdatePolicy(1);
		consoleTextPane.setFont(new Font("Dialog", 0, 11));
		nowrapPanel.add(consoleTextPane, "Center");
	}

	/**
	 * Creates and returns a SpringLayout.
	 * 
	 * @return A SpringLayout.
	 */
	private SpringLayout getSpringLayout()
	{
		SpringLayout layout = new SpringLayout();
		layout.putConstraint(SpringLayout.NORTH, this.scrollPane, 10, SpringLayout.NORTH, getContentPane());
		layout.putConstraint(SpringLayout.EAST, this.scrollPane, -10, SpringLayout.EAST, getContentPane());
		layout.putConstraint(SpringLayout.WEST, this.scrollPane, 10, SpringLayout.WEST, getContentPane());
		layout.putConstraint(SpringLayout.NORTH, this.input, 5, SpringLayout.SOUTH, this.scrollPane);
		layout.putConstraint(SpringLayout.EAST, this.input, -7, SpringLayout.EAST, getContentPane());
		layout.putConstraint(SpringLayout.WEST, this.input, 7, SpringLayout.WEST, getContentPane());
		layout.putConstraint(SpringLayout.SOUTH, this.scrollPane, -40, SpringLayout.SOUTH, getContentPane());
		return layout;
	}

	/**
	 * Adds a String to the text area and colors the font white.
	 * 
	 * @param text
	 *            String to add.
	 */
	public void addText(String text)
	{
		addText(text, Color.white);
	}

	/**
	 * Adds a String to the text area.
	 * 
	 * @param text
	 *            String to add.
	 * @param Color
	 *            of font to use.
	 */
	public void addText(String text, Color color)
	{
		StyleConstants.setForeground(this.consoleAttributeSet, color);
		try
		{
			final JScrollBar vbar = this.scrollPane.getVerticalScrollBar();
			boolean atBottom = vbar.getMaximum() == vbar.getValue() + vbar.getVisibleAmount();
			this.consoleDoc.insertString(this.consoleDoc.getLength(), text, this.consoleAttributeSet);
			if (atBottom)
			{
				EventQueue.invokeLater(new Runnable()
				{
					public void run()
					{
						vbar.setValue(vbar.getMaximum());
					}
				});
			}
		}
		catch (BadLocationException e)
		{
			addText("[ERROR]: " + e.getStackTrace().toString() + "/n", Color.red);
		}
	}

	/**
	 * Receives an ArrayList of commands and adds them to the commands list.
	 * 
	 * @param commands
	 *            ArrayList of commands.
	 */
	public void loadCommands(ArrayList<Command<?>> commands)
	{
		for (Command<?> command : commands)
			Command.add(command);
	}

	/**
	 * Overrides the Eclipse console.
	 * <p>
	 * System.out.println and System.err.println Strings will be printed in the MooConsole instead of in the Eclipse console.
	 */
	public void setOutputOverride()
	{
		System.setOut(new OutputOverride(System.out, false));
		System.setErr(new OutputOverride(System.err, true));
	}

	private class OutputOverride extends PrintStream
	{
		private boolean error;

		public OutputOverride(OutputStream str, boolean error)
		{
			super(str);
			this.error = error;
		}

		@Override
		public void write(byte[] b) throws IOException
		{
			write(new String(b).trim());
		}

		@Override
		public void write(byte[] buf, int off, int len)
		{
			write(new String(buf, off, len).trim());
		}

		private void write(String text)
		{
			if (!text.equals("") && !text.equals("\n"))
				if (this.error)
				{
					addText(text + "\n", Color.red);
				}
				else
				{
					addText("[From Console (" + Thread.currentThread().getStackTrace()[10].getFileName().subSequence(0, Thread.currentThread().getStackTrace()[10].getFileName().indexOf(".java")) + ":" + Thread.currentThread().getStackTrace()[10].getLineNumber() + ")] " + text + "\n", Color.gray);
				}
		}

		@Override
		public void write(int b)
		{
			write("" + b);
		}
	}
}
