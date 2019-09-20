import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TokenSource {

	private FileReader fr;
	private BufferedReader br;
	
	public TokenSource(File f, int n) throws FileNotFoundException{
		fr = new FileReader(f);
		br = new BufferedReader(fr);
		
		if (n > 0){ // skipping header metadata
			try {
				jumpLine(n);
			}
			catch (IOException e) {
				System.out.println("jumpLine(n) IO error");
				e.printStackTrace();
			}
		}
	}

	public String getNextToken() throws IOException{
		String token = "";
		int c;
		// get first valid char
		do {
			c = fr.read();
			if(c == -1){
				fr.close();
				return null;	
			}
		} while(!isValidChar(c));
		// get following valid chars
		do {
			token = token + (char)c;
			c = fr.read();
		} while(isValidChar(c));
		return token.toLowerCase();
	}

	public String getNextElement() throws IOException {
		return br.readLine();
	}
	
	private void jumpLine(int n) throws IOException {
		int c = fr.read();
		for (int i = 0; i < n; i++){
			while (c != 10) c = fr.read(); // http://www.fileformat.info/info/unicode/char/000a/index.htm
		}
		return;
	}
	
	private boolean isValidChar(int c) {
		return ((c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z'));
	}
}







