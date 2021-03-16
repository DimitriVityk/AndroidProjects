package com.example.defensecommander;

import android.content.Context;
import android.media.SoundPool;
import android.util.Log;

import java.util.HashMap;
import java.util.HashSet;

class SoundPlayer {

    private static final String TAG = "SoundPlayer";
    private static boolean initialized = false;

    private static SoundPool soundPool;
    private static final int MAX_STREAMS = 15;
    private static final HashSet<Integer> loaded = new HashSet<>();
    private static final HashMap<String, Integer> soundNameToStreamId = new HashMap<>();
    private static final HashSet<String> loopList = new HashSet<>();

    private static void init() {
        initialized = true;

        SoundPool.Builder builder = new SoundPool.Builder();
        builder.setMaxStreams(MAX_STREAMS);
        soundPool = builder.build();
        soundPool.setOnLoadCompleteListener((soundPool, sampleId, status) -> {
            Log.d(TAG, "onLoadComplete: #" + sampleId + "  " + status);
            loaded.add(sampleId);
        });
    }

    public static void setupSound(Context context, String soundName, int resource, boolean loop) {
        if (!initialized)
            init();
        if(!soundNameToStreamId.containsKey(soundName)) {
            int streamId;
            if(soundName.equals("background")) {
                streamId = soundPool.load(context, resource, 2);
            } else
            {
                streamId = soundPool.load(context, resource, 1);
            }
            soundNameToStreamId.put(soundName, streamId);
        }

        if(loop)
            loopList.add(soundName);

        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }



    public static void start(final String soundName) {
        if (!initialized)
            init();

        if (!loaded.contains(soundNameToStreamId.get(soundName))) {
            Log.d(TAG, "start: SOUND NOT LOADED: " + soundName);
            return;
        }

        int loop = 0;
        if(loopList.contains(soundName))
        {
            loop=-1;
        }

        Integer streamId = soundNameToStreamId.get(soundName);
        if (streamId == null)
            return;
        if(soundName.equals("background")) {
            soundPool.play(streamId, 1f, 1f, 2, loop, 1f);
        }else
        {
            soundPool.play(streamId, 1f, 1f, 1, loop, 1f);
        }

    }

    static void pauseAll()
    {
        soundPool.autoPause();
    }

    static void resumeAll()
    {
        soundPool.autoResume();
    }

    static void stopAll()
    {
        for(String s : soundNameToStreamId.keySet())
        {
            soundPool.stop(soundNameToStreamId.get(s));
        }
    }


}

