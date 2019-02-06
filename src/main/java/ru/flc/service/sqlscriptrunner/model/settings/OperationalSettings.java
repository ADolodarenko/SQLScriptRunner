package ru.flc.service.sqlscriptrunner.model.settings;

import org.dav.service.settings.TransmissiveSettings;
import org.dav.service.settings.parameter.ParameterHeader;
import org.dav.service.util.ResourceManager;

public class OperationalSettings extends TransmissiveSettings
{
	private static final int PARAM_COUNT = 1;

	public OperationalSettings(ResourceManager resourceManager)
	{
		super(resourceManager);

		headers = new ParameterHeader[PARAM_COUNT];
		//headers[0] = new ParameterHeader()
	}



	@Override
	public void save() throws Exception
	{

	}
}
