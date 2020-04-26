package pl.javastart.mp3player.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.input.MouseEvent;

public class ControlPaneController {
    @FXML
    private Button previousButton;

    @FXML
    private ToggleButton playButton;

    @FXML
    private Button nextButton;

    @FXML
    private Slider volumeSlider;

    @FXML
    private Slider progressSlider;

    public Button getPreviousButton(){
        return previousButton;
    }

    public ToggleButton getPlayButton(){
        return playButton;
    }
    public Button getNextButton() {
        return nextButton;
    }
    public Slider getVolumeSlider() {
        return volumeSlider;
    }
    public Slider getProgressSlider() {
        return progressSlider;
    }

}
