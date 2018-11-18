package com.example.alik.phoneTouchPadForComputer;

import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    public static int time;
    private ConnectToServer client;
    private SendDataToServer sendDataToServer;
    private View touchPadView;
    private boolean actionDownProvided;
    private float lastTouchXLocation;
    private float lastTouchYLocation;
    private CountDownTimer countClickTimeThread;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        WifiManager wm = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        String ip = "10.100.102.8";
//        this.client.execute(ip);
        new ConnectToServer().execute();
        this.touchPadView = (View) findViewById(R.id.view);

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

    class countClickTime extends Thread implements Runnable{
        @Override
        public void run() {
            super.run();
            time = 0;
            while (!interrupted()) {
                time++;
            }
        }

        public int getTime() {
            return time;
        }
    }
}
