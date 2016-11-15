package com.sopovs.moradanen.jmh.intern;

import java.io.File;
import java.io.PrintStream;

public class Generator {
	public static void main(String[] args) throws Exception {
		writeClass("Foobar10fieldsFoo", 10, "foo");
		writeClass("Foobar100fieldsFoo", 100, "foo");
		writeClass("Foobar1000fieldsFoo", 1000, "foo");
		writeClass("Foobar10000fieldsFoo", 10000, "foo");

		writeClass("Foobar10fieldsFoooooooooo", 10, "foooooooooo");
		writeClass("Foobar100fieldsFoooooooooo", 100, "foooooooooo");
		writeClass("Foobar1000fieldsFoooooooooo", 1000, "foooooooooo");
		writeClass("Foobar10000fieldsFoooooooooo", 10000, "foooooooooo");
	}

	private static final void writeClass(String name, int fields, String baseFieldName) throws Exception {
		String baseAccessorname = String.valueOf(baseFieldName.charAt(0)).toUpperCase() + baseFieldName.substring(1);
		try (PrintStream out = new PrintStream(
				new File("src/main/java/com/sopovs/moradanen/jmh/intern/test/" + name + ".java"))) {
			out.println("package com.sopovs.moradanen.jmh.intern.test;");
			out.println("");
			out.println("public class " + name + " {");
			for (int i = 0; i < fields; i++) {
				out.println("private String " + baseFieldName + i + ";");

				out.println("public String get" + baseAccessorname + i + "(){");
				out.println("return " + baseFieldName + i + ";");
				out.println("}");

				out.println("public void set" + baseAccessorname + i + "(String " + baseFieldName + i + "){");
				out.println("this. " + baseFieldName + i + " = " + baseFieldName + i + ";");
				out.println("}");
			}
			out.println("}");
		}
	}

	public static String getJson(int fields, String baseFieldName) {
		StringBuilder result = new StringBuilder();
		result.append("{");
		for (int i = 0; i < fields; i++) {
			if (i > 0) {
				result.append(", \n");
			}
			result.append('"').append(baseFieldName).append(i).append('"').append(':')
					.append('"').append(baseFieldName).append(i).append(" value").append('"');
		}
		result.append("}");
		return result.toString();
	}

}
