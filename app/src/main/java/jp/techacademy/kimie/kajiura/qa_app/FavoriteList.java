package jp.techacademy.kimie.kajiura.qa_app;

import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;


public class FavoriteList extends BaseAdapter{

    private ListView mListView;
    private ArrayList<Question> mQuestionArrayList;
    private QuestionsListAdapter mAdapter;
    private DatabaseReference mFavoriteRef;
    private Question mQuestion;
    private DatabaseReference mDatabase;

    //@Override
    //protected void onCreate(Bundle savedInstanceState) {
    //    super.onCreate(savedInstanceState);

     //   mDatabase = FirebaseDatabase.getInstance().getReference();

    //    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

    //    mDatabase.child(Const.FavoritePATH).child(String.valueOf(user.getUid())).addListenerForSingleValueEvent(
     //           new ValueEventListener() {
     //               @Override
     //               public void onDataChange(DataSnapshot dataSnapshot) {

     //               }

     //               @Override
     //               public void onCancelled(DatabaseError databaseError) {

    //                }
    //            }
    //    )
    //}



    @Override
    public int getCount() {
        return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return null;
    }


    private ChildEventListener mFavoriteListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s){

        }

        @Override
        public void onChildChanged(DataSnapshot dataSnapshot,String s) {

        }

        @Override
        public void onChildRemoved(DataSnapshot dataSnapshot) {

        }

        @Override
        public void onChildMoved(DataSnapshot dataSnapshot,String s) {

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }


    };



}


