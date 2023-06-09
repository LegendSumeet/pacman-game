package code.uci.pacman.game;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class ItemGenerator {
	static int spacingBetweenPills;
	static FileWriter f;
	static String folder = "levels/";

	/**
	 * @throws IOException
	 */
	public static void execute() throws IOException {
		// Generate pill object locations
		makePillsOne();
	}


	private static void makePillsOne() throws IOException {
		String item = "pills";
		f = new FileWriter(new File(folder + item + "/1.txt"));
		spacingBetweenPills = 20;
		
		//unique ones
		horizontalBlock(42, 100, 520);
		horizontalBlock(20, 560, 562);
		horizontalBlock(131, 441, 342);
		
		//horizontal blocks of pills
		horizontalBlock(42,20,227);
		horizontalBlock(334, 20, 227);
		
		horizontalBlock(334, 388, 250);
		horizontalBlock(20, 388, 250);
		
		horizontalBlock(20,500,125);
		horizontalBlock(450, 500, 125);
		
		horizontalBlock(40,160,65);
		horizontalBlock(490,160,65);
		
		horizontalBlock(530, 444, 58);
		horizontalBlock(20, 444, 58);
		
		horizontalBlock(198, 500, 78);
		horizontalBlock(330, 500, 78);
		
		horizontalBlock(198, 160, 78);
		horizontalBlock(330, 160, 78);
		
		//vertical rectangles of pills
		verticalBlock(20,  20, 150);
		verticalBlock(573, 20, 150);
		
		verticalBlock(130, 125, 250);
		verticalBlock(460, 125, 250);
		
		verticalBlock(20,  406, 22);
		verticalBlock(131, 406, 22);
		verticalBlock(260, 406, 22);
		verticalBlock(332, 406, 22);
		verticalBlock(460, 406, 22);
		verticalBlock(573, 406, 22);
		
		verticalBlock(60,  461, 22);
		verticalBlock(128, 461, 22);
		verticalBlock(200, 461, 22);
		verticalBlock(390, 461, 22);
		verticalBlock(460, 461, 22);
		verticalBlock(530, 461, 22);
		
		verticalBlock(22,  520, 22);
		verticalBlock(260, 520, 22);
		verticalBlock(332, 520, 22);
		verticalBlock(573, 520, 22);
		
		verticalBlock(130, 42, 42);
		verticalBlock(260, 42, 42);
		verticalBlock(332, 42, 42);
		verticalBlock(462, 42, 42);
		
		verticalBlock(198, 120, 22);
		verticalBlock(390, 120, 22);
		f.close();
	}

	private static void verticalBlock(int xPos, int yPos, int height) throws IOException {
			for (int y = yPos; y < yPos + height; y += spacingBetweenPills) {
				f.write(xPos + "," + y);
				f.write("\r\n");
			}
	}
	private static void horizontalBlock(int xPos, int yPos, int width) throws IOException{
		for (int x = xPos; x < xPos + width; x += spacingBetweenPills) {
			f.write(x + "," + yPos);
			f.write("\r\n");
		}
	}
	

}
