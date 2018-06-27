package jp.techacademy.kimie.kajiura.qa_app;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    //メンバ変数

    //各ボタンをタップした時に入力されている文字列を取得する
    EditText mEmailEditText;
    EditText mPasswordEditText;
    EditText mNameEditText;

    //ログイン・アカウント作成中にプログレスダイアログ表示
    ProgressDialog mProgress;

    //FireAuthクラス
    FirebaseAuth mAuth;

    //処理の完了を受け取るリスナー（アカウント作成処理）
    OnCompleteListener<AuthResult> mCreateAccountListener;

    //処理の完了を受け取るリスナー（ログイン処理）
    OnCompleteListener<AuthResult> mLoginListener;

    //データベースへの書き込みに必要なクラス
    DatabaseReference mDataBaseReference;

    // アカウント作成時にフラグを立て、ログイン処理後に名前をFirebaseに保存
    boolean mIsCreateAccount = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mDataBaseReference = FirebaseDatabase.getInstance().getReference();

        // FirebaseAuthのオブジェクトを取得する
        mAuth = FirebaseAuth.getInstance();

        // アカウント作成処理のリスナー
        //このクラスは、onCompleteクラスをオーバーライドする必要あり。
        //その中で、引数で渡ってきたTaskクラスのisSuccessfulメソッドで成功したかどうか確認
        mCreateAccountListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // 成功した場合
                    // ログインを行う
                    String email = mEmailEditText.getText().toString();
                    String password = mPasswordEditText.getText().toString();
                    login(email, password);
                } else {
                    // 失敗した場合
                    // エラーを表示する
                    View view = findViewById(android.R.id.content);
                    Snackbar.make(view, "アカウント作成に失敗しました", Snackbar.LENGTH_LONG).show();

                    // プログレスダイアログを非表示にする
                    mProgress.dismiss();
                }
            }
        };
        // ログイン処理のリスナー
        mLoginListener = new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    // 成功した場合
                    FirebaseUser user = mAuth.getCurrentUser();
                    DatabaseReference userRef = mDataBaseReference.child(Const.UsersPATH).child(user.getUid());

                    if (mIsCreateAccount) {
                        // アカウント作成の時は表示名をFirebaseに保存する
                        String name = mNameEditText.getText().toString();


                        Map<String, String> data = new HashMap<String, String>();
                        data.put("name", name);
                        userRef.setValue(data);

                        // 表示名をPrefarenceに保存する
                        saveName(name);
                    } else {
                        //Firebaseからデータを1度だけ取得する場合は、DatabaseReferenceクラスが実装する
                        //QueryクラスのaddListenerForSingleValueEventメソッドを使用。
                        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                Map data = (Map) snapshot.getValue();
                                saveName((String) data.get("name"));
                            }

                            @Override
                            public void onCancelled(DatabaseError firebaseError) {
                            }
                        });
                    }
                    // プログレスダイアログを非表示にする
                    mProgress.dismiss();

                    // Activityを閉じる
                    finish();

                } else {
                    // 失敗した場合
                    // エラーを表示する
                    View view = findViewById(android.R.id.content);
                    Snackbar.make(view, "ログインに失敗しました", Snackbar.LENGTH_LONG).show();

                    // プログレスダイアログを非表示にする
                    mProgress.dismiss();
                }
            }
        };

        //UIの準備
        setTitle("ログイン");

        //インスタンスをメンバ変数に保持
        mEmailEditText = (EditText) findViewById(R.id.emailText);
        mPasswordEditText = (EditText) findViewById(R.id.passwordText);
        mNameEditText = (EditText) findViewById(R.id.nameText);

        mProgress = new ProgressDialog(this);
        mProgress.setMessage("処理中・・・");

        Button createButton = (Button) findViewById(R.id.createButton);
        createButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //キーボードが出ていたら閉じる
                InputMethodManager im = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(v.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                String email = mEmailEditText.getText().toString();
                String password = mPasswordEditText.getText().toString();
                String name = mNameEditText.getText().toString();

                if (email.length() != 0 && password.length() >= 6 && name.length() != 0) {
                    //ログイン時に表示名を保存するようにフラグを立てる
                    mIsCreateAccount = true;

                    createAccount(email, password);
                } else {
                    //エラー表示
                    Snackbar.make(v, "正しく入力してください", Snackbar.LENGTH_LONG).show();
                }
            }
        });

        Button loginButton =(Button) findViewById(R.id.loginButton);
        loginButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //キーボードがでていたら閉じる
                InputMethodManager im = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                im.hideSoftInputFromWindow(v.getWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);

                String email = mEmailEditText.getText().toString();
                String passWord = mPasswordEditText.getText().toString();

                if (email.length() != 0 && passWord.length() >= 6){
                    //フラグを落としておく
                    mIsCreateAccount = false;

                    login(email,passWord);
                }else{
                    //エラー表示
                    Snackbar.make(v,"正しく入力してください",Snackbar.LENGTH_LONG).show();
                }
            }
        });
    }

    private void createAccount (String email, String password) {
        //プログレスダイアログを表示
        mProgress.show();

        //ｱｶｳﾝﾄを作成
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(mCreateAccountListener);
    }

    private void login(String email,String password){
        //プログレスを表示
        mProgress.show();

        //ログイン
        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(mLoginListener);
    }

    private void saveName(String name){
        //Preferenceに保存
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(Const.NameKEY,name);
        editor.commit();
    }
}
