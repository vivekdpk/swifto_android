package com.haski.swifto.interfaces;

import android.location.Location;

public interface IOneTimeGPSSearchNotifiable {

	void locationReceiveSuccess(Location location);
	void locationReceiveFail(String reason);
}
