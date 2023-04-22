package com.example.musicchatapp.Activities;

import android.os.Bundle;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.musicchatapp.Adapters.CustomMusicAdapter;
import com.example.musicchatapp.Models.Music;
import com.example.musicchatapp.R;

import java.util.ArrayList;

public class MusicActivity extends AppCompatActivity {
    private ArrayList<Music> arrayList;
    private CustomMusicAdapter adapter;
    private ListView songList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music);

        songList = (ListView) findViewById(R.id.songList);
        arrayList = new ArrayList<>();
        arrayList.add(new Music("Friends", "MarshMellow", R.raw.friends));
        arrayList.add(new Music("Brown Munde ", "AP Dhillon", R.raw.brown_munde));
        arrayList.add(new Music("I Will Be There For You", "The Rembrandts", R.raw.i_will_there_for_you));
        arrayList.add(new Music("Wavin Flag", "K'naan", R.raw.wavin_flag));
        arrayList.add(new Music("Suno Ganpati Bappa Morya","Amit Mishra",R.raw.suno_ganpati_bappa_morya));
        arrayList.add(new Music("Zaalima","Arijit singh",R.raw.zaalima));
        arrayList.add(new Music("Lo Safar"," Jubin Nautiyal",R.raw.lo_safar));
        arrayList.add(new Music("Believer","Imagin Dragons",R.raw.believer));
        arrayList.add(new Music("Everybody Hates Me","The Chain Smokers",R.raw.everybody_hates_me));
        arrayList.add(new Music("Na ja","Pav Dharia",R.raw.naja));
        arrayList.add(new Music("Back To You","Selena Gomez",R.raw.back_to_you));
        adapter = new CustomMusicAdapter(this, R.layout.custom_music_item, arrayList);
        songList.setAdapter(adapter);

    }
}