package jp.techacademy.kimie.kajiura.qa_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.ColorSpace;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.lang.String;

public class QuestionDetailActivity extends AppCompatActivity  implements View.OnClickListener,DatabaseReference.CompletionListener{

    private int favoriteFL;
    private FloatingActionButton mFavoriteButton;

    DatabaseReference mDataBaseReference;
    private int mGenre;
    private ProgressDialog mProgress;

    private ListView mListView;
    private Question mQuestion;
    private QuestionDetailListAdapter mAdapter;

    private DatabaseReference mAnswerRef;
    private DatabaseReference mFavoriteRef;

    private ChildEventListener mEventListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot, String s){
            HashMap map = (HashMap) dataSnapshot.getValue();


            String answerUid = dataSnapshot.getKey();

            for(Answer answer : mQuestion.getAnswers()) {
                //同じAnswerUidのものが存在する時は何もしない
                if (answerUid.equals(answer.getAnswerUid())) {
                    return;
                }
            }

            String body = (String) map.get("body");
            String name = (String) map.get("name");
            String uid = (String) map.get("uid");

            Answer answer = new Answer(body,name,uid,answerUid);
            mQuestion.getAnswers().add(answer);
            mAdapter.notifyDataSetChanged();

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

    private ChildEventListener mFavoriteListener = new ChildEventListener() {
        @Override
        public void onChildAdded(DataSnapshot dataSnapshot ,String s){
            HashMap map = (HashMap) dataSnapshot.getValue();

            String genre = (String) map.get("getQuestionUid");

            String questionUid = dataSnapshot.getKey();

            for(Question question : mQuestion.getQuestion()) {
                if (questionUid.equals(question.getQuestionUid())) {
                    favoriteFL = 1;
                }else{
                    favoriteFL = 0;
                }
            }

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);

        mFavoriteButton = (FloatingActionButton) findViewById(R.id.favorite);
        mFavoriteButton.setOnClickListener(this);

        mFavoriteButton.setBackgroundTintList(ColorStateList.valueOf(Color.GRAY));

        // 渡ってきたQuestionのオブジェクトを保持する
        Bundle extras = getIntent().getExtras();
        mQuestion = (Question) extras.get("question");
        mGenre = extras.getInt("genre");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null) {
            //ログインしていない：お気に入りボタン不可視
            mFavoriteButton.setVisibility(View.INVISIBLE);

        }else{
            //ログイン済：お気に入りボタン可視
            mFavoriteButton.setVisibility(View.VISIBLE);
        }

        setTitle(mQuestion.getTitle());

        //ListViewの準備
        mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new QuestionDetailListAdapter(this,mQuestion);
        mListView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();

        FloatingActionButton fab =(FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //ログイン済のユーザー取得
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                if (user == null) {
                    //ログインしていなければログイン画面に遷移
                    Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                }else{
                    //Questionを渡して回答作成画面を起動
                    Intent intent = new Intent(getApplicationContext(),AnswerSendActivity.class);
                    intent.putExtra("question",mQuestion);
                    startActivity(intent);
                }
            }
        });

        final DatabaseReference dataBaseReference = FirebaseDatabase.getInstance().getReference();
        mAnswerRef = dataBaseReference.child(Const.ContentsPATH).child(String.valueOf(mQuestion.getGenre())).child(mQuestion.getQuestionUid()).child(Const.AnswersPATH);
        mAnswerRef.addChildEventListener(mEventListener);

        if (user != null) {
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
            mFavoriteRef = databaseReference.child(Const.FavoritePATH).child(String.valueOf(user.getUid())).child(String.valueOf(mQuestion.getQuestionUid()));
            mFavoriteRef.addChildEventListener(mFavoriteListener);
        }

    }

    @Override
    public void onClick(View v) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        mFavoriteRef = databaseReference.child(Const.FavoritePATH).child(String.valueOf(user.getUid())).child(String.valueOf(mQuestion.getQuestionUid()));
        if (favoriteFL == 0 ){
            Map<String, String> data = new HashMap<String, String>();
            data.put("ジャンル",String.valueOf(mQuestion.getGenre()));
            mFavoriteRef.setValue(data, this);
        }else{
            mFavoriteRef.removeValue();
        }

    }

    @Override
    public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
        if (databaseError == null) {
            Snackbar.make(findViewById(android.R.id.content),"お気に入りに追加しました", Snackbar.LENGTH_LONG).show();
            finish();

        }else {
            Snackbar.make(findViewById(android.R.id.content),"お気に入り追加に失敗しました", Snackbar.LENGTH_LONG).show();
        }
    }
}
