package com.projects.sebastian.Smuuz;

import com.projects.sebastian.Smuuz.Database.DatabaseHelper;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;

public class SmuuzActivity extends TabActivity {
	public static DatabaseHelper databaseHelper = null;
	 
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        
        // Create or open our database backend
        databaseHelper = new DatabaseHelper(this);
        
        
        // Create our service if it doesn't exist already
        ((GlobalState) getApplicationContext()).newServiceConnection();
        
        
        // Build our tabs
        TabHost tabs = getTabHost();
        
        // Player tab
        TabHost.TabSpec playerTab = tabs.newTabSpec("tab_player");
//        playerTab.setIndicator("Player", getResources().getDrawable(R.drawable.play));
        playerTab.setIndicator("Player");
        playerTab.setContent(new Intent(this, PlayerTabActivity.class));
        tabs.addTab(playerTab);
       
        // Equalizer tab
        TabHost.TabSpec equalizerTab = tabs.newTabSpec("tab_equalizer");
        equalizerTab.setIndicator("Equalizer");
        equalizerTab.setContent(new Intent(this, EqualizerTabActivity.class));
        tabs.addTab(equalizerTab);
/*        
        // Playlist tab
        TabHost.TabSpec playlistTab = tabs.newTabSpec("tab_playlist");
        playlistTab.setIndicator("Playlists");
        playlistTab.setContent(new Intent(this, PlaylistsTabActivity.class));
        tabs.addTab(playlistTab);
*/        
        
        // Audio library
        TabHost.TabSpec playerLibrary = tabs.newTabSpec("tab_player");
        playerLibrary.setIndicator("Library");
        playerLibrary.setContent(new Intent(this, LibraryTabActivity.class));
        tabs.addTab(playerLibrary);
        
        
        tabs.setCurrentTab(0);
    }
    
    @Override
    public void onPause() {
    	super.onPause();
    	
    	databaseHelper.Finish();
    }
}
