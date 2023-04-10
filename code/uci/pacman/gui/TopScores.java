package code.uci.pacman.gui;
import ucigame.*;
import java.util.*;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;

import code.uci.pacman.controllers.GameController;
import code.uci.pacman.controllers.SoundController;
import code.uci.pacman.gui.MainMenu;
import code.uci.pacman.game.GameState;
import code.uci.pacman.game.PacManGame;


public class TopScores{

	private Sprite topScoresMainMenu;
	private Sprite creditsButton;
	private ArrayList<String> nameList = new ArrayList<String>();
	private ArrayList<Integer> scoreList = new ArrayList<Integer>();
	private String name;
	private int score;
	private int addIndex;
	private int numberOfScores = 10;
	private Sprite backGround;
	private int bgX = 0;
	private int bgY = 0;
	private int nameStartPosX = 175;
	private int nameStartPosY = 150;
	private int scoreStartPosX = 375;
	private int scoreStartPosY = 150;
	private int spaceIncrement = 30;
	FileReader inFile;
	FileWriter outFile;
	Scanner scanner;
	private int menuButtonX = 175;
	private int menuButtonY = 450;
	private Sound topScoresTheme;
	

	public TopScores(){
		// TODO Auto-generated constructor stub
		addIndex = -1;
		name = "";
		topScoresTheme = GameController.getInstance().getPacInstance().getSound("sounds/final/TopScores.mp3");
		topScoresMainMenu = GameController.getInstance().getPacInstance().makeButton("TopScoresMainMenu",GameController.getInstance().getPacInstance().getImage("mainmenubutton.png"),
                249, 76);
		topScoresMainMenu.position(menuButtonX, menuButtonY);
		
		creditsButton = GameController.getInstance().getPacInstance().makeButton("CreditsFromTopScores",GameController.getInstance().getPacInstance().getImage("creditsbutton.png"),
                249, 76);
		creditsButton.position(menuButtonX, menuButtonY + 80);
		
		readScores();
		backGround = GameController.getInstance().getPacInstance().makeSpriteFromPath("scorespriteadjuster.png");
		backGround.position(bgX, bgY);
		backGround.font(PacManGame.font, PacManGame.BOLD, 24, 255, 255, 255);
		if(isTopList()){
			addTopScore();
		}
	}

	public void readScores(){
		try
		{
			inFile = new FileReader("scores.txt");
			scanner = new Scanner(inFile);
			while(scanner.hasNextLine())
			{
				nameList.add(scanner.next());
				scoreList.add(Integer.parseInt(scanner.next()));
			}
			inFile.close();
		}
		catch(IOException e)
		{
			System.err.println("No such file.");
			e.printStackTrace();
			//for(int x = 0; x < numberOfScores; x++)
			//{
			//	nameList.add("Name"+(x+1));
			//	scoreList.add(10-x);
			//}
		}
	}
	

	public void writeScores(){
		try
		{
			outFile = new FileWriter("scores.txt");
			for(int x = 0; x < numberOfScores; x++)
			{
				if(nameList.get(x).equals("") || nameList.get(x).equals("<Enter Name!>"))
				{
					outFile.write("PLAYER ");
				}
				else
				{
					outFile.write(nameList.get(x)+" ");
				}
				outFile.write(scoreList.get(x)+"");
				if(x!=9)
					outFile.write("\n");
			}
			outFile.close();
		}
		catch(IOException e)
		{
			System.err.println("Output scores.txt does not exist!");
			e.printStackTrace();
		}			
	}
	

	public void draw(){
		//put text on screen from arrayLists
		
		

		for(int x = 0; x < numberOfScores; x++)
		{
			if( scoreList.size() > x )
			{
				backGround.putText(nameList.get(x)+"", nameStartPosX, nameStartPosY + x*spaceIncrement);
				backGround.putText(scoreList.get(x)+"", scoreStartPosX, scoreStartPosY + x*spaceIncrement);
			}
		}
		backGround.draw();
		topScoresMainMenu.draw();
		creditsButton.draw();
	}
	private boolean isTopList(){
		//score = 7000;
		score = GameState.getInstance().getScore();

		//for(int x = 0; x < numberOfScores; x++)
		for(int x = 0; x < scoreList.size(); x++)
		{
			if(score >= scoreList.get(x))
			{
				addIndex = x;
				return true;
			}
		}
		return false;
	}
	

	public void addTopScore(){
		// we can't use getProperty(user.name) because if the user's name has a space in it, 
		// readScores() can't read it properly later
		name = "PLAYER";
		nameList.add(addIndex, name);
		nameList.remove(10);
		scoreList.add(addIndex, score);
		scoreList.remove(10);
		writeScores();
		name = "<Enter Name!>";
		nameList.set(addIndex, name);
		name = "";
		GameState.getInstance().addToScore(score*-1);
	}
		

	public void addToName(String nextChar){
		if(addIndex != -1 && name.length()< 10){
			name = name + nextChar;
			nameList.set(addIndex, name);
			writeScores();
		}
	}
	

	public void removeFromName()
	{
		if(addIndex != -1 && name.length() > 0){
			name = name.substring(0, name.length() - 1);
			nameList.set(addIndex, name);
			writeScores();
		}
	}
	

	public void hideButtonsAndScores()
	{
		topScoresMainMenu.hide();
		creditsButton.hide();
		backGround.hide();
	}
	

	public void startTopScoresTheme()
	{
		topScoresTheme.loop();		
	}
	

	public void stopTopScoresTheme()
	{
		topScoresTheme.stop();
	}
}
