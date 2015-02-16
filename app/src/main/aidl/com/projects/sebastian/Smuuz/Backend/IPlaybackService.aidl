package com.projects.sebastian.Smuuz.Backend;

interface IPlaybackService {
	   void prepare();
	   void pause();
	   void stop();
	   void start(String filename);
	   void setEQ(int channel, double vol);
	   void resetEQ();
}