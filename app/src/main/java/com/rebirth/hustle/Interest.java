package com.rebirth.hustle;


import java.util.List;

/**
 * Created by adria on 11/15/2016.
 */

public class Interest {
    public String interestName;
    public List<String>placeTypes;

    public Interest() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Interest(String interstName, List<String>placeTypes) {
        this.interestName=interstName;
        this.placeTypes=placeTypes;
    }

    public List<String> getPlaceTypes() {
        return placeTypes;
    }

    public String getInterestName() {
        return interestName;
    }

    @Override
    public boolean equals(Object obj) {
        boolean isMatch=false;
        if(obj!=null&&obj instanceof Interest){
            Interest a=(Interest)obj;
            isMatch=a.interestName.equalsIgnoreCase(this.interestName);
        }
        return isMatch;
    }

    @Override
    public int hashCode() {

        int result = 17;

        //hash code for checking rollno
        //result = 31 * result + (this.s_rollNo == 0 ? 0 : this.s_rollNo);

        //hash code for checking fname
        result = 31 * result + (this.interestName == null ? 0 : this.interestName.hashCode());

        return result;    }
}
