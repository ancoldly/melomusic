import android.app.Application
import android.app.Service
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModelProvider
import com.example.myapplication.data.Music
import com.example.myapplication.viewmodel.MusicViewModel

enum class RepeatMode {
    OFF, ONE
}

class MusicService : Service() {
    private val binder = MusicBinder()
    var mediaPlayer: MediaPlayer? = null

    var currentMusic by mutableStateOf<Music?>(null)
        private set
    var isMusicPlaying by mutableStateOf(false)
        private set
    private var currentPlayingPosition by mutableLongStateOf(0L)
        private set
    var totalDuration by mutableLongStateOf(0L)
        private set
    var musics by mutableStateOf<List<Music>>(emptyList())
    var currentPlayingIndex by mutableIntStateOf(0)
    var repeatMode by mutableStateOf(RepeatMode.OFF)
        private set

    var isMiniPlayerVisible by mutableStateOf(false)
        private set

    var isCheckView by mutableStateOf(false)

    fun setMiniPlayerVisibility(visible: Boolean) {
        isMiniPlayerVisible = visible
    }

    inner class MusicBinder : Binder() {
        val service: MusicService
            get() = this@MusicService
    }

    override fun onBind(intent: Intent): IBinder = binder

    fun startMusic(context: Context, music: Music) {
        isCheckView = false
        currentMusic = music
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, Uri.parse(music.audioUrl))
        mediaPlayer?.start()
        isMusicPlaying = true
        totalDuration = mediaPlayer?.duration?.toLong() ?: 0L
        currentPlayingPosition = 0L
        currentPlayingIndex = musics.indexOf(music)

        mediaPlayer?.setOnCompletionListener {
            when (repeatMode) {
                RepeatMode.OFF -> playNextMusic(context)
                RepeatMode.ONE -> startMusic(context, music)
            }
        }
    }

    fun pauseMusic() {
        mediaPlayer?.pause()
        isMusicPlaying = false
    }

    fun resumeMusic() {
        mediaPlayer?.start()
        isMusicPlaying = true
    }

    fun stopMusic() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isMusicPlaying = false
        currentMusic = null
    }

    fun seekTo(position: Long) {
        mediaPlayer?.seekTo(position.toInt())
    }

    fun playNextMusic(context: Context) {
        val nextIndex = (currentPlayingIndex + 1) % musics.size
        val nextMusic = musics[nextIndex]
        startMusic(context, nextMusic)
        isCheckView = false
    }

    fun playPreviousMusic(context: Context) {
        val previousIndex = if (currentPlayingIndex == 0) musics.size - 1 else currentPlayingIndex - 1
        val previousMusic = musics[previousIndex]
        startMusic(context, previousMusic)
        isCheckView = false
    }

    fun togglePlayPause() {
        if (isMusicPlaying) {
            pauseMusic()
        } else {
            resumeMusic()
        }
    }

    fun toggleRepeatMode() {
        repeatMode = when (repeatMode) {
            RepeatMode.OFF -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
    }

    fun getInternalCurrentMusic(): Music? {
        return currentMusic
    }

    fun isPlaying(): Boolean {
        return isMusicPlaying
    }

    fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }

    fun isMusicLoaded(): Boolean {
        return mediaPlayer != null
    }
}