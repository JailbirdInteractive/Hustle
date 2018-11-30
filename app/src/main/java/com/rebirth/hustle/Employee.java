package com.rebirth.hustle;

import java.util.ArrayList;
import java.util.List;

public class Employee {
    String empName,empID,empEmail,photoUrl;
    List<String> empTags;
    List<Job>jobs=new ArrayList<>();

    public Employee(String ID,String name,String email, List<String>tags,String url){
        this.empID=ID;
        this.empName=name;
        this.empTags=tags;
        this.empEmail=email;
        this.photoUrl=url;

    }

    public Employee(){
        //Empty constructor for firebase
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public List<String> getEmpTags() {
        return empTags;
    }

    public String getEmpEmail() {
        return empEmail;
    }

    public String getEmpID() {
        return empID;
    }

    public String getEmpName() {
        return empName;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }
    @Override
    public boolean equals(Object obj) {
        boolean isMatch=false;
        if(obj!=null&&obj instanceof Employee){
            Employee a=(Employee)obj;
            isMatch=a.empID.equalsIgnoreCase(this.empID);
        }
        return isMatch;
    }

    @Override
    public int hashCode() {

        int result = 17;

        //hash code for checking rollno
        //result = 31 * result + (this.s_rollNo == 0 ? 0 : this.s_rollNo);

        //hash code for checking fname
        result = 31 * result + (this.empID == null ? 0 : this.empID.hashCode());

        return result;    }
}
