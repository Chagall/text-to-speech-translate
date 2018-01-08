package mobile.pi.tri.text_to_speech_translate;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private GoogleTranslate googleTranslate;
    private TextToSpeech textToSpeech;
    private Button speakButton;
    private Button translateButton;
    private String textToSpeak;
    private EditText textToSpeakEditText;
    private TextView translatedTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        textToSpeak = "";
        speakButton = (Button) findViewById(R.id.speak_button);
        translateButton = (Button) findViewById(R.id.translate_button);
        textToSpeakEditText = (EditText) findViewById(R.id.text_to_speak);
        translatedTextView = (TextView) findViewById(R.id.translated_text);
    }

    @Override
    protected void onResume() {
        super.onResume();
        initTextToSpeech();
        initButtonListeners();
        initEditTextListener();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopTextToSpeech();
    }

    private void initTextToSpeech() {
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {
                if(TextToSpeech.SUCCESS == i) {
                    try {
                        Locale currentLocale = textToSpeech.getDefaultVoice().getLocale();
                        int result = textToSpeech.setLanguage(currentLocale);

                        if (TextToSpeech.LANG_MISSING_DATA == result
                                || TextToSpeech.LANG_NOT_SUPPORTED == result) {
                            Toast.makeText(getBaseContext(),
                                    getString(R.string.unsupported_language),
                                    Toast.LENGTH_LONG).show();
                        } else {
                            speak(getString(R.string.supported_language));
                            //Toast.makeText(getBaseContext(),
                            //        getString(R.string.supported_language),
                            //        Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e){
                        Toast.makeText(getBaseContext(),
                                e.toString(),
                                Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getBaseContext(),
                            getString(R.string.tts_initialization_failure),
                            Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void initEditTextListener() {
        textToSpeakEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                textToSpeak = editable.toString();
            }
        });
    }

    private void initButtonListeners() {
        speakButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                speak(textToSpeak);
            }
        });

        translateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new EnglishToTagalog().execute();
            }
        });
    }

    private void speak(String whatToSay) {
        if("".equals(whatToSay)) {
            textToSpeech.speak("Você não digitou nada para eu dizer", TextToSpeech.QUEUE_FLUSH, null, null);
        }
        else {
            textToSpeech.speak(whatToSay, TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }

    private void stopTextToSpeech() {
        if(textToSpeech != null) {
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
    }

    private class EnglishToTagalog extends AsyncTask<Void, Void, Void> {
        private ProgressDialog progress = null;

        @Override
        protected Void doInBackground(Void... params) {

            try {
                googleTranslate = new GoogleTranslate("AIzaSyAWZjQQua_knvNiyBg6QaswNBDZ1TpWGnM");
                Thread.sleep(1000);

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
        }

        @Override
        protected void onPreExecute() {
            //start the progress dialog
            progress = ProgressDialog.show(MainActivity.this, null, "Translating...");
            progress.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progress.setIndeterminate(true);
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            progress.dismiss();
            super.onPostExecute(result);
            translated();
        }

        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);
        }

    }


    public void translated(){
        String text = googleTranslate.translate(textToSpeak, "pt-br", "en");
        translatedTextView.setText(text);

    }
}

