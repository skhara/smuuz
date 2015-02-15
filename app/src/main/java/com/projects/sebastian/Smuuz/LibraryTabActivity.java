package com.projects.sebastian.Smuuz;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.provider.MediaStore;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.SimpleCursorAdapter.ViewBinder;

public class LibraryTabActivity extends Activity {
	//private static AlbumArtUtils albumUtils = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.librarytab);
        
        /*
		 * Build list of all existing audio files
		 */
		String[] PROJECTION = new String[] { MediaStore.Audio.Media._ID, 
				MediaStore.Audio.Media.TITLE, MediaStore.Audio.Media.ARTIST }; 
		
		/*
		 * Some audio may be explicitly marked as not being music
		 */
		String SELECTION = MediaStore.Audio.Media.IS_MUSIC + " != 0";
		
		Cursor c = managedQuery(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, PROJECTION, SELECTION, null, 
				MediaStore.Audio.Media.ARTIST);
		if(c != null && c.moveToFirst())
		{
			SimpleCursorAdapter listAdapter = new SimpleCursorAdapter(this, android.R.layout.simple_list_item_1, 
				c, new String[] { MediaStore.Audio.Media.ARTIST }, 
				new int[] { android.R.id.text1 });
		
			final ListView listAudioFiles = (ListView) findViewById(R.id.ListViewLibrary);
			listAdapter.setViewBinder(new ViewBinder() {
				@Override
				public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
					int columnArtist = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
					/*
					int columnAlbumArt = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ART);
					if(columnIndex == columnAlbumArt)
					{
						((ImageView) view).setImageResource(R.drawable.play);
						return true;
					}
					*/
					if(columnIndex == columnArtist)	
					{
						String text = cursor.getString(columnArtist);
						if(text.equals("<unknown>"))
							text = "";
						else
							text += " - ";
						
						text += cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
						((TextView) view).setText(text);
						return true;
					}
					return false;
				}
			});
			
			/**
			 * React on clicks on a song item
			 */
			listAudioFiles.setOnItemClickListener(new OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					Uri uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
			        Uri selectedUri = ContentUris.withAppendedId(uri, id);
			        
			        String[] songProjection = new String[] { MediaStore.Audio.Media.ARTIST, 
			        		MediaStore.Audio.Media.ALBUM, MediaStore.Audio.Media.TITLE,
			        		MediaStore.Audio.Media.DATA };
			        
			        Cursor cFile = managedQuery(selectedUri, songProjection, null, null, null);
			        if(cFile != null && cFile.moveToFirst() )
			        {
			        	AlertDialog.Builder dialog = new AlertDialog.Builder(LibraryTabActivity.this);
			        	//dialog.setMessage();
			        	
			        	try 
			        	{
			        		// Play this song
			        		((GlobalState) getApplicationContext()).PlaySong(cFile.getString(3));
						
			        		// TODO:
			        		boolean b = SmuuzActivity.databaseHelper.insertSong(cFile.getString(0), cFile.getString(1),
			        				cFile.getString(2), cFile.getString(3));
			        		
			        	}catch(RemoteException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
			        }
				}
			});
			listAudioFiles.setAdapter(listAdapter);
		}
	}
}
