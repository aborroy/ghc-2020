package org.alfresco.main;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.alfresco.bean.Input;
import org.alfresco.bean.LibraryOutput;
import org.alfresco.bean.Output;
import org.alfresco.engine.SimpleEngine;
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

		Output out = simpleEngine.run(in);

		File outFile = new File(ps.getProperty("fileOut").toString());
		Translator.writeOutput(out, outFile);

	}

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

}
