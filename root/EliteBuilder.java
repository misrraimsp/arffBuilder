import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.TreeMap;
import java.util.HashMap;
import java.util.Map.Entry;

public class EliteBuilder {

	private static final int ESIZE = 10001;
	private static final boolean SORTED = false;
	
	private String ePath;
	private String vPath;
	
	public EliteBuilder(String root){
		ePath = root;
		vPath = root + "\\Vector";
	}
	
	public void build(){
		TreeMap<String, Integer> tree = new TreeMap<String, Integer>();
		HashMap<String, Integer> elite = new HashMap<String, Integer>();
		String last = null;
		TokenSource ts = null;
		String element = null;
		
		File vDirectory = new File(vPath);
		if (vDirectory.exists()){
			//
			//walk through all vector files processing its words
			//
			File[] vFiles = vDirectory.listFiles();
			int size = vFiles.length;
			for (int i = 0; i < size; i++){
				// try open vector file
				try {
					ts = new TokenSource(vFiles[i], 2);
				}
				catch (FileNotFoundException e) {
					System.out.println("error: file not found " + vFiles[i].getName());
					e.printStackTrace();
				}
				// try skip first two lines (header: no element lines)
				for (int j = 0; j < 3; j++){
					try {
						element = ts.getNextElement();
					}
					catch (IOException e) {
						System.out.println("error reading header in file " + vFiles[i].getName());
						e.printStackTrace();
					}
				}
				// process elements
				while (element != null){
					String[] parts = element.split(",");
					String token = parts[0];
					Integer newFreq = Integer.parseInt(parts[1]);
					// update tree
					Integer oldFreq = tree.get(token);
					if (oldFreq != null) newFreq += oldFreq;
					tree.put(token, newFreq);
					// update elite
					if (elite.size() < ESIZE){
						elite.put(token, newFreq);
						if (token.equals(last)) last = getLast(elite);
						else if (last == null || elite.get(last) > newFreq) last = token;
					}
					else {
						if (elite.get(last) < newFreq){
							elite.remove(last);
							elite.put(token, newFreq);
							last = getLast(elite);
						}
					}
					// try read next element
					try {
						element = ts.getNextElement();
					}
					catch (IOException e) {
						System.out.println("error reading element in file " + vFiles[i].getName());
						e.printStackTrace();
					}
				}
			}
			//write w-f selected (elite) pairs in vector format file
			try {
				write(elite, SORTED);
			} catch (IOException e) {
				System.out.println("error writing elite file");
				e.printStackTrace();
			}
		}
		else System.out.println("error: vPath does not exists");
	}

	private void write(HashMap<String, Integer> elite, boolean sorted) throws IOException {
		String eFileName = ePath + "\\Elite";
		PrintWriter writer = new PrintWriter(eFileName);
		writer.println("ELITE-" + ESIZE);
		if (sorted){
			while (!elite.isEmpty()){
				String last = getLast(elite);
				writer.println(last + "," + elite.get(last));
				elite.remove(last);
			}
		}
		else {
			for(Entry<String,Integer> e : elite.entrySet()) {
				writer.println(e.getKey() + "," + e.getValue());
		    }
		}
		writer.close();
	}

	private String getLast(HashMap<String, Integer> elite) {
		String last = null;
		Integer least = 10000000;
		for(Entry<String,Integer> e : elite.entrySet()) {
			int freq = e.getValue();
			if (freq < least){
				last = e.getKey();
				least = freq;
			}
    	}
		return last;
	}
}