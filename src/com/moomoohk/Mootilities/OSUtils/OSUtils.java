package com.moomoohk.Mootilities.OSUtils;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

/**
 * This class contains useful methods which help with OS interaction.
 * 
 * @author Meshulam Silk (moomoohk@ymail.com)
 * @since Dec 24, 2013
 */
public class OSUtils
{
	//	private static byte[] cachedMacAddress;
	private static String cachedUserHome;

	/**
	 * Represents operating systems.
	 */
	public static enum OS
	{
		/**
		 * Microsoft Windows
		 */
		WINDOWS,
		/**
		 * Unix (Linux)
		 */
		UNIX,
		/**
		 * Mac OS X
		 */
		MACOSX,
		/**
		 * Anything else
		 */
		OTHER,
	}

	static
	{
		cachedUserHome = System.getProperty("user.home");
	}

	/**
	 * Used to get the dynamic storage location based off OS
	 * 
	 * @return string containing dynamic storage location
	 */
	public static String getDynamicStorageLocation()
	{
		switch (getCurrentOS())
		{
			case WINDOWS:
				return System.getenv("APPDATA") + "/";
			case MACOSX:
				return cachedUserHome + "/Library/Application Support/";
			case UNIX:
				return cachedUserHome + "/.local/share/";
			default:
				return System.getProperty("user.desktop") + "/";
		}
	}

	/**
	 * Used to get the java delimiter for current OS
	 * 
	 * @return string containing java delimiter for current OS
	 */
	public static String getJavaDelimiter()
	{
		switch (getCurrentOS())
		{
			case WINDOWS:
				return ";";
			case UNIX:
				return ":";
			case MACOSX:
				return ":";
			default:
				return ";";
		}
	}

	/**
	 * Used to get the current operating system
	 * 
	 * @return OS enum representing current operating system
	 */
	public static OS getCurrentOS()
	{
		String osString = System.getProperty("os.name").toLowerCase();
		if (osString.contains("win"))
			return OS.WINDOWS;
		else
			if (osString.contains("nix") || osString.contains("nux"))
				return OS.UNIX;
			else
				if (osString.contains("mac"))
					return OS.MACOSX;
				else
					return OS.OTHER;
	}

	//	/**
	//	 * Grabs the mac address of computer and makes it 10 times longer
	//	 * 
	//	 * @return a byte array containing mac address
	//	 */
	//	public static byte[] getMacAddress()
	//	{
	//		if (cachedMacAddress != null && cachedMacAddress.length >= 10)
	//		{
	//			return cachedMacAddress;
	//		}
	//		try
	//		{
	//			Enumeration<NetworkInterface> networkInterfaces = NetworkInterface.getNetworkInterfaces();
	//			while (networkInterfaces.hasMoreElements())
	//			{
	//				NetworkInterface network = networkInterfaces.nextElement();
	//				byte[] mac = network.getHardwareAddress();
	//				if (mac != null && mac.length > 0)
	//				{
	//					cachedMacAddress = new byte[mac.length * 10];
	//					for (int i = 0; i < cachedMacAddress.length; i++)
	//						cachedMacAddress[i] = mac[i - (Math.round(i / mac.length) * mac.length)];
	//					return cachedMacAddress;
	//				}
	//			}
	//		}
	//		catch (SocketException e)
	//		{
	//			Logger.logWarn("Failed to get MAC address, using default logindata key", e);
	//		}
	//		return new byte[]
	//		{ 0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15 };
	//	}

	/**
	 * Opens the given URL in the default browser
	 * 
	 * @param url
	 *            The URL
	 */
	public static void browse(String url)
	{
		try
		{
			if (Desktop.isDesktopSupported())
				Desktop.getDesktop().browse(new URI(url));
			else
				if (getCurrentOS() == OS.UNIX && new File("/usr/bin/xdg-open").exists() || new File("/usr/local/bin/xdg-open").exists())
					new ProcessBuilder("xdg-open", url).start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Opens the given path with the default application
	 * 
	 * @param f
	 *            The path
	 */
	public static void open(File f)
	{
		if (!f.exists())
			return;
		try
		{
			if (Desktop.isDesktopSupported())
				Desktop.getDesktop().open(f);
			else
				if (getCurrentOS() == OS.UNIX && new File("/usr/bin/xdg-open").exists() || new File("/usr/local/bin/xdg-open").exists())
					new ProcessBuilder("xdg-open", f.toString()).start();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static void main(String[] args)
	{
		System.out.println(getDynamicStorageLocation());
	}
}