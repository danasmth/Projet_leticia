package model;
import java.util.ArrayList;
import java.util.List;

public class MetaDonnees {

    private String chemin;
    private List<String> tags= new ArrayList<>();
    private List<Transformation> transform=new ArrayList<>();

    public MetaDonnees() {
    }
    public void setChemin(String chemin) {
        this.chemin = chemin;
    }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    public void setTransformations(List<Transformation> transform) {
        this.transform = transform;
    }

    public MetaDonnees(String chemin) {
        this.chemin = chemin;
    }

    public String getChemin() {
        return chemin;
    }
    public List<String> getTags() {
        return tags;
    }
    public List<Transformation> getTransformations(){
        return transform;
    }
    public void ajouterTag(String tag) {
        if(! tags.contains(tag)) {
            tags.add(tag);
        }
    }

    public void ajouterTransformation(Transformation t){
        transform.add(t);
    }
}
