package com.moomoohk.Mootilities.MooDownloader;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import com.moomoohk.Mootilities.FileUtils.FileUtils;

/**
 * Simple download tool.
 * 
 * @author Meshulam Silk (moomoohk@ymail.com)
 * @since Dec 24, 2013
 */
public class Downloader
{
	private DownloadHandler handler;
	private URL source;
	private final String dest;

	/**
	 * Constructor.
	 * 
	 * @param source
	 *            URL of remote file to download
	 * @param destination
	 *            Local filepath to download the remote file to
	 */
	public Downloader(String source, String destination)
	{
		super();
		try
		{
			this.source = new URL(source);
		}
		catch (MalformedURLException e)
		{
			this.handler.handleException(e);
		}
		this.dest = destination;
	}

	/**
	 * Set the {@link DownloadHandler} for this {@link Downloader}.
	 * 
	 * @param handler
	 *            {@link DownloadHandler} to set
	 */
	public void setHandler(DownloadHandler handler)
	{
		this.handler = handler;
	}

	/**
	 * Gets the size of a remote file.
	 * 
	 * @param link
	 *            URL of remote file
	 * @return File Remote file's size (in bytes)
	 * @throws Exception
	 */
	public static long getFilesize(String link) throws Exception
	{
		long filesize = -1L;
		URL source = new URL(link);
		HttpURLConnection connection = (HttpURLConnection) source.openConnection();
		String filesizeString = connection.getHeaderField("Content-Length");
		if (filesizeString != null)
			filesize = Long.parseLong(filesizeString);
		return filesize;
	}

	/**
	 * Starts the download process.
	 */
	public void download()
	{
		if (this.handler == null)
			throw new IllegalStateException("Handler is null!");
		try
		{
			File destFile = new File(this.dest);
			if (!destFile.exists())
			{
				destFile.getParentFile().mkdirs();
				destFile.createNewFile();
			}
			long filesize = getFilesize(this.source.toString());
			this.handler.gotFileSize(filesize);
			FileOutputStream fos = new FileOutputStream(this.dest);
			BufferedInputStream in = new BufferedInputStream(this.source.openStream());
			BufferedOutputStream bout = new BufferedOutputStream(fos, 1024);
			byte[] data = new byte[1024];
			int x = 0;
			while ((x = in.read(data, 0, 1024)) >= 0)
			{
				final long pos = fos.getChannel().position();
				this.handler.updateProgress(pos);
				bout.write(data, 0, x);
			}
			bout.close();
			in.close();
		}
		catch (Exception e)
		{
			this.handler.handleException(e);
		}
		this.handler.finish();
	}

	/**
	 * Deletes the downloaded file.
	 */
	public void cleanUp()
	{
		FileUtils.delete(new File(this.dest));
	}
}
