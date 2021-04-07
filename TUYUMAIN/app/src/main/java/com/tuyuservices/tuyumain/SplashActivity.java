package com.tuyuservices.tuyumain;

/* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import java.util.Timer;
import java.util.TimerTask;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

        Timer mTimer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        };

        mTimer.schedule(timerTask, 1000);
    }
    /* © 2020 All rights reserved. abilash432@gmail.com/@thenextbiggeek® Extending to Water360*/

}
