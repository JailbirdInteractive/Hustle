package com.rebirth.hustle;

import java.util.List;

public class Job {

    public String jobName;
    public List<String>jobTags;
    String jobID;

    public Job(String ID,String name,List<String>tags){
        this.jobName=name;
        this.jobTags=tags;
        this.jobID=ID;
    }
    public Job(){
        //Empty constructor for Firebase
    }

    public List<String> getJobTags() {
        return jobTags;
    }

    public String getJobName() {
        return jobName;
    }

    public String getJobID() {
        return jobID;
    }
    @Override
    public boolean equals(Object obj) {
        boolean isMatch=false;
        if(obj!=null&&obj instanceof Job){
            Job a=(Job) obj;
            isMatch=a.jobID.equalsIgnoreCase(this.jobID);
        }
        return isMatch;
    }

    @Override
    public int hashCode() {

        int result = 17;

        //hash code for checking rollno
        //result = 31 * result + (this.s_rollNo == 0 ? 0 : this.s_rollNo);

        //hash code for checking fname
        result = 31 * result + (this.jobID == null ? 0 : this.jobID.hashCode());

        return result;    }
}
