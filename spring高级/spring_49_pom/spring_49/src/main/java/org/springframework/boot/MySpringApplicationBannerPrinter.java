package org.springframework.boot;

import java.io.PrintStream;

import org.springframework.core.env.Environment;
import org.springframework.core.io.ResourceLoader;

public class MySpringApplicationBannerPrinter extends SpringApplicationBannerPrinter{

	public MySpringApplicationBannerPrinter(ResourceLoader resourceLoader, Banner fallbackBanner) {
		super(resourceLoader, fallbackBanner);
	}

	@Override
	public Banner print(Environment environment, Class<?> sourceClass, PrintStream out) {
		return super.print(environment, sourceClass, out);
	}

}
