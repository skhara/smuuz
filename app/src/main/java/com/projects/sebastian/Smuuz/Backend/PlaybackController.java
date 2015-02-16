package com.projects.sebastian.Smuuz.Backend;

import com.projects.sebastian.Smuuz.Backend.PlaybackService.PlaybackState;


class PlaybackController implements Runnable {
	public enum PlaybackEvent {
		none,
        prepare,
		pauseSong,
		stopSong,
		startSong,
		endSong,
		songStopped
	};
	
	private PlaybackEvent event = PlaybackEvent.none;
	private String filename;
	
	private Trigger controllerTrigger = new Trigger();
	private Trigger eventSetterTrigger = new Trigger();
	private Trigger songStarterTrigger = new Trigger();
	
	private Player player;
	private PlaybackState playback = PlaybackState.stop;
	
	
	private void startSong(String filename)
	{
		// End possibly existing old song
		endSong();
		
		// Create new song
		player = new Player(this, filename, songStarterTrigger);
		
		// Create new song thread
        final Thread playerThread = new Thread(player);
        playerThread.start();
        
        // Wait for thread to finish initializing
        songStarterTrigger.pauseThread();
        
        // Start it
        player.setState(PlaybackState.play);
        setPlaybackState(PlaybackState.play);
	}
	
	private void endSong()
	{
		if((getPlaybackState() != PlaybackState.none) && (player != null))
		{
			// Make thread finish
			player.setState(PlaybackState.end);
			
			// Wait for thread to finish
			songStarterTrigger.pauseThread();
			setPlaybackState(PlaybackState.none);
		}
	}
	
	public synchronized void setPlaybackState(PlaybackState state) {
		playback = state;
	}
	
	public PlaybackState getPlaybackState() {
		return playback;
	}
	
	/**
	 * 
	 * @param event
	 */
	public synchronized void setEvent(PlaybackEvent event, String filename) {
		// Wait if controller thread is running
		if(!controllerTrigger.isWaiting())
			eventSetterTrigger.pauseThread();
		
		// Player handler is ready to work
		this.event = event;
		this.filename = filename;
		controllerTrigger.resumeThread();
	}
	
	@Override
	public void run() {
		// We're important
		//android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
		
		do
		{
			controllerTrigger.pauseThread();
			
			switch(event)
			{
				case prepare:
					if((getPlaybackState() != PlaybackState.play) && (player != null))
					{
						player.setState(PlaybackState.play);
						setPlaybackState(PlaybackState.play);
					}
				break;
				
				case pauseSong:
					if((getPlaybackState() != PlaybackState.pause) && (player != null))
					{
						player.setState(PlaybackState.pause);
						setPlaybackState(PlaybackState.pause);
					}
				break;
					
				case stopSong:
					if((getPlaybackState() != PlaybackState.stop) && (player != null))
					{
						player.setState(PlaybackState.stop);
						setPlaybackState(PlaybackState.stop);
					}
				break;
					
				case startSong:
					startSong(filename);
				break;
				
				case endSong:
					endSong();
				break;
				
				case songStopped:
					setPlaybackState(PlaybackState.none);
					player = null;
					PlaybackService.databaseHelper.markSongCurrPlayed(false, filename);
				break;
			}
			
			eventSetterTrigger.resumeThread();
		}while(true);
	}
}
