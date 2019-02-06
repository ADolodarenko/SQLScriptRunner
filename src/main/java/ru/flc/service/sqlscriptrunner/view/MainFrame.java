package ru.flc.service.sqlscriptrunner.view;

import org.dav.service.settings.DatabaseSettings;
import org.dav.service.settings.Settings;
import org.dav.service.settings.TransmissiveSettings;
import org.dav.service.settings.ViewSettings;
import org.dav.service.util.Constants;
import org.dav.service.util.ResourceManager;
import org.dav.service.view.ExtensionInfoType;
import org.dav.service.view.Title;
import org.dav.service.view.TitleAdjuster;
import org.dav.service.view.ViewUtils;
import org.dav.service.view.dialog.SettingsDialog;
import org.dav.service.view.dialog.SettingsDialogInvoker;
import ru.flc.service.sqlscriptrunner.RunnerResourceManager;
import ru.flc.service.sqlscriptrunner.model.ApplicationState;
import ru.flc.service.sqlscriptrunner.model.logic.ScriptLoader;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class MainFrame extends JFrame implements ResultView, SettingsDialogInvoker
{
	private static final Dimension PANE_MIN_SIZE = new Dimension(200, 100);
	private static final Dimension MAIN_WIN_PREF_SIZE = new Dimension(600, 400);
	private static final Dimension MAIN_WIN_MIN_SIZE = new Dimension(200, 300);

	private static final String newLineChar = "\n";

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
	private AboutDialog aboutDialog;

	private ApplicationState currentState;

	private ScriptLoader scriptLoader;
	private List<String> scriptLines;

	public MainFrame()
	{
		resourceManager = RunnerResourceManager.getInstance();
		ViewUtils.resetResourceManager(resourceManager);

		loadAllSettings();
		initComponents();
		initFrame();

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

	private void initComponents()
	{
		resourceManager.setCurrentLocale(viewSettings.getAppLocale());
		titleAdjuster = new TitleAdjuster();
		ViewUtils.setDialogOwner(this);
		ViewUtils.adjustDialogs();

		initActions();
		initToolBar();
		initScriptArea();
		initLogTable();
		initSplitter();

		String assemblyInfo = ViewUtils.getAssemblyInformationString(this, " - ",
				new ExtensionInfoType[]{ExtensionInfoType.IMPLEMENTATION_TITLE,
						ExtensionInfoType.IMPLEMENTATION_VERSION});

		titleAdjuster.registerComponent(this, new Title(resourceManager,
				Constants.KEY_MAIN_FRAME,
				assemblyInfo,
				""));

		titleAdjuster.resetComponents();

		pack();
	}

	private void initFrame()
	{
		setIconImage(resourceManager.getImageIcon(RunnerConstants.ICON_NAME_MAIN).getImage());

		setMinimumSize(MAIN_WIN_MIN_SIZE);
		setPreferredSize(MAIN_WIN_MIN_SIZE);

		setResizingPolicy();
		setBoundsFromSettings();
		setClosingPolicy();
	}

	private void setResizingPolicy()
	{
		addComponentListener(new ComponentAdapter()
		{
			@Override
			public void componentResized(ComponentEvent e)
			{
				Dimension currentDim = MainFrame.this.getSize();
				Dimension minimumDim = MainFrame.this.getMinimumSize();

				if (currentDim.width < minimumDim.width)
					currentDim.width = minimumDim.width;
				if (currentDim.height < minimumDim.height)
					currentDim.height = minimumDim.height;

				MainFrame.this.setSize(currentDim);
			}
		});
	}

	private void setBoundsFromSettings()
	{
		if (viewSettings.isMainWindowMaximized())
			setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
		else
			setBounds(viewSettings.getMainWindowPosition().x,
					viewSettings.getMainWindowPosition().y,
					viewSettings.getMainWindowSize().width,
					viewSettings.getMainWindowSize().height);
	}

	private void setClosingPolicy()
	{
		addWindowListener(new WindowAdapter()
		{
			@Override
			public void windowClosing(WindowEvent e)
			{
				//cancelProcesses();
				updateViewSettings();
			}
		});
	}

	private void initActions()
	{
		openFileAction = new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				openScript();
			}
		};

		runAction = new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				JOptionPane.showMessageDialog(MainFrame.this,
						"Run the script.", "Message", JOptionPane.INFORMATION_MESSAGE);
			}
		};

		closeAction = new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				closeScript();
			}
		};

		settingsAction = new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				showSettings();
			}
		};

		helpAction = new AbstractAction()
		{
			@Override
			public void actionPerformed(ActionEvent e)
			{
				showAboutInfo();
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

	private void updateViewSettings()
	{
		viewSettings.setMainWindowMaximized(getExtendedState() == JFrame.MAXIMIZED_BOTH);
		viewSettings.setMainWindowPosition(getBounds().getLocation());
		viewSettings.setMainWindowSize(getSize());

		updateSpecificSettings(viewSettings);
	}

	private void updateSpecificSettings(Settings settings)
	{
		try
		{
			settings.save();
		}
		catch (Exception e)
		{
			log(e);
		}
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

		Font font = new Font(Font.MONOSPACED, Font.PLAIN, 14);
		scriptArea.setFont(font);
		scriptArea.setLineWrap(true);
		scriptArea.setWrapStyleWord(true);

		TextLineNumber lineNumber = new TextLineNumber(scriptArea);

		scriptPane = new JScrollPane(scriptArea);
		scriptPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		scriptPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scriptPane.setMinimumSize(PANE_MIN_SIZE);
		scriptPane.setRowHeaderView(lineNumber);
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
			JFileChooser fileChooser = ViewUtils.getFileChooser(new File("."));
			fileChooser.resetChoosableFileFilters();
			fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("SQL", "SQL"));

			if (fileChooser.showOpenDialog(ViewUtils.getDialogOwner()) == JFileChooser.APPROVE_OPTION)
			{
				scriptLoader = new ScriptLoader(fileChooser.getSelectedFile(), this);
				scriptLoader.getPropertyChangeSupport().addPropertyChangeListener("state",
						evt -> doForWorkerEvent(scriptLoader, evt));
				scriptLoader.execute();
			}
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
			clearData();
			clearLogTable();

			setApplicationState(ApplicationState.READY);
		}
	}

	private void showAboutInfo()
	{
		if (aboutDialog == null)
		{
			try
			{
				aboutDialog = new AboutDialog(this, resourceManager);
				aboutDialog.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
			}
			catch (Exception e)
			{
				log(e);
			}
		}

		if (aboutDialog != null)
			aboutDialog.setVisible(true);
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
			TransmissiveSettings[] settingsArray = new TransmissiveSettings[]{dbSettings, viewSettings};

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
		reloadViewSettings();
	}

	private void reloadViewSettings()
	{
		if (viewSettings != null)
		{
			resourceManager.setCurrentLocale(viewSettings.getAppLocale());

			repaintFrame();

			ViewUtils.adjustDialogs();
		}
	}

	private void repaintFrame()
	{
		titleAdjuster.resetComponents();

		validate();
	}

	@Override
	public void addData(List<String> chunks)
	{
		for (String line : chunks)
			scriptLines.add(line);
	}

	@Override
	public void clearData()
	{
		if (scriptLines == null)
			scriptLines = new LinkedList<>();
		scriptLines.clear();

		clearScriptArea();
	}

	@Override
	public void presentData()
	{
		for (String line : scriptLines)
			scriptArea.append(line + newLineChar);
	}

	private void doForWorkerEvent(SwingWorker worker, PropertyChangeEvent event)
	{
		if ("state".equals(event.getPropertyName()))
		{
			Object newValue = event.getNewValue();

			if (newValue instanceof SwingWorker.StateValue)
			{
				SwingWorker.StateValue stateValue = (SwingWorker.StateValue) newValue;

				switch (stateValue)
				{
					case STARTED:
						doForWorkerStarted(worker);
						break;
					case DONE:
						doForWorkerDone(worker);
				}
			}
		}
	}

	private void doForWorkerStarted(SwingWorker worker)
	{
		ApplicationState desirableState = null;

		switch (worker.getClass().getSimpleName())
		{
			case RunnerConstants.CLASS_NAME_SCRIPTLOADER:
				clearData();
				desirableState = ApplicationState.SCRIPT_LOADING;
		}

		if (desirableState != null)
			setApplicationState(desirableState);
	}

	private void doForWorkerDone(SwingWorker worker)
	{
		ApplicationState desirableState = ApplicationState.READY;

		switch (worker.getClass().getSimpleName())
		{
			case RunnerConstants.CLASS_NAME_SCRIPTLOADER:
				if ( !worker.isCancelled() )
					desirableState = ApplicationState.SCRIPT_LOADED;
		}

		setApplicationState(desirableState);
	}
}
