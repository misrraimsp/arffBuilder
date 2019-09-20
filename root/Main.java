
public class Main {
		
	public static void main(String[] args) {
		
		if (args.length != 2) {
			System.out.println("Usage: main path mode");
			return;
		}
		else {
			String root = args[0];
			switch (args[1]){
			case "v":
				VectorBuilder vb = new VectorBuilder(root);
				vb.build();
				break;
			case "e":
				EliteBuilder eb = new EliteBuilder(root);
				eb.build();
				break;
			case "a":
				ArffBuilder ab = new ArffBuilder(root);
				ab.build();
				break;
			default :
				System.out.println("valid modes: ");	
			}
			System.out.println("Done");
		}
	}
}
