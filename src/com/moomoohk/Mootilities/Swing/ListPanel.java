package com.moomoohk.Mootilities.Swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.border.BevelBorder;

/**
 * Generates a JPanel which contains a list of JPanels.
 * 
 * @author Meshulam Silk (moomoohk@ymail.com)
 * @since Dec 24, 2013
 */
public class ListPanel extends JPanel
{
	private static final long serialVersionUID = 1L;
	private JPanel fillerPanel;
	private ArrayList<JPanel> panels;

	/**
	 * Constructor.
	 * 
	 * @param panels
	 *            List of JPanels to add to the ListPanel
	 * @param height
	 *            Uniform height of the JPanels in the list
	 */
	public ListPanel(List<JPanel> panels, int height)
	{
		this(panels, height, new Insets(2, 0, 2, 0));
	}

	/**
	 * Constructor.
	 * 
	 * @param panels
	 *            List of JPanels to add to the ListPanel
	 * @param height
	 *            Uniform height of the JPanels in the list
	 * @param insets
	 *            Insets to apply to each JPanel in the list
	 */
	public ListPanel(List<JPanel> panels, int height, Insets insets)
	{
		this();
		for (JPanel panel : panels)
			addPanel(panel, height, insets);
	}

	/**
	 * Constructor.
	 * <p>
	 * Starts with an empty list.
	 */
	public ListPanel()
	{
		super();
		this.fillerPanel = new JPanel();
		this.fillerPanel.setMinimumSize(new Dimension(0, 0));
		this.panels = new ArrayList<JPanel>();
		setLayout(new GridBagLayout());
	}

	/**
	 * Adds a JPanel to the list.
	 * 
	 * @param p
	 *            JPanel to add
	 * @param height
	 *            Height of JPanel
	 */
	public void addPanel(JPanel p, int height)
	{
		addPanel(p, height, new Insets(2, 0, 2, 0));
	}

	/**
	 * Adds a JPanel to the list.
	 * 
	 * @param p
	 *            JPanel to add
	 * @param height
	 *            Height of JPanel
	 * @param insets
	 *            Insets to apply to the JPanel
	 */
	public void addPanel(JPanel p, int height, Insets insets)
	{
		super.remove(fillerPanel);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = getComponentCount();
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.anchor = GridBagConstraints.PAGE_START;
		gbc.ipady = height;
		gbc.insets = insets;
		gbc.weightx = 1.0;
		panels.add(p);
		add(p, gbc);
		gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = getComponentCount();
		gbc.fill = GridBagConstraints.VERTICAL;
		gbc.weighty = 1.0;
		add(fillerPanel, gbc);
		revalidate();
		invalidate();
		repaint();
	}

	/**
	 * Removes a JPanel from the list.
	 * 
	 * @param p
	 *            JPanel to remove
	 */
	public void removePanel(JPanel p)
	{
		removePanel(panels.indexOf(p));
	}

	/**
	 * Removes a JPanel from the list by index.
	 * 
	 * @param i
	 *            Index of JPanel to remove
	 */
	public void removePanel(int i)
	{
		super.remove(i);
		panels.remove(i);
		revalidate();
		invalidate();
		repaint();
	}

	/**
	 * Returns the list of JPanels.
	 * 
	 * @return The list of JPanels.
	 */
	public ArrayList<JPanel> getPanels()
	{
		return this.panels;
	}
	
	public static void main(String[] args)
	{
		JFrame f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setMinimumSize(new Dimension(500, 500));
		f.setLocationRelativeTo(null);
		f.getContentPane().setLayout(new BorderLayout());
		final ListPanel listPanel = new ListPanel();
		for (int i = 1; i <= 10; i++)
			listPanel.addPanel(getRandomJPanel(), new Random().nextInt(50) + 50);
		JButton btnAdd = new JButton("Add");
		btnAdd.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent paramActionEvent)
			{
				listPanel.addPanel(getRandomJPanel(), new Random().nextInt(50) + 50);
			}
		});
		JButton btnRemove = new JButton("Remove");
		btnRemove.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent paramActionEvent)
			{
				listPanel.removePanel(0);
			}
		});
		f.getContentPane().add(btnRemove, BorderLayout.NORTH);
		f.getContentPane().add(btnAdd, BorderLayout.SOUTH);
		JScrollPane scrollPane = new JScrollPane();
		scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		scrollPane.setViewportView(listPanel);
		f.getContentPane().add(scrollPane, BorderLayout.CENTER);
		f.setVisible(true);
	}

	public static JPanel getRandomJPanel()
	{
		JPanel panel = new JPanel();
		panel.setBorder(new BevelBorder(BevelBorder.LOWERED, null, null, null, null));
		panel.add(new JLabel("This is a randomly sized JPanel"));
		panel.setBackground(new Color(new Random().nextFloat(), new Random().nextFloat(), new Random().nextFloat()));
		return panel;
	}
}
