
package com.moomoohk.Mootilities.MooCommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

/**
 * @author      Meshulam Silk <moomoohk@ymail.com>
 * @version     1.0
 * @since       2013-03-05
 */
public abstract class Command<T>
{
	public static ArrayList<Command<?>> commands = new ArrayList<Command<?>>();
	protected String command, message, help;
	protected int minParams, maxParams;
	protected boolean containsHTML;
	protected T handler;

	/**
	 * Command constructor method.
	 * 
	 * @param handler
	 *            Your command handler.
	 * @param command
	 *            The command in string form (e.g. "/test").
	 * @param help
	 *            Usage or any other help message (e.g.
	 *            "Prints a test message").
	 * @param minParams
	 *            The minimum number of parameters one is allowed to send.
	 * @param maxParams
	 *            The maximum number of parameters one is allowed to send (-1
	 *            for infinity).
	 */
	public Command(T handler, String command, String help, int minParams, int maxParams)
	{
		this.handler = handler;
		this.command = command;
		this.help = help;
		this.containsHTML = false;
		this.minParams = minParams;
		this.maxParams = maxParams;
	}

	/**
	 * Add a command to the command list.
	 * 
	 * @param command
	 *            The command you'd like to add.
	 */
	public static void add(Command<?> command)
	{
		if (getCommand(command.getCommand()) == null)
			commands.add(command);
		else
			throw new IllegalStateException(command.getCommand() + " already exists in the list!");
	}

	/**
	 * @return The message of this command.
	 */
	public String getMessage()
	{
		return message;
	}

	/**
	 * @return The help/usage message of this command.
	 */
	public String getHelp()
	{
		return help;
	}

	/**
	 * @return The command as a String.
	 */
	public String getCommand()
	{
		return command;
	}

	/**
	 * Whether or not the message contains HTML. Use this when you're printing
	 * your command messages in an HTML compatible text area.
	 * 
	 * @return True if the message contains HTML, else false.
	 */
	public boolean containsHTML()
	{
		return this.containsHTML;
	}

	/**
	 * Will return a String with all the commands and their help/usage messages.
	 * 
	 * @param HTML
	 *            True if you want it formatted in HTML, false if not.
	 * @return A String with all the command and their help/usage messages. If
	 *         no commands are in the list "No commands" will be returned.
	 */
	public static String getAllHelp(boolean HTML)
	{
		if (commands.size() == 0)
			return "No commands";
		if (HTML)
		{
			String help = "<b><u>Commands:</u></b><br>";
			for (int i = 0; i < commands.size(); i++)
			{
				help += "&nbsp;&nbsp;- - -<br>";
				help += "&nbsp;&nbsp;" + commands.get(i).getCommand() + "<br>";
				help += "&nbsp;&nbsp;" + commands.get(i).getHelp() + "<br>";
			}
			help += "&nbsp;&nbsp;- - -";
			return help;
		}
		String help = "Commands:\n";
		for (int i = 0; i < commands.size(); i++)
		{
			help += "- - -\n";
			help += commands.get(i).getCommand() + "\n";
			help += commands.get(i).getHelp() + "\n";
		}
		help += "- - -";
		return help;
	}

	/**
	 * This method will take your parameters String array and combine all the
	 * indexes into one String object.
	 * 
	 * @param params
	 *            The String array of parameters.
	 * @param start
	 *            The index with which you'd like to start combining.
	 * @return A String object which contains all the parameters combined.
	 */
	public static String stringParams(String[] params, int start)
	{
		if (params.length == 0 || start >= params.length)
			return null;
		String temp = params[start];
		for (int i = start + 1; i < params.length; i++)
			temp += " " + params[i];
		return temp;
	}

	/**
	 * Will parse a command from an input String. For example:
	 * "/test param1 param2" will return "/test".
	 * 
	 * @param command
	 *            The input to parse.
	 * @return The parsed command.
	 */
	public static String parseCommand(String command)
	{
		if (command.trim().contains(" "))
			return command.trim().substring(command.trim().indexOf(0)=='/'?command.indexOf("/"):0, command.indexOf(" ")).trim();
		return command;
	}

	/**
	 * Will parse the parameters from an input String into a String array.
	 * 
	 * For example: "/test param1 param2" will return {"param1", "param2}.
	 * 
	 * @param command
	 *            The input to parse.
	 * @return The parsed parameters.
	 */
	public static String[] parseParams(String command)
	{
		command = command.trim();
		int count = 0;
		for (int i = 0; i < command.length(); i++)
			if (command.charAt(i) == ' ')
			{
				count++;
				while (i < command.length() && command.charAt(i) == ' ')
					i++;
			}
		if (count == 0)
			return new String[] {};
		command += " ";
		String[] params = new String[0];
		String temp = null;
		for (int i = 0; i < command.length(); i++)
		{
			if (command.charAt(i) == ' ')
			{
				if (temp != null)
				{
					String[] temp2 = new String[params.length + 1];
					for (int j = 0; j < params.length; j++)
						temp2[j] = params[j];
					temp2[temp2.length - 1] = temp;
					params = temp2;
				}
				temp = null;
				continue;
			}
			if (temp == null)
				temp = "";
			temp += command.charAt(i);
		}
		String[] temp2 = new String[params.length - 1];
		for (int i = 1; i < params.length; i++)
			temp2[i - 1] = params[i];
		params = temp2;
		return params;
	}

	/**
	 * Possibly the most important method. Use this method to get a command
	 * object using String from the command list.
	 * 
	 * @param command
	 *            The command you'd like to get (e.g. "/test")
	 * @return The command object corresponding to the provided String. Null if
	 *         not found.
	 */
	public static Command<?> getCommand(String command)
	{
		for (Command<?> temp : commands)
			if (temp.getCommand().equals(command))
				return temp;
		return null;
	}

	public String toString()
	{
		return "Command: " + this.command + "\nMessage: " + this.message + "\nHelp: " + this.help;
	}

	/**
	 * This method will check the validity of the parameters and execute it if
	 * the check is successful.
	 * 
	 * @param params
	 *            A String array of the parameters you'd like to execute this
	 *            command with.
	 */
	public void checkAndExecute(String[] params)
	{
		this.message="";
		try
		{
			if (check(this.handler, params))
				execute(this.handler, params);
		}
		catch (Exception e)
		{
			this.message = "[COMMAND ERROR] Problem with check method! (Consider making an override for your command)";
			e.printStackTrace();
		}
	}

	/**
	 * The method that checks the validity of a parameters String array for this
	 * command.
	 * 
	 * You should never really have to explicitly call this method. Use
	 * {@link #checkAndExecute(String[])} instead.
	 * <p>
	 * The default check method simply checks if the length of the String array
	 * of parameters falls within the bounds of the {@link #minParams} and
	 * {@link #maxParams} of this command. You could override it with your own
	 * class if it's creating issues.
	 * 
	 * @param handler
	 *            The command handler you're working with.
	 * @param params
	 *            A String array of parameters.
	 * @return True if the check is successful, else false.
	 */
	public boolean check(T handler, String[] params)
	{
		if (params.length >= this.minParams && ((this.maxParams >= 0) ? params.length <= this.maxParams : true))
			return true;
		if (params.length < this.minParams)
			missingParameters(handler, params);
		else
			if (params.length > this.maxParams)
				tooManyParameters(handler, params);
		return false;
	}

	/**
	 * This method gets called if the check method finds that the parameter
	 * array provided contains too many parameters.
	 * 
	 * The idea is that you can override this method to write your own code for
	 * these situations.
	 * 
	 * @param handler
	 *            The command handler you're working with.
	 * @param params
	 *            A String array of parameters.
	 */
	public void tooManyParameters(T handler, String[] params)
	{
		this.message = "Too many parameters!";
	}

	/**
	 * This method gets called if the check method finds that the parameter
	 * array provided contains too little parameters.
	 * 
	 * The idea is that you can override this method to write your own code for
	 * these situations.
	 * 
	 * @param handler
	 *            The command handler you're working with.
	 * @param params
	 *            A String array of parameters.
	 */
	public void missingParameters(T handler, String[] params)
	{
		this.message = "Missing parameters!";
	}

	/**
	 * The execute code.
	 * 
	 * You should never really have to explicitly call this method. Use
	 * {@link #checkAndExecute(String[])} instead.
	 * <p>
	 * Simply put whatever code you'd like to be run in here, and when this
	 * command is executed the code will be run.
	 * 
	 * @param handler
	 *            The command handler you're working with.
	 * @param params
	 *            A String array of parameters.
	 */
	public abstract void execute(T handler, String[] params);

	/**
	 * This method receives a String array of parameters which contain flags (which are formatted "flag:value") and will create a HashMap<String, String> which contains the flags as keys and their values.
	 * @param params String array of parameters where each place contains a flag.
	 * @return A HashMap<String, String> which contains the flags and their values.
	 */
	public static HashMap<String, String> parseFlags(String[] params)
	{
		HashMap<String, String> flags=new HashMap<String, String>();
		for(String param:params)
		{
			Scanner sc=new Scanner(param);
			sc.useDelimiter(":");
			try
			{
				flags.put(sc.next().trim(), sc.next().trim());
			}
			catch(Exception e)
			{
				throw new IllegalStateException("Incorrect syntax!");
			}
		}
		return flags;
	}
}
