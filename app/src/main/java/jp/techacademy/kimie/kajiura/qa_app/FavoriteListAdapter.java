package jp.techacademy.kimie.kajiura.qa_app;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
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
import java.util.List;
import java.util.Map;

public class FavoriteListAdapter extends AppCompatActivity{

    private DatabaseReference mDatabase;
    private DatabaseReference mQuestionRef;
    private ListView mListView;
    private ArrayList<Question> mQuestionArrayList;
    private QuestionsListAdapter mAdapter;
    private int mGenre;
    private DatabaseReference mFavoriteRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_list_adapter);

        setTitle("お気に入り");

        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new QuestionsListAdapter(this);
        mQuestionArrayList = new ArrayList<Question>();
        mAdapter.notifyDataSetChanged();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Questionのインスタンスを渡して質問詳細画面を起動する
               Intent intent = new Intent(getApplicationContext(), QuestionDetailActivity.class);
                intent.putExtra("question", mQuestionArrayList.get(position));
                startActivity(intent);
            }
        });

        mDatabase = FirebaseDatabase.getInstance().getReference();

    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        //質問のリストをクリアしてから再度Adapterにセットし、AdapterをListViewにセットしなおす
        mQuestionArrayList.clear();
        mAdapter.setQuestionArrayList(mQuestionArrayList);
        mListView.setAdapter(mAdapter);


        ChildEventListener mFavoriteListener = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot ,String s){
                HashMap map = (HashMap) dataSnapshot.getValue();

                String genru = (String) map.get("ジャンル");
                String questionUid = dataSnapshot.getKey();

                //addListenerForSingleValueEvent：データ1つだけの時に使う
                mDatabase.child(Const.ContentsPATH).child(genru).child(questionUid).addListenerForSingleValueEvent(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                HashMap map = (HashMap) dataSnapshot.getValue();

                                String title = (String) map.get("title");
                                String body = (String) map.get("body");
                                String name = (String) map.get("name");
                                String uid = (String) map.get("uid");
                                String imageString =(String) map.get("image");
                                byte[] bytes;
                                if (imageString != null) {
                                    bytes = Base64.decode(imageString, Base64.DEFAULT);
                                }else{
                                    bytes = new byte[0];
                                }

                                ArrayList<Answer> answerArrayList = new ArrayList<Answer>();
                                HashMap answerMap = (HashMap) map.get("answers");
                                if (answerMap != null) {
                                    for (Object key : answerMap.keySet()) {
                                        HashMap temp = (HashMap) answerMap.get((String) key);
                                        String answerBody = (String) temp.get("body");
                                        String answerName = (String) temp.get("name");
                                        String answerUid = (String) temp.get("uid");
                                        Answer answer = new Answer(answerBody,answerName,answerUid,(String) key);
                                        answerArrayList.add(answer);
                                    }
                                }

                                Question question = new Question(title, body, name, uid, dataSnapshot.getKey(),mGenre,bytes,answerArrayList);
                                mQuestionArrayList.add(question);
                                mAdapter.notifyDataSetChanged();
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }
                );
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot,String s) {

            }

            @Override
            public  void onChildRemoved(DataSnapshot dataSnapshot) {

            }
            @Override
            public void onChildMoved(DataSnapshot dataSnapshot,String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        };

        mFavoriteRef = mDatabase.child(Const.FavoritePATH).child(String.valueOf(user.getUid()));
        mFavoriteRef.addChildEventListener(mFavoriteListener);
        return;

    }

}
