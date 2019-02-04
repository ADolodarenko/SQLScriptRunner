package ru.flc.service.sqlscriptrunner.view;

import org.dav.service.settings.DatabaseSettings;
import org.dav.service.settings.TransmissiveSettings;
import org.dav.service.settings.ViewSettings;
import org.dav.service.util.ResourceManager;
import org.dav.service.view.Title;
import org.dav.service.view.TitleAdjuster;
import org.dav.service.view.ViewUtils;
import org.dav.service.view.dialog.SettingsDialog;
import org.dav.service.view.dialog.SettingsDialogInvoker;
import ru.flc.service.sqlscriptrunner.RunnerResourceManager;
import ru.flc.service.sqlscriptrunner.model.ApplicationState;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class MainFrame extends JFrame implements SettingsDialogInvoker
{
	private static final Dimension PANE_MIN_SIZE = new Dimension(200, 100);

	private static final Dimension MAIN_WIN_PREF_SIZE = new Dimension(600, 400);
	private static final Dimension MAIN_WIN_MIN_SIZE = new Dimension(200, 300);

	private ResourceManager resourceManager;
	private TitleAdjuster titleAdjuster;

	private JTextArea scriptArea;
	private JScrollPane scriptPane;

	private JTable logTable;
	private JScrollPane logPane;

	private AbstractAction openFileAction;
	private AbstractAction runAction;
	private AbstractAction closeAction;
	private AbstractAction helpAction;
	private AbstractAction settingsAction;

	private DatabaseSettings dbSettings;
	private ViewSettings viewSettings;
	private SettingsDialog settingsDialog;

	private ApplicationState currentState;

	public MainFrame()
	{
		resourceManager = RunnerResourceManager.getInstance();
		ViewUtils.resetResourceManager(resourceManager);

		loadAllSettings();
		loadComponents();

		setApplicationState(ApplicationState.READY);
	}

	private void loadAllSettings()
	{
		loadDatabaseSettings();
		loadViewSettings();
	}

	private void loadDatabaseSettings()
	{
		try
		{
			dbSettings = new DatabaseSettings(resourceManager);
			dbSettings.load();
		}
		catch (Exception e)
		{
			log(e);
		}
	}

	private void loadViewSettings()
	{
		try
		{
			viewSettings = new ViewSettings(resourceManager, MAIN_WIN_PREF_SIZE);
			viewSettings.load();
		}
		catch (Exception e)
		{
			log(e);
		}
	}

	private void loadComponents()
	{
		titleAdjuster = new TitleAdjuster();

		initActions();
		initToolBar();
		initScriptArea();
		initLogTable();
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
				closeScript();
			}
		};

		settingsAction = new AbstractAction() {
			@Override
			public void actionPerformed(ActionEvent e)
			{
				showSettings();
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

	private void initLogTable()
	{
		logTable = new JTable();

		logPane = new JScrollPane(logTable);
		logPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		logPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		logPane.setMinimumSize(PANE_MIN_SIZE);
	}

	private void initSplitter()
	{
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, scriptPane, logPane);
		add(splitPane, BorderLayout.CENTER);
	}

	private void openScript()
	{
		if (checkApplicationStates(ApplicationState.READY, ApplicationState.SCRIPT_LOADED))
		{
			//TODO: Open a script here.

			setApplicationState(ApplicationState.SCRIPT_LOADING);
		}
	}

	private void runScript()
	{
		if (checkApplicationStates(ApplicationState.SCRIPT_LOADED))
		{
			//TODO: Run the script here

			setApplicationState(ApplicationState.SCRIPT_RUNNING);
		}
	}

	private void closeScript()
	{
		if (checkApplicationStates(ApplicationState.SCRIPT_LOADING, ApplicationState.SCRIPT_LOADED))
		{
			clearScriptArea();
			clearLogTable();

			setApplicationState(ApplicationState.READY);
		}
	}

	private void clearScriptArea()
	{
		scriptArea.setText(null);
	}

	private void clearLogTable()
	{
		//TODO: clear the log table here
	}

	private void setApplicationState(ApplicationState desirableState)
	{
		currentState = desirableState;
	}

	private boolean checkApplicationStates(ApplicationState... desirableStates)
	{
		for (ApplicationState status : desirableStates)
			if (status == currentState)
				return true;

		return false;
	}

	private void showSettings()
	{
		if (settingsDialog == null)
		{
			TransmissiveSettings[] settingsArray = new TransmissiveSettings[]{dbSettings};

			try
			{
				settingsDialog = new SettingsDialog(this, this, resourceManager, settingsArray);
				settingsDialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			}
			catch (Exception e)
			{
				log(e);
			}
		}

		if (settingsDialog != null)
			settingsDialog.setVisible(true);
	}

	@Override
	public void log(Exception e)
	{

	}

	@Override
	public void setFocus()
	{

	}

	@Override
	public void reloadSettings()
	{

	}
}
