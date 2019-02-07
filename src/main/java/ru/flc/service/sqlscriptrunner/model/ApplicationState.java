package ru.flc.service.sqlscriptrunner.model;

/**
 * This class represents the current status of the application.
 * The application is at one particular status at every single moment.
 */
public enum ApplicationState
{
	READY,
	SCRIPT_LOADING,
	SCRIPT_LOADED,
	SCRIPT_RUNNING
}
