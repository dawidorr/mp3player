package pl.javastart.mp3player.controller;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.farng.mp3.MP3File;
import org.farng.mp3.TagException;
import pl.javastart.mp3player.mp3.Mp3Parser;
import pl.javastart.mp3player.mp3.Mp3Song;
import pl.javastart.mp3player.player.Mp3Player;

import java.io.File;
import java.io.IOException;

public class MainController {

    @FXML
    private ContentPaneController contentPaneController;
    @FXML
    private ControlPaneController controlPaneController;
    @FXML
    private MenuPaneController menuPaneController;

    private Mp3Player player;

    public void initialize() {
        createPlayer();
        configureTableClick();
        configureButtons();
        addTestMp3();
        configureMenu();
    }

    private void createPlayer(){
        ObservableList<Mp3Song> items = contentPaneController.getContentTable().getItems();
        player = new Mp3Player(items);
    }

    private void configureTableClick(){
        TableView<Mp3Song> contentTable = contentPaneController.getContentTable();
        contentTable.addEventHandler(MouseEvent.MOUSE_CLICKED,mouseEvent -> {
            if(mouseEvent.getClickCount() == 2){
                int selectedIndex = contentTable.getSelectionModel().getSelectedIndex();
                playSelectedSong(selectedIndex);
            }
        });
    }

    private void playSelectedSong(int selectedIndex) {
        player.loadSong(selectedIndex);
        configureProgressBar();
        configureVolume();
        controlPaneController.getPlayButton().setSelected(true);
    }
    private void configureProgressBar(){
        Slider progressSlider = controlPaneController.getProgressSlider();
        player.getMediaPlayer().setOnReady(() -> progressSlider.setMax(player.getLoadedSongLength()));
        player.getMediaPlayer().currentTimeProperty().addListener((arg,oldVal,newVal) ->
                progressSlider.setValue(newVal.toSeconds()));
        progressSlider.valueProperty().addListener((observableValue, number, t1) -> {
            if(progressSlider.isValueChanging()){
                player.getMediaPlayer().seek(Duration.seconds(t1.doubleValue()));
            }
        });
    }
    private void configureVolume(){
        Slider volumeSlider = controlPaneController.getVolumeSlider();
        volumeSlider.valueProperty().unbind();
        volumeSlider.setMax(1.0);
        volumeSlider.valueProperty().bindBidirectional(player.getMediaPlayer().volumeProperty());
    };

    private void configureButtons(){
        TableView<Mp3Song>contentTable = contentPaneController.getContentTable();
        ToggleButton playButton = controlPaneController.getPlayButton();
        Button prevButton = controlPaneController.getPreviousButton();
        Button nextButton = controlPaneController.getNextButton();

        playButton.setOnAction(actionEvent -> {
            if(playButton.isSelected()){
                player.play();
            }else {
                player.stop();
            }
        });

        nextButton.setOnAction(actionEvent -> {
            contentTable.getSelectionModel().select(contentTable.getSelectionModel().getSelectedIndex() + 1);
            playSelectedSong(contentTable.getSelectionModel().getSelectedIndex());
        });

        prevButton.setOnAction(actionEvent -> {
            contentTable.getSelectionModel().select(contentTable.getSelectionModel().getSelectedIndex() - 1);
            playSelectedSong(contentTable.getSelectionModel().getSelectedIndex());
        });

    }

    private void addTestMp3(){
        ObservableList<Mp3Song> items = contentPaneController.getContentTable().getItems();
        Mp3Song mp3SongFromPath = createMp3SongFromPath("1.mp3");
        items.add(mp3SongFromPath);
        items.add(mp3SongFromPath);
        items.add(mp3SongFromPath);
    };
    private Mp3Song createMp3SongFromPath(String filePath) {
        File file = new File(filePath);
        try {
            MP3File mp3File = new MP3File(file);
            String absolutePath = file.getAbsolutePath();
            String title = mp3File.getID3v2Tag().getSongTitle();
            String author = mp3File.getID3v2Tag().getLeadArtist();
            String album = mp3File.getID3v2Tag().getAlbumTitle();
            return new Mp3Song(title, author, album, absolutePath);
        } catch (IOException | TagException e) {
            e.printStackTrace();
            return null; //ignore
        }
    }

    private void configureMenu(){
        MenuItem openFile = menuPaneController.getFileMenuItem();
        MenuItem openDir = menuPaneController.getDirMenuItem();

        openFile.setOnAction(actionEvent -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Mp3", "*.mp3"));
            File file = fileChooser.showOpenDialog(new Stage());
            try{
                contentPaneController.getContentTable().getItems().add(Mp3Parser.createMp3Song(file));
            }catch (IOException | TagException e) {
                e.printStackTrace();
            }
        });

        openDir.setOnAction(actionEvent -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            File dir = directoryChooser.showDialog(new Stage());
            try{
                contentPaneController.getContentTable().getItems().addAll(Mp3Parser.createMp3List(dir));
            }catch (Exception e){
                e.printStackTrace();
            }
        });
    }



}