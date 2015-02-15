package com.projects.sebastian.Smuuz.Backend;

import com.projects.sebastian.Smuuz.Backend.PlaybackHandler.PlaybackEvent;
import com.projects.sebastian.Smuuz.Backend.PlaybackService.PlaybackState;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;


class Playback implements Runnable {
	// Out handler for thread synchronization and control
	private WaitNotify signalControl = new WaitNotify();
	private WaitNotify signalThread = null;
	private PlaybackHandler handler = null;
	
	private AudioTrack track = null;
	private boolean paused = true;
	private boolean ended = false;
	
	private String filename = null;
	private short[] buffer = null;
	
	private final static int MPG123_DONE = -12;
	private final static int MPG123_NEW_FORMAT = -11;
	private final static int MPG123_OK = 0;

	
	public Playback(PlaybackHandler playbackHandler, String inputFilename, WaitNotify signal_thread)
	{
		handler = playbackHandler;
		filename = inputFilename;
		signalThread = signal_thread;
	}

	@Override
	public void run() {
		// We're important
		android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO); 
		
		int err = NativeWrapper.initMP3(filename);
		if (err != MPG123_OK)
		{
			// TODO: Show error
		}

        Log.d("MPG123", "NativeWrapper.initMP3 resulted in  " + err);
		
		AudioFileInformations audioInfo = NativeWrapper.getAudioInformations();
		if(!audioInfo.success)
		{
			// TODO: Show error
		}
		else
		{
			int minBufferSize = AudioTrack.getMinBufferSize((int) audioInfo.rate, 
					AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT);
			
			// Initialize one AudioTrack instance with given values
			track = new AudioTrack(AudioManager.STREAM_MUSIC, (int) audioInfo.rate, 
					AudioFormat.CHANNEL_CONFIGURATION_STEREO, AudioFormat.ENCODING_PCM_16BIT, minBufferSize * 4, 
					AudioTrack.MODE_STREAM);


            Log.d("MPG123", "Created AudioTrack:   " + track.toString());

			// This is our buffer for PCM audio data
			buffer = new short[minBufferSize];

			// Shorten waiting time for the first real audio data
			boolean first = true;
			
			// Inform playback handler that we're ready
			signalThread.doNotify();
			
			/*
			 * This is our "big" playing and decoding loop
			 */
			do
			{
				// Wait until the user wants to play the file
				if(paused && !first)
					signalControl.doWait();
				
				// If user has stopped, jump out immediately
				if(ended)
					break;
				
				// Decode compressed MP3-File via native MPG123 library
				err = NativeWrapper.decodeMP3(minBufferSize * 2, buffer);
				if(err == MPG123_OK || err == MPG123_NEW_FORMAT)
				{
					first = false;
					track.write(buffer, 0, minBufferSize); // Write to output (is blocking if playing!)
				}
				else
					break;	// TODO: Show error A error occurred, jump out immediately
			}while(!ended);
			
			
			// We have finished correctly, play the last frames
			if(err == MPG123_DONE)
			{
				track.flush();
				track.stop();
				
				// Inform playback handler, that we have finished normally
				handler.SetEvent(PlaybackEvent.songStopped, filename);
			}
			
			// Cleanup
			track.release();
			NativeWrapper.cleanupMP3();
			
			// If we've been stopped, inform caller
			if(ended)
				signalThread.doNotify();
		}
	}
	
	public void Control(PlaybackState con)
	{
		// If we have stopped it once, we cannot do anything with it again
		if(ended)
			return;
		
		switch(con)
		{
			case play:	
				paused = false;
				track.play();
				signalControl.doNotify();
			break;
			
			case pause:
				paused = true;
				track.pause();
			break;
				
			case stop:
				paused = true;
				track.flush();
				track.stop();
				NativeWrapper.seekTo(0);	// Start decoding from beginning
			break;
			
			case end:
				ended = true;
				track.stop();
				signalControl.doNotify();
			break;
		}
	}
}
