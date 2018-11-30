package com.rebirth.hustle;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.adroitandroid.chipcloud.ChipCloud;
import com.adroitandroid.chipcloud.ChipListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TagActivity extends AppCompatActivity implements View.OnClickListener {
FloatingActionButton doneButton;
List<String>empTags=new ArrayList<>();
Context context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picker);
        context=this;
        final List<String> tags = Arrays.asList(getResources().getStringArray(R.array.specialities));
doneButton=findViewById(R.id.done_button);
doneButton.setOnClickListener(this);
        ChipCloud chipCloud = (ChipCloud) findViewById(R.id.chip_cloud);
                chipCloud.setMode(ChipCloud.Mode.MULTI);

        chipCloud.addChips((String[]) tags.toArray());
chipCloud.setChipListener(new ChipListener() {
    @Override
    public void chipSelected(int i) {
if(!empTags.contains(tags.get(i))){
    empTags.add(tags.get(i));
}
/*if(empTags.size()>=5){
    doneButton.setVisibility(View.VISIBLE);
}*/
    }

    @Override
    public void chipDeselected(int i) {
empTags.remove(tags.get(i));
    }
});
    }
    private void createUser(String ID,String name,String email,List<String> tags){
        Employee employee=new Employee(ID,name,email,tags,"photo");

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference();

        myRef.child("Users").child(ID).setValue(employee);
    }

    @Override
    public void onClick(View v) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
createUser(user.getUid(),user.getDisplayName(),user.getEmail(),empTags);
startActivity(new Intent(context,AccountActivity.class).putExtra("ID",user.getUid()));
    }
}
