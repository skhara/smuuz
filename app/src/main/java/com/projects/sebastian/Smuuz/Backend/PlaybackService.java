package com.projects.sebastian.Smuuz.Backend;

import com.projects.sebastian.Smuuz.Backend.PlaybackHandler.PlaybackEvent;
import com.projects.sebastian.Smuuz.Database.DatabaseHelper;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

public class PlaybackService extends Service {
	public enum PlaybackState {
		none,
		play,
		pause,
		stop,
		end
	};
	
	public static DatabaseHelper databaseHelper = null;
	private PlaybackHandler playback_handler = null;
	
	
	private final IPlaybackService.Stub myPlaybackServiceStub = new IPlaybackService.Stub() {
		@Override
		public void PlayPlayback() throws RemoteException {
			if(playback_handler != null)
				playback_handler.SetEvent(PlaybackEvent.playSong, null);
		}
		
		@Override
		public void PausePlayback() throws RemoteException {
			if(playback_handler != null)
				playback_handler.SetEvent(PlaybackEvent.pauseSong, null);
		}

		@Override
		public void StopPlayback() throws RemoteException {
			if(playback_handler != null)
				playback_handler.SetEvent(PlaybackEvent.stopSong, null);
		}

		@Override
		public void PlaySong(String filename) throws RemoteException {
			if(playback_handler != null)
				playback_handler.SetEvent(PlaybackEvent.startSong, filename);
		}
		
		@Override
		public void setEQ(int channel, double vol) throws RemoteException {
			NativeWrapper.setEQ(channel, vol);
		}
		
		@Override
		public void resetEQ() throws RemoteException {
			NativeWrapper.resetEQ();
		}
	};
	
	@Override
	public IBinder onBind(Intent intent) {
		return myPlaybackServiceStub;
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		 // Initialize our MPG123 native library (once per process)
        Log.d("MPG123", "Native lib init result is " + NativeWrapper.initLib());

		playback_handler = new PlaybackHandler();
		databaseHelper = new DatabaseHelper(this);
		
		// Start our controlling thread
        final Thread playbackHandlerThread = new Thread(playback_handler);
        playbackHandlerThread.start();
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		
		// Stop our controlling thread
		// TODO:
		
		databaseHelper.Finish();
		
		// Finalize MPG123 native library (once per process)
		NativeWrapper.cleanupLib();
	}
}
