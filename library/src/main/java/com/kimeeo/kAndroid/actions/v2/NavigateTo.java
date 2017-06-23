package com.kimeeo.kAndroid.actions.v2;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.kimeeo.kAndroid.actions.v2.BaseAction;

/**
 * Created by bpa001 on 6/21/17.
 */

public class NavigateTo extends BaseAction {
    public NavigateTo(Activity activity) {
        super(activity);
    }
    public void gotoLocation(long latitude,long longitude,String address) {
        try
        {
            String locationURL = null;
            if(latitude!=-1 && longitude!=-1)
                locationURL = "http://maps.google.com/maps?daddr="+latitude+","+longitude+"";
            else if(address!=null)
                locationURL = "http://maps.google.com/maps?daddr="+address;

            if(locationURL!=null) {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(locationURL));
                activity.startActivity(intent);
            }
        }
        catch (Exception e)
        {

        }
    }
    public void gotoLocation(String latitude,String longitude,String address) {
        try
        {
            long latitude1;
            long longitude1;
            if(latitude!=null && longitude!=null) {
                latitude1 = Long.parseLong(latitude);
                longitude1 = Long.parseLong(longitude);
                gotoLocation(latitude1, longitude1, address);
            }
        }
        catch (Exception e){
            gotoLocation(-1, -1, address);}
    }
    public void gotoLatLong(long latitude,long longitude) {
        gotoLocation(latitude,longitude,null);
    }
    public void gotoLatLong(String latitude,String longitude) {
        gotoLocation(latitude,longitude,null);
    }
    public void gotoAddress(String address) {
        gotoLocation(-1, -1, address);
    }

}
