package com.moomoohk.Mootilities.MooDownloader;

public interface DownloadHandler
{
	/**
	 * Notifies the handler that a file size has been discovered.
	 * 
	 * @param filesize
	 *            The file's size (in bytes)
	 */
	public void gotFileSize(long filesize);

	/**
	 * Tells the handler to update the download progress.
	 * 
	 * @param progress
	 *            Progress to update to (in bytes)
	 */
	public void updateProgress(long progress);

	/**
	 * Tells the handler to handle an exception in the event that one is thrown mid-download.
	 * 
	 * @param e
	 *            The {@link Exception} to handle
	 */
	public void handleException(Exception e);

	/**
	 * Notifies the handler that the download has finished.
	 */
	public void finish();
}
