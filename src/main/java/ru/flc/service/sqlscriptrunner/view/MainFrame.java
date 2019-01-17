package ru.flc.service.sqlscriptrunner.view;

import org.dav.service.util.ResourceManager;
import org.dav.service.view.Title;
import org.dav.service.view.TitleAdjuster;
import ru.flc.service.sqlscriptrunner.RunnerResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame
{
	private static final Dimension PANE_MIN_SIZE = new Dimension(200, 100);
	private static final Dimension MAIN_WIN_MIN_SIZE = new Dimension(200, 300);

	private ResourceManager resourceManager;
	private TitleAdjuster titleAdjuster;

	private JTextArea scriptArea;
	private JScrollPane scriptPane;

	private JTable resultTable;
	private JScrollPane resultPane;

	private AbstractAction openFileAction;
	private AbstractAction runAction;
	private AbstractAction closeAction;
	private AbstractAction helpAction;
	private AbstractAction settingsAction;

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
		initResultTable();
		initSplitter();
		initFrame();

		pack();
	}

	private void initFrame()
	{
		setIconImage(resourceManager.getImageIcon(RunnerConstants.ICON_NAME_MAIN).getImage());
		setPreferredSize(MAIN_WIN_MIN_SIZE);
		setMinimumSize(MAIN_WIN_MIN_SIZE);
	}

	private void initActions()
	{
		openFileAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(MainFrame.this,
						"Open a script.", "Message", JOptionPane.INFORMATION_MESSAGE);
			}
		};

		runAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(MainFrame.this,
						"Run the script.", "Message", JOptionPane.INFORMATION_MESSAGE);
			}
		};

		closeAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(MainFrame.this,
						"Close the script.", "Message", JOptionPane.INFORMATION_MESSAGE);
			}
		};

		settingsAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(MainFrame.this,
						"Show settings.", "Message", JOptionPane.INFORMATION_MESSAGE);
			}
		};

		helpAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(MainFrame.this,
						"Show info about the program.", "Message", JOptionPane.INFORMATION_MESSAGE);
			}
		};

		resetActions();
	}

	private void resetActions()
	{
		resetAction(openFileAction,
				new Title(resourceManager, RunnerConstants.KEY_ACTION_OPEN_SCRIPT).getText(),
				new Title(resourceManager, RunnerConstants.KEY_ACTION_OPEN_SCRIPT_DESCR).getText(),
				RunnerConstants.ICON_NAME_OPEN);

		resetAction(runAction,
				new Title(resourceManager, RunnerConstants.KEY_ACTION_RUN_SCRIPT).getText(),
				new Title(resourceManager, RunnerConstants.KEY_ACTION_RUN_SCRIPT_DESCR).getText(),
				RunnerConstants.ICON_NAME_RUN);

		resetAction(closeAction,
				new Title(resourceManager, RunnerConstants.KEY_ACTION_CLOSE_SCRIPT).getText(),
				new Title(resourceManager, RunnerConstants.KEY_ACTION_CLOSE_SCRIPT_DESCR).getText(),
				RunnerConstants.ICON_NAME_CLOSE);

		resetAction(settingsAction,
				new Title(resourceManager, RunnerConstants.KEY_ACTION_SHOW_SETTINGS).getText(),
				new Title(resourceManager, RunnerConstants.KEY_ACTION_SHOW_SETTINGS_DESCR).getText(),
				RunnerConstants.ICON_NAME_SETTINGS);

		resetAction(helpAction,
				new Title(resourceManager, RunnerConstants.KEY_ACTION_HELP).getText(),
				new Title(resourceManager, RunnerConstants.KEY_ACTION_HELP_DESCR).getText(),
				RunnerConstants.ICON_NAME_QUESTION);
	}

	private void resetAction(AbstractAction action,
							 String actionName,
							 String actionShortDescription,
							 String actionIconName)
	{
		action.putValue(Action.NAME, actionName);
		action.putValue(Action.SHORT_DESCRIPTION, actionShortDescription);
		action.putValue(Action.SMALL_ICON, resourceManager.getImageIcon(actionIconName));
	}

	private void initToolBar()
	{
		JToolBar toolBar = new JToolBar();
		toolBar.setFloatable(false);

		toolBar.add(openFileAction);
		toolBar.add(runAction);
		toolBar.add(closeAction);
		toolBar.add(settingsAction);
		toolBar.add(helpAction);

		add(toolBar, BorderLayout.NORTH);
	}

	private void initScriptArea()
	{
		scriptArea = new JTextArea();
		scriptArea.setEditable(false);

		scriptPane = new JScrollPane(scriptArea);
		scriptPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scriptPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scriptPane.setMinimumSize(PANE_MIN_SIZE);
	}

	private void initResultTable()
	{
		resultTable = new JTable();

		resultPane = new JScrollPane(resultTable);
		resultPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		resultPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		resultPane.setMinimumSize(PANE_MIN_SIZE);
	}

	private void initSplitter()
	{
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scriptPane, resultPane);
		add(splitPane, BorderLayout.CENTER);
	}
}
