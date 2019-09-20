import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeSet;
import java.util.Iterator;

/*
   @relation itai-t4
 
   @attribute word1 integer
   .
   .
   .
   @attribute wordN integer
   @attribute class {class1,...,classM}
 
   @data
   {index1 f1,...,indexN fN,indexN+1 classi}
   .
   .
   .
 */

public class ArffBuilder {
	
	// BUILDER PARAMETERS
	/////////////////////////////////////////////////////////
	private static final String FILENAME = "itai-t4";      //
	private static final int PROB = 33;                    //
	/////////////////////////////////////////////////////////
	
	private String attPath; //arff for train and test
	private String afsPath; //arff for feature selection
	private String ePath;
	private String vPath;
	private String tPath;
	
	public ArffBuilder(String root){
		attPath = root + "\\" + FILENAME + "-tt.arff";
		afsPath = root + "\\" + FILENAME + "-fs.arff";
		ePath = root + "\\Elite";
		vPath = root + "\\Vector";
		tPath = root + "\\Text";
	}

	public void build(){
		// read elite file and load its words in a binary tree
		TreeSet<String> elite = loadElite();
		if (elite == null) {
			System.out.println("error loading elite");
			return;
		}
		// try open arff-tt
		PrintWriter writer_tt = null;
		try {
			writer_tt = new PrintWriter(attPath);
		} catch (FileNotFoundException e) {
			System.out.println("error: file not found " + attPath);
			e.printStackTrace();
		}
		// try open arff-fs
		PrintWriter writer_fs = null;
		try {
			writer_fs = new PrintWriter(afsPath);
		} catch (FileNotFoundException e) {
			System.out.println("error: file not found " + afsPath);
			e.printStackTrace();
		}
		// build header
		String h = "";
		Iterator<String> ite = elite.iterator();
		while(ite.hasNext()) h = h + "@attribute " + ite.next() + " integer\n";
		h = h + "@attribute myclass {";
		File tDirectory = new File(tPath);
		File[] cDirectories = tDirectory.listFiles();
		int csize = cDirectories.length;
		for (int i = 0; i < csize; i++){
			h = h + cDirectories[i].getName();
			if (i < (csize - 1)) h = h + ",";
			else h = h + "}\n\n@data\n";
		}
		//write arff-tt header
		writer_tt.println("@relation " + FILENAME + "-tt\n\n" + h);
		//write arff-fs header
		writer_fs.println("@relation " + FILENAME + "-fs\n\n" + h);
		// write bodies
		File vDirectory = new File(vPath);
		File[] vFiles = vDirectory.listFiles();
		for (int j = 0; j < vFiles.length; j++){
			if (rdm(1, 100) > PROB) writer_tt.println(getAttributeValue(vFiles[j], elite));
			else writer_fs.println(getAttributeValue(vFiles[j], elite));
		}
		// close
		writer_fs.close();
		writer_tt.close();
	}
	
	private int rdm(int lowBound, int highBound){
		int range = highBound - lowBound;
		double random = Math.random();
		return (int) (lowBound + (random * range) + 0.5);
	}

	private String getAttributeValue(File vFile, TreeSet<String> elite) {
		String element = null, atr = null;
		TokenSource ts = null;
		// try open vector file
		try {
			ts = new TokenSource(vFile, 0);
		} catch (FileNotFoundException e) {
			System.out.println("error: file not found " + vFile.getName());
			e.printStackTrace();
		}
		// try skip first line (vector file ID)
		try {
			element = ts.getNextElement();
		}
		catch (IOException e) {
			System.out.println("error reading header in file " + vFile.getName());
			e.printStackTrace();
		}
		// try read category
		try {
			atr = ts.getNextElement();
			atr = ",10000 " + atr + "}";
		} catch (IOException e1) {
			System.out.println("error reading category in file " + vFile.getName());
			e1.printStackTrace();
		}
		// try read first vector element
		try {
			element = ts.getNextElement();
		} catch (IOException e1) {
			System.out.println("error reading first element in file " + vFile.getName());
			e1.printStackTrace();
		}
		// try process vector elements
		while (element != null){
			String[] parts = element.split(",");
			String token = parts[0];
			Integer freq = Integer.parseInt(parts[1]);
			if (elite.contains(token)) atr = "," + elite.headSet(token).size() + " " + freq + atr;
			try {
				element = ts.getNextElement();
			}
			catch (IOException e) {
				System.out.println("error reading elements in file " + vFile.getName());
				e.printStackTrace();
			}
		}
		atr = "{" + atr.substring(1);
		return atr;
	}

	private TreeSet<String> loadElite() {
		TokenSource ts = null;
		String element = null;
		TreeSet<String> elite = new TreeSet<String>();
		File eFile = new File(ePath);
		// try open elite file
		try {
			ts = new TokenSource(eFile, 0);
		} catch (FileNotFoundException e) {
			System.out.println("error: file not found " + eFile.getName());
			e.printStackTrace();
		}
		// try skip first line (elite-header: no element line)
		for (int j = 0; j < 2; j++){
			try {
				element = ts.getNextElement();
			}
			catch (IOException e) {
				System.out.println("error reading header in file " + eFile.getName());
				e.printStackTrace();
			}
		}
		// populate elite tree
		while (element != null){
			String[] parts = element.split(",");
			elite.add(parts[0]);
			try {
				element = ts.getNextElement();
			}
			catch (IOException e) {
				System.out.println("error reading header in file " + eFile.getName());
				e.printStackTrace();
			}
		}
		return elite;
	}
}