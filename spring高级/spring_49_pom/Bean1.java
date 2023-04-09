import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class Bean1 {
	
	
	public void foo(String name,int age){

	}

	public static void main(String[] args) throws NoSuchMethodException, SecurityException {
		Method m =  Bean1.class.getMethod("foo",String.class,int.class);
		for (Parameter p :m.getParameters()) {
			System.out.println(p.getName());
		}
	}
	
}