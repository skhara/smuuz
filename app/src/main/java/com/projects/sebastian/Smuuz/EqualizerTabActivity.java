package com.projects.sebastian.Smuuz;

import android.app.Activity;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class EqualizerTabActivity extends Activity {
	private static boolean enabled = false;
	private static int bassVol = 10;
	private static int midVol = 10;
	private static int trebleVol = 10;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.equalizertab);

        
        /**
         * Enable/Disable equalizer
         */
        final CheckBox checkBoxEnable = (CheckBox) findViewById(R.id.EqualizerCheckBoxEnable);
        checkBoxEnable.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				enabled = isChecked;
				
				if(isChecked)
				{
					try 
					{
						double dVol = bassVol;
						dVol /= 10;
						
						((GlobalState) getApplicationContext()).setEQ(0, dVol);
						
						dVol = midVol;
						dVol /= 10;
						
						((GlobalState) getApplicationContext()).setEQ(1, dVol);
						
						dVol = trebleVol;
						dVol /= 10;
						
						for(int i = 2; i < 32; i++)
							((GlobalState) getApplicationContext()).setEQ(i, dVol);
						
					} catch (RemoteException re) {
						// TODO:
					}
				}
				else
				{ 
					try 
					{
						((GlobalState) getApplicationContext()).resetEQ();
					} catch (RemoteException re) {
						// TODO:
					}
				}
			}
        });
        
        
        /**
         * Set current equalizer
         */
        final TextView textBassSt = (TextView) findViewById(R.id.EqualizerTextView1);
		textBassSt.setText(Integer.valueOf(bassVol).toString());
		
		final TextView textMidSt = (TextView) findViewById(R.id.EqualizerTextView2);
		textMidSt.setText(Integer.valueOf(midVol).toString());
		
		final TextView textTrebleSt = (TextView) findViewById(R.id.EqualizerTextView3);
		textTrebleSt.setText(Integer.valueOf(trebleVol).toString());
		
        /**
		 * Bass
		 */
		final Button buttonBassUp = (Button) findViewById(R.id.EqualizerButtonUp1);
		buttonBassUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(bassVol < 30)
				{
					bassVol += 1;
					try 
					{
						double dVol = bassVol;
						dVol /= 10;
						
						if(enabled)
							((GlobalState) getApplicationContext()).setEQ(0, dVol);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				final TextView textBass = (TextView) findViewById(R.id.EqualizerTextView1);
				textBass.setText(Integer.valueOf(bassVol).toString());
			}
        });
		
		final Button buttonBassDown = (Button) findViewById(R.id.EqualizerButtonDown1);
		buttonBassDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(bassVol > 0)
				{
					bassVol -= 1;
					try 
					{
						double dVol = bassVol;
						dVol /= 10;
						
						if(enabled)
							((GlobalState) getApplicationContext()).setEQ(0, dVol);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				final TextView textBass = (TextView) findViewById(R.id.EqualizerTextView1);
				textBass.setText(Integer.valueOf(bassVol).toString());
			}
        });
		
		/**
		 * Mid
		 */
		final Button buttonMidUp = (Button) findViewById(R.id.EqualizerButtonUp2);
		buttonMidUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(midVol < 30)
				{
					midVol += 1;
					try 
					{
						double dVol = midVol;
						dVol /= 10;
						
						if(enabled)
							((GlobalState) getApplicationContext()).setEQ(1, dVol);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				final TextView textMid = (TextView) findViewById(R.id.EqualizerTextView2);
				textMid.setText(Integer.valueOf(midVol).toString());
			}
        });
		
		final Button buttonMidDown = (Button) findViewById(R.id.EqualizerButtonDown2);
		buttonMidDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(midVol > 0)
				{
					midVol -= 1;
					try 
					{
						double dVol = midVol;
						dVol /= 10;
						
						if(enabled)
							((GlobalState) getApplicationContext()).setEQ(1, dVol);
					} catch (RemoteException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
				
				final TextView textMid = (TextView) findViewById(R.id.EqualizerTextView2);
				textMid.setText(Integer.valueOf(midVol).toString());
			}
        });
		
		/**
		 * Treble
		 */
		final Button buttonTrebleUp = (Button) findViewById(R.id.EqualizerButtonUp3);
		buttonTrebleUp.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(trebleVol < 30)
				{
					trebleVol += 1;
					
					double dVol = trebleVol;
					dVol /= 10;
					
					if(enabled)
					{
						for(int i = 2; i < 32; i++)
							try 
							{
								((GlobalState) getApplicationContext()).setEQ(i, dVol);
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
				}
				
				final TextView textTreble = (TextView) findViewById(R.id.EqualizerTextView3);
				textTreble.setText(Integer.valueOf(trebleVol).toString());
			}
        });
		
		final Button buttonTrebleDown = (Button) findViewById(R.id.EqualizerButtonDown3);
		buttonTrebleDown.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(trebleVol > 0)
				{
					trebleVol -= 1;
					
					double dVol = trebleVol;
					dVol /= 10;
					
					if(enabled)
					{
						for(int i = 2; i < 32; i++)
							try 
							{
								((GlobalState) getApplicationContext()).setEQ(i, dVol);
							} catch (RemoteException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
					}
				}
				
				final TextView textTreble = (TextView) findViewById(R.id.EqualizerTextView3);
				textTreble.setText(Integer.valueOf(trebleVol).toString());
			}
        });
	}
}
