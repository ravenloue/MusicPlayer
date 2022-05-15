package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;


import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class Controller implements Initializable{

	@FXML
	private Pane pane;
	@FXML
	private Label songTitle;
	@FXML
	private Button playBtn, pauseBtn, resetBtn, prevBtn, nextBtn;
	@FXML
	private ComboBox<String> speedBox;
	@FXML
	private Slider volSlider;
	@FXML
	private ProgressBar songProgressBar;
	
	private Media media;
	private MediaPlayer mediaPlayer;
	
	private File directory;
	private File[] files;
	private ArrayList<File> songs;
	
	private int songNum;	
	private int[] speeds = {25, 50, 75, 100, 125, 150 ,175, 200};
	
	private Timer timer;
	private TimerTask task;
	private boolean running;
	
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		// TODO Auto-generated method stub
		
		songs = new ArrayList<File>();
		directory = new File("src\\music");
		
		files = directory.listFiles();
				
		if(files != null) {
			for(File file : files) {
				songs.add(file);
				}
		}
		
		media = new Media(songs.get(songNum).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		
		songTitle.setText(songs.get(songNum).getName());
		
		for(int i = 0; i < speeds.length;i++) {
			speedBox.getItems().add(Integer.toString(speeds[i])+"%");
		}
		
		speedBox.setOnAction(this::changeSpeed);
		
		volSlider.valueProperty().addListener(new ChangeListener<Number>() {

			@Override
			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
				// TODO Auto-generated method stub
				
				mediaPlayer.setVolume(volSlider.getValue() * 0.01);
				
			}
			
		});
		
		songProgressBar.setStyle("-fx-accent: #374cfe;");
	
		
	}
	
	public void playMedia() {
		beginTimer();
		changeSpeed(null);
		mediaPlayer.setVolume(volSlider.getValue() * 0.01);
		mediaPlayer.play();
		
	}
	public void pauseMedia() {
		cancelTimer();
		mediaPlayer.pause();
	}
	public void resetMedia() {
		
		songProgressBar.setProgress(0);
		mediaPlayer.seek(Duration.seconds(0));
	}
	public void prevMedia() {
		if(songNum > 0) {
			songNum--;
			mediaPlayer.stop();
			
			if(running) {
				cancelTimer();
			}
			
			media = new Media(songs.get(songNum).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songTitle.setText(songs.get(songNum).getName());
			
			playMedia();
		}
		else {
			songNum = songs.size() - 1;
			mediaPlayer.stop();
			
			if(running) {
				cancelTimer();
			}
			
			media = new Media(songs.get(songNum).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songTitle.setText(songs.get(songNum).getName());
			
			playMedia();
		}
		
	}
	public void nextMedia() {
		if(songNum < songs.size()-1) {
			songNum++;
			mediaPlayer.stop();
			
			if(running) {
				cancelTimer();
			}
			
			media = new Media(songs.get(songNum).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songTitle.setText(songs.get(songNum).getName());
			
			playMedia();
		}
		else {
			songNum = 0;
			mediaPlayer.stop();
			
			if(running) {
				cancelTimer();
			}
			
			media = new Media(songs.get(songNum).toURI().toString());
			mediaPlayer = new MediaPlayer(media);
			
			songTitle.setText(songs.get(songNum).getName());
			
			playMedia();
		}
	}
	public void changeSpeed(ActionEvent event) {
		if (speedBox.getValue() == null) {
			mediaPlayer.setRate(1);
		}
		else {
		mediaPlayer.setRate(Integer.parseInt(speedBox.getValue().substring(0, speedBox.getValue().length() - 1)) * 0.01);
		}
	}
	public void beginTimer() {
		
		timer = new Timer();
		
		task = new TimerTask() {

			@Override
			public void run() {
				running = true;
				double current = mediaPlayer.getCurrentTime().toSeconds();
				double end = media.getDuration().toSeconds();
				
				songProgressBar.setProgress(current/end);
				
				if(current/end == 1) {
					cancelTimer();
				}
				
			}
			
		};
		
		timer.scheduleAtFixedRate(task, 0, 1000);
		
	}
	public void cancelTimer() {
		running = false;
		timer.cancel();
	}
	
}
