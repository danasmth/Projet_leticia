package model;

public class Transformation {
    private String type;
    private String parametre;

    public Transformation(String type, String parametre) {
        this.type = type;
        this.parametre = parametre;

    }
    // obligatoire pour json
    public Transformation() {}

    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }

    public String getParametre() {
        return parametre;
    }
    public void setParametre(String parametre) {
        this.parametre = parametre;
    }



}
