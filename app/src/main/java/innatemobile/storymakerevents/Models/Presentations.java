package innatemobile.storymakerevents.Models;

/**
 * Created by rphovley on 1/31/2016.
 */
public class Presentations {
    private int id;
    private String title;
    private String description;
    private int speaker_id;
    private boolean isIntensive;
    private boolean isKeynote;

    public Presentations(){

    }
    public Presentations(int id, String title, String description, int speaker_id, int isIntensive, int isKeynote) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.speaker_id = speaker_id;
        setIsIntensive(isIntensive);
        setIsKeynote(isKeynote);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getSpeaker_id() {
        return speaker_id;
    }

    public void setSpeaker_id(int speaker_id) {
        this.speaker_id = speaker_id;
    }

    public int isIntensive() {
        if (isIntensive) {
            return 1;
        }else{
            return 0;
        }
    }

    public void setIsIntensiveJSON(String isIntensive) {
        if(isIntensive.toLowerCase().equals("yes")) {
            this.isIntensive = true;
        }else{
            this.isIntensive = false;
        }
    }

    public void setIsIntensive(int isIntensive){
        if(isIntensive == 1){
            this.isIntensive = true;
        }else{
            this.isIntensive = false;
        }
    }

    public int isKeynote() {
        if (isKeynote==true) {
            return 1;
        }else{
            return 0;
        }
    }

    public void setIsKeynote(int isKeynote){
        if(isKeynote == 1){
            this.isKeynote = true;
        }else{
            this.isKeynote = false;
        }
    }
    public void setIsKeynoteJSON(String isKeynote) {
        if(isKeynote.toLowerCase().equals("yes")) {
            this.isKeynote = true;
        }else{
            this.isKeynote = false;
        }
    }
}
