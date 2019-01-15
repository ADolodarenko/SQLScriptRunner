package ru.flc.service.sqlscriptrunner.view;

import org.dav.service.util.ResourceManager;
import org.dav.service.view.TitleAdjuster;
import ru.flc.service.sqlscriptrunner.RunnerResourceManager;

import javax.swing.*;

public class MainFrame extends JFrame
{
	private ResourceManager resourceManager;
	private TitleAdjuster titleAdjuster;

	private AbstractAction openFileAction;
	private AbstractAction executeAction;
	private AbstractAction closeAction;
	private AbstractAction aboutAction;
	private AbstractAction exitAction;

	public MainFrame()
	{
		resourceManager = RunnerResourceManager.getInstance();

		loadAllSettings();
		loadComponents();
	}

	private void loadAllSettings()
	{


	}

	private void loadComponents()
	{
		titleAdjuster = new TitleAdjuster();

		initActions();
		initToolBar();
		initScriptArea();
		initResultArea();
	}

	private void initActions()
	{

	}

	private void initToolBar()
	{

	}

	private void initScriptArea()
	{

	}

	private void initResultArea()
	{

	}
}
