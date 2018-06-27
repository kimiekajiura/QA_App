package jp.techacademy.kimie.kajiura.qa_app;

import java.io.Serializable;

public class Answer implements Serializable {
    //Firebaseから取得した回答本文
    private String mBody;

    //Firebaseから取得した回答者名
    private String mName;

    //Firebaseから取得した回答者のUID
    private String mUid;

    //Firebaseから取得した回答UID
    private String mAnswerUid;

    public Answer(String body, String name, String uid, String answerUid) {
        mBody = body;
        mName = name;
        mUid = uid;
        mAnswerUid = answerUid;
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

    public String getAnswerUid() {
        return mAnswerUid;
    }
}
