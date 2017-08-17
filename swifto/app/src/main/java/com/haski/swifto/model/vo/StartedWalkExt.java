package com.haski.swifto.model.vo;

public class StartedWalkExt {

	public String WalkId;
	
	/**In milliseconds*/
	public long StartedTime;

	public boolean Completed;
	public boolean MessageSent;
	public boolean PhotoUploaded;
	public boolean PhotoSkipped;
	public boolean PhotoUploadTriedOnce;
	public boolean RemindersShownOnce;
	public boolean StopWalkSent;
	public boolean UnsentDataSent;
	public boolean UnsentDataSkipped;
	
	public void makeDefault() {
		Completed = MessageSent = PhotoSkipped = PhotoUploaded = PhotoUploadTriedOnce = StopWalkSent = UnsentDataSent = UnsentDataSkipped = RemindersShownOnce = false;
	}
}
