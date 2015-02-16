package com.projects.sebastian.Smuuz;

import java.util.List;

import com.projects.sebastian.Smuuz.Backend.IPlaybackService;
import com.projects.sebastian.Smuuz.Backend.PlaybackService;
import com.projects.sebastian.Smuuz.Backend.PlaybackService.PlaybackState;

import android.app.ActivityManager;
import android.app.Application;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;

public class App extends Application {
	private Intent serviceIntent = null;
	private ServiceConnection serviceConnection = null;
	private IPlaybackService myPlaybackService = null;
	private boolean serviceRunning = false;
	
	private PlaybackState state = PlaybackState.none;
	
	
	public synchronized boolean start(String filepath) throws RemoteException {
		if(filepath == null || filepath.equals("") || myPlaybackService == null)
			return false;
		
		state = PlaybackState.play;
		myPlaybackService.start(filepath);
		return true;
	}

	public synchronized boolean prepare() throws RemoteException {
		if(myPlaybackService == null)
			return false;
		
		state = PlaybackState.play;
		myPlaybackService.prepare();
		return true;
	}
	
	public synchronized boolean pause() throws RemoteException {
		if(myPlaybackService == null)
			return false;
		
		state = PlaybackState.pause;
		myPlaybackService.pause();
		return true;
	}
	
	public synchronized boolean stop() throws RemoteException {
		if(myPlaybackService == null)
			return false;
		
		state = PlaybackState.stop;
		myPlaybackService.stop();
		return true;
	}
	
	public synchronized boolean setEQ(int channel, double vol) throws RemoteException {
		if(myPlaybackService == null)
			return false;
		
		myPlaybackService.setEQ(channel, vol);
		return true;
	}
	
	public synchronized boolean resetEQ() throws RemoteException {
		if(myPlaybackService == null)
			return false;
		
		myPlaybackService.resetEQ();
		return true;
	}
	
	public synchronized void newServiceConnection() {
		serviceIntent = new Intent(this, PlaybackService.class);
		 
		// Check if service is already running
		if(!serviceRunning)
		{
			// Service is not running, start and bind it
			startService(serviceIntent);
			
			serviceConnection = new ServiceConnection() {
	        	@Override
				public void onServiceConnected(ComponentName name, IBinder service) {
					myPlaybackService = IPlaybackService.Stub.asInterface((IBinder) service);
				}
	
				@Override
				public void onServiceDisconnected(ComponentName name) {
					myPlaybackService = null;
				}
			};
			
			bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
			serviceRunning = true;
		}
	}
	
	/**
	 * Indicates whether the specified service is already started. This 
	 * method queries the activity manager for launched services that can
	 * respond to an binding with an specific service name.
	 * If no existed service is found, this method returns null.
	 * 
	 * @param context The context of the activity
	 * @param className The service full name to check for availability.
	 * 
	 * @return ComponentName if the service is already existed, NULL otherwise.
	 */
	public static ComponentName isServiceExisted(Context context, String className)
	{
		ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
		
		List<ActivityManager.RunningServiceInfo> serviceList = 
			activityManager.getRunningServices(Integer.MAX_VALUE);
		
		if(!(serviceList.size() > 0))
		{
			return null;
		}
		
		for(int i = 0; i < serviceList.size(); i++)
		{
			RunningServiceInfo serviceInfo = serviceList.get(i);
			ComponentName serviceName = serviceInfo.service;
			
			if(serviceName.getClassName().equals(className))
			{
				return serviceName;
			}
		}
		return null;
	}
}
