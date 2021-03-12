package com.smilias.smarket;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LoginFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LoginFragment extends Fragment implements View.OnClickListener {
    private FirebaseUser cUser;
    private Button btnLogInOut, btnLangChange, btnSpeech, btnVoice, btnMaps;
    private SharedPreferences preferences;
    private String language;
    private Locale locale;
    private TextToSpeech tts;

    private static final int REC_RESULT = 653;

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnLogInOut:
                login();
                break;
            case R.id.btnLangChange:
                lang_change();
                break;
            case R.id.btnSpeech:
                speech();
                break;
            case R.id.btnVoice:
                recognize();
                break;
            case R.id.btnMaps:
                Intent intent = new Intent(getActivity(), MapsActivity.class);
                startActivity(intent);
                break;
        }
    }

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public LoginFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LoginFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LoginFragment newInstance(String param1, String param2) {
        LoginFragment fragment = new LoginFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        tts = new TextToSpeech(getActivity(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });

        cUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_login, container, false);
        // Inflate the layout for this fragment
        btnLogInOut=v.findViewById(R.id.btnLogInOut);
        btnLogInOut.setOnClickListener(this);

        btnLangChange=v.findViewById(R.id.btnLangChange);
        btnLangChange.setOnClickListener(this);

        btnSpeech=v.findViewById(R.id.btnSpeech);
        btnSpeech.setOnClickListener(this);

        btnVoice=v.findViewById(R.id.btnVoice);
        btnVoice.setOnClickListener(this);

        btnMaps=v.findViewById(R.id.btnMaps);
        btnMaps.setOnClickListener(this);

        preferences = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE);
        language  = preferences.getString("lang","en");

        if (cUser != null)  btnLogInOut.setText(getString(R.string.sign_out).toUpperCase());
        else btnLogInOut.setText(getString(R.string.button_login).toUpperCase());
        return v;
    }


    private void login() {
        if (cUser == null) {
            Intent intent2 = new Intent(getActivity(), LogInActivity.class);
            startActivity(intent2);
        }
        else{
            FirebaseAuth.getInstance().signOut();
            cUser =null;
            getActivity().getSupportFragmentManager().beginTransaction()
                    .replace(R.id.flFragment, new LoginFragment(), "findThisFragment")
                    .commit();
        }
    }

    private void lang_change(){
        SharedPreferences.Editor editor = getActivity().getSharedPreferences("MyPref", MODE_PRIVATE).edit();
        if (language.equals("el")) {
            editor.putString("lang", "en");
            locale = new Locale("en");
            language="en";
            Toast.makeText(getActivity(),"Language changed to english",Toast.LENGTH_SHORT).show();
        }
        else {
            editor.putString("lang", "el");
            locale = new Locale("el");
            language="el";
            Toast.makeText(getActivity(),"Η γλώσσα άλλαξε σε ελληνικά",Toast.LENGTH_SHORT).show();
        }
        editor.commit();
        Intent refresh = new Intent(getActivity(), MainActivity.class);
        getActivity().finish();
        startActivity(refresh);
    }

    private void speech(){
        tts.setLanguage(Locale.US);
        tts.setSpeechRate((float) 0.5);
        if (cUser != null)
            tts.speak(FirebaseAuth.getInstance().getCurrentUser().getUid(), TextToSpeech.QUEUE_ADD, null);
        else Toast.makeText(getActivity(),getString(R.string.please_login), Toast.LENGTH_LONG).show();
    }

    private void recognize(){
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.getDefault().toString());
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, getString(R.string.plz_say_order));

        startActivityForResult(intent,REC_RESULT);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        StringBuffer buffer = new StringBuffer();
        super.onActivityResult(requestCode, resultCode, data);
        if (cUser != null) {
            if (requestCode==REC_RESULT && resultCode==RESULT_OK){
                ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                if(matches.contains(getString(R.string.order))){
                    buffer.append(getString(R.string.code)+": " + FirebaseAuth.getInstance().getCurrentUser().getUid() + "\n");
                    buffer.append("---------------------------------\n");
                    showMessage(buffer.toString());
                }else Toast.makeText(getActivity(),getString(R.string.plz_say_order), Toast.LENGTH_LONG).show();

            }
        }else Toast.makeText(getActivity(),getString(R.string.please_login), Toast.LENGTH_LONG).show();
    }

    public void showMessage(String s){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setCancelable(true);
//        if (language.equals("el")) builder.setTitle("ΠΑΡΑΓΓΕΛΙΑ");
//        else builder.setTitle("ORDER");
        builder.setTitle(getString(R.string.order).toUpperCase());
        builder.setMessage(s);
        builder.show();
    }
}