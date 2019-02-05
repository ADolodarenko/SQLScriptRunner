package ru.flc.service.sqlscriptrunner.view;

import java.util.List;

public interface ResultView
{
	void addData(List<String> chunks);
	void clearData();
	void presentData();
}
