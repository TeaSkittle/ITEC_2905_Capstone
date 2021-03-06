/*
=========================================
By: Travis Dowd
Date: 1-7-2021

This class file contains all of the JavaFX code  and is the main file to be launched
=========================================
 */
package src;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.geometry.Insets;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.*;
import javafx.scene.paint.Color;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.Media;
import javafx.util.Duration;
import javafx.scene.control.ToggleButton; 

public class Main extends Application {
    public static final int SIZE = 8;
    public static int music = 1;
    public static int sound = 1;
    private static Game game = new Game();
    private static Problem problem = new Problem();
    public static int problemNumber = problem.ReadCurrent();
    public static String position = problem.ReadPosition( problemNumber );
    public static String correctMove = problem.ReadMove( problemNumber );
    public static int[][] fenArray = game.FenParser( position ); 
    public static Label[][] labels = new Label[ SIZE ][ SIZE ];
    public static GridPane chessBoard = new GridPane();
    public static VBox vbox = new VBox();
    public static HBox topHbox = new HBox();
    public static Label vboxLabel = new Label( String.valueOf( problem.ReadCurrent() ));
    public static Label topHboxLabel = game.GetTurn() == 'w' ? new Label( "White to play" ) : new Label( "Black to play" );
    public static Font font = Font.font( "IBM Plex Sans", 24 );
    public static final String MUSIC_FILE = "resources/music.mp3";
    public static InputStream musicStream = Main.class.getResourceAsStream( MUSIC_FILE );
    public static Media musicMedia = new Media(Main.class.getResource( MUSIC_FILE ).toString()); 
    public static MediaPlayer musicPlayer = new MediaPlayer( musicMedia );
    /*
    ====================
    Start
     Display all the JavaFX stuff
    ====================
    */
    @Override
    public void start( Stage primaryStage ) throws Exception {
        GameMenu( primaryStage );
    }
    /*
    ====================
    GameMenu
     The start screen of the game, in which options can be selected and the can is started from.
    ====================
    */
    private static void GameMenu( Stage primaryStage ){
        BorderPane pane = new BorderPane();
        Scene scene = new Scene( pane, 60 * SIZE, 60 * SIZE );
        try ( InputStream inputStream = Main.class.getResourceAsStream( "resources/menu_bg.png" )) {
            ImageView image = new ImageView( new Image( inputStream ));
            pane.getChildren().add( image );
        } catch ( IOException ex ) { ex.printStackTrace(); }
        HBox hbox = new HBox();
        hbox.setAlignment( Pos.CENTER );
        hbox.setPadding( new Insets( 50, 0, 0, 0 ));
        Text title = new Text();
        title.setText( "Check Yourself" );
        title.setFont( new Font( "IBM Plex Sans", 50 ));
        title.setFill( Color.WHITE );
        hbox.getChildren().add( title );
        VBox vbox = new VBox();
        vbox.setAlignment( Pos.CENTER );
        vbox.setPadding( new Insets( -50, 0, 0, 0));
        Button playButton = new Button( "Play" );
        playButton.setFont( font );
        playButton.setMaxWidth( 150 );
        ToggleButton musicButton = new ToggleButton( "Music: On" );
        musicButton.setFont( font );
        musicButton.setMaxWidth( 150 );
        ToggleButton soundButton = new ToggleButton( "Sound: On" );
        soundButton.setFont( font );
        soundButton.setMaxWidth( 150 );
        playButton.setOnAction( e -> {
            PlayGame( primaryStage );
        });
        musicButton.setOnAction( e -> {
            if ( music == 1 ) { 
                music = 0;
                musicButton.setText( "Music: Off" ); 
            } else { 
                music = 1; 
                musicButton.setText( "Music: On" ); 
            }
        });
        soundButton.setOnAction( e -> {
            if ( sound == 1 ) { 
                sound = 0; 
                soundButton.setText( "Sound: Off" );
            } else { 
                sound = 1;
                soundButton.setText( "Sound: On" ) ;
            }
        });
        vbox.getChildren().addAll( playButton, musicButton, soundButton );
        pane.setCenter( vbox );
        pane.setTop( hbox );
        primaryStage.setTitle( "Check Yourself" );
        primaryStage.setScene( scene );
        primaryStage.show();
    }
    /*
    ====================
    GameOver
     The screen after completing the game
    ====================
    */
    public static void GameOver( Stage primaryStage ){
        problem.WriteCurrent( 0 );
        BorderPane pane = new BorderPane();
        Scene scene = new Scene( pane, 60 * SIZE, 60 * SIZE );
        try ( InputStream inputStream = Main.class.getResourceAsStream( "resources/final_bg.png" )) {
            ImageView image = new ImageView( new Image( inputStream ));
            pane.getChildren().add( image );
        } catch ( IOException ex ) { ex.printStackTrace(); }
        HBox hbox = new HBox();
        hbox.setAlignment( Pos.CENTER );
        hbox.setPadding( new Insets( 60, 0, 0, 0 ));
        Text title = new Text();
        title.setText( "Game Over" );
        title.setFont( new Font( "IBM Plex Sans", 50 ));
        title.setFill( Color.WHITE );
        hbox.getChildren().add( title );
        VBox vbox = new VBox();
        vbox.setAlignment( Pos.CENTER );
        vbox.setPadding( new Insets( -50, 0, 0, 0));
        Button exitButton = new Button( "Exit" );
        exitButton.setFont( font );
        exitButton.setMaxWidth( 150 );
        vbox.getChildren().addAll( exitButton );
        exitButton.setOnAction( e -> {
            primaryStage.close();
        });
        pane.setCenter( vbox );
        pane.setTop( hbox );
        primaryStage.setTitle( "Check Yourself" );
        primaryStage.setScene( scene );
        primaryStage.show();
    }
    /*
    ====================
    PlayGame
     After setttings are picked in menu, this meant to be run to start the game, this helps clean up the start method.
    ====================
    */
    private static void PlayGame( Stage primaryStage ){
        BorderPane pane = new BorderPane();
        Scene scene = new Scene( pane, 60 * SIZE, 60 * SIZE );
        TextField textField = new TextField();
        Label textLabel = new Label( "Move: " );
        Button button = new Button( "Submit" );
        HBox hbox = new HBox();
        VBox vboxRight = new VBox();
        ToggleButton soundButton = new ToggleButton( "Sound" );
        ToggleButton musicButton = new ToggleButton( "Music" );
        soundButton.setFont( new Font( "IBM Plex Sans", 16  ));
        musicButton.setFont( new Font( "IBM Plex Sans", 16  ));
        vboxRight.getChildren().addAll( soundButton, musicButton );
        vboxRight.setPadding( new Insets( -40, 10, 0, 0 ));
        textLabel.setFont( font );
        textLabel.setStyle( "-fx-text-fill: white" );
        hbox.getChildren().addAll( textLabel, textField, button );
        hbox.setAlignment( Pos.CENTER );
        hbox.setSpacing( 10 );
        hbox.setPadding( new Insets( 0, 40, 50, 0 ));
        vbox.setPadding( new Insets( 40, 0, 0, 20 ));
        vboxLabel.setFont( font );
        vbox.getChildren().add( vboxLabel );
        topHboxLabel.setFont( font );
        topHbox.setPadding( new Insets( 20, 0, 0, 0 ));
        topHbox.setAlignment( Pos.CENTER );
        topHbox.getChildren().add( topHboxLabel );
        chessBoard.setAlignment( Pos.CENTER );
        try ( InputStream inputStream = Main.class.getResourceAsStream( "resources/game_bg.png" )) {
            ImageView image = new ImageView( new Image( inputStream ));
            pane.getChildren().add( image );
        } catch ( IOException ex ) { ex.printStackTrace(); }
        if ( music == 1 ) { 
            musicPlayer.play();             
        }
        PrintBoard();
        PrintPieces( fenArray );
        textField.setOnAction( e -> {
            String inputString = textField.getText();
            textField.clear();
            if ( inputString.equals( correctMove )) {
                UpdateBoard( primaryStage );
            } else {
                if ( sound == 1 ){
                    String SOUND_FILE = "resources/piecesound.mp3";
                    Media soundMedia = new Media( Main.class.getResource( SOUND_FILE ).toString() );
                    MediaPlayer soundPlayer = new MediaPlayer( soundMedia );
                    soundPlayer.setAutoPlay( true );
                }
            }
        }); 
        button.setOnAction( e -> {
            String inputString = textField.getText();
            textField.clear();
            if ( inputString.equals( correctMove )) {
                UpdateBoard( primaryStage );
            } else {
                if ( sound == 1 ){
                    String SOUND_FILE = "resources/piecesound.mp3";
                    Media soundMedia = new Media( Main.class.getResource( SOUND_FILE ).toString() );
                    MediaPlayer soundPlayer = new MediaPlayer( soundMedia );
                    soundPlayer.setAutoPlay( true );
                }
            }
        }); 
        soundButton.setOnAction( e -> {
            if ( sound == 1 ) { 
                sound = 0; 
            } else { 
                sound = 1;
            }
        });
        musicButton.setOnAction( e -> {
            if ( music == 1 ) { 
                music = 0;
                musicPlayer.stop();
            } else { 
                music = 1; 
                musicPlayer.play();
            }
        });
        pane.setCenter( chessBoard );
        pane.setBottom( hbox );
        pane.setLeft( vbox );
        pane.setTop( topHbox );
        pane.setRight( vboxRight );
        primaryStage.setScene( scene );
        primaryStage.show();
    }
    /*
    ====================
    UpdateBoard
     Clear and reprint the pieces with correct array, reassign values to match new problem, and play sound effect
    ====================
    */
    private static void UpdateBoard( Stage primaryStage ){
        if ( problem.ReadCurrent() >= problem.FileSize() ) { GameOver( primaryStage ); }
        problem.IncrementCurrent();
        problemNumber = problem.ReadCurrent();
        position = problem.ReadPosition( problemNumber );
        correctMove = problem.ReadMove( problemNumber );
        fenArray = game.FenParser( position );
        chessBoard.getChildren().clear();
        vbox.getChildren().clear();
        vboxLabel = new Label( String.valueOf( problemNumber ));
        vboxLabel.setFont( font );
        vbox.getChildren().add( vboxLabel );
        topHbox.getChildren().clear();
        topHboxLabel = game.GetTurn() == 'w' ? new Label( "White to play" ) : new Label( "Black to play" );
        topHboxLabel.setFont( font );
        topHbox.getChildren().add( topHboxLabel );
        PrintBoard();
        PrintPieces( fenArray );
        if ( sound == 1 ) {
            Media media = new Media( Main.class.getResource( "resources/checkmate.mp3" ).toString() );
            MediaPlayer mediaPlayer = new MediaPlayer( media );
            mediaPlayer.setAutoPlay( true );            
        }
    }
    /*
    ====================
    PrintBoard
     Print the checkerboard pattern for the background of the pieces
    ====================
    */
    private static void PrintBoard(){
        for ( int i = 0; i < SIZE; i++ ) {
            for ( int j = 0; j < SIZE; j++ ) {
                chessBoard.add( labels[ i ][ j ] = new Label(), j, i );
                labels[ i ][ j ].setPrefSize( 37, 37 );
                if ( i % 2 == 0 && j % 2 != 0 ) {
                    labels[ i ][ j ].setStyle( "-fx-border-color: black; -fx-background-color: Peru" );
                } if ( i % 2 != 0 && j % 2 == 0 ) {
                    labels[ i ][ j ].setStyle( "-fx-border-color: black; -fx-background-color: Peru" );
                } if ( i % 2 == 0 && j % 2 == 0 ) {
                    labels[ i ][ j ].setStyle( "-fx-border-color: black; -fx-background-color: AntiqueWhite" );
                } if ( i % 2 != 0 && j % 2 != 0 ) {
                    labels[ i ][ j ].setStyle( "-fx-border-color: black; -fx-background-color: AntiqueWhite" );
                }
            }    
        }
    }
    /*
    ====================
    PrintPieces
     Print the pieces of the problem to the board, the numbers are inverted for some reason but it works.
    ====================
    */
    private static void PrintPieces( int[][] arr ){
        for ( int i = 0; i < SIZE; i++ ) {
            for ( int j = 0; j < SIZE; j++ ) {
                switch ( fenArray[ i ][ j ] ) {
                    case 21: 
                        InputStream whitePawnStream = Main.class.getResourceAsStream( "resources/whitepawn.png" );
                        ImageView whitePawnImage = new ImageView( new Image( whitePawnStream ));
                        labels[ i ][ j ].setGraphic( new ImageView( whitePawnImage.getImage() ));
                        break;
                    case 22:
                        InputStream whiteKnightStream = Main.class.getResourceAsStream( "resources/whiteknight.png" );
                        ImageView whiteKnightImage = new ImageView( new Image( whiteKnightStream ));
                        labels[ i ][ j ].setGraphic( new ImageView( whiteKnightImage.getImage() ));
                        break;
                    case 23:
                        InputStream whiteBishopStream = Main.class.getResourceAsStream( "resources/whitebishop.png" );
                        ImageView whiteBishopImage = new ImageView( new Image( whiteBishopStream ));
                        labels[ i ][ j ].setGraphic( new ImageView( whiteBishopImage.getImage() ));
                        break;
                    case 24:
                        InputStream whiteRookStream = Main.class.getResourceAsStream( "resources/whiterook.png" );
                        ImageView whiteRookImage = new ImageView( new Image( whiteRookStream ));
                        labels[ i ][ j ].setGraphic( new ImageView( whiteRookImage.getImage() ));
                        break;
                    case 25:
                        InputStream whiteQueenStream = Main.class.getResourceAsStream( "resources/whitequeen.png" );
                        ImageView whiteQueenImage = new ImageView( new Image( whiteQueenStream ));
                        labels[ i ][ j ].setGraphic( new ImageView( whiteQueenImage.getImage() ));
                        break;
                    case 26:
                        InputStream whiteKingStream = Main.class.getResourceAsStream( "resources/whiteking.png" );
                        ImageView whiteKingImage = new ImageView( new Image( whiteKingStream ));
                        labels[ i ][ j ].setGraphic( new ImageView( whiteKingImage.getImage() ));
                        break;
                    case 11:
                        InputStream blackPawnStream = Main.class.getResourceAsStream( "resources/blackpawn.png" );
                        ImageView blackPawnImage = new ImageView( new Image( blackPawnStream ));
                        labels[ i ][ j ].setGraphic( new ImageView( blackPawnImage.getImage() ));
                        break;
                    case 12:
                        InputStream blackKnightStream = Main.class.getResourceAsStream( "resources/blackknight.png" );
                        ImageView blackKnightImage = new ImageView( new Image( blackKnightStream ));
                        labels[ i ][ j ].setGraphic( new ImageView( blackKnightImage.getImage() ));
                        break;
                    case 13:
                        InputStream blackBishopStream = Main.class.getResourceAsStream( "resources/blackbishop.png" );
                        ImageView blackBishopImage = new ImageView( new Image( blackBishopStream ));
                        labels[ i ][ j ].setGraphic( new ImageView( blackBishopImage.getImage() ));
                        break;
                    case 14:
                        InputStream blackRookStream = Main.class.getResourceAsStream( "resources/blackrook.png" );
                        ImageView blackRookImage = new ImageView( new Image( blackRookStream ));
                        labels[ i ][ j ].setGraphic( new ImageView( blackRookImage.getImage() ));
                        break;
                    case 15:
                        InputStream blackQueenStream = Main.class.getResourceAsStream( "resources/blackqueen.png" );
                        ImageView blackQueenImage = new ImageView( new Image( blackQueenStream ));
                        labels[ i ][ j ].setGraphic( new ImageView( blackQueenImage.getImage() ));
                        break;
                    case 16:
                        InputStream blackKingStream = Main.class.getResourceAsStream( "resources/blackking.png" );
                        ImageView blackKingImage = new ImageView( new Image( blackKingStream ));
                        labels[ i ][ j ].setGraphic( new ImageView( blackKingImage.getImage() ));
                        break;
                }
            }
        }
    }
    /*
    ====================
    Main
    ====================
    */
    public static void main( String[] args ){
        launch( args );
    }
}
