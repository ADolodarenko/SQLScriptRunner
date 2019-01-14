package ru.flc.service.sqlscriptrunner.view;

import javax.swing.*;

public class MainFrame extends JFrame
{
	private ActionsManager actionsManager;

	private JMenuBar menuBar;
	private JMenu fileMenu;
	private JMenu helpMenu;



	public MainFrame()
	{
		initMainMenu();
		initTextArea();
		initResultTabs();
	}

	private void initMainMenu()
	{
		menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		fileMenu = new JMenu();

	}

	private void initTextArea()
	{

	}

	private void initResultTabs()
	{

	}
}
