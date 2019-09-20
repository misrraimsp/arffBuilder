import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.NavigableSet;
import java.util.TreeMap;

public class VectorBuilder {
	
	// BUILDER PARAMETERS
	/////////////////////////////////////////////////////////
	private static final int PROB = 101;                    //
	/////////////////////////////////////////////////////////
	
	private String tRootPath;
	private String vRootPath;
	
	public VectorBuilder(String root){
		tRootPath = root + "\\Text";
		vRootPath = root + "\\Vector";
	}
	
	public void build(){
		String className;
		TreeMap<String, Integer> tree;
		File textRootDirectory = new File(tRootPath);
		if (textRootDirectory.exists()){
			//walk through all class directories
			File[] classDirectories = textRootDirectory.listFiles();
			for (int i = 0; i < classDirectories.length; i++){
				int cont = i + 1;
				System.out.println("Processing " + cont + " of " + classDirectories.length);
				className = classDirectories[i].getName();
				//walk through all text files within a class directory
				File classDirectory = new File(tRootPath + "\\" + className);
				if (classDirectory.exists()){
					File[] textFiles = classDirectory.listFiles();
					for (int j = 0; j < textFiles.length; j++){
						if (rdm(1, 100) < PROB) {
							//count words in a file
							tree = buildTree(textFiles[j]);
							//write w-f pairs in vector file
							write(tree, className, textFiles[j].getName());
						}
					}
				}
				else System.out.println("error: textClassPath does not exists");
			}
		}
		else System.out.println("error: textRootPath does not exists");
	}
	
	private int rdm(int lowBound, int highBound){
		int range = highBound - lowBound;
		double random = Math.random();
		return (int) (lowBound + (random * range) + 0.5);
	}

	private TreeMap<String, Integer> buildTree(File file) {
		TokenSource ts = null;
		String token = null;
		TreeMap<String, Integer> tree = new TreeMap<String, Integer>();
		// open file
		try {
			ts = new TokenSource(file, 2);
		}
		catch (FileNotFoundException e) {
			System.out.println("error: file not found " + file.getName());
			e.printStackTrace();
		}
		// read first token
		try {
			token = ts.getNextToken();
		}
		catch (IOException e) {
			System.out.println("error reading token in file " + file.getName());
			e.printStackTrace();
		}
		// read all next tokens, storing them on the tree
		while (token != null){
			Integer f = tree.get(token);
			if (f == null) tree.put(token, 1);
			else tree.put(token, f + 1);
			try {
				token = ts.getNextToken();
			}
			catch (IOException e) {
				System.out.println("error reading token in file " + file.getName());
				e.printStackTrace();
			}
		}
		return tree;
	}

	private void write(TreeMap<String, Integer> tree, String className, String textName) {
		String vFileName = vRootPath + "\\" + className + textName;
		try{
		    PrintWriter writer = new PrintWriter(vFileName);
		    writer.println(className + "-" + textName);
		    writer.println(className);
		    NavigableSet<String> ns = tree.descendingKeySet();
		    Iterator<String> ite = ns.iterator();
		    while(ite.hasNext()) {
		    	String key = ite.next();
		    	writer.println(key + "," + tree.get(key));
		    	}
		    writer.close();
		}
		catch (IOException e) {
			System.out.println("error writing w-f file " + className + "-" + textName);
			e.printStackTrace();
		}
	}
}
