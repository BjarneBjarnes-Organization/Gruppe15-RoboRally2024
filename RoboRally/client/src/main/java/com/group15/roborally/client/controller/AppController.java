/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package com.group15.roborally.client.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import com.group15.roborally.client.utils.TextUtils;
import com.group15.roborally.client.view.MultiplayerMenuView;
import com.group15.roborally.server.model.*;

import com.group15.roborally.client.model.*;
import com.group15.observer.Observer;
import com.group15.observer.Subject;
import com.group15.roborally.client.RoboRally;
import com.group15.roborally.server.utils.ServerCommunication;
import com.group15.roborally.client.coursecreator.CC_CourseData;
import com.group15.roborally.client.coursecreator.CC_JsonUtil;
import com.group15.roborally.client.exceptions.EmptyCourseException;
import com.group15.roborally.client.exceptions.GameLoadingException;
import com.group15.roborally.client.exceptions.NoCoursesException;
import com.group15.roborally.client.utils.Adapter;
import com.group15.roborally.client.utils.SaveAndLoadUtils;
import com.group15.roborally.client.templates.BoardTemplate;
import com.group15.roborally.client.model.boardelements.BoardElement;

import com.group15.roborally.server.model.Player;
import javafx.animation.PauseTransition;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;

import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.util.Duration;
import javafx.util.Pair;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.*;
import java.io.File;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

import static com.group15.roborally.client.BoardOptions.*;

/**
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
 */
public class AppController {
    private final RoboRally roboRally;
    public boolean isCourseCreatorOpen = false;
    private GameController gameController;
    private List<CC_CourseData> courses = new ArrayList<>();

    private final NetworkingController networkingController = new NetworkingController(this);

    private MultiplayerMenuView multiplayerMenuView;
    private final StackPane infoPane = new StackPane();
    private final Text infoText = new Text();

    public AppController(@NotNull RoboRally roboRally, StackPane mainPane) {
        this.roboRally = roboRally;
        initializeInfoPane(mainPane);
    }

    public void initializeInfoPane(StackPane mainPane) {
        infoPane.getChildren().add(infoText);
        mainPane.getChildren().add(infoPane);
        StackPane.setAlignment(infoPane, Pos.CENTER);
        infoPane.setAlignment(Pos.CENTER);
        infoPane.setStyle("-fx-background-color: #000000A5");
        Font textFont = TextUtils.loadFont("OCRAEXT.TTF", 90);
        infoText.setFont(textFont);
        infoText.setFill(Color.WHITE);
        infoText.setStroke(Color.BLACK);
        infoText.setStrokeWidth(3);
        infoText.setStrokeType(StrokeType.OUTSIDE);
        infoText.setWrappingWidth(2560);
        infoText.setTextAlignment(TextAlignment.CENTER);
        StackPane.setMargin(infoText, new Insets(0, 0, 75, 0));
        setInfoText("");
    }

    /**
     * Method for going to the join/host multiplayer menu.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void initializeMultiplayerMenu() {
        setInfoText("Setting up multiplayer...", false);
        Platform.runLater(() -> {
            multiplayerMenuView = new MultiplayerMenuView();
            roboRally.createMultiplayerMenu(multiplayerMenuView);
            multiplayerMenuView.setupMenuUI(networkingController);
            multiplayerMenuView.setupBackButton(roboRally::goToMainMenu);
            setInfoText("");
        });
    }

    public void showLobby(boolean showLobby) {
        multiplayerMenuView.showLobby(showLobby);
    }

    public void leftGame() {
        roboRally.goToMainMenu();
    }

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void beginCourse(CC_CourseData courseData, List<Player> players) {
        Pair<List<Space[][]>, Space[][]> courseSpaces = courseData.getGameSubBoards();
        Board board = new Board(courseSpaces.getKey(), courseSpaces.getValue(), courseData.getCourseName(), courseData.getNoOfCheckpoints());

        // GameController
        gameController = new GameController(board, this::gameOver);

        // Adding players
        for (int i = 0; i < NO_OF_PLAYERS; i++) {
            com.group15.roborally.client.model.Player player =
                    new com.group15.roborally.client.model.Player(players.get(i).getPlayerId(),
                            players.get(i).getPlayerName(),
                            board,
                            Objects.requireNonNull(Robots.getRobotByName(players.get(i).getRobotName())));
            player.setHeading(Heading.EAST);
            board.addPlayer(player);
        }

        board.setCurrentPlayer(board.getPlayer(0));

        roboRally.createBoardView(gameController);
    }

    /**
     * Load a game from a file. A new game controller is created, with the board loaded
     * from the file. The Phase of the game is manually set to Programming.
     * @param loadedFile the .json to be deserialized into a board
     *
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void loadGame(File loadedFile) throws GameLoadingException {
        // Loading file
        GsonBuilder simpleBuilder = new GsonBuilder().
                registerTypeAdapter(BoardElement.class, new Adapter<BoardElement>()).
                excludeFieldsWithModifiers(java.lang.reflect.Modifier.TRANSIENT);
        Gson gson = simpleBuilder.create();
        BoardTemplate boardTemplate = null;
        try {
            Scanner scanner = new Scanner(loadedFile);
            StringBuilder boardStringBuilder = new StringBuilder();
            while (scanner.hasNextLine()) {
                boardStringBuilder.append(scanner.nextLine());
            }
            scanner.close();
            boardTemplate = gson.fromJson(boardStringBuilder.toString(), BoardTemplate.class);
        } catch (IOException e1) {
            System.out.println(e1.getMessage());
        }
        if (boardTemplate == null) {
            throw new GameLoadingException();
        }

        // Creating and initializing the GameController, Board, and Players.
        // Board
        Board newBoard;
        try {
            newBoard = SaveAndLoadUtils.loadBoard(boardTemplate, courses);
        } catch (EmptyCourseException e) {
            System.out.println(e.getMessage());
            return;
        }

        // GameController
        gameController = new GameController(newBoard, this::gameOver);
        SaveAndLoadUtils.loadPlayers(boardTemplate, newBoard, gameController);
        newBoard.setCurrentPhase(Phase.PROGRAMMING);

        // Players
        for (int i = 0; i < newBoard.getNoOfPlayers(); i++) {
            com.group15.roborally.client.model.Player player = newBoard.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < com.group15.roborally.client.model.Player.NO_OF_REGISTERS; j++) {
                    CardField field = player.getProgramField(j);
                    field.setVisible(true);
                }
                for (int j = 0; j < NO_OF_CARDS_IN_HAND; j++) {
                    CardField field = player.getCardField(j);
                    field.setVisible(true);
                }
            }
        }
        newBoard.setCurrentPlayer(newBoard.getPlayer(0));

        roboRally.createBoardView(gameController);
    }


    /**
     * Save the current game to a file with the given file name. If the
     * game is not in the programming phase, the game is not saved and
     * the method returns false. Otherwise, the game is saved and the
     * method returns true.
     *
     * @author Marcus Rémi Lemser Eychenne, s230985
     * @return true if the game was saved, false otherwise
     */
    public boolean saveGame(File file) {
        if (gameController.board.getCurrentPhase() != Phase.PROGRAMMING) {
            return false;
        }
        SaveAndLoadUtils.saveBoard(gameController.board, file);
        return true;
    }

    /**
     * sets ends game
     * @author Maximillian Bjørn Mortensen
     */
    public void gameOver() {
        roboRally.goToWinScreen(gameController);
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean stopGame() {
        if (gameController != null) {
            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    /**
     * sets game controller to null
     * @param gameController The gameController.
     * @author Maximillian Bjørn Mortensen
     */
    public void setGameController(GameController gameController){
        this.gameController = gameController;
    }

    public void quit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || stopGame()) {
            Platform.exit();
        }
    }

    public boolean isGameRunning() {
        return gameController != null;
    }

    /**
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void createCourseCreator(Scene primaryScene) {
        roboRally.createCourseCreator(primaryScene);
        isCourseCreatorOpen = true;
    }

    /**
     * Loads the courses from resources and puts them in the courses variable.
     * @throws NoCoursesException If there were not found any courses.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void loadCourses() throws NoCoursesException {
        // Loading courses
        courses = CC_JsonUtil.getCoursesInFolder("courses");
        if (courses.isEmpty()) {
            throw new NoCoursesException();
        }
    }

    public List<CC_CourseData> getCourses() {
        return courses;
    }

    public void resetMultiplayerMenuView() {
        multiplayerMenuView = null;
    }

    /**
     * Method for showing the connection status to the player.
     * @param connectionInfo If blank, hides the connection info pane. If not, shows the pane and sets the connection info text to connectionInfo.
     * @author Carl Gustav Bjergaard Aggeboe, s235063@dtu.dk
     */
    public void setInfoText(String connectionInfo, boolean printMessage) {
        if (connectionInfo != null && !connectionInfo.isBlank()) {
            infoText.setText(connectionInfo);
            if (printMessage) System.out.println(connectionInfo);

            infoPane.setVisible(true);
            infoPane.setDisable(false);
        } else {
            infoPane.setVisible(false);
            infoPane.setDisable(true);
        }
    }
    public void setInfoText(String connectionInfo) {
        setInfoText(connectionInfo, true);
    }

    public void disconnectFromServer(String s, int i) {
        networkingController.disconnectFromServer(s, i);
    }
}