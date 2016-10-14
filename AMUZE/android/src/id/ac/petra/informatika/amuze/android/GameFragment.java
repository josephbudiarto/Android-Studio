package id.ac.petra.informatika.amuze.android;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import id.ac.petra.informatika.amuze.android.game.PuzzleActivity;
import id.ac.petra.informatika.amuze.android.game.PuzzleList;
import id.ac.petra.informatika.amuze.android.game.QuizList;
import id.ac.petra.informatika.amuze.android.game.SilhouetteList;

public class GameFragment extends Fragment {
    private ImageButton mButtonSilhouette, mButtonPuzzle, mButtonQuiz;
    public GameFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_game, container, false);
        //mButtonSilhouette = (ImageButton) rootView.findViewById(R.id.button_silhouette);
        mButtonPuzzle = (ImageButton) rootView.findViewById(R.id.button_puzzle);
        mButtonQuiz = (ImageButton) rootView.findViewById(R.id.button_quiz);

        /*mButtonSilhouette.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getActivity(), "silhouette", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getActivity(), SilhouetteList.class);
                startActivity(intent);
            }
        });*/
        mButtonPuzzle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), PuzzleList.class);
                startActivity(intent);
            }
        });
        mButtonQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), QuizList.class);
                startActivity(intent);
            }
        });
        return rootView;
    }


}