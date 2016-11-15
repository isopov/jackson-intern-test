package com.sopovs.moradanen.jmh.intern;

import java.io.IOException;
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
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.profile.GCProfiler;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;

/**
 * @author vladimir.dolzhenko@gmail.com
 * @since 2016-11-12
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@Warmup(iterations = 2, time = 3, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 2, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(3)
public class PerfInternCache {
	@State(Scope.Benchmark)
	public static class BenchmarkState {
		final JsonFactory factory = new JsonFactory();
		String json;

		@Param({ "100", "1000", "10000", "100000" })
		public int size;

		@Param({ "true", "false" })
		public boolean intern;

		@Setup
		public void setUp() {
			if (intern) {
				factory.enable(JsonFactory.Feature.INTERN_FIELD_NAMES);
			} else {
				factory.disable(JsonFactory.Feature.INTERN_FIELD_NAMES);
			}
			StringBuilder builder = new StringBuilder("{");
			for (int i = 0; i < size; i++) {
				if (i > 0) {
					builder.append(", \n");
				}
				builder.append('"')
						.append("someQName")
						.append(i)
						.append("\": ")
						.append(i);
			}
			builder.append("}");
			json = builder.toString();
		}
	}

	@Benchmark
	public void parseJson(BenchmarkState state, Blackhole bh) throws IOException {
		JsonParser parser = state.factory.createParser(state.json);
		for (;;) {
			JsonToken x = parser.nextToken();
			if (x == null) {
				break;
			}
			bh.consume(x);
		}
	}

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder()
				.include(PerfInternCache.class.getSimpleName())
				.addProfiler(GCProfiler.class)
				.build();

		new Runner(opt).run();
	}
}