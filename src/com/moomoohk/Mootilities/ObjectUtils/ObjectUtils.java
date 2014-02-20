package com.moomoohk.Mootilities.ObjectUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * This class contains methods to save (serialize) and load (deserialize) objects to and from the filesystem.
 * 
 * @author Meshulam Silk (moomoohk@ymail.com)
 * @since Dec 24, 2013
 */
public class ObjectUtils
{
	/**
	 * Save an object to file.
	 * 
	 * @param o
	 *            Object to save
	 * @param path
	 *            Directory to save to (will be created if nonexistent)
	 * @param fileName
	 *            Name of file to save to (will be created until the path)
	 * @param extension
	 *            Extension of the file to save to
	 * @return True is save was successful, else false
	 * @throws IOException
	 */
	public static boolean save(Object o, String path, String fileName, String extension) throws IOException
	{
		File f = new File(path);
		if (!f.exists())
			f.mkdirs();
		ObjectOutputStream save = new ObjectOutputStream(new FileOutputStream(path + "/" + fileName + "." + extension));
		save.writeObject(o);
		save.close();
		return true;
	}

	/**
	 * Loads an object from file.
	 * 
	 * @param path
	 *            Directory which contains file to load
	 * @param fileName
	 *            Name of file (within the path) to load
	 * @param extension
	 *            Extension of the file to load
	 * @return The loaded object (will need to be casted)
	 * @throws IOException
	 */
	public static Object load(String path, String fileName, String extension) throws IOException
	{
		Object temp = null;
		ObjectInputStream load = new ObjectInputStream(new FileInputStream(path + "/" + fileName + "." + extension));
		try
		{
			temp = load.readUnshared();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
			return null;
		}
		load.close();
		return temp;
	}
}
