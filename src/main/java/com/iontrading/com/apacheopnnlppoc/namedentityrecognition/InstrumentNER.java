package com.iontrading.com.apacheopnnlppoc.namedentityrecognition;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import opennlp.tools.namefind.BioCodec;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.NameSample;
import opennlp.tools.namefind.NameSampleDataStream;
import opennlp.tools.namefind.RegexNameFinder;
import opennlp.tools.namefind.TokenNameFinderFactory;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.sentdetect.SentenceDetectorME;
import opennlp.tools.sentdetect.SentenceModel;
import opennlp.tools.tokenize.SimpleTokenizer;
import opennlp.tools.tokenize.Tokenizer;
import opennlp.tools.util.InputStreamFactory;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Span;
import opennlp.tools.util.TrainingParameters;

import com.iontrading.com.apacheopnnlppoc.models.custom.CustomModels;
import com.iontrading.com.apacheopnnlppoc.models.standard.StandardModels;

// Custom named entity recognition
public class InstrumentNER {
	public static void main(String[] args) {
		InstrumentNER ner = new InstrumentNER();
		
//		System.out.println("Preparing training set");
//		new InstrumentTrainingDataGenerator().generateTrainingSet();
		ner.train();
		ner.find();
	}

	 void find() {
		TokenNameFinderModel model = StandardModels.getTokenNameFinderModel();
		NameFinderME nameFinder = new NameFinderME(CustomModels.getTokenNameFinderModel());

		// prev DE0001143303
		// refdata DE0001142644
//		String str = "BID - 37,000,000  US CONV GILT  RegS 1.5 [ DE0001142644 ] S/D 21/";
//		String str = "BID - 37,000,000  US CONV GILT  RegSj 1.5 DE0001142644 S/D 21/";
//		String str = "BID - 37,000,000  US CONV GILT  RegS DE0001142644 S/D 21/";
//		String str = "BID - 37,000,000  DBR 3.5 01/04/16  [ DE0001135291 ] S/D 21/ ";
//		String str = "BID - 37,000,000  DBR 3.5 01/04/16  [ DE0001135291 ] S/D 21/ please have a look ";
//		String str = "BID - 37,000,000  US CONV GILT  RegS 1.5 22-Mar-2001 [ DE0001142644 ] S/D 21/";
			
		
//		performNER(str);

		// SECOND CASE		
//		String str = "US302154csdcfsdfBA68 EIBKOR 4 3/8 09/15/21  40,000,000";
//		performNER(str);
//		System.out.println();
//		System.out.println("----------------------------------");
//		
//		
//		// THIRD CASE
//		str = "***Looking for XS1598835822 FCABNK 1 11/15/21  5.3mio tom";
//		performNER(str);
//		System.out.println();
//		System.out.println("----------------------------------");
//		
//		String str1 = "$$ 1% offered the following AAA/AA on open for the below";
//		performNER(str1);
//		System.out.println();
//		System.out.println("----------------------------------");
		
		
		
//		SentenceModel sentenceModel = StandardModels.getSentenceModel();
//		String paragraph = "Hi. How are you? This jis Mike.";
		String paragraph = 
				"$$ 1% offered the following AAA/AA on open                      \r\n"+                
"XS1568004060 ERSTAA 1 5/8 02/21/19 20mm                     \r\n"+
"US500630CM82 USD KDB 0 02/27/20        5mm                      \r\n"+
"XS1558184096 USD BNG 2 3/8 02/01/22    5mm						";
		
		long t = System.currentTimeMillis();
		performNER(paragraph);
		System.out.println("Time it took = "+ (System.currentTimeMillis() - t)+" milliseconds");
		
	}

	private void performNER(String str) {
		
		String[] sentences = str.split("\r\n");
		System.out.println("--------------------------------");
		Arrays.stream(sentences).forEach(sentence -> {
			performSentenceNER(StandardModels.getTokenizer().tokenize(sentence));
			System.out.println();
			System.out.println("--------------------------------");
		});

	}

	private void performSentenceNER(String[] strArray) {
		NameFinderME nameFinder = new NameFinderME(CustomModels.getTokenNameFinderModel()); 
		Span nameSpans[] = nameFinder.find(strArray);		
		printSpans(strArray, nameSpans);
	}

	private void printSpans(String[] strArray, Span[] nameSpans) {
		Arrays.stream(nameSpans).forEach(s -> {
			System.out.print("type = "+s.getType()+" ");
			int start = s.getStart();
			int end = s.getEnd();
			System.out.print("{");
			for(int i = start;i<end;i++){
				String entity = strArray[i];
				System.out.print(entity);
			}
			System.out.print("}    ");
		});
	}

	void train(){
		InputStreamFactory in = null;
		try {
			in = new MarkableFileInputStreamFactory(new File("InstrumentTrainingSet_NER.txt"));
		} catch (FileNotFoundException e2) {
			e2.printStackTrace();
		}
		ObjectStream<NameSample> sampleStream = null;
		try {
			sampleStream = new NameSampleDataStream(
					new PlainTextByLineStream(in, StandardCharsets.UTF_8));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		TrainingParameters params = new TrainingParameters();
		params.put(TrainingParameters.ITERATIONS_PARAM, 70);
		params.put(TrainingParameters.CUTOFF_PARAM, 1);

		TokenNameFinderModel nameFinderModel = null;
		try {
			Map<String,Object>m=new HashMap<String, Object>();
			nameFinderModel = NameFinderME.train("en", null, sampleStream,
					params, TokenNameFinderFactory.create(null, null, m, new BioCodec()));

		} catch (IOException e) {
			e.printStackTrace();
		}
		try{
			File output = new File("ner-custom-model.bin");
			Files.deleteIfExists(output.toPath());
			FileOutputStream outputStream = new FileOutputStream(output);
			nameFinderModel.serialize(outputStream);
		}catch(Exception e){

		}

	}



	private void poc1() throws IOException {
		//        Logger log = LoggerFactory.getLogger(BasicNameFinder.class);

		String[] sentences = {
				"Bernard Shaw is a fine artist.",
				"If President John F. Kennedy, after visiting France in 1961 with his immensely popular wife,"
						+ " famously described himself as 'the man who had accompanied Jacqueline Kennedy to Paris,'"
						+ " Mr. Hollande has been most conspicuous on this state visit for traveling alone.",
						"Mr. Draghi spoke on the first day of an economic policy conference here organized by"
								+ " the E.C.B. as a sort of counterpart to the annual symposium held in Jackson"
								+ " Hole, Wyo., by the Federal Reserve Bank of Kansas City. " };

		// Load the model file downloaded from OpenNLP
		// http://opennlp.sourceforge.net/models-1.5/en-ner-person.bin
		TokenNameFinderModel model = new TokenNameFinderModel(new File(
				"en-ner-person.bin"));

		// Create a NameFinder using the model
		NameFinderME finder = new NameFinderME(model);

		Tokenizer tokenizer = SimpleTokenizer.INSTANCE;

		for (String sentence : sentences) {

			// Split the sentence into tokens
			String[] tokens = tokenizer.tokenize(sentence);

			// Find the names in the tokens and return Span objects
			Span[] nameSpans = finder.find(tokens);

			// Print the names extracted from the tokens using the Span data
			//            log.info(Arrays.toString(Span.spansToStrings(nameSpans, tokens)));
			System.out.println(Arrays.toString(Span.spansToStrings(nameSpans, tokens)));
		}
	}



}
