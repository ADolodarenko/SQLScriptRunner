package ru.flc.service.sqlscriptrunner;

import org.dav.service.filesystem.FileSystem;
import org.dav.service.log.LogUtil;
import ru.flc.service.sqlscriptrunner.view.RunnerConstants;
import ru.flc.service.sqlscriptrunner.view.MainFrame;

import javax.swing.*;
import java.awt.*;

public class Main
{
	public static void main(String[] args)
	{
		setLogger();

		EventQueue.invokeLater(() -> runGUI());
	}

	private static void setLogger()
	{
		LogUtil.setLogger(FileSystem.getCurrentDir(Main.class), RunnerConstants.MESS_LOGGING_PROPERTIES_FILE_NAME);
	}

	private static void setLookAndFeel()
	{
		try
		{
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		}
		catch (IllegalAccessException e)
		{
			e.printStackTrace();
		}
		catch (InstantiationException e)
		{
			e.printStackTrace();
		}
		catch (UnsupportedLookAndFeelException e)
		{
			e.printStackTrace();
		}
		catch (ClassNotFoundException e)
		{
			e.printStackTrace();
		}
	}

	private static void runGUI()
	{
		setLookAndFeel();

		JFrame mainFrame = new MainFrame();
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setLocationRelativeTo(null);
		mainFrame.setVisible(true);
	}
}
