
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.beans.value.*;
import javafx.event.*;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.*;
import org.apache.log4j.Logger;

import sun.rmi.runtime.Log;



public class TicTacToe extends Application {


	static	String firstName ;
	static String secondName ;
	static Date dateAndTime ;
	static double turnCount ;
	
	private static final Logger log = Logger.getLogger(TicTacToe.class);

	@Override public void start(Stage stage) throws Exception {

		GameManager gameManager = new GameManager();
		SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		log.info("we get time ");
		Date date = new Date();  
		System.out.println("Start Date & Time Of game= "+formatter.format(date));  

        this.dateAndTime=date;
		System.out.println("Enter First Name Of player");
		BufferedReader reader = new BufferedReader(
				new InputStreamReader(System.in));

		// Reading data using readLine
		this.firstName  = reader.readLine();

		// Printing the read line
		// System.out.println(Firstname);

		log.info("get name of player ");
		System.out.println("Enter Second Name Of player");
		BufferedReader reader2 = new BufferedReader(
				new InputStreamReader(System.in));

		// Reading data using readLine
		this.secondName = reader2.readLine();

		// Printing the read line
		//  System.out.println(Secondname);

		log.info("start game");

		Scene scene = gameManager.getGameScene();
		scene.getStylesheets().add(
				getResource(
						"tictactoe-blueskin.css"
						)
				);

		stage.setTitle("Tic-Tac-Toe");
		stage.getIcons().add(SquareSkin.crossImage);
		stage.setScene(scene);
		stage.show();
	}


	String  getX(){ 
		return firstName; 
	} 


	void setCounter(double value){ 
		this.turnCount=value;	
	}
	String getY(){ 
		return secondName; 
	} 
	Date getdate(){ 
		return dateAndTime; 
	} 
	private String getResource(String resourceName) {
		return getClass().getResource(resourceName).toExternalForm();
	}

	public static void main(String[] args) {
		Application.launch(TicTacToe.class);
	}
}

class GameManager {
	private Scene gameScene;
	private Game  game;

	GameManager() {
		newGame();
	}

	public void newGame() {
		game = new Game(this);

		if (gameScene == null) {
			gameScene = new Scene(game.getSkin());
		} else {
			gameScene.setRoot(game.getSkin());
		}
	}

	public void quit() {
		gameScene.getWindow().hide();
	}

	public Game getGame() {
		return game;
	}

	public Scene getGameScene() {
		return gameScene;
	}
}

class GameControls extends HBox {
	GameControls(final GameManager gameManager, final Game game) {
		getStyleClass().add("game-controls");

		visibleProperty().bind(game.gameOverProperty());

		;

	

		Label playAgainLabel = new Label("play Again?");
		playAgainLabel.getStyleClass().add("info");

		Button playAgainButton = new Button("Yes");
		playAgainButton.getStyleClass().add("play-again");
		playAgainButton.setDefaultButton(true);
		playAgainButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override public void handle(ActionEvent actionEvent) {
				gameManager.newGame();
			}
		});

		Button exitButton = new Button("No");
		playAgainButton.getStyleClass().add("exit");
		exitButton.setCancelButton(true);
		exitButton.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent actionEvent) {
				gameManager.quit();
			}
		});

		getChildren().setAll(
				playAgainLabel,
				playAgainButton,
				exitButton
				);
	}
}

class StatusIndicator extends HBox {
	private final ImageView playerToken = new ImageView();
	private final Label     playerLabel = new Label("Current player: ");

	StatusIndicator(Game game) {
		getStyleClass().add("status-indicator");

		bindIndicatorFieldsToGame(game);

		playerToken.setFitHeight(32);
		playerToken.setPreserveRatio(true);

		playerLabel.getStyleClass().add("info");

		getChildren().addAll(playerLabel, playerToken);
	}

	private void bindIndicatorFieldsToGame(Game game) {

		playerToken.imageProperty().bind(
				Bindings.when(
						game.currentplayerProperty().isEqualTo(Square.State.NOUGHT)
						)
						.then(SquareSkin.noughtImage)
						.otherwise(
								Bindings.when(
										game.currentplayerProperty().isEqualTo(Square.State.CROSS)
										)
										.then(SquareSkin.crossImage)
										.otherwise((Image) null)
								)
				);



		playerLabel.textProperty().bind(
				Bindings.when(
						game.gameOverProperty().not()
						)
						.then("Current player: ")
						.otherwise(
								Bindings.when(
										game.winnerProperty().isEqualTo(Square.State.EMPTY)
										)
										.then("Draw")
										.otherwise("loss player: ")
								)
				);
	}
}

class Game {
	private GameSkin skin;
	private Board board = new Board(this);
	private WinningStrategy winningStrategy = new WinningStrategy(board);

	private ReadOnlyObjectWrapper<Square.State> currentplayer = new ReadOnlyObjectWrapper<>(Square.State.CROSS);
	public ReadOnlyObjectProperty<Square.State> currentplayerProperty() {
		return currentplayer.getReadOnlyProperty();
	}
	public Square.State getCurrentplayer() {
		return currentplayer.get();
	}

	private ReadOnlyObjectWrapper<Square.State> winner = new ReadOnlyObjectWrapper<>(Square.State.EMPTY);
	public ReadOnlyObjectProperty<Square.State> winnerProperty() {


		return winner.getReadOnlyProperty();
	}

	private ReadOnlyBooleanWrapper drawn = new ReadOnlyBooleanWrapper(false);
	public ReadOnlyBooleanProperty drawnProperty() {
		return drawn.getReadOnlyProperty();
	}
	public boolean isDrawn() {
		return drawn.get();
	}

	private ReadOnlyBooleanWrapper gameOver = new ReadOnlyBooleanWrapper(false);
	public ReadOnlyBooleanProperty gameOverProperty() {
		return gameOver.getReadOnlyProperty();
	}
	public boolean isGameOver() {
		return gameOver.get();
	}

	public Game(GameManager gameManager) {
		gameOver.bind(
				winnerProperty().isNotEqualTo(Square.State.EMPTY)
				.or(drawnProperty())
				);

		skin = new GameSkin(gameManager, this);
	}

	public Board getBoard() {
		return board;
	}

	public void nextTurn() {
		if (isGameOver()) return;

		switch (currentplayer.get()) {
		case EMPTY:
		case NOUGHT: currentplayer.set(Square.State.CROSS);  break;
		case CROSS:  currentplayer.set(Square.State.NOUGHT); break;
		}
	}

	private void checkForWinner() {
		winner.set(winningStrategy.getWinner());
		drawn.set(winningStrategy.isDrawn());

		if (isDrawn()) {
			currentplayer.set(Square.State.EMPTY);
		}
	}

	public void boardUpdated() {
		checkForWinner();
	}

	public Parent getSkin() {
		return skin;
	}
}

class GameSkin extends VBox {
	GameSkin(GameManager gameManager, Game game) {
		getChildren().addAll(
				game.getBoard().getSkin(),
				new StatusIndicator(game),
				new GameControls(gameManager, game)
				);
	}
}

class WinningStrategy {
	private final Board board;




	private static final int NOUGHT_WON = 3;
	private static final int CROSS_WON  = 30;

	private static final Map<Square.State, Integer> values = new HashMap<>();
	static {
		values.put(Square.State.EMPTY,  0);
		values.put(Square.State.NOUGHT, 1);
		values.put(Square.State.CROSS,  10);
	}

	public WinningStrategy(Board board) {
		this.board = board;
	}

	public Square.State getWinner() {

		for (int i = 0; i < 5; i++) {
			int score = 0;
			for (int j = 0; j < 5; j++) {
				score += valueOf(i, j);
			}
			if (isWinning(score)) {





				return winner(score);
			}
		}

		for (int i = 0; i < 5; i++) {
			int score = 0;
			for (int j = 0; j < 5; j++) {
				score += valueOf(j, i);
			}
			if (isWinning(score)) {
				return winner(score);
			}
		}



		int score = 0;
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {

				// Condition for secondary diagonal
				if ((i + j) == (5 - 1)) {
					score += valueOf(i, j);
				}
			}
		} 


		if (isWinning(score)) {
			return winner(score);
		}




		score = 0;
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {

				// Condition for secondary diagonal
				if (i  == j) {
					score += valueOf(i, j);
				}
			}
		} 


		if (isWinning(score)) {
			return winner(score);
		}




		/*  int score = 0;
    score += valueOf(0, 0);
    score += valueOf(1, 1);
    score += valueOf(2, 2);
    score += valueOf(3, 3);
    score += valueOf(4, 4);

    if (isWinning(score)) {
      return winner(score);
    }

    score = 0;
    //score += valueOf(2, 0);
    //score += valueOf(1, 1);
   // score += valueOf(0, 2);


    score += valueOf(4, 0);
    score += valueOf(3, 1);
    score += valueOf(2, 2);
    score += valueOf(1, 3);
    score += valueOf(0, 4);

    if (isWinning(score)) {
      return winner(score);
    }*/

		return Square.State.EMPTY;
	}

	public boolean isDrawn() {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				if (board.getSquare(i, j).getState() == Square.State.EMPTY) {
					return false;
				}
			}
		}

		return getWinner() == Square.State.EMPTY;
	}

	private Integer valueOf(int i, int j) {
		return values.get(board.getSquare(i, j).getState());
	}

	private boolean isWinning(int score) {
		return score == NOUGHT_WON || score == CROSS_WON;
	}

	private Square.State winner(int score) {
		if (score == NOUGHT_WON) 
		{
			
			
			
			
		 	
	        try {
	        	
	        	
	        	String path = System.getProperty("user.dir");
	            System.out.println(path + "\\mytest.xml");
	            File file = new File(path + "\\mytest.xml");
	           
	            Scanner myReader = new Scanner(file);
	            while (myReader.hasNextLine()) {
	              String data = myReader.nextLine();
	              System.out.println(data);
	            }
	            myReader.close();
	          } catch (FileNotFoundException e) {
	            System.out.println("An error occurred.");
	            e.printStackTrace();
	          }
			
			
			TicTacToe tic = new TicTacToe();

			Alert a = new Alert(AlertType.NONE);
			Info inn = new Info();

			a.setAlertType(AlertType.CONFIRMATION);

			// set content text
			a.setContentText("Winning player is =" +tic.getX());
			
			System.out.println("Total Turns Attempt="+tic.turnCount);
			
			String path = System.getProperty("user.dir");

			  PrintWriter writer;
			try {
				writer = new PrintWriter(new BufferedWriter(new FileWriter(path + "\\mytest.xml", true)));
				   
			    writer.println("Winning player name  is =" +tic.getY()+" "+"Game Start Date & time  is =" +tic.getdate()+"" +"Total turns attemp =" +tic.turnCount);
			    
			    	writer.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// show the dialog
			a.show();

			return Square.State.NOUGHT;
		}
		if (score == CROSS_WON) 

		{
			
			
			  try {
		        	
		        	
		        	String path = System.getProperty("user.dir");
		            System.out.println(path + "\\mytest.xml");
		            File file = new File(path + "\\mytest.xml");
		           
		            Scanner myReader = new Scanner(file);
		            while (myReader.hasNextLine()) {
		              String data = myReader.nextLine();
		              System.out.println(data);
		            }
		            myReader.close();
		          } catch (FileNotFoundException e) {
		            System.out.println("An error occurred.");
		            e.printStackTrace();
		          }
			
			
			TicTacToe tic = new TicTacToe();
			Alert a = new Alert(AlertType.NONE);
			Info inn = new Info();

			a.setAlertType(AlertType.CONFIRMATION);

			// set content text
			a.setContentText("Winning player is =" +tic.getY());
			
        	String path = System.getProperty("user.dir");

			  PrintWriter writer;
			try {
				writer = new PrintWriter(new BufferedWriter(new FileWriter(path + "\\mytest.xml", true)));
				   
			    writer.println("Winning player name  is =" +tic.getY()+" "+"Game Start Date & time  is =" +tic.getdate()+"" +"Total turns attemp =" +tic.turnCount);
			    
			    	writer.close();
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			    
			 
				
			
			
			
			System.out.println("Total Turns Attempt="+tic.turnCount);


			// show the dialog
			a.show();
			return Square.State.CROSS;
		}

		return Square.State.EMPTY;
	}
}

class Board {
	private final BoardSkin skin;

	private final Square[][] squares = new Square[5][5];

	public Board(Game game) {
		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				squares[i][j] = new Square(game);
			}
		}

		skin = new BoardSkin(this);
	}

	public Square getSquare(int i, int j) {
		return squares[i][j];
	}

	public Node getSkin() {
		return skin;
	}
}

class BoardSkin extends GridPane {
	BoardSkin(Board board) {
		getStyleClass().add("board");

		for (int i = 0; i < 5; i++) {
			for (int j = 0; j < 5; j++) {
				add(board.getSquare(i, j).getSkin(), i, j);
			}
		}
	}
}

class Square {
	enum State { EMPTY, NOUGHT, CROSS }

	private final SquareSkin skin;
	private static int counter = 2;


	int  getCounter(){ 
		return counter; 
	} 


	private ReadOnlyObjectWrapper<State> state = new ReadOnlyObjectWrapper<>(State.EMPTY);
	public ReadOnlyObjectProperty<State> stateProperty() {
		return state.getReadOnlyProperty();
	}
	public State getState() {
		return state.get();
	}

	private final Game game;

	public Square(Game game) {
		this.game = game;

		skin = new SquareSkin(this);
	}

	public void pressed() {
		counter=counter+1;
       
		TicTacToe tic2 = new TicTacToe();
		double value =(counter-1)/2;
		tic2.setCounter(value);
		
		System.out.println("Total Turns Attempt ="+(counter-1)/2);

		if (!game.isGameOver() && state.get() == State.EMPTY) {
			state.set(game.getCurrentplayer());
			game.boardUpdated();
			game.nextTurn();
		}


	}

	public Node getSkin() {
		return skin;
	}
}

class SquareSkin extends StackPane {





	static final Image noughtImage = new Image(
			"https://icons.iconarchive.com/icons/hopstarter/soft-scraps/128/Button-Blank-Red-icon.png"
			);
	static final Image crossImage = new Image(
			"https://icons.iconarchive.com/icons/hopstarter/soft-scraps/128/Button-Blank-Blue-icon.png"
			);

	private final ImageView imageView = new ImageView();

	SquareSkin(final Square square) {
		getStyleClass().add("square");

		imageView.setMouseTransparent(true);

		getChildren().setAll(imageView);
		setPrefSize(crossImage.getHeight() + 20, crossImage.getHeight() + 20);

		setOnMousePressed(new EventHandler<MouseEvent>() {

			@Override public void handle(MouseEvent mouseEvent) {


				square.pressed();
			}
		});

		square.stateProperty().addListener(new ChangeListener<Square.State>() {
			@Override public void changed(ObservableValue<? extends Square.State> observableValue, Square.State oldState, Square.State state) {
				switch (state) {
				case EMPTY:  imageView.setImage(null);        break;
				case NOUGHT: imageView.setImage(noughtImage); break;
				case CROSS:  imageView.setImage(crossImage);  break;
				}
			}
		});
	}
}
