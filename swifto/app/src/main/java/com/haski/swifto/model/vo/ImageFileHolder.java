package com.haski.swifto.model.vo;

import java.io.File;

/**
 * Created by viku on 4/30/2016.
 */
public class ImageFileHolder {


    String status;
    File file;

    public ImageFileHolder(String status, File file) {
        this.status = status;
        this.file = file;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public File getFile() {

        status = "Processed";
        setStatus(status);

        return file;
    }


    public File getFileWithoutStatusChanged() {


        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    @Override
    public String toString() {
        return "ImageFileHolder{" +
                "status='" + status + '\'' +
                ", file=" + file +
                '}';
    }
}
