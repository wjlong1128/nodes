package com.wjl.asm;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

// 
public class TestProxy {
	public static void main(String[] args) throws Exception {
		byte[] dump = $Proxy0Dump.dump();
		
		// FileOutputStream os = new FileOutputStream("$Proxy0.class");
		// os.write(dump,0,dump.length);
		// os.close();
		
		ClassLoader loader = new ClassLoader() {
			@Override
			protected Class<?> findClass(String name) throws ClassNotFoundException{
				// TODO Auto-generated method stub
				return super.defineClass(name,dump,0,dump.length);
			}
		};
		// 加载类对象
		Class<?> loadClass = loader.loadClass("com.wjl.asm.$Proxy0");
		Constructor<?> constructor = loadClass.getConstructor(InvocationHandler.class);
		Foo foo = (Foo)constructor.newInstance(new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				System.out.println("before");
				///method.invoke(constructor, args);
				System.out.println("after");
				return null;
			}
		});
		
		foo.foo();
		
		
	}
}
