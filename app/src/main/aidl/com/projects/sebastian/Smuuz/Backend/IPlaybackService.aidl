package com.projects.sebastian.Smuuz.Backend;

interface IPlaybackService {
	   void PlayPlayback();
	   void PausePlayback();
	   void StopPlayback();
	   void PlaySong(String filename);
	   void setEQ(int channel, double vol);
	   void resetEQ();
}