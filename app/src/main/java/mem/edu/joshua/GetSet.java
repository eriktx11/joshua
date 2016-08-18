package mem.edu.joshua;

/**
 * Created by erikllerena on 8/14/16.
 */
public class GetSet {

    private double latitude;
    private double longitude;


    public void setLat(double lat){
        this.latitude = lat;
    }
    public void setLon(double lon){
        this.longitude = lon;
    }


    public double getLat(){
        return this.latitude;
    }
    public double getLon(){
        return this.longitude;
    }

}
