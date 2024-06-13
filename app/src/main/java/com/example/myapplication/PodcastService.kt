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
import com.example.myapplication.data.Podcast

enum class RepeatModePodcast {
    OFF, ONE
}
class PodcastService : Service() {
    private val binder = PodcastBinder()
    var mediaPlayer: MediaPlayer? = null

    var currentPodcast by mutableStateOf<Podcast?>(null)
        private set
    var isPodcastPlaying by mutableStateOf(false)
        private set
    private var currentPlayingPosition by mutableLongStateOf(0L)
        private set
    var totalDuration by mutableLongStateOf(0L)
        private set
    var podcasts by mutableStateOf<List<Podcast>>(emptyList())
    var currentPlayingIndex by mutableIntStateOf(0)

    var repeatMode by mutableStateOf(RepeatModePodcast.OFF)
        private set

    var isMiniPodcastPlayerVisible by mutableStateOf(false)
        private set

    fun setMiniPlayerVisibility(visible: Boolean) {
        isMiniPodcastPlayerVisible = visible
    }
    inner class PodcastBinder : Binder() {
        val service: PodcastService
            get() = this@PodcastService
    }

    override fun onBind(intent: Intent): IBinder = binder

    fun startPodcast(context: Context, podcast: Podcast) {
        currentPodcast = podcast
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = MediaPlayer.create(context, Uri.parse(podcast.audioUrl))
        mediaPlayer?.start()
        isPodcastPlaying = true
        totalDuration = mediaPlayer?.duration?.toLong() ?: 0L
        currentPlayingPosition = 0L
        currentPlayingIndex = podcasts.indexOf(podcast)

        mediaPlayer?.setOnCompletionListener {
            when (repeatMode) {
                RepeatModePodcast.OFF -> playNextPodcast(context)
                RepeatModePodcast.ONE -> startPodcast(context, podcast)
            }
        }
    }

    fun pausePodcast() {
        mediaPlayer?.pause()
        isPodcastPlaying = false
    }

    fun resumePodcast() {
        mediaPlayer?.start()
        isPodcastPlaying = true
    }

    fun stopPodcast() {
        mediaPlayer?.stop()
        mediaPlayer?.release()
        mediaPlayer = null
        isPodcastPlaying = false
        currentPodcast = null
    }

    fun seekTo(position: Long) {
        mediaPlayer?.seekTo(position.toInt())
    }

    fun playNextPodcast(context: Context) {
        val nextIndex = (currentPlayingIndex + 1) % podcasts.size
        val nextPodcast = podcasts[nextIndex]
        startPodcast(context, nextPodcast)
    }

    fun playPreviousPodcast(context: Context) {
        val previousIndex = if (currentPlayingIndex == 0) podcasts.size - 1 else currentPlayingIndex - 1
        val previousPodcast = podcasts[previousIndex]
        startPodcast(context, previousPodcast)
    }

    fun togglePlayPause() {
        if (isPodcastPlaying) {
            pausePodcast()
        } else {
            resumePodcast()
        }
    }

    fun toggleRepeatMode() {
        repeatMode = when (repeatMode) {
            RepeatModePodcast.OFF -> RepeatModePodcast.ONE
            RepeatModePodcast.ONE -> RepeatModePodcast.OFF
        }
    }

    fun getInternalCurrentPodcast(): Podcast? {
        return currentPodcast
    }

    fun isPlaying(): Boolean {
        return isPodcastPlaying
    }

    fun getCurrentPosition(): Long {
        return mediaPlayer?.currentPosition?.toLong() ?: 0L
    }

    fun isPodcastLoaded(): Boolean {
        return mediaPlayer != null
    }
}
