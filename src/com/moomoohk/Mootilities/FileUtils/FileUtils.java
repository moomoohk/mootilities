package com.moomoohk.Mootilities.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

/**
 * This class contains some useful file operation methods.
 * 
 * @author Meshulam Silk (moomoohk@ymail.com)
 * @since Dec 24, 2013
 */
public class FileUtils
{
	/**
	 * Copies a folder from one place to another. <br>
	 * If the source folder is a file, {@link FileUtils#copyFile(File, File)} will be used.
	 * 
	 * @param sourceFolder
	 *            Folder to copy
	 * @param destinationFolder
	 *            Folder to copy to (will be created if nonexistent)
	 * @throws IOException
	 */
	public static void copyFolder(File sourceFolder, File destinationFolder) throws IOException
	{
		if (sourceFolder.isDirectory())
		{
			if (!destinationFolder.exists())
			{
				destinationFolder.mkdirs();
			}
			String files[] = sourceFolder.list();
			for (String file : files)
			{
				File srcFile = new File(sourceFolder, file);
				File destFile = new File(destinationFolder, file);
				copyFolder(srcFile, destFile);
			}
		}
		else
		{
			copyFile(sourceFolder, destinationFolder);
		}
	}

	/**
	 * Copies a file from one place to another.
	 * 
	 * @param source
	 *            File to copy
	 * @param destination
	 *            File to copy to (will be created if nonexistent)
	 * @throws IOException
	 */
	public static void copyFile(File source, File destination) throws IOException
	{
		if (source.exists())
		{
			if (!destination.exists())
				destination.createNewFile();
			FileChannel sourceStream = null, destinationStream = null;
			try
			{
				sourceStream = new FileInputStream(source).getChannel();
				destinationStream = new FileOutputStream(destination).getChannel();
				destinationStream.transferFrom(sourceStream, 0, sourceStream.size());
			}
			finally
			{
				if (sourceStream != null)
					sourceStream.close();
				if (destinationStream != null)
					destinationStream.close();
			}
		}
	}

	/**
	 * Deletes a file.<br>
	 * If the provided file is a directory, all of its children will be deleted.
	 * 
	 * @param f
	 *            File to delete
	 * @return True if the deletion was successful, else false;
	 */
	public static boolean delete(File f)
	{
		if (f.isDirectory())
		{
			File[] children = f.listFiles();
			for (File child : children)
				delete(child);
		}
		return f.delete();
	}

	//	public static void extractZipTo(String zipLocation, String outputLocation)
	//	{
	//		ZipInputStream zipinputstream = null;
	//		try
	//		{
	//			byte[] buf = new byte[1024];
	//			zipinputstream = new ZipInputStream(new FileInputStream(zipLocation));
	//			ZipEntry zipentry = zipinputstream.getNextEntry();
	//			while (zipentry != null)
	//			{
	//				String entryName = zipentry.getName();
	//				int n;
	//				if (!zipentry.isDirectory() && !entryName.equalsIgnoreCase("minecraft") && !entryName.equalsIgnoreCase(".minecraft") && !entryName.equalsIgnoreCase("instMods"))
	//				{
	//					new File(outputLocation + File.separator + entryName).getParentFile().mkdirs();
	//					FileOutputStream fileoutputstream = new FileOutputStream(outputLocation + File.separator + entryName);
	//					while ((n = zipinputstream.read(buf, 0, 1024)) > -1)
	//					{
	//						fileoutputstream.write(buf, 0, n);
	//					}
	//					fileoutputstream.close();
	//				}
	//				zipinputstream.closeEntry();
	//				zipentry = zipinputstream.getNextEntry();
	//			}
	//		}
	//		catch (Exception e)
	//		{
	//			Logger.logError(e.getMessage(), e);
	//		}
	//		finally
	//		{
	//			try
	//			{
	//				zipinputstream.close();
	//			}
	//			catch (IOException e)
	//			{
	//			}
	//		}
	//	}
}