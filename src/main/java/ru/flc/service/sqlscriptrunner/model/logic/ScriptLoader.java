package ru.flc.service.sqlscriptrunner.model.logic;

import ru.flc.service.sqlscriptrunner.view.ResultView;

import javax.swing.*;
import java.io.*;
import java.nio.charset.Charset;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class ScriptLoader extends SwingWorker<Void, String>
{
	private File scriptFile;
	private Charset fileCharset;
	private ResultView view;

	public ScriptLoader(File scriptFile, Charset fileCharset, ResultView view)
	{
		this.scriptFile = scriptFile;
		this.view = view;

		if (fileCharset != null)
			this.fileCharset = fileCharset;
		else
			this.fileCharset = Charset.defaultCharset();
	}

	@Override
	protected Void doInBackground() throws Exception
	{
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(scriptFile), fileCharset));
		String line;

		while (!isCancelled() && (line = reader.readLine()) != null)
			publish(line);

		reader.close();

		return null;
	}

	@Override
	protected void process(List<String> chunks)
	{
		if (isCancelled()) return;

		view.addData(chunks);
	}

	@Override
	protected void done()
	{
		if (isCancelled())
			view.clearData();
		else
		{
			try
			{
				get();
			}
			catch (InterruptedException e)
			{}
			catch (ExecutionException e)
			{}

			view.presentData();
		}
	}
}
