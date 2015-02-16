package com.projects.sebastian.Smuuz;

import com.projects.sebastian.Smuuz.Database.DatabaseColumns;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class PlayerTabActivity extends Activity {
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.playertab);

        SmuuzActivity.databaseHelper.getExistingPlaylists();
        
        /*
         * Query our database for all song assigned to playing playlist.
         * Furthermore identify, if exists, currently played song.
         */
        String[] PROJECTION = new String[] { DatabaseColumns._ID, DatabaseColumns.SONG_PLAYING, 
        		DatabaseColumns.SONG_ARTIST, DatabaseColumns.SONG_ALBUM, DatabaseColumns.SONG_TITLE, 
        		DatabaseColumns.SONG_PATH };
        
        final Cursor c = SmuuzActivity.databaseHelper.queryAllPlayingSongs(PROJECTION);
        if(c != null && c.moveToFirst())
        {
        	SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, 
				c, new String[] { DatabaseColumns.SONG_ARTIST }, new int[] { android.R.id.text1 });
		
        	/*
        	 * Build list view, showing all song assigned to playlist playing
        	 */
			final ListView listAudioFiles = (ListView) findViewById(R.id.ListViewPlayingSongs);
			listAdapter.setViewBinder(new ViewBinder() {
				@Override
				public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
					int columnArtist = cursor.getColumnIndex(DatabaseColumns.SONG_ARTIST);
					/*int columnAlbumArt = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ART);
					
					if(columnIndex == columnAlbumArt)
					{
						((ImageView) view).setImageResource(R.drawable.play);
						return true;
					}
					*/
					if(columnIndex == columnArtist)	
					{
						String text = cursor.getString(columnArtist);
						text += " - " + cursor.getString(cursor.getColumnIndex(DatabaseColumns.SONG_TITLE));
						
						((TextView) view).setText(text);
						return true;
					}
					return false;
				}
			});
			
			/*
			 * React on clicks on a song item
			 */
			listAudioFiles.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					if(c != null && c.moveToFirst())
					{
						do
						{
							// Search for the correct cursor position,
							// according to the given row id.
							if(c.getLong(0) == id)
								break;
						}while(c.moveToNext());
						
				        {
				        	try 
				        	{
				        		// Play this song, and mark as currently played
				        		((App) getApplicationContext()).start(c.getString(5));
				        		SmuuzActivity.databaseHelper.markSongCurrPlayed(true, c.getString(5));
				        	}catch(RemoteException e) {
								// TODO: Auto-generated catch block
								e.printStackTrace();
							}
				        }
					}
				}
			});
			
			// Assign adapter to view
			listAudioFiles.setAdapter(listAdapter);
        }
        
		/**
		 * Start playback
		 */
		final Button buttonStart = (Button) findViewById(R.id.ButtonStart);
        buttonStart.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
		       /*try
		       {
		    	   ((App) getApplicationContext()).prepare();
		       }
		       catch(RemoteException re)
		       {
		    	   Log.e("Error", re.toString());
		       }*/
			}
        });
        
        /**
         * Stop playback
         */
        final Button buttonPause = (Button) findViewById(R.id.ButtonPause);
        buttonPause.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try
				{
					((App) getApplicationContext()).pause();
				}
				catch(RemoteException re)
				{
					Log.e("Error", re.toString());
				}
			}
        });
        
        /**
         * Stop playback
         */
        final Button buttonStop = (Button) findViewById(R.id.ButtonStop);
        buttonStop.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				try
				{
					((App) getApplicationContext()).stop();
				}
				catch(RemoteException re)
				{
					Log.e("Error", re.toString());
				}
			}
        });
	}
}
