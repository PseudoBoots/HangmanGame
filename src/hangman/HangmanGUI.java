package hangman;

/*
 * @author Victor Bieniek
 * 11-29-17
 * This is a simple hangman game with a javafx GUI
 */

import java.util.ArrayList;

import javafx.application.Application;
import javafx.geometry.HPos;
import javafx.stage.Stage;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.paint.Color;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.control.Label;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import java.io.File;
import java.util.Scanner;
import javafx.scene.transform.Rotate;
import java.util.stream.Stream;

public class HangmanGUI extends Application
{
	private Canvas cvsHang;
	private TextField tfInput;
	private Label lblInput;
	private Label lblLettersGuessed;
	private Button btnSubmit;
	private Label lblWord;
	private Label lblWordLetters;
	private String displayWordLetters;
	private ArrayList<Character> guessed = new ArrayList<>();
	private File wordList;
	private final int WORDLIST_SIZE = 4554;
	private String wordToGuess;
	
	private int numOfWrongGuesses = 0;
	private boolean gameOver = false;
	

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception
	{
		primaryStage.setTitle("Hangman");
		primaryStage.setResizable(false);
		
		wordList = new File("res/HangManWordList.txt");
		
		//get word from list
		try(Scanner scan = new Scanner(wordList);)
		{
			int wordNum = (int) (Math.random() * WORDLIST_SIZE);
			for(int i = 0; i < wordNum; i++)
			{
				scan.next();
			}
			wordToGuess = scan.next();
		} catch (Exception e) {
			showAlert("Error", "Could not get word");
			e.printStackTrace();
			System.exit(0);
		}
		
		//initialize nodes
		cvsHang = new Canvas();
		tfInput = new TextField();
		lblInput = new Label("Enter a letter: ");
		lblLettersGuessed = new Label("");
		btnSubmit = new Button("Enter");
		lblWord = new Label("Word: ");
		lblWordLetters = new Label();
		
		
		
		//give lblWordLetters underscores
		displayWordLetters = "";
		for(int i = 0; i < wordToGuess.length(); i++)
		{
			displayWordLetters += "_ ";
		}
		lblWordLetters.setText(displayWordLetters);
		
		//create layouts
		BorderPane rootLayout = new BorderPane();
		GridPane lowerLayout = new GridPane();
		
		for(int i = 0; i < 10; i++)
		{
			ColumnConstraints col = new ColumnConstraints();
			col.setPercentWidth(10);
			lowerLayout.getColumnConstraints().add(col);
		}
		for (int i = 0; i < 5; i++)
		{
			RowConstraints row = new RowConstraints();
			row.setPercentHeight(20);
			lowerLayout.getRowConstraints().add(row);
        }
		
		rootLayout.setCenter(cvsHang);
		rootLayout.setBottom(lowerLayout);
		
		lowerLayout.getChildren().addAll(lblInput, tfInput, btnSubmit, lblLettersGuessed, lblWord, lblWordLetters);
		GridPane.setConstraints(lblInput, 0, 0, 3, 1);
		GridPane.setHalignment(lblInput, HPos.RIGHT);
		GridPane.setConstraints(tfInput, 3, 0);
		GridPane.setConstraints(btnSubmit, 4, 0, 2, 1);
		GridPane.setConstraints(lblLettersGuessed, 2, 3, 4, 1);
		GridPane.setConstraints(lblWord, 5, 0, 2, 1);
		GridPane.setHalignment(lblWord, HPos.RIGHT);
		GridPane.setConstraints(lblWordLetters, 7, 0, 4, 1);
		
		cvsHang.getGraphicsContext2D().setFill(Color.WHITE);
		cvsHang.getGraphicsContext2D().fillRect(0, 0, cvsHang.getWidth(), cvsHang.getHeight());
		
		tfInput.setOnKeyTyped(event ->{
			//only let user type one letter
			if(tfInput.getText().length() > 0) event.consume();
		});
		
		btnSubmit.setOnAction(event ->{
			String guess = tfInput.getText();
			//check that valid letter is entered
			if(!guess.matches("[A-Za-z]")) showAlert("Error", "Invalid letter");
			else if(guessed.contains(guess.charAt(0))) showAlert(null, "Letter already guessed");
			else
			{
				//enter letter
				guessed.add(guess.charAt(0));
				if(wordToGuess.contains(guess))
				{
					//if guess is in word
					//displayWordLetters
					char[] letters = wordToGuess.toCharArray();
					char[] displayLetters = displayWordLetters.toCharArray();
					for(int i = 0; i < letters.length; i++)
					{
						if(letters[i] == guess.charAt(0))
						{
							displayLetters[i*2] = letters[i];
						}
					}
					displayWordLetters = new String(displayLetters);
					lblWordLetters.setText(displayWordLetters);
					
					//check if player won
					if(!displayWordLetters.contains("_"))
					{
						//player has won
						tfInput.setDisable(true);
						btnSubmit.setDisable(true);
						cvsHang.getGraphicsContext2D().setFill(Color.GREEN);
						cvsHang.getGraphicsContext2D().fillText("You Win!", 190, 40);
					}
				}
				else
				{
					//if guess is not in word
					lblLettersGuessed.setText(lblLettersGuessed.getText() + guess + " ");
					drawHangman(cvsHang.getGraphicsContext2D());
				}
				tfInput.clear();
			}
		});
		
		tfInput.setOnAction(event -> btnSubmit.fire());
		
		
		Scene scene = new Scene(rootLayout, 450, 450); //size is set (width, height)
		
		primaryStage.setScene(scene);
		primaryStage.show();
		
		cvsHang.setWidth(primaryStage.getWidth());
		cvsHang.setHeight(primaryStage.getHeight() * 0.6);
	}

	private void showAlert(String title, String message)
	{
		Alert alert = new Alert(Alert.AlertType.INFORMATION);
		alert.setTitle(title);
		alert.setHeaderText(null);
		alert.setContentText(message);
		alert.showAndWait();
	}
	
	private void drawHangman(GraphicsContext gc)
	{
		switch(numOfWrongGuesses)
		{
			//draw head
			case 0: gc.setFill(Color.BLACK);
				gc.fillOval(200, 50, 50, 50);
				break;
			//draw body
			case 1: gc.setFill(Color.BLACK);
				gc.fillRect(220, 100, 5, 100);
				break;
			//draw west arm
			case 2: gc.setFill(Color.BLACK);
				gc.fillRect(170, 130, 50, 5);
				break;
			//draw east arm
			case 3: gc.setFill(Color.BLACK);
				gc.fillRect(225, 130, 50, 5);
				break;
			//draw west leg
			case 4: gc.setFill(Color.BLACK);
				rotateGC(gc, 45, gc.getCanvas().getWidth()/2, gc.getCanvas().getHeight()/2);
				gc.fillRect(260, 180, 50, 5);
				rotateGC(gc, 0, gc.getCanvas().getWidth()/2, gc.getCanvas().getHeight()/2);
				break;
			//draw east leg
			case 5: gc.setFill(Color.BLACK);
				rotateGC(gc, -45, gc.getCanvas().getWidth()/2, gc.getCanvas().getHeight()/2);
				gc.fillRect(135, 175, 50, 5);
				rotateGC(gc, 0, gc.getCanvas().getWidth()/2, gc.getCanvas().getHeight()/2);
				//break;
			//game over
			default: rotateGC(gc, 0, gc.getCanvas().getWidth()/2, gc.getCanvas().getHeight()/2);
				gc.setFill(Color.RED);
				gc.fillText("You loose", 190, 40);
				gc.fillText("The word was " + wordToGuess, 170, 250);
				tfInput.setDisable(true);
				btnSubmit.setDisable(true);
				break;
		}
		
		numOfWrongGuesses++;
	}
	
	private void rotateGC(GraphicsContext gc, double angle, double px, double py) {
        Rotate r = new Rotate(angle, px, py);
        gc.setTransform(r.getMxx(), r.getMyx(), r.getMxy(), r.getMyy(), r.getTx(), r.getTy());
    }
	
}
