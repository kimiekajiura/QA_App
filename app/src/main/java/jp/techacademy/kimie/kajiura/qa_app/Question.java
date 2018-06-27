package jp.techacademy.kimie.kajiura.qa_app;

import java.io.Serializable;
import java.util.ArrayList;

//Intentでデータを渡せるようにSerializableクラスを実装
public class Question implements Serializable{

    //FireBaseから取得したタイトル
    private String mTitle;

    //Firebaseから取得した質問本文
    private String mBody;

    //Firebaseから取得した質問者名
    private String mName;

    //Firebaseから取得した質問者のUID
    private String mUid;

    //Firebaseから取得した質問のUID
    private String mQuestionUid;

    //質問ジャンル
    private int mGenre;

    //Firebaseから取得した画像をbyte型の配列にしたもの
    private byte[] mBitmapArray;

    //Firebaseから取得した質問のモデルクラスであるAnswerのArrayList
    private ArrayList<Answer> mAnswerArrayList;

    public String getTitle() {
        return mTitle;
    }

    public String getBody() {
        return mBody;
    }

    public String getName() {
        return mName;
    }

    public String getUid() {
        return mUid;
    }

    public String getQuestionUid() {
        return mQuestionUid;
    }

    public int getGenre() {
        return mGenre;
    }

    public byte[] getImageBytes() {
        return mBitmapArray;
    }

    public ArrayList<Answer> getAnswers() {
        return mAnswerArrayList;
    }

    public Question(String title,String body,String name,String uid,String questionUid,int genre,byte[] bytes,ArrayList<Answer> answers) {
        mTitle = title;
        mBody = body;
        mName = name;
        mUid = uid;
        mQuestionUid = questionUid;
        mGenre = genre;
        mBitmapArray = bytes.clone();
        mAnswerArrayList = answers;
    }
}
