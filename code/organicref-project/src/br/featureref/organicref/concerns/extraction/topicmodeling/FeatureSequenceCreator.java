package br.featureref.organicref.concerns.extraction.topicmodeling;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import cc.mallet.pipe.CharSequence2TokenSequence;
import cc.mallet.pipe.Pipe;
import cc.mallet.pipe.PrintInputAndTarget;
import cc.mallet.pipe.SerialPipes;
import cc.mallet.pipe.TokenSequence2FeatureSequence;
import cc.mallet.pipe.TokenSequenceRemoveStopwords;
import cc.mallet.pipe.iterator.StringListIterator;
import cc.mallet.types.InstanceList;

public class FeatureSequenceCreator {

	private Pipe pipe;

	public FeatureSequenceCreator() {
		pipe = buildPipe();
	}

	private Pipe buildPipe() {
		ArrayList pipeList = new ArrayList();

		// Read data from File objects
//		pipeList.add(new Input2CharSequence("UTF-8"));

		// Regular expression for what constitutes a token.
		// This pattern includes Unicode letters, Unicode numbers,
		// and the underscore character. Alternatives:
		// "\\S+" (anything not whitespace)
		// "\\w+" ( A-Z, a-z, 0-9, _ )
		// "[\\p{L}\\p{N}_]+|[\\p{P}]+" (a group of only letters and numbers OR
		// a group of only punctuation marks)
		Pattern tokenPattern = Pattern.compile("\\S+");

		// Tokenize raw strings
		pipeList.add(new CharSequence2TokenSequence(tokenPattern));

		//TODO allow users to inform a stop words list. Command below:
		final TokenSequenceRemoveStopwords removeStopwords = new TokenSequenceRemoveStopwords(false, false);
		List<String> stopWords = new ArrayList<>();
		InputStream is = getClass().getClassLoader().getResourceAsStream("en-stop-words.txt");
		try (InputStreamReader streamReader =
				new InputStreamReader(is, StandardCharsets.UTF_8);
				BufferedReader reader = new BufferedReader(streamReader)) {
			String line;
			while ((line = reader.readLine()) != null) {
				stopWords.add(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		removeStopwords.addStopWords(stopWords.toArray(new String[0]));
		pipeList.add(removeStopwords);

		// Rather than storing tokens as strings, convert
		// them to integers by looking them up in an alphabet.
		pipeList.add(new TokenSequence2FeatureSequence());

		// Print out the features and the label
		pipeList.add(new PrintInputAndTarget());

		return new SerialPipes(pipeList);
	}

	public InstanceList createInstancesForData(List<String> data) {

		StringListIterator iterator = new StringListIterator(data);

		// Construct a new instance list, passing it the pipe
		// we want to use to process instances.
		InstanceList instances = new InstanceList(pipe);

		// Now process each instance provided by the iterator.
		instances.addThruPipe(iterator);

		return instances;
	}

//	public void create() {
//		CommandOption.setSummary (Text2Vectors.class,
//				  "A tool for creating instance lists of FeatureVectors or FeatureSequences from text documents.\n");
//CommandOption.process (Text2Vectors.class, args);
////String[] classDirs = CommandOption.process (Text2Vectors.class, args);
//
//// Print some helpful messages for error cases
//if (args.length == 0) {
//CommandOption.getList(Text2Vectors.class).printUsage(false);
//System.exit (-1);
//}
//if (classDirs.value.length == 0) {
//throw new IllegalArgumentException ("You must include --input DIR1 DIR2 ...' in order to specify a " +
//				"list of directories containing the documents for each class.");
//}
//
//// Remove common prefix from all the input class directories
//int commonPrefixIndex = Strings.commonPrefixIndex (classDirs.value);
//
//logger.info ("Labels = ");
//File[] directories = new File[classDirs.value.length];
//for (int i = 0; i < classDirs.value.length; i++) {
//directories[i] = new File (classDirs.value[i]);
//if (commonPrefixIndex < classDirs.value.length) {
//logger.info ("   "+classDirs.value[i].substring(commonPrefixIndex));
//}
//else {
//logger.info ("   "+classDirs.value[i]);
//}
//}
//
//Pipe instancePipe;
//InstanceList previousInstanceList = null;
//
//if (usePipeFromVectorsFile.wasInvoked()) {
//previousInstanceList = InstanceList.load (usePipeFromVectorsFile.value);
//instancePipe = previousInstanceList.getPipe();
//}
//else if (usePipeFromVectorsFileNoRewrite.wasInvoked()) {
//previousInstanceList = InstanceList.load (usePipeFromVectorsFileNoRewrite.value);
//instancePipe = previousInstanceList.getPipe();
//}
//else {
//
//// Build a new pipe
//
//// Create a list of pipes that will be added to a SerialPipes object later
//ArrayList<Pipe> pipeList = new ArrayList<Pipe>();
//
//// Convert the "target" object into a numeric index
////  into a LabelAlphabet.
//pipeList.add(new Target2Label());
//
//// The "data" field is currently a filename. Save it as "source".
//pipeList.add( new SaveDataInSource() );
//
//// Set "data" to the file's contents. "data" is now a String.
//pipeList.add( new Input2CharSequence(encoding.value) );
//
//// Optionally save the text to "source" -- not recommended if memory is scarce.
//if (saveTextInSource.wasInvoked()) {
//pipeList.add( new SaveDataInSource() );
//}
//
//// Allow the user to specify an arbitrary Pipe object
////  that operates on Strings
//if (stringPipe.wasInvoked()) {
//pipeList.add( (Pipe) stringPipe.value );
//}
//
//// Remove all content before the first empty line. 
////  Useful for email and usenet news posts.
//if (skipHeader.value) {
//pipeList.add( new CharSubsequence(CharSubsequence.SKIP_HEADER) );
//}
//
//// Remove HTML tags. Suitable for SGML and XML.
//if (skipHtml.value) {
//pipeList.add( new CharSequenceRemoveHTML() );
//}
//
//// String replacements
//
//if (! preserveCase.value()) {
//pipeList.add(new CharSequenceLowercase());
//}
//
//if (replacementFiles.value != null || deletionFiles.value != null) {
//NGramPreprocessor preprocessor = new NGramPreprocessor();
//
//if (replacementFiles.value != null) {
//	for (String filename: replacementFiles.value) { preprocessor.loadReplacements(filename); }
//}
//if (deletionFiles.value != null) {
//	for (String filename: deletionFiles.value) { preprocessor.loadDeletions(filename); }
//}
//
//pipeList.add(preprocessor);
//}
//
////
//// Tokenize the input: first compile the tokenization pattern
//// 
//
//Pattern tokenPattern = null;
//
//if (keepSequenceBigrams.value) {
//// We do not want to record bigrams across punctuation,
////  so we need to keep non-word tokens.
//tokenPattern = CharSequenceLexer.LEX_NONWHITESPACE_CLASSES;
//}
//else {
//// Otherwise, try to compile the regular expression pattern.
//              
//try {
//	tokenPattern = Pattern.compile(tokenRegex.value);
//} catch (PatternSyntaxException pse) {
//	throw new IllegalArgumentException("The token regular expression (" + tokenRegex.value + 
//					   ") was invalid: " + pse.getMessage());
//}
//}
//      
//// Add the tokenizer
//pipeList.add(new CharSequence2TokenSequence(tokenPattern));
//
//// Allow user to specify an arbitrary Pipe object
////  that operates on TokenSequence objects.
//if (tokenPipe.wasInvoked()) {
//pipeList.add( (Pipe) tokenPipe.value );
//}
//      
//if (keepSequenceBigrams.value) {
//// Remove non-word tokens, but record the fact that they
////  were there.
//pipeList.add(new TokenSequenceRemoveNonAlpha(true));
//}
//
//// Stopword removal.
//
//if (stoplistFile.wasInvoked()) {
//
//// The user specified a new list
//
//TokenSequenceRemoveStopwords stopwordFilter =
//	new TokenSequenceRemoveStopwords(stoplistFile.value,
//									 encoding.value,
//									 false, // don't include default list
//									 preserveCase.value,
//									 keepSequenceBigrams.value);
//
//if (extraStopwordsFile.wasInvoked()) {
//	stopwordFilter.addStopWords(extraStopwordsFile.value);
//}
//
//pipeList.add(stopwordFilter);
//}
//else if (removeStopWords.value) {
//
//// The user did not specify a new list, so use the default
////  built-in English list, possibly adding extra words.
//
//TokenSequenceRemoveStopwords stopwordFilter =
//	new TokenSequenceRemoveStopwords(false, keepSequenceBigrams.value);
//
//if (extraStopwordsFile.wasInvoked()) {
//	stopwordFilter.addStopWords(extraStopwordsFile.value);
//}
//
//pipeList.add(stopwordFilter);
//
//}
//
//if (stopPatternFile.wasInvoked()) {
//TokenSequenceRemoveStopPatterns stopPatternFilter = 
//	new TokenSequenceRemoveStopPatterns(stopPatternFile.value);
//pipeList.add(stopPatternFilter);
//}
//
//// gramSizes is an integer array, with default value [1].
////  Check if we have a non-default value.
//if (! (gramSizes.value.length == 1 && gramSizes.value[0] == 1)) {
//pipeList.add( new TokenSequenceNGrams(gramSizes.value) );
//}
//
//// So far we have a sequence of Token objects that contain 
////  String values. Look these up in an alphabet and store integer IDs
////  ("features") instead of Strings.
//if (keepSequenceBigrams.value) {
//pipeList.add( new TokenSequence2FeatureSequenceWithBigrams() );
//}
//else {
//pipeList.add( new TokenSequence2FeatureSequence() );
//}
//
//// For many applications, we do not need to preserve the sequence of features,
////  only the number of times times a feature occurs.
//if (! (keepSequence.value || keepSequenceBigrams.value)) {
//pipeList.add( new FeatureSequence2AugmentableFeatureVector(binaryFeatures.value) );
//}
//
//// Allow users to specify an arbitrary Pipe object that operates on 
////  feature vectors.
//if (featureVectorPipe.wasInvoked()) {
//pipeList.add( (Pipe) featureVectorPipe.value );
//}
//
//if (printOutput.value) {
//pipeList.add(new PrintInputAndTarget());
//}
//
//instancePipe = new SerialPipes(pipeList);
//
//}
//
//InstanceList instances = new InstanceList (instancePipe);
//
//boolean removeCommonPrefix = true;
//instances.addThruPipe (new FileIterator (directories, FileIterator.STARTING_DIRECTORIES, removeCommonPrefix));
//
//// write vector file
//ObjectOutputStream oos;
//if (outputFile.value.toString().equals ("-")) {
//oos = new ObjectOutputStream(System.out);
//}
//else {
//oos = new ObjectOutputStream(new FileOutputStream(outputFile.value));
//}
//oos.writeObject(instances);
//oos.close();
//
//// *rewrite* vector file used as source of pipe in case we changed the alphabet(!)
//if (usePipeFromVectorsFile.wasInvoked()) {
//logger.info(" rewriting previous instance list, with ID = " + previousInstanceList.getPipe().getInstanceId());
//oos = new ObjectOutputStream(new FileOutputStream(usePipeFromVectorsFile.value));
//oos.writeObject(previousInstanceList);
//oos.close();
//}
//	}

}
