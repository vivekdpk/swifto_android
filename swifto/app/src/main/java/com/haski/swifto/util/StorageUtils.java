package com.haski.swifto.util;

import java.io.File;

import android.os.Environment;
import android.util.Log;

import static android.content.ContentValues.TAG;

public class StorageUtils {
	
	public static final String IMAGES_DIRECTORY = "swiftoTokenPhotos";

	public static boolean hasStorage(boolean requireWriteAccess) {
	    //TODO: After fix the bug,  add "if (VERBOSE)" before logging errors.
	    String state = Environment.getExternalStorageState();
	    //Log.v(TAG, "storage state is " + state);
	    //Log.v("nkp", "storage state is " + state);

	    if (Environment.MEDIA_MOUNTED.equals(state)) {
	        if (requireWriteAccess) {
	            boolean writable = checkFsWritable();
	            //Log.v(TAG, "storage writable is " + writable);
	            return writable;
	        } else {
	            return true;
	        }
	    } else if (!requireWriteAccess && Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
	        return true;
	    }
	    return false;
	}
	
	private static boolean checkFsWritable() {
        // Create a temporary file to see whether a volume is really writeable.
        // It's important not to put it in the root directory which may have a
        // limit on the number of files.
        String directoryName = Environment.getExternalStorageDirectory().toString() + "/DCIM";
        File directory = new File(directoryName);
        if (!directory.isDirectory()) {
            if (!directory.mkdirs()) {
                return false;
            }
        }
        return directory.canWrite();
    }
	
}
