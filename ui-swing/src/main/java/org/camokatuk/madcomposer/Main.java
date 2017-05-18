package org.camokatuk.madcomposer;

import org.camokatuk.madcomposer.engine.Application;
import org.camokatuk.madcomposer.engine.DefaultApplicationConfig;
import org.camokatuk.madcomposer.ui.View;

public class Main
{
	public static void main(String[] sd)
	{
		Application application = new Application();
		application.initialize(new DefaultApplicationConfig());

		View view = new View(application.getApplicationControlRoom());
		view.initialize();
	}
}
