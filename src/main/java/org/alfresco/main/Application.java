package org.alfresco.main;

import java.io.File;

import org.alfresco.bean.Input;
import org.alfresco.bean.Output;
import org.alfresco.engine.SimpleEngine;
import org.alfresco.engine.SimpleEngine.Strategy;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.PropertySource;
import org.springframework.core.env.SimpleCommandLinePropertySource;

/**
 * Main class to be launched from command line
 * 
 * java -jar target/hashcode-2020-1.0.0.jar
 * --fileIn=src/main/resources/in/a_example.in
 * --fileOut=src/main/resources/in/a_example.out
 * --strategy=0
 * 
 * Available strategies:
 * - 0: SHORTER_SIGNUPDAYS_MORE_BOOKS
 * - 1: MORE_VALUABLE_LIBRARY_FIRST
 * 
 */
@SpringBootApplication
public class Application implements CommandLineRunner {

	private SimpleEngine simpleEngine = new SimpleEngine();

	@Override
	public void run(String... args) throws Exception {

		PropertySource<?> ps = new SimpleCommandLinePropertySource(args);
		File inFile = new File(ps.getProperty("fileIn").toString());
		Input in = Translator.getInput(inFile);
		
		Integer strategyNumber = Integer.valueOf(ps.getProperty("strategy").toString());
		Output out = simpleEngine.run(in, Strategy.values()[strategyNumber]);
		
		File outFile = new File(ps.getProperty("fileOut").toString());
		Translator.writeOutput(out, outFile);

	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
