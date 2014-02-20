package com.moomoohk.Mootilities.Swing;

import java.awt.Component;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

/**
 * A simple tool that enables the dragging of windows around the screen.
 * <p>
 * Usage:
 * 
 * <pre>
 * new FrameDragger().applyTo(window);
 * </pre>
 * 
 * @author Meshulam Silk (moomoohk@ymail.com)
 * @since Dec 24, 2013
 */
public class FrameDragger extends MouseAdapter implements MouseMotionListener
{
	private Component c;
	private Point mouseDownScreenCoords = null;
	private Point mouseDownCompCoords = null;
	private Runnable onPress = null, onRelease = null, onDrag = null;

	public void mouseDragged(MouseEvent e)
	{
		if (onDrag != null)
			onDrag.run();
		GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
		Rectangle bounds = gc.getBounds();
		Insets screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(gc);
		Rectangle screen = new Rectangle();
		screen.x = bounds.x + screenInsets.left;
		screen.y = bounds.y + screenInsets.top;
		screen.height = bounds.height - screenInsets.top - screenInsets.bottom;
		screen.width = bounds.width - screenInsets.left - screenInsets.right;
		Point currCoords = e.getLocationOnScreen();
		int newX = mouseDownScreenCoords.x + (currCoords.x - mouseDownScreenCoords.x) - mouseDownCompCoords.x;
		int newY = mouseDownScreenCoords.y + (currCoords.y - mouseDownScreenCoords.y) - mouseDownCompCoords.y;
		if (newX < 0)
			newX = 0;
		if (newX + c.getWidth() > screen.width)
			newX = (int) Toolkit.getDefaultToolkit().getScreenSize().getWidth() - c.getWidth();
		if (newY < screen.y)
			newY = screen.y;
		if (newY + c.getHeight() > Toolkit.getDefaultToolkit().getScreenSize().getHeight())
			newY = (int) Toolkit.getDefaultToolkit().getScreenSize().getHeight() - c.getHeight();
		c.setLocation(newX, newY);
	}

	public void mouseReleased(MouseEvent e)
	{
		if (onRelease != null)
			onRelease.run();
		this.mouseDownScreenCoords = null;
		this.mouseDownCompCoords = null;
	}

	public void mousePressed(MouseEvent e)
	{
		if (onPress != null)
			onPress.run();
		this.mouseDownScreenCoords = e.getLocationOnScreen();
		this.mouseDownCompCoords = e.getPoint();
	}

	/**
	 * Will make a Component draggable.
	 * 
	 * @param c
	 *            Component to make draggable
	 */
	public void applyTo(Component c)
	{
		applyTo(c, null, null, null);
	}

	/**
	 * Will make a Component draggable and add hooks that will be run at different events.
	 * 
	 * @param c
	 *            Component to make draggable
	 * @param onPress
	 *            Hook that will run when the mouse presses on the component
	 * @param onRelease
	 *            Hook that will run when the mouse releases the component
	 * @param onDrag
	 *            Hook that will run when the component is dragged
	 */
	public void applyTo(Component c, Runnable onPress, Runnable onRelease, Runnable onDrag)
	{
		this.c = c;
		c.addMouseListener(this);
		c.addMouseMotionListener(this);
		this.onPress = onPress;
		this.onRelease = onRelease;
		this.onDrag = onDrag;
	}
}
