package com.nikhil.lab3;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.*;
import java.net.URL;
import java.util.Comparator;
import java.util.Random;
import java.util.ResourceBundle;

/**
 * Created by NikhilVerma on 07/09/15.
 */
public class Hangman extends Application implements Initializable{//single controller for the entire application TODO name change

    public static final String TITLE = "Hangman";
    public static final double WIDTH = 530;
    public static final double HEIGHT = 715;

    //=============================================================================================
    //Scenes
    //=============================================================================================
    public static final String MAIN_MENU = "main-menu.fxml";
    public static final String INSTRUCTIONS= "instructions.fxml";
    public static final String GAME_INFO= "game-info.fxml";
    public static final String GAMEPLAY= "gameplay.fxml";
    public static final String SCORE= "score.fxml";

    //=============================================================================================
    //Main menu View
    //=============================================================================================
    @FXML private Button playButton;
    @FXML private Button instructionsButton;
    @FXML private Button exitButton;

    //=============================================================================================
    //Game info View
    //=============================================================================================
    @FXML private Button gameInfoBackButton;
    @FXML private Button gameInfoGoButton;
    @FXML private TextField totalPlayersTextField;
    @FXML private TextField totalRoundsTextField;

    //=============================================================================================
    //Instructions View
    //=============================================================================================
    @FXML private Button instructionsBackButton;

    //=============================================================================================
    //Score View
    //=============================================================================================
    @FXML private Button scoreMainMenuButton;
    @FXML private TableView<PlayerScore> scoreboard;
    @FXML private TableColumn<PlayerScore,String> playerColumn;
    @FXML private TableColumn<PlayerScore,Number> scoreColumn;
    private ObservableList<PlayerScore> playerScores;


    //=============================================================================================
    //Gameplay View
    //=============================================================================================
    @FXML private Button gameplayQuitButton;
    @FXML private Label roundLabel;
    @FXML private Button letterGuessGoButton;
    @FXML private Button wordGuessGoButton;
    @FXML private Circle head;
    @FXML private Ellipse torso;
    @FXML private Rectangle leftHand;
    @FXML private Rectangle leftForearm;
    @FXML private Rectangle rightHand;
    @FXML private Rectangle rightForearm;
    @FXML private Rectangle leftLeg;
    @FXML private Rectangle rightLeg;
    @FXML private TextField letterTextField;
    @FXML private TextField wordTextField;
    @FXML private RadioButton letterRadioButton;
    @FXML private RadioButton wordRadioButton;
    @FXML private Label playerNameLabel;
    @FXML private Text wordText;
    @FXML private Text missesText;

    //=============================================================================================
    //Business Logic
    //=============================================================================================
    HangmanLogic hangmanLogic;

    public static void main(String[] args) {
        // read words from file that was passed in command line
        HangmanLogic.readWords("words.txt");
        launch(args);
    }

    @Override
    public void initialize(URL location,ResourceBundle resourceBundle){
        //populate scoreboard
    }

    private void populateScoreboard() {
        if(scoreboard!=null){
            scoreboard.setItems(playerScores);
            playerColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PlayerScore, String>, ObservableValue<String>>() {
                @Override
                public ObservableValue<String> call(TableColumn.CellDataFeatures<PlayerScore, String> playerScoreStringCellDataFeatures) {
                    return new SimpleStringProperty(playerScoreStringCellDataFeatures.getValue().playerName);
                }
            });

            scoreColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<PlayerScore, Number>, ObservableValue<Number>>() {
                @Override
                public ObservableValue<Number> call(TableColumn.CellDataFeatures<PlayerScore, Number> playerScoreIntegerCellDataFeatures) {
                    Number score = playerScoreIntegerCellDataFeatures.getValue().score;
                    return new SimpleIntegerProperty(score.intValue());
                }
            });
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(MAIN_MENU));
        Scene scene = new Scene(root, WIDTH, HEIGHT);
        primaryStage.setTitle(TITLE);
        primaryStage.setScene(scene);
        primaryStage.show();//layout containers wont be initialized until primary stage is shown
    }

    @FXML
    private void play(ActionEvent actionEvent) throws Exception{
        // take to game info scene
        if(actionEvent.getTarget()==playButton){
            goToScene(GAME_INFO,playButton);
        }else if(actionEvent.getTarget()==gameInfoGoButton){
            //collect the data
            Integer totalPlayers;
            Integer totalRounds;
            try {
                totalPlayers=Integer.parseInt(totalPlayersTextField.getText());
            } catch (NumberFormatException e) {
                info("Please enter a valid number for total players");
                return;
            }
            try {
                totalRounds=Integer.parseInt(totalRoundsTextField.getText());
            } catch (NumberFormatException e) {
                info("Please enter a valid number for total rounds");
                return;
            }

            System.out.println("Total Players :" +totalPlayers+" total rounds :"+totalRounds);
            // send over to the gameplay view
            Stage stage=(Stage)gameInfoGoButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(GAMEPLAY));
            Parent root=(Parent)fxmlLoader.load();
            Hangman hangmanController=(Hangman)fxmlLoader.<Hangman>getController();
            HangmanLogic hangmanLogic=new HangmanLogic(hangmanController);
            hangmanController.setHangmanLogic(hangmanLogic);
            hangmanLogic.setTotalPlayers(totalPlayers);
            hangmanLogic.setTotalRounds(totalRounds);
            hangmanLogic.freshGame();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    private void instructions(ActionEvent actionEvent) throws Exception{
        //take to instructions scene
        if(actionEvent.getTarget()==instructionsButton){
            goToScene(INSTRUCTIONS,instructionsButton);
        }
    }

    @FXML
    private void exit(ActionEvent actionEvent) {
        //close the window
        if(actionEvent.getTarget()==exitButton){
            Stage stage=(Stage)exitButton.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void back(ActionEvent actionEvent) throws  Exception{
        if(actionEvent.getTarget()==instructionsBackButton){
            goToScene(MAIN_MENU,instructionsBackButton);
        }else if(actionEvent.getTarget()==gameInfoBackButton){
            goToScene(MAIN_MENU,gameInfoBackButton);
        }else if(actionEvent.getTarget()==gameplayQuitButton){
            goToScene(MAIN_MENU,gameplayQuitButton);
        }else if(actionEvent.getTarget()==scoreMainMenuButton){
            goToScene(MAIN_MENU,scoreMainMenuButton);
        }
    }

    @FXML
    private void guess(ActionEvent actionEvent) throws Exception{
        if(actionEvent.getTarget()==letterGuessGoButton){
            int length = letterTextField.getText().length();
            if(length ==1){
                hangmanLogic.guessLetter(letterTextField.getText());
            }else if(length>1){
                info("Please enter only one letter in the letter text field");
            }else{
                info("Please enter one letter in the letter text field");
            }
        }else if(actionEvent.getTarget()==wordGuessGoButton){
            int length = wordTextField.getText().length();
            if(length >0){
                hangmanLogic.guessWord(wordTextField.getText());
            }else{
                info("Please enter a word in the word text field");
            }

//            testing
//            int []dummyScores={30,50,20,80};
//            showScoreboard(dummyScores, 3);
        }
    }

    @FXML
    private void toggleGuessOption(ActionEvent actionEvent){
        if(actionEvent.getTarget()==letterRadioButton){
            wordTextField.setDisable(true);
            wordGuessGoButton.setDisable(true);
            letterGuessGoButton.setDisable(false);
            letterTextField.setDisable(false);
        }else if(actionEvent.getTarget()==wordRadioButton){
            wordTextField.setDisable(false);
            wordGuessGoButton.setDisable(false);
            letterGuessGoButton.setDisable(true);
            letterTextField.setDisable(true);
        }
    }

    private void goToScene(String fxml, Button usingButton)throws Exception{
        Stage stage= (Stage) usingButton.getScene().getWindow();
        Parent root = FXMLLoader.load(getClass().getResource(fxml));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void info(String message){
        info(message,null);
    }

    public static  void info(String message, final PostDialogDismissal postDialogDismissal){
        final Stage window = new Stage();// final so that anonymous innner
        // classes can use this

        //Block events to other windows
        window.initModality(Modality.APPLICATION_MODAL);
        window.setTitle("Info");
        window.setMinWidth(250);

        Label label = new Label();
        label.setText(message);
        Button closeButton = new Button("Close");
        closeButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent arg0) {
                window.close();
                if (postDialogDismissal != null) {
                    postDialogDismissal.afterClosingDialog();
                }
            }
        });

        VBox layout = new VBox(10);
        layout.getChildren().addAll(label, closeButton);
        layout.setAlignment(Pos.CENTER);

        //Display window and wait for it to be closed before returning
        Scene scene = new Scene(layout);
        window.setScene(scene);
        window.show();
//        window.showAndWait();
    }

    //=============================================================================================
    //Business methods
    //=============================================================================================

    public void showPart(boolean visible){
        head.setVisible(visible);
        torso.setVisible(visible);
        leftHand.setVisible(visible);
        leftForearm.setVisible(visible);
        rightHand.setVisible(visible);
        rightForearm.setVisible(visible);
        leftLeg.setVisible(visible);
        rightLeg.setVisible(visible);
    }

    public void showPart(boolean visible, int partNo){
        switch (partNo){
            case 0:
                head.setVisible(visible);
                break;
            case 1:
                torso.setVisible(visible);
                break;
            case 2:
                leftHand.setVisible(visible);
                break;
            case 3:
                rightHand.setVisible(visible);
                break;
            case 4:
                leftForearm.setVisible(visible);
                break;
            case 5:
                rightForearm.setVisible(visible);
                break;
            case 6:
                leftLeg.setVisible(visible);
                break;
            case 7:
                rightLeg.setVisible(visible);
                break;
        }
    }

    public void setRoundLabel(String roundString){
        roundLabel.setText(roundString);
    }

    public void setHangmanLogic(HangmanLogic hangmanLogic) {
        this.hangmanLogic = hangmanLogic;
    }

    public void setHitLetters(String hitLetters){
        setHitLetters(hitLetters,Color.BLACK);
    }

    public void setHitLetters(String hitLetters,Color color){
        StringBuilder spaceSeperatedLetters=new StringBuilder();
        int length= hitLetters.length();
        for (int i = 0; i < length; i++) {
            spaceSeperatedLetters.append(hitLetters.charAt(i));
            if(i+1<length){
                spaceSeperatedLetters.append(" ");
            }
        }
        wordText.setText(spaceSeperatedLetters.toString());
        wordText.setFill(color);
    }

    public void setMissedLetters(String missedLetters){
        StringBuilder commaSeperatedLetters=new StringBuilder();
        int length=missedLetters.length();
        for (int i = 0; i < length; i++) {
            commaSeperatedLetters.append(missedLetters.charAt(i));
            if(i+1<length){
                commaSeperatedLetters.append(",");
            }
        }
        missesText.setText(commaSeperatedLetters.toString());
    }
    public void setCurrentPlayer(String currentPlayer){
        playerNameLabel.setText(currentPlayer);
    }
    public void clearFields(){
        letterTextField.setText("");
        wordTextField.setText("");
    }

    public void showScoreboard(int[] playerScores, int totalPlayers) {
        try {
            ObservableList<PlayerScore> observableList= FXCollections.observableArrayList();

            for (int i = 0; i < playerScores.length; i++) {
                String playerName = HangmanLogic.getPlayerName(i);
                PlayerScore playerScore = new PlayerScore(playerName, playerScores[i]);
                observableList.add(playerScore);
            }
            //sort the scores in descending order
            FXCollections.sort(observableList, new Comparator<PlayerScore>() {
                @Override
                public int compare(PlayerScore o1, PlayerScore o2) {
                    if(o1.score==o2.score){
                        return 0;
                    }else if(o1.score<o2.score){
                        return 1;
                    }else{
                        return -1;
                    }
                }
            });
            Stage stage=(Stage)wordGuessGoButton.getScene().getWindow();
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource(SCORE));
            Parent root=(Parent)fxmlLoader.load();
            Hangman hangmanController=(Hangman)fxmlLoader.<Hangman>getController();
            hangmanController.playerScores=observableList;
            hangmanController.populateScoreboard();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            info("Some problem arose in showing the score board");
        }
    }
}

class HangmanLogic{ //TODO name change

    public static final int LETTER_GUESS_STEP =10;
    public static final int WORD_GUESS_STEP=100;//TODO not needed, that player won
    public static final int MAX_TURNS_IN_ROUND=8;
    private static final Random random=new Random();
    private Hangman hangmanController;

    public HangmanLogic(Hangman hangmanController) {
        this.hangmanController = hangmanController;
    }

    private static String[] playerNames=new String[50];
    private static int totalKnownPlayerNames=0;
    private static String[] wordList=new String[100];
    private static int totalWords=8;
    private int round=0;
    private int totalRounds;
    private int totalPlayers;
    private String currentWord;
    private int currentPlayer;
    private int turnsInCurrentRound=0;
    private StringBuilder filled;
    private StringBuilder misses;
    private int playerScores[]=new int[10];
    private int wrongGuesses;

    static{
        playerNames[0]="Alex";
        playerNames[1]="Mason";
        playerNames[2]="John";
        playerNames[3]="Natasha";
        playerNames[4]="Natalie";
        playerNames[5]="Scarlett";
        totalKnownPlayerNames=6;
    }

    public static String getPlayerName(int index){
        String playerName=null;
        if(index<totalKnownPlayerNames){
            playerName=playerNames[index];
        }else{
            playerName=String.format("Player %d",index+1);
        }
        return playerName;
    }

    public static void readWords(String filename){
        try {

            InputStream stream=HangmanLogic.class.getResourceAsStream(filename);
            InputStreamReader inputStreamReader=new InputStreamReader(stream);
            BufferedReader bufferedReader=new BufferedReader(inputStreamReader);
            String lastLineRead=null;
            totalWords=0;
            while((lastLineRead = bufferedReader.readLine()) != null){
                String []words=lastLineRead.split(" ");
                for (int i = 0; i < words.length; i++) {
                    wordList[totalWords++]=words[i];
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Hangman.info("The file for the wordlist was not found");
        }catch (IOException e) {
            e.printStackTrace();
            Hangman.info("There was some problem reading the word list");
        }
        System.out.println("Final word list is ");
        for (int i = 0; i < totalWords; i++) {
            System.out.println(wordList[i]);
        }
    }

    public void freshGame(){
        round=-1;//no rounds done so far
        for (int i = 0; i < playerScores.length; i++) {
            playerScores[i]=0;
        }
        nextRound();

    }

    public boolean nextRound(){
        if(round+1<totalRounds) {
            round++;
            String roundString = String.format("Round %d", round+1);
            hangmanController.setRoundLabel(roundString);
            currentWord=wordList[random.nextInt(totalWords)];
            filled=new StringBuilder();
            misses=new StringBuilder();
            hangmanController.setMissedLetters(misses.toString());
            int length = currentWord.length();
            for (int i = 0; i < length; i++) {
                filled.append("_");
            }
            hangmanController.setHitLetters(filled.toString());
            hangmanController.showPart(false);
            turnsInCurrentRound=0;
            currentPlayer=-1;
            nextPlayer();
            wrongGuesses=0;
            System.out.println("Word :"+currentWord);

            return true;
        }else{
            return false;
        }
    }

    public int nextPlayer(){
        if(currentPlayer+1<totalPlayers){
            currentPlayer++;
        }else{
            currentPlayer=0;
        }
        String currentPlayerString = getPlayerName(currentPlayer);
        hangmanController.setCurrentPlayer(currentPlayerString);
        return currentPlayer;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public void setTotalPlayers(int totalPlayers) {
        this.totalPlayers = totalPlayers;
    }

    public int getTotalRounds() {
        return totalRounds;
    }

    public void setTotalRounds(int totalRounds) {
        this.totalRounds = totalRounds;
    }

    private int fillHit(String hitLetter){
        int totalCharectersFilled=0;
        //fill the word
        int length=currentWord.length();
        for (int i = 0; i < length; i++) {
            if (currentWord.charAt(i)==hitLetter.charAt(0)) {
                filled.replace(i,i+1,hitLetter);
                playerScores[currentPlayer]+= LETTER_GUESS_STEP;
                totalCharectersFilled++;
            }
        }
        System.out.println("filled letters now are "+filled.toString());
        hangmanController.setHitLetters(filled.toString());
        return totalCharectersFilled;
    }

    private void fillMiss(String missedLetter){
        misses.append(missedLetter.charAt(0));
        hangmanController.setMissedLetters(misses.toString());
        hangmanController.showPart(true,wrongGuesses++);
    }

    public int guessLetter(String a) {
        if (a.length() != 1) {
            return 0;//does not move the state forward
        }
        int totalHits=0;
        //check for containment
        if (currentWord.contains(a)) {
            if(misses.indexOf(a)==-1){//it should not be in the missed words too
                totalHits= fillHit(a);
                //check for complete word fill completion
                turnsInCurrentRound++;
            }else{
                hangmanController.info("You already guessed " + a);
            }
        }else{
            fillMiss(a);
            turnsInCurrentRound++;
        }
        hangmanController.clearFields();
        proceedGameForward();
        return totalHits;
    }

    public boolean guessWord(String a){
        boolean hit=false;
        if (a.length() <=0) {
            return false;//does not move the state forward
        }
        //check for containment
        if (currentWord.equalsIgnoreCase(a)) {
            filled=new StringBuilder(currentWord);
            playerScores[currentPlayer]+=WORD_GUESS_STEP;
            hangmanController.setHitLetters(currentWord);
            turnsInCurrentRound++;
        }else{
            hangmanController.showPart(true,wrongGuesses++);
            turnsInCurrentRound++;
        }
        hangmanController.clearFields();
        proceedGameForward();
        return hit;
    }

    private boolean proceedGameForward(){
        boolean roundDidEnd=false;
        if(currentWord.equalsIgnoreCase(filled.toString())){
            //winner
            roundDidEnd=true;
            hangmanController.setHitLetters(currentWord,Color.GREEN);

            PostDialogDismissal postDialogDismissal = new PostDialogDismissal() {

                @Override
                public void afterClosingDialog() {
                    if (!nextRound()) {
                        hangmanController.showScoreboard(playerScores, totalPlayers);
                    }
                }
            };
            hangmanController.info(getPlayerName(currentPlayer)+" won this round!",postDialogDismissal);
        }else if(turnsInCurrentRound>=MAX_TURNS_IN_ROUND){
            //game loose
            roundDidEnd=true;
            hangmanController.setHitLetters(currentWord,Color.RED);

            PostDialogDismissal postDialogDismissal=new PostDialogDismissal() {
                @Override
                public void afterClosingDialog() {
                    if(!nextRound()){
                        hangmanController.showScoreboard(playerScores, totalPlayers);
                    }

                }
            };
            hangmanController.info("Game over!",postDialogDismissal);
        }else{
            //game continues
            nextPlayer();
        }
        return roundDidEnd;
    }
}

interface PostDialogDismissal{
        public void afterClosingDialog();
}
class PlayerScore{
    String playerName;
    int score;

    public PlayerScore(String playerName, int score) {
        this.playerName = playerName;
        this.score = score;
    }
}