package org.springframework.boot;

import org.springframework.core.env.ConfigurablePropertyResolver;
import org.springframework.core.env.MutablePropertySources;

public class MyApplicationEnvironment extends ApplicationEnvironment{

	@Override
	public String doGetActiveProfilesProperty() {
		return super.doGetActiveProfilesProperty();
	}

	@Override
	public String doGetDefaultProfilesProperty() {
		return super.doGetDefaultProfilesProperty();
	}

	@Override
	public ConfigurablePropertyResolver createPropertyResolver(MutablePropertySources propertySources) {
		return super.createPropertyResolver(propertySources);
	}
	
}
