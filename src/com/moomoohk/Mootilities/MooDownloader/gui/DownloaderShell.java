package com.moomoohk.Mootilities.MooDownloader.gui;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.SpringLayout;
import javax.swing.UIManager;

import com.moomoohk.Mootilities.ExceptionHandling.ExceptionDisplayDialog;
import com.moomoohk.Mootilities.MooDownloader.Downloader;
import com.moomoohk.Mootilities.OSUtils.OSUtils;
import com.moomoohk.Mootilities.Swing.FrameDragger;
import com.moomoohk.Mootilities.Swing.ListPanel;

/**
 * GUI wrapped {@link Downloader}.
 * 
 * @author Meshulam Silk (moomoohk@ymail.com)
 * @since Jan 12, 2014
 */
public class DownloaderShell extends JFrame
{
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private ListPanel listPanel;
	private Runnable callback;

	public static void main(String[] args)
	{
		EventQueue.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					DownloaderShell frame = new DownloaderShell();
					frame.setCallback(new Runnable()
					{
						public void run()
						{
							JOptionPane.showMessageDialog(null, "Done downloading", "", JOptionPane.PLAIN_MESSAGE);
						}
					});
					frame.setVisible(true);
					ArrayList<String> lwjglFiles = new ArrayList<String>();
					switch (OSUtils.getCurrentOS())
					{
						case MACOSX:
							lwjglFiles.add("macosx/libjinput-osx.jnilib");
							lwjglFiles.add("macosx/liblwjgl.jnilib");
							lwjglFiles.add("macosx/openal.dylib");
							break;
						case WINDOWS:
							lwjglFiles.add("windows/OpenAL32.dll");
							lwjglFiles.add("windows/OpenAL64.dll");
							lwjglFiles.add("windows/jinput-dx8.dll");
							lwjglFiles.add("windows/jinput-dx8_64.dll");
							lwjglFiles.add("windows/jinput-raw.dll");
							lwjglFiles.add("windows/jinput-raw_64.dll");
							lwjglFiles.add("windows/lwjgl.dll");
							lwjglFiles.add("windows/lwjgl64.dll");
							break;
						case UNIX:
							lwjglFiles.add("linux/libjinput-linux.so");
							lwjglFiles.add("linux/libjinput-linux64.so");
							lwjglFiles.add("linux/liblwjgl.so");
							lwjglFiles.add("linux/liblwjgl64.so");
							lwjglFiles.add("linux/libopenal.so");
							lwjglFiles.add("linux/libopenal64.so");
							break;
						case OTHER:
							break;
						default:
							break;
					}
					for (String file : lwjglFiles)
					{
						System.out.println("Downloading " + file);
						frame.download("https://maceswinger.com/utils/assets/lwjgl/" + file, OSUtils.getDynamicStorageLocation() + "Mace Swinger/lwjgl/" + file, false);
						//						frame.download("https://dl.dropboxusercontent.com/u/74838431/random.rar", OSUtils.getDynamicStorageLocation() + "Mace Swinger/lwjgl/test.rar", false);
					}
					new FrameDragger().applyTo(frame);
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Constructor.
	 */
	public DownloaderShell()
	{
		try
		{
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
		}
		catch (Exception e)
		{
			dispose();
			new ExceptionDisplayDialog(this, e).setVisible(true);
		}
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setUndecorated(true);
		setSize(400, 70);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		setContentPane(contentPane);
		SpringLayout sl_contentPane = new SpringLayout();
		contentPane.setLayout(sl_contentPane);

		JScrollPane scrollPane = new JScrollPane();
		sl_contentPane.putConstraint(SpringLayout.NORTH, scrollPane, 10, SpringLayout.NORTH, contentPane);
		sl_contentPane.putConstraint(SpringLayout.WEST, scrollPane, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, scrollPane, -10, SpringLayout.EAST, contentPane);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		listPanel = new ListPanel(new ArrayList<JPanel>(), 70);
		scrollPane.setViewportView(listPanel);
		contentPane.add(scrollPane);

		JButton btnCancelAll = new JButton("Cancel all");
		sl_contentPane.putConstraint(SpringLayout.WEST, btnCancelAll, 10, SpringLayout.WEST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.EAST, btnCancelAll, -10, SpringLayout.EAST, contentPane);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, scrollPane, -6, SpringLayout.NORTH, btnCancelAll);
		sl_contentPane.putConstraint(SpringLayout.SOUTH, btnCancelAll, -10, SpringLayout.SOUTH, contentPane);
		contentPane.add(btnCancelAll);
		btnCancelAll.addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent arg0)
			{
				do
				{
					((DownloaderShellPane) listPanel.getPanels().get(0)).cancel();
				}
				while (listPanel.getPanels().size() > 0);
			}
		});
	}

	/**
	 * Adds a download process to the list.
	 * 
	 * @param source
	 *            URL to file to download.
	 * @param destination
	 *            Path to file to download to.
	 * @param indeterminate
	 *            True for indeteminate progress bar, else false.
	 */
	public void download(String source, String destination, boolean indeterminate)
	{
		download(source, destination, indeterminate, null);
	}

	/**
	 * Adds a download process to the list.
	 * 
	 * @param source
	 *            URL to file to download.
	 * @param destination
	 *            Path to file to download to.
	 * @param indeterminate
	 *            True for indeteminate progress bar, else false.
	 * @param c
	 *            Color of progress bar. Null for default.
	 */
	public void download(String source, String destination, boolean indeterminate, Color c)
	{
		DownloaderShellPane pane = new DownloaderShellPane(this, source, destination, indeterminate, c);
		this.listPanel.addPanel((JPanel) pane, 60);
		if (this.listPanel.getPanels().size() < 5)
		{
			setSize(400, getSize().height + 70);
			setLocation(getLocation().x, getLocation().y - 35);
		}
		pane.download();
	}

	/**
	 * Cancels a download and removes its panel from the list.
	 * 
	 * @param downloaderShellPane
	 *            Download to cancel.
	 * @param delete
	 *            True to delete the downloaded file, else false.
	 */
	public void removeFromList(DownloaderShellPane downloaderShellPane, boolean delete)
	{
		if (delete)
			downloaderShellPane.cleanUp();
		this.listPanel.removePanel(downloaderShellPane);
		if (this.listPanel.getPanels().size() < 4)
		{
			setSize(400, getSize().height - 70);
			setLocation(getLocation().x, getLocation().y + 35);
		}
		if (this.listPanel.getPanels().size() == 0)
		{
			dispose();
			if (callback != null)
				callback.run();
		}
	}

	/**
	 * Sets a callback to be run when the list is cleared.
	 * 
	 * @param callback
	 *            Runnable to be run when the download list is cleared.
	 */
	public void setCallback(Runnable callback)
	{
		this.callback = callback;
	}
}