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
	private final boolean indeterminate;
	private final String source, destination;
	private JProgressBar prog;
	private final JLabel downloading;
	private final JButton cancel;
	private String filesize;
	private FilesizeScale scale;
	private int cachedScaleIndex;
	private long kbs;
	private final DownloaderShell parent;

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
	 * @param parent
	 *            Parent shell that contains this pane
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
	 * @param parent
	 *            Parent shell that contains this pane
	 * @param source
	 *            URL to remote file to download
	 * @param destination
	 *            Local filepath to download the remote file to
	 * @param indeterminate
	 *            True for an indeterminate download progress bar, false for a determinate bar
	 * @param color
	 *            Color of progress bar
	 */
	public DownloaderShellPane(DownloaderShell parent, String source, String destination, boolean indeterminate, Color color)
	{
		setLayout(null);
		UIManager.put("nimbusOrange", color);
		this.parent = parent;
		this.indeterminate = indeterminate;
		this.source = source;
		this.destination = destination;
		this.downloading = new JLabel("Downloading " + source.substring(source.lastIndexOf("/") + 1));
		this.cancel = new JButton("X");
		this.cancel.setEnabled(false);

		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				DownloaderShellPane.this.prog = new JProgressBar();
				DownloaderShellPane.this.prog.setStringPainted(true);
				DownloaderShellPane.this.prog.setString("Download not started");
				DownloaderShellPane.this.prog.setIndeterminate(true);
				DownloaderShellPane.this.prog.setBounds(10, 25, 280, 30);
				add(DownloaderShellPane.this.prog);
			}
		});

		this.downloading.setBounds(10, 0, 280, 30);
		this.cancel.setBounds(300, 24, 43, 30);
		this.cancel.addActionListener(new ActionListener()
		{
			@Override
			public void actionPerformed(ActionEvent arg0)
			{
				DownloaderShellPane.this.downloaderWorker.finish();
				DownloaderShellPane.this.downloader.cleanUp();
			}
		});

		add(this.downloading);
		add(this.cancel);
	}

	/**
	 * Starts the download process.
	 */
	public void download()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				DownloaderShellPane.this.cancel.setEnabled(true);
				DownloaderShellPane.this.prog.setString("Waiting...");
			}
		});
		final Thread t = new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				while (!DownloaderShellPane.this.downloaderWorker.isCancelled() && !DownloaderShellPane.this.downloaderWorker.isDone())
				{
					try
					{
						Thread.sleep(1000);
					}
					catch (Exception e)
					{
						//There's no need to catch this...
					}
					long deltaBytes = (DownloaderShellPane.this.downloaderWorker.getPosition() - DownloaderShellPane.this.kbs) / 1024L;
					DownloaderShellPane.this.downloading.setText("Downloading " + DownloaderShellPane.this.source.substring(DownloaderShellPane.this.source.lastIndexOf("/") + 1) + " (" + (deltaBytes) + "KB/s)");
					DownloaderShellPane.this.kbs = DownloaderShellPane.this.downloaderWorker.getPosition();
				}
			}
		}, "KB/s Thread");
		this.downloader = new Downloader(this.source, this.destination);
		this.downloaderWorker = new DownloaderWorker(this.downloader)
		{
			@Override
			public void handleException(Exception e)
			{
				if (e instanceof ClosedByInterruptException)
					return;
				e.printStackTrace();
				cancel(true);
				t.interrupt();
				DownloaderShellPane.this.parent.removeFromList(DownloaderShellPane.this, true);
				DownloaderShellPane.this.downloader.cleanUp();
				new ExceptionDisplayDialog(DownloaderShellPane.this.parent, e).setVisible(true);
			}

			@Override
			public void finish()
			{
				cancel(true);
				t.interrupt();
				DownloaderShellPane.this.parent.removeFromList(DownloaderShellPane.this, false);
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
				DownloaderShellPane.this.scale = FilesizeScale.values()[i];
				DownloaderShellPane.this.cachedScaleIndex = i;
				DownloaderShellPane.this.filesize = ("" + tempSize).substring(0, ("" + tempSize).indexOf(".") + 2);
				SwingUtilities.invokeLater(new Runnable()
				{
					@Override
					public void run()
					{
						DownloaderShellPane.this.prog.setMaximum((int) filesize);
						double temp = filesize;
						for (int i = 1; i <= DownloaderShellPane.this.cachedScaleIndex; i++)
							temp /= 1024;
						if (!DownloaderShellPane.this.indeterminate)
							DownloaderShellPane.this.prog.setString("0" + DownloaderShellPane.this.scale + "/" + ("" + temp).substring(0, ("" + temp).indexOf(".") + 2) + DownloaderShellPane.this.scale);
						else
							DownloaderShellPane.this.prog.setString("0" + DownloaderShellPane.this.scale);
					}
				});
				t.start();
			}
		};
		this.downloader.setHandler(this.downloaderWorker);
		this.downloaderWorker.addPropertyChangeListener(new PropertyChangeListener()
		{
			@Override
			public void propertyChange(PropertyChangeEvent evt)
			{
				if ("progress".equals(evt.getPropertyName()))
				{
					DownloaderShellPane.this.prog.setIndeterminate(DownloaderShellPane.this.indeterminate);
					long position = DownloaderShellPane.this.downloaderWorker.getPosition();
					DownloaderShellPane.this.prog.setValue((int) position);
					double temp = position;
					for (int i = 1; i <= DownloaderShellPane.this.cachedScaleIndex; i++)
						temp /= 1024;
					if (!DownloaderShellPane.this.indeterminate)
						DownloaderShellPane.this.prog.setString(("" + temp).substring(0, ("" + temp).indexOf(".") + 2) + "" + DownloaderShellPane.this.scale + "/" + DownloaderShellPane.this.filesize + DownloaderShellPane.this.scale);
					else
						DownloaderShellPane.this.prog.setString(("" + temp).substring(0, ("" + temp).indexOf(".") + 2) + "" + DownloaderShellPane.this.scale);
				}
			}
		});
		this.downloaderWorker.execute();
	}

	/**
	 * Deletes the file that this process has downloaded.
	 */
	public void cleanUp()
	{
		this.downloader.cleanUp();
	}

	/**
	 * Cancels the download process.
	 */
	public void cancel()
	{
		this.downloaderWorker.finish();
	}
}