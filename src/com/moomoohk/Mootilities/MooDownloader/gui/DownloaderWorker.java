package com.moomoohk.Mootilities.MooDownloader.gui;

import javax.swing.SwingWorker;

import com.moomoohk.Mootilities.MooDownloader.DownloadHandler;
import com.moomoohk.Mootilities.MooDownloader.Downloader;

/**
 * Should be using when writing a GUI shell for the {@link Downloader}.
 * <p>
 * The {@link DownloaderShell} class utilizes this class.
 * 
 * @author Meshulam Silk (moomoohk@ymail.com)
 * @since Dec 24, 2013
 */
public abstract class DownloaderWorker extends SwingWorker<Void, Void> implements DownloadHandler
{
	private long filesize;
	private Downloader downloader;
	private long position;

	/**
	 * Construcor.
	 * 
	 * @param downloader
	 *            {@link Downloader} to use
	 */
	public DownloaderWorker(Downloader downloader)
	{
		this.downloader = downloader;
	}

	@Override
	protected Void doInBackground() throws Exception
	{
		setProgress(0);
		this.downloader.download();
		return null;
	}

	@Override
	public void gotFileSize(long filesize)
	{
		this.filesize = filesize;
	}

	@Override
	public void updateProgress(long progress)
	{
		setProgress((int) (((double) progress / this.filesize) * 100) + 1);
		this.position = progress;
	}

	public long getPosition()
	{
		return this.position;
	}
}
