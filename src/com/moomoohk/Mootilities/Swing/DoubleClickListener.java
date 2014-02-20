package com.moomoohk.Mootilities.Swing;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * A listener to detect double clicks.
 * 
 * @author Meshulam Silk (moomoohk@ymail.com)
 * @since Dec 24, 2013
 */
public abstract class DoubleClickListener extends MouseAdapter
{
	private boolean clicked = false;

	public final void mousePressed(MouseEvent e)
	{
		if (e.getButton() != 1)
			return;
		if (!clicked)
		{
			Thread t = new Thread(new Runnable()
			{
				public void run()
				{
					clicked = true;
					try
					{
						Thread.sleep(175);
					}
					catch (InterruptedException e)
					{
						clicked = false;
						return;
					}
					clicked = false;
				}
			});
			t.start();
		}
		else
			mouseDoubleClicked();
	}
	
	public abstract void mouseDoubleClicked();
}
