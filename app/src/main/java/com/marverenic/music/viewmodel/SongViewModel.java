package com.marverenic.music.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.support.v7.widget.PopupMenu;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;

import com.marverenic.music.R;
import com.marverenic.music.activity.NowPlayingActivity;
import com.marverenic.music.activity.instance.AlbumActivity;
import com.marverenic.music.activity.instance.ArtistActivity;
import com.marverenic.music.instances.Library;
import com.marverenic.music.instances.PlaylistDialog;
import com.marverenic.music.instances.Song;
import com.marverenic.music.player.PlayerController;
import com.marverenic.music.utils.Navigate;
import com.marverenic.music.utils.Prefs;

import java.util.List;

public class SongViewModel extends BaseObservable {

    private Context mContext;
    private List<Song> mSongList;
    private int mIndex;
    private Song mReference;

    public SongViewModel(Context context, List<Song> songs) {
        mContext = context;
        mSongList = songs;
    }

    public void setIndex(int index) {
        setSong(mSongList, index);
    }

    protected int getIndex() {
        return mIndex;
    }

    protected Song getReference() {
        return mReference;
    }

    protected List<Song> getSongs() {
        return mSongList;
    }

    public void setSong(List<Song> songList, int index) {
        mSongList = songList;
        mIndex = index;
        mReference = songList.get(index);

        notifyChange();
    }

    public String getTitle() {
        return mReference.getSongName();
    }

    public String getDetail() {
        return mContext.getString(R.string.format_compact_song_info,
                mReference.getArtistName(), mReference.getAlbumName());
    }

    public View.OnClickListener onClickSong() {
        return v -> {
            PlayerController.setQueue(mSongList, mIndex);
            PlayerController.begin();

            if (Prefs.getPrefs(mContext)
                    .getBoolean(Prefs.SWITCH_TO_PLAYING, true)) {
                Navigate.to(mContext, NowPlayingActivity.class);
            }
        };
    }

    public View.OnClickListener onClickMenu() {
        return v -> {
            final PopupMenu menu = new PopupMenu(mContext, v, Gravity.END);
            String[] options = mContext.getResources()
                    .getStringArray(R.array.queue_options_song);

            for (int i = 0; i < options.length;  i++) {
                menu.getMenu().add(Menu.NONE, i, i, options[i]);
            }
            menu.setOnMenuItemClickListener(onMenuItemClick(v));
            menu.show();
        };
    }

    private PopupMenu.OnMenuItemClickListener onMenuItemClick(View view) {
        return menuItem -> {
            switch (menuItem.getItemId()) {
                case 0: //Queue this song next
                    PlayerController.queueNext(mReference);
                    return true;
                case 1: //Queue this song last
                    PlayerController.queueLast(mReference);
                    return true;
                case 2: //Go to artist
                    Navigate.to(
                            mContext,
                            ArtistActivity.class,
                            ArtistActivity.ARTIST_EXTRA,
                            Library.findArtistById(mReference.getArtistId()));
                    return true;
                case 3: // Go to album
                    Navigate.to(
                            mContext,
                            AlbumActivity.class,
                            AlbumActivity.ALBUM_EXTRA,
                            Library.findAlbumById(mReference.getAlbumId()));
                    return true;
                case 4: //Add to playlist...
                    PlaylistDialog.AddToNormal.alert(view, mReference, mContext.getString(
                            R.string.header_add_song_name_to_playlist, mReference));
                    return true;
            }
            return false;
        };
    }

}
