package com.wjl.spring.x39_BootStart;

import org.springframework.boot.DefaultBootstrapContext;
import org.springframework.boot.MyApplicationEnvironment;
import org.springframework.boot.MySpringApplicationBannerPrinter;
import org.springframework.boot.MySpringBootBanner;
import org.springframework.boot.SpringBootVersion;
import org.springframework.core.io.DefaultResourceLoader;

public class RunStep7 {
	public static void main(String[] args) {
		MyApplicationEnvironment env = new MyApplicationEnvironment();

		// 测试文字 banner
		// env.getPropertySources().addLast(new MapPropertySource("custom", Map.of("spring.banner.location","banner1.txt")));
		// 测试图片 banner
		// env.getPropertySources().addLast(new MapPropertySource("custom", Map.of("spring.banner.image.location","banner2.png")));
		// 版本号的获取
		System.out.println(SpringBootVersion.getVersion());

		MySpringApplicationBannerPrinter printer = new MySpringApplicationBannerPrinter(new DefaultResourceLoader(),
				new MySpringBootBanner());

		printer.print(env, RunStep7.class, System.out);
	}
}
