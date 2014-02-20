package com.moomoohk.Mootilities.MooDownloader.gui;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.nio.channels.ClosedByInterruptException;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.moomoohk.Mootilities.ExceptionHandling.ExceptionDisplayDialog;
import com.moomoohk.Mootilities.MooDownloader.Downloader;

/**
 * Part of the {@link Downloader} GUI wrapper. <br>
 * Is used in the {@link DownloaderShell} class but can be implemented anywhere.
 * 
 * @author Meshulam Silk (moomoohk@ymail.com)
 * @since Dec 24, 2013
 */
public class DownloaderShellPane extends JPanel
{
	private static final long serialVersionUID = 1L;

	private Downloader downloader;
	private DownloaderWorker downloaderWorker;
	private boolean indeterminate;
	private String source, destination;
	private JProgressBar prog;
	private JLabel downloading;
	private JButton cancel;
	private String filesize;
	private FilesizeScale scale;
	private int cachedScaleIndex;
	private long kbs;
	private DownloaderShell parent;

	/**
	 * Represents all the possible file size scales.
	 */
	public static enum FilesizeScale
	{
		/**
		 * Bytes
		 */
		B,
		/**
		 * Kilobytes
		 */
		KB,
		/**
		 * Megabytes
		 */
		MB,
		/**
		 * Gigabytes
		 */
		GB,
		/**
		 * Terabytes
		 */
		TB,
		/**
		 * Petabytes
		 */
		PB,
		/**
		 * Exabyte
		 */
		EB,
		/**
		 * Zettabyte
		 */
		ZB,
		/**
		 * Yottabyte
		 */
		YB;
	}

	/**
	 * Constructor.
	 * 
	 * @param source
	 *            URL to remote file to download
	 * @param destination
	 *            Local filepath to download the remote file to
	 * @param indeterminate
	 *            True for an indeterminate download progress bar, false for a determinate bar
	 */
	public DownloaderShellPane(DownloaderShell parent, String source, String destination, boolean indeterminate)
	{
		this(parent, source, destination, indeterminate, null);
	}

	/**
	 * Constructor.
	 * 
	 * @param source
	 *            URL to remote file to download
	 * @param destination
	 *            Local filepath to download the remote file to
	 * @param indeterminate
	 *            True for an indeterminate download progress bar, false for a determinate bar
	 * @param color
	 *            Color of progress bar
	 * @wbp.parser.constructor
	 */
	public DownloaderShellPane(DownloaderShell parent, String source, String destination, boolean indeterminate, Color color)
	{
		setLayout(null);
		UIManager.put("nimbusOrange", color);
		this.parent = parent;
		this.indeterminate = indeterminate;
		this.source = source;
		this.destination = destination;
		downloading = new JLabel("Downloading " + source.substring(source.lastIndexOf("/") + 1));
		cancel = new JButton("X");
		cancel.setEnabled(false);

		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				prog = new JProgressBar();
				prog.setStringPainted(true);
				prog.setString("Download not started");
				prog.setIndeterminate(true);
				prog.setBounds(10, 25, 280, 30);
				add(prog);
			}
		});

		downloading.setBounds(10, 0, 280, 30);
		cancel.setBounds(300, 24, 43, 30);
		cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				downloaderWorker.finish();
				downloader.cleanUp();
			}
		});

		add(downloading);
		add(cancel);
	}

	/**
	 * Starts the download process.
	 */
	public void download()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				cancel.setEnabled(true);
				prog.setString("Waiting...");
			}
		});
		final Thread t = new Thread(new Runnable()
		{
			public void run()
			{
				while (!downloaderWorker.isCancelled() && !downloaderWorker.isDone())
				{
					try
					{
						Thread.sleep(1000);
					}
					catch (Exception e)
					{
						//There's no need to catch this...
					}
					long deltaBytes = (downloaderWorker.getPosition() - kbs) / 1024L;
					downloading.setText("Downloading " + source.substring(source.lastIndexOf("/") + 1) + " (" + (deltaBytes) + "KB/s)");
					kbs = downloaderWorker.getPosition();
				}
			}
		}, "KB/s Thread");
		downloader = new Downloader(source, destination);
		downloaderWorker = new DownloaderWorker(downloader)
		{
			@Override
			public void handleException(Exception e)
			{
				if (e instanceof ClosedByInterruptException)
					return;
				e.printStackTrace();
				cancel(true);
				t.interrupt();
				parent.removeFromList(DownloaderShellPane.this, true);
				downloader.cleanUp();
				new ExceptionDisplayDialog(parent, e).setVisible(true);
			}

			@Override
			public void finish()
			{
				cancel(true);
				t.interrupt();
				parent.removeFromList(DownloaderShellPane.this, false);
			}

			@Override
			public void gotFileSize(final long filesize)
			{
				super.gotFileSize(filesize);
				int i = 0;
				double tempSize = filesize;
				while (tempSize > 1024)
				{
					tempSize /= 1024;
					i++;
				}
				scale = FilesizeScale.values()[i];
				cachedScaleIndex = i;
				DownloaderShellPane.this.filesize = ("" + tempSize).substring(0, ("" + tempSize).indexOf(".") + 2);
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						prog.setMaximum((int) filesize);
						double temp = filesize;
						for (int i = 1; i <= cachedScaleIndex; i++)
							temp /= 1024;
						if (!indeterminate)
							prog.setString("0" + scale + "/" + ("" + temp).substring(0, ("" + temp).indexOf(".") + 2) + scale);
						else
							prog.setString("0" + scale);
					}
				});
				t.start();
			}
		};
		downloader.setHandler(downloaderWorker);
		downloaderWorker.addPropertyChangeListener(new PropertyChangeListener()
		{
			public void propertyChange(PropertyChangeEvent evt)
			{
				if ("progress".equals(evt.getPropertyName()))
				{
					prog.setIndeterminate(indeterminate);
					long position = downloaderWorker.getPosition();
					prog.setValue((int) position);
					double temp = position;
					for (int i = 1; i <= cachedScaleIndex; i++)
						temp /= 1024;
					if (!indeterminate)
						prog.setString(("" + temp).substring(0, ("" + temp).indexOf(".") + 2) + "" + scale + "/" + filesize + scale);
					else
						prog.setString(("" + temp).substring(0, ("" + temp).indexOf(".") + 2) + "" + scale);
				}
			}
		});
		downloaderWorker.execute();
	}

	/**
	 * Deletes the file that this process has downloaded.
	 */
	public void cleanUp()
	{
		downloader.cleanUp();
	}

	/**
	 * Cancels the download process.
	 */
	public void cancel()
	{
		downloaderWorker.finish();
	}
}