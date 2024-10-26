package com.example.carmaintenancetracker;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.TextWatcher;

import java.io.*;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Notes_Activity#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Notes_Activity extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private EditText editText1;
    private EditText editText2;
    private EditText editText3;
    private EditText editText4;
    private EditText editText5;
    private EditText editText6;
    private EditText editText7;
    private EditText editText8;
    private EditText editText9;
    private EditText editText10;

    public Notes_Activity() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Notes_Activity.
     */
    // TODO: Rename and change types and number of parameters
    public static Notes_Activity newInstance(String param1, String param2) {
        Notes_Activity fragment = new Notes_Activity();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.notes_activity, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set up variables
        editText1 = view.findViewById(R.id.editText1);
        editText2 = view.findViewById(R.id.editText2);
        editText3 = view.findViewById(R.id.editText3);
        editText4 = view.findViewById(R.id.editText4);
        editText5 = view.findViewById(R.id.editText5);
        editText6 = view.findViewById(R.id.editText6);
        editText7 = view.findViewById(R.id.editText7);
        editText8 = view.findViewById(R.id.editText8);
        editText9 = view.findViewById(R.id.editText9);
        editText10 = view.findViewById(R.id.editText10);

        editText1.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                saveTextToFile(text, 1);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        editText2.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                saveTextToFile(text, 2);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        editText3.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                saveTextToFile(text, 3);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        editText4.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                saveTextToFile(text, 4);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        editText5.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                saveTextToFile(text, 5);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        editText6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                saveTextToFile(text, 6);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        editText7.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                saveTextToFile(text, 7);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        editText8.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                saveTextToFile(text, 8);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        editText9.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                saveTextToFile(text, 9);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });
        editText10.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String text = s.toString();
                saveTextToFile(text, 10);
            }

            @Override
            public void afterTextChanged(Editable editable) {}
        });

        // read notes from file and populate editText fields
        File file = new File(Objects.requireNonNull(getContext()).getFilesDir(), "notes.txt");

        if (!file.exists() || file.length() == 0) {
            try {
                FileWriter writer = new FileWriter(file);
                for (int i = 1; i <= 10; i++) {
                    writer.write("Line " + i + ": \n");
                }
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);
            String line;
            int lineNumber = 1;
            while ((line = bufferedReader.readLine()) != null) {
                String text = line.substring(line.indexOf(": ") + 2); // extract text after "Line X: "
                switch (lineNumber) {
                    case 1:
                        editText1.setText(text);
                        break;
                    case 2:
                        editText2.setText(text);
                        break;
                    case 3:
                        editText3.setText(text);
                        break;
                    case 4:
                        editText4.setText(text);
                        break;
                    case 5:
                        editText5.setText(text);
                        break;
                    case 6:
                        editText6.setText(text);
                        break;
                    case 7:
                        editText7.setText(text);
                        break;
                    case 8:
                        editText8.setText(text);
                        break;
                    case 9:
                        editText9.setText(text);
                        break;
                    case 10:
                        editText10.setText(text);
                        break;
                    default:
                        break;
                }
                lineNumber++;
            }
        } catch (java.io.IOException e) {
            // handle error
            e.printStackTrace();
        }
    }

    private void saveTextToFile(String text, int lineNumber) {
        String fileName = "notes.txt";
        File file = new File(Objects.requireNonNull(getContext()).getFilesDir(), fileName);

        try {
            // Read all lines from the file
            BufferedReader reader = new BufferedReader(new FileReader(file));
            StringBuilder content = new StringBuilder();
            String line;
            int currentLine = 1;

            while ((line = reader.readLine()) != null) {
                if (currentLine == lineNumber) {
                    // Replace the line with the updated text
                    content.append("Line ").append(lineNumber).append(": ").append(text).append("\n");
                } else {
                    // Retain the existing line
                    content.append(line).append("\n");
                }
                currentLine++;
            }

            reader.close();

            // If new lines are added beyond the current line count
            while (currentLine <= lineNumber) {
                if (currentLine == lineNumber) {
                    content.append("Line ").append(lineNumber).append(": ").append(text).append("\n");
                } else {
                    content.append("Line ").append(currentLine).append(": \n");
                }
                currentLine++;
            }

            // Write updated content back to the file
            FileWriter writer = new FileWriter(file, false); // overwrite mode
            writer.write(content.toString());
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}