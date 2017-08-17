package com.haski.swifto.service;

import android.location.Location;

public interface IWalkGPSLocationNotifiable {
	boolean locationChanged(Location location);
}
