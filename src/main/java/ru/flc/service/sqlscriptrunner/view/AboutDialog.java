package ru.flc.service.sqlscriptrunner.view;

import org.dav.service.util.ResourceManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class AboutDialog extends JDialog
{
	private JLabel imageLabel;
	private JLabel titleLabel;
	private JLabel copyrightTitle;

	private Frame owner;
	private ResourceManager resourceManager;

	public AboutDialog(Frame owner, ResourceManager resourceManager)
	{
		super(owner, "", true);

		setUndecorated(true);

		this.owner = owner;
		this.resourceManager = resourceManager;

		initComponents();

		pack();

		addMouseListener(new AboutDialogMouseListener());
	}

	private void initComponents()
	{
		imageLabel = new JLabel(resourceManager.getImageIcon(RunnerConstants.ICON_NAME_ABOUT));
		imageLabel.setLayout(new BorderLayout());

		titleLabel = new JLabel(" SQL Script Runner 1.0.0");
		titleLabel.setFont(new Font(Font.MONOSPACED, Font.BOLD + Font.ITALIC, 20));
		titleLabel.setForeground(Color.WHITE);
		imageLabel.add(titleLabel, BorderLayout.NORTH);

		copyrightTitle = new JLabel(" (c) Family Leasure Club");
		copyrightTitle.setFont(new Font(Font.MONOSPACED, Font.BOLD, 14));
		copyrightTitle.setForeground(Color.WHITE);
		imageLabel.add(copyrightTitle, BorderLayout.SOUTH);

		add(imageLabel);
	}

	@Override
	public void setVisible(boolean b)
	{
		if (b)
			setLocationRelativeTo(owner);

		super.setVisible(b);
	}

	private class AboutDialogMouseListener extends MouseAdapter
	{
		@Override
		public void mouseClicked(MouseEvent e)
		{
			AboutDialog.this.setVisible(false);
		}
	}
}
