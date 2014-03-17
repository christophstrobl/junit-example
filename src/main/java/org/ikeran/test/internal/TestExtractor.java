package org.ikeran.test.internal;

import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.ikeran.test.Sequential;
import org.ikeran.test.Source;
import org.ikeran.test.Values;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;

/**
 *
 * @author dhasenan
 */
public class TestExtractor {

	public TestClass findTests(Class<?> classUnderTest) {
		TestClass testClass = new TestClass(classUnderTest);
		for (Method method : classUnderTest.getMethods()) {
			Annotation[] annotations = method.getAnnotations();
			org.junit.Test testAnnotation = null;
			Source source = null;
			Values values = null;
			boolean ignore = false;
			boolean combinatorial = true;
			for (Annotation ann : annotations) {
				if (ann instanceof Ignore) {
					ignore = true;
				} else if (ann instanceof org.junit.Test) {
					testAnnotation = (org.junit.Test) ann;
				} else if (ann instanceof Before) {
					testClass.before = method;
					continue;
				} else if (ann instanceof After) {
					testClass.after = method;
					continue;
				} else if (ann instanceof BeforeClass) {
					testClass.beforeClass = method;
					continue;
				} else if (ann instanceof AfterClass) {
					testClass.afterClass = method;
					continue;
				} else if (ann instanceof Source) {
					source = (Source) ann;
				} else if (ann instanceof Values) {
					values = (Values) ann;
				} else if (ann instanceof Sequential) {
					combinatorial = false;
				}
			}
			if (testAnnotation == null) {
				continue;
			}
			TestMethod test = extractTestMethod(testClass, method, testAnnotation, source, values, combinatorial);
			test.ignore = ignore;
			testClass.tests.add(test);
		}
		return testClass;
	}

	public TestMethod extractTestMethod(TestClass testClass, Method method, org.junit.Test testAnnotation, Source source, Values values, boolean combinatorial) {
		TestMethod testMethod = new TestMethod(testClass, method);
		testMethod.maxElapsedMilliseconds = testAnnotation.timeout();
		if (testMethod.maxElapsedMilliseconds == 0) {
			testMethod.maxElapsedMilliseconds = Integer.MAX_VALUE;
		}
		if (testAnnotation.expected() != org.junit.Test.None.class) {
			testMethod.expectedException = testAnnotation.expected();
		}
		try {
			readChildTests(method, testMethod, values, source, testClass, combinatorial);
		} catch (Exception ex) {
			testMethod.thrownWhenFilling = ex;
			if (testMethod.tests.isEmpty()) {
				// make sure we have something to report an error on
				testMethod.tests.add(new Test(testMethod));
			}
		}
		return testMethod;
	}

	private void readChildTests(Method method, TestMethod testMethod, Values values, Source source, TestClass testClass, boolean combinatorial) {
		final Class<?>[] paramTypes = method.getParameterTypes();
		Annotation[][] annotations = method.getParameterAnnotations();
		if (paramTypes.length == 0) {
			// TODO error here if you provided source or values
			testMethod.tests.add(new Test(testMethod));
		} else {
			if (paramTypes.length == 1 && values != null) {
				final List<Object[]> parameterSets = toParameterSets(new List[]{extractValues(values, paramTypes[0])}, false);
				addTests(parameterSets, testMethod);
			} else if (source != null) {
				// assume it's List<Object[]>()
				final List<Object[]> parameterSets = extractAllFromSource(testClass, source);
				addTests(parameterSets, testMethod);
			} else {
				List[] byParameter = readArgumentsFromParameterAnnotations(paramTypes, annotations, testClass);
				addTests(toParameterSets(byParameter, combinatorial), testMethod);
			}
		}
	}

	private List[] readArgumentsFromParameterAnnotations(final Class<?>[] paramTypes, Annotation[][] annotations, TestClass testClass) {
		// ignore any @Values on method level
		List[] byParameter = new List[paramTypes.length];
		int i = 0;
		for (Annotation[] arr : annotations) {
			for (Annotation ann : arr) {
				if (ann instanceof Values) {
					byParameter[i] = extractValues((Values) ann, paramTypes[i]);
				} else if (ann instanceof Source) {
					byParameter[i] = extractSingleFromSource(testClass, (Source) ann);
				}
			}
			if (byParameter[i] == null) {
				// TODO error here
			}
			i++;
		}
		return byParameter;
	}

	private void addTests(final List<Object[]> parameterSets, TestMethod testMethod) {
		// TODO error here if you provide method-level and parameter-level stuff
		for (Object[] paramSet : parameterSets) {
			Test test = new Test(testMethod);
			test.parameters = paramSet;
			testMethod.tests.add(test);
		}
	}

	// We assume !combinatorial means sequential
	private List<Object[]> toParameterSets(List[] params, boolean combinatorial) {
		for (List o : params) {
			if (o == null) {
				Object[] prototype = new Object[params.length];
				return Arrays.asList(new Object[][]{prototype});
			}
			Object first = o.size() > 0 ? o.get(0) : null;
		}
		List<Object[]> result = new ArrayList<Object[]>();
		if (combinatorial) {
			Object[] prototype = new Object[params.length];
			runCombinatorial(result, params, 0, prototype);
		} else {
			int i = 0;
			while (true) {
				Object[] current = new Object[params.length];
				int j = 0;
				boolean gotOne = false;
				for (List list : params) {
					if (list.size() <= i) {
						current[j] = null;
					} else {
						gotOne = true;
						current[j] = list.get(i);
					}
					j++;
				}
				if (gotOne) {
					result.add(current);
				} else {
					break;
				}
				i++;
			}
		}
		return result;
	}

	private List extractValues(Values values, Class<?> parameterType) {
		// Arrays.asList(String[]) returns List<String>
		// But Arrays.asList(double[]) returns List<double[]>
		// Fucking hell, java, can you get *anything* right?
		Object array = null;
		if (values.chars().length > 0) {
			array = values.chars();
		} else if (values.numbers().length > 0) {
			array = values.numbers();
		} else if (values.strings().length > 0) {
			return Arrays.asList(values.strings());
		}
		int length = Array.getLength(array);
		List<Object> objs = new ArrayList<Object>();
		for (int i = 0; i < length; i++) {
			objs.add(cast(Array.get(array, i), parameterType));
		}
		return objs;
	}

	private Object cast(Object o, Class<?> clazz) {
		// int.cast(double) fails. This is why I wrote this.
		// I feel compelled to add that this would work in C#.
		if (o == null) {
			if (clazz.isPrimitive() || clazz.isEnum()) {
				throw new RuntimeException("Cannot cast `null' to type " + clazz.getName());
			}
			return null;
		}
		if (o.getClass() == Double.TYPE || o.getClass() == Double.class) {
			Double boxed = (Double) o;
			double d = boxed;
			if (clazz == Byte.class || clazz == Byte.TYPE) {
				return (byte) d;
			} else if (clazz == Short.class || clazz == Short.TYPE) {
				return (short) d;
			} else if (clazz == Integer.class || clazz == Integer.TYPE) {
				return (int) d;
			} else if (clazz == Long.class || clazz == Long.TYPE) {
				return (long) d;
			} else if (clazz == Float.class || clazz == Float.TYPE) {
				return (float) d;
			}
		}
		throw new RuntimeException("Cannot cast `" + o + "' to " + clazz.getName());
	}

	private void runCombinatorial(List<Object[]> result, List[] params, int i, Object[] argumentSet) {
		if (i >= params.length) {
			result.add(Arrays.copyOf(argumentSet, argumentSet.length));
			return;
		}
		List current = params[i];
		for (Object o : current) {
			argumentSet[i] = o;
			runCombinatorial(result, params, i + 1, argumentSet);
		}
	}

	private List extractSingleFromSource(TestClass testClass, Source source) {
		try {
			Method method = testClass.classUnderTest.getMethod(source.value());
			Object o = testClass.classUnderTest.newInstance();
			Collection<Object> result = (Collection<Object>) method.invoke(o);
			return new ArrayList<Object>(result);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}

	private List<Object[]> extractAllFromSource(TestClass testClass, Source source) {
		try {
			Method method = testClass.classUnderTest.getMethod(source.value());
			Object o = testClass.classUnderTest.newInstance();
			Collection<Object[]> result = (Collection<Object[]>) method.invoke(o);
			return new ArrayList<Object[]>(result);
		} catch (Exception ex) {
			throw new RuntimeException(ex);
		}
	}
}
