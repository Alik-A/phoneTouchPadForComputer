package com.example.alik.phoneTouchPadForComputer.fragments;

import android.content.Context;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.example.alik.phoneTouchPadForComputer.R;

import static android.content.Context.WIFI_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link TouchPadFragment.OnTouchPadFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link TouchPadFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TouchPadFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnTouchPadFragmentInteractionListener mListener;

    public static int time;
    private ConnectToServer client;
    private SendDataToServer sendDataToServer;
    private View touchPadView;
    private boolean actionDownProvided;
    private float lastTouchXLocation;
    private float lastTouchYLocation;
    private CountDownTimer countClickTimeThread;

    public TouchPadFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TouchPadFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TouchPadFragment newInstance(String param1, String param2) {
        TouchPadFragment fragment = new TouchPadFragment();
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
        return inflater.inflate(R.layout.fragment_touch_pad, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onTouchPadFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnTouchPadFragmentInteractionListener) {
            mListener = (OnTouchPadFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.client = new ConnectToServer();

        this.countClickTimeThread = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                actionDownProvided = false;
                new SendDataToServer().execute(String.valueOf(TCPClient.RIGHT_CLICK_EVENT));
            }
        };
        WifiManager wm = (WifiManager) getContext().getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = "10.100.102.8";
//        this.client.execute(ip);
        new ConnectToServer().execute();
        this.touchPadView = (View) view.findViewById(R.id.view);

        this.touchPadView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                System.out.println("x: " + event.getX() + " y: " + event.getY());
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        countClickTimeThread.start();
                        lastTouchXLocation = event.getX();
                        lastTouchYLocation = event.getY();
                        actionDownProvided = true;
                        return true;

                    case MotionEvent.ACTION_MOVE:

                        float currX = event.getX();
                        float currY = event.getY();
                        int differenceBetweenX = (int) (currX - lastTouchXLocation);
                        int differenceBetweenY = (int) (currY - lastTouchYLocation);
                        if (differenceBetweenX > 100 || differenceBetweenX < -100 || differenceBetweenY > 100 || differenceBetweenY < -100) {
                            if (actionDownProvided) {
                                actionDownProvided = false;
                                countClickTimeThread.cancel();
                            }
                            StringBuilder dotsToMove = new StringBuilder();
                            dotsToMove.append(TCPClient.MOVE_EVENT).append(" ");
                            dotsToMove.append(differenceBetweenX > 100 ? 1 : differenceBetweenX < -100 ? -1 : 0);
                            dotsToMove.append(" ").append(differenceBetweenY > 100 ? 1 :
                                    differenceBetweenY < -100 ? -1 : 0);

                            System.out.println(dotsToMove.toString());
                            new SendDataToServer().execute(dotsToMove.toString());
//                        lastTouchXLocation= event.getX();
//                        lastTouchYLocation= event.getY();
                        }
                        break;

                    case MotionEvent.ACTION_UP:
                        if (actionDownProvided) {
                            actionDownProvided = false;
                            countClickTimeThread.cancel();
                            new SendDataToServer().execute(String.valueOf(TCPClient.LEFT_CLICK_EVENT));


                        }
                }
                return true;
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnTouchPadFragmentInteractionListener {
        // TODO: Update argument type and name
        void onTouchPadFragmentInteraction(Uri uri);
    }
}
