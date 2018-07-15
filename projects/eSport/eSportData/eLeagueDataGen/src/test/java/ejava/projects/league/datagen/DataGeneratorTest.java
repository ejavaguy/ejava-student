package ejava.projects.league.datagen;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ejava.projects.eleague.datagen.DataGenerator;

import junit.framework.TestCase;

public class DataGeneratorTest extends TestCase {
	Logger log = LoggerFactory.getLogger(DataGeneratorTest.class);
	String outputDir = System.getProperty("outputDir");
	String auctionCountStr = System.getProperty("auction.count");
	int auctionCount=-1;
	
	public void setUp() {
		assertNotNull("outputDir not supplied", outputDir);
		
		if (auctionCountStr != null && auctionCountStr.length() > 0) {
			auctionCount = Integer.parseInt(auctionCountStr);
		}
	}
	
	public void testGenerator() throws Exception {
		Map<String, String> props = DataGenerator.getProps("emf");
		DataGenerator gen = DataGenerator.createDataGenerator(props);
		
		File outDir = new File(outputDir);
		if (!outDir.exists()) {
		    outDir.mkdir();	
		}
		
		int counts[] = { -1 };
		for (int count : counts) {
			String suffix = (count>0 ? new Integer(count).toString() : "all");
			String outputFile = outDir.getCanonicalFile() + 
			    "/eLeague-" + suffix + ".xml";
			log.info("creating file:" + outputFile);
			
			FileWriter writer = new FileWriter(outputFile);
			BufferedWriter bw = new BufferedWriter(writer);
			gen.generate(bw, count);
			bw.flush();
			bw.close();
		}
	}	
}
