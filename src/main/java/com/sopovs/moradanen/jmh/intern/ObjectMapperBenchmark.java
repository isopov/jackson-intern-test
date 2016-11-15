package com.sopovs.moradanen.jmh.intern;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sopovs.moradanen.jmh.intern.test.Foobar1000fieldsFoo;
import com.sopovs.moradanen.jmh.intern.test.Foobar1000fieldsFoooooooooo;
import com.sopovs.moradanen.jmh.intern.test.Foobar100fieldsFoo;
import com.sopovs.moradanen.jmh.intern.test.Foobar100fieldsFoooooooooo;
import com.sopovs.moradanen.jmh.intern.test.Foobar10fieldsFoo;
import com.sopovs.moradanen.jmh.intern.test.Foobar10fieldsFoooooooooo;

/**
 * @author github.com/isopov
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 2, timeUnit = TimeUnit.SECONDS)
@Fork(1)
@State(Scope.Benchmark)
public class ObjectMapperBenchmark {

	@Param({ "true", "false" })
	public boolean intern;

	@Param({ "10", "100", "1000" })
	public int fields;

	@Param({ "foo", "foooooooooo" })
	public String baseFieldName;

	private ObjectMapper mapper;
	private Class<?> klass;
	private String json;
	private Object object;

	@Setup
	public void setup() throws Exception {
		JsonFactory factory = new JsonFactory();
		if (intern) {
			factory.enable(JsonFactory.Feature.INTERN_FIELD_NAMES);
		} else {
			factory.disable(JsonFactory.Feature.INTERN_FIELD_NAMES);
		}
		mapper = new ObjectMapper(factory);

		switch (baseFieldName) {
		case "foo": {
			switch (fields) {
			case 10:
				klass = Foobar10fieldsFoo.class;
				json = Generator.getJson(10, "foo");
				break;
			case 100:
				klass = Foobar100fieldsFoo.class;
				json = Generator.getJson(100, "foo");
				break;
			case 1000:
				klass = Foobar1000fieldsFoo.class;
				json = Generator.getJson(1000, "foo");
				break;
			default:
				break;
			}
			break;
		}
		case "foooooooooo": {
			switch (fields) {
			case 10:
				klass = Foobar10fieldsFoooooooooo.class;
				json = Generator.getJson(10, "foooooooooo");
				object = mapper.readValue(json, klass);
				break;
			case 100:
				klass = Foobar100fieldsFoooooooooo.class;
				json = Generator.getJson(100, "foooooooooo");
				break;
			case 1000:
				klass = Foobar1000fieldsFoooooooooo.class;
				json = Generator.getJson(1000, "foooooooooo");
				break;

			default:
				break;
			}
			break;
		}
		default:
			throw new IllegalStateException();
		}

		object = mapper.readValue(json, klass);
	}

	@Benchmark
	public Object readValue() throws Exception {
		return mapper.readValue(json, klass);
	}

	@Benchmark
	public String writeValueAsString() throws Exception {
		return mapper.writeValueAsString(object);
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(ObjectMapperBenchmark.class.getSimpleName())
				// .addProfiler(GCProfiler.class)
				.build();

		new Runner(opt).run();
	}

}
