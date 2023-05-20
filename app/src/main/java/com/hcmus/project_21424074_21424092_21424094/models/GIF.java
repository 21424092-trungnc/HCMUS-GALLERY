package com.hcmus.project_21424074_21424092_21424094.models;

import java.util.List;

public class GIF {
    private String pathPre;
    private String name;
    private List<String> listImage;
    public GIF(String pathPre, String name, List<String> listImage) {
        this.pathPre = pathPre;
        this.name = name;
        this.listImage = listImage;
    }
    public String getPathPre() {
        return pathPre;
    }
    public void setPathPre(String pathPre) {
        this.pathPre = pathPre;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public List<String> getListImage() {
        return listImage;
    }
    public void setListImage(List<String> listImage) {
        this.listImage = listImage;
    }
}
