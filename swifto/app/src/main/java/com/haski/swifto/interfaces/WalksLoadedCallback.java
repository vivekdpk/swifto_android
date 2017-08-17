package com.haski.swifto.interfaces;

import java.util.ArrayList;

import com.haski.swifto.model.vo.walk.Walk;

public interface WalksLoadedCallback {

	void walksLoaded(ArrayList<Walk> walks, String portionId);
	void interrupt();
}
