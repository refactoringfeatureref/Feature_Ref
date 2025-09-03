package br.featureref.organicref.util;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.math.NumberUtils;

import br.featureref.organicref.dataanalysis.AnalysisMode;
import br.featureref.organicref.optimization.algorithms.localsearch.CoolingSchedule;

public class OrganicRefOptions
{
	
	public static final String SOURCE_FOLDER = "source-folder";
	public static final String OUTPUT_FOLDER = "output-folder";
	public static final String CONCERNS_MODEL_DIR = "concerns-model-dir";
	public static final String OPTIMIZATION_ALGORITHM = "optimization-algorithm";
	public static final String CONTEXT_STRATEGY = "context-strategy";
	public static final String MAX_EVALUATIONS = "max-evaluations";
	public static final String POPULATION_SIZE = "population-size";
	public static final String TOPIC_MODEL_MODE = "topic-model-mode";
	public static final String ANALYSIS_MODE = "analysis-mode";
	private static final String COOLING_SCHEDULE = "cooling-schedule";

	private Options options;
	
	private CommandLine line;
	
	private static OrganicRefOptions singleton;

  static {
		singleton = new OrganicRefOptions();
	}
	
	private OrganicRefOptions() {
		options = new Options();
		createOptions();
	}
	
	public static OrganicRefOptions getInstance() {
		return singleton;
	}

	private void createOptions() {
		Option topicModelMode = Option.builder("tm")
				.longOpt(TOPIC_MODEL_MODE)
				.desc("Run the tool only for creating a topic model based on files of source folder. " +
						"Will save the topic model in the output directory.")
				.build();

		Option analysisMode = Option.builder("am")
				.longOpt(ANALYSIS_MODE)
				.desc("Run the tool for analyzing refactoring recommendation results which were saved in json. " +
						"Will save the analysis result in the output directory." +
						"Can be either single project (SP) or multi project (MP)")
				.hasArg()
				.argName("kind")
				.build();

		Option sourceFolder = Option.builder("src")
				.longOpt(SOURCE_FOLDER)
				.desc("Folder containing all files to be processed")
				.required()
				.hasArg()
				.argName("folder")
				.build();

		Option outFolder = Option.builder("out")
				.longOpt(OUTPUT_FOLDER)
				.desc("Folder for saving the resulting output")
				.required()
				.hasArg()
				.argName("folder")
				.build();

		Option selectedAlgorithm = Option.builder("alg")
				.longOpt(OPTIMIZATION_ALGORITHM)
				.desc("Identifier of the selected search-based algorithm. " +
						"Available Options: [SA, NSGAII, NSGAIII, BASELINE]")
				.hasArg()
				.argName("algorithm")
				.build();
		
		Option contextStrategy = Option.builder("con")
				.longOpt(CONTEXT_STRATEGY)
				.desc("Identifier of the desired context selection strategy. Available Options: [SMELLS, LOCAL_CHANGE]")
				.hasArg()
				.argName("strategy")
				.build();

		Option concernsModelFile = Option.builder("cmf")
				.longOpt(CONCERNS_MODEL_DIR)
				.desc("Path to the directory containing the concern model file (topics-model.gz) and the inferencer file (inferencer.mallet)")
				.hasArg()
				.argName("file")
				.build();

		Option maxEvaluations = Option.builder("mev")
				.longOpt(MAX_EVALUATIONS)
				.desc("Maximum number of evaluations for the optimization algorithm")
				.hasArg()
				.argName("evaluations")
				.build();

		Option populationSize = Option.builder("ps")
				.longOpt(POPULATION_SIZE)
				.desc("Size of the initial population (only for global search algorithms)")
				.hasArg()
				.argName("size")
				.build();

		Option coolingSchedule = Option.builder("cs")
				.longOpt(COOLING_SCHEDULE)
				.desc("Cooling schedule for the Simulated Annealing algorithm. Available Options: [EXPONENTIAL, LINEAR, BOLTZ]")
				.hasArg()
				.argName("schedule")
				.build();

		options.addOption(topicModelMode);
		options.addOption(analysisMode);
		options.addOption(sourceFolder);
		options.addOption(outFolder);
		options.addOption(selectedAlgorithm);
		options.addOption(contextStrategy);
		options.addOption(concernsModelFile);
		options.addOption(maxEvaluations);
		options.addOption(populationSize);
		options.addOption(coolingSchedule);
	}
	
	public String getValue(String key) {
		return this.line.getOptionValue(key);
	}
	
	public void parse(String[] args) throws ParseException {
		CommandLineParser parser = new DefaultParser();
		this.line = parser.parse(getOptions(), args);
	}
	
	public void printHelp() {
		HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("organic", new OrganicRefOptions().getOptions() );
	}
	
	public Options getOptions() {
		return options;
	}

	public int getMaxEvaluations(int defaultValue) {
		return NumberUtils.toInt(getValue(MAX_EVALUATIONS), defaultValue);
	}

	public int getPopulationSize(int defaultValue) {
		return NumberUtils.toInt(getValue(POPULATION_SIZE), defaultValue);
	}

	public boolean isTopicModelMode() {
		return this.line.hasOption(TOPIC_MODEL_MODE);
	}

	public boolean isAnalysisMode() {
		return this.line.hasOption(ANALYSIS_MODE);
	}

	public CoolingSchedule getCoolingSchedule(final CoolingSchedule defaultValue)
	{
		try
		{
			return CoolingSchedule.valueOf(getValue(COOLING_SCHEDULE));
		} catch (Exception ex) {
			return defaultValue;
		}
	}

	public AnalysisMode getAnalysisMode(final AnalysisMode defaultValue)
	{
		try
		{
			return AnalysisMode.valueOf(getValue(ANALYSIS_MODE));
		} catch (Exception ex) {
			return defaultValue;
		}
	}
}
