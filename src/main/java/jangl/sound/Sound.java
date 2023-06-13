package jangl.sound;

import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL;
import org.lwjgl.openal.ALC;
import org.lwjgl.openal.ALCCapabilities;

import javax.sound.sampled.UnsupportedAudioFileException;

import static org.lwjgl.openal.ALC11.*;
import static org.lwjgl.openal.AL11.*;
import static org.lwjgl.stb.STBVorbis.stb_vorbis_decode_filename;

import java.io.File;
import java.io.UncheckedIOException;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class Sound implements AutoCloseable {
    private final int bufferID;
    private final int sourceID;
    private static boolean initialized = false;

    public static void init() {
        if (initialized) {
            return;
        }

        String defaultDeviceName = alcGetString(0, ALC_DEFAULT_DEVICE_SPECIFIER);
        long device = alcOpenDevice(defaultDeviceName);
        ALCCapabilities deviceCapabilities = ALC.createCapabilities(device);
        long context = alcCreateContext(device, (IntBuffer) null);

        alcMakeContextCurrent(context);
        AL.createCapabilities(deviceCapabilities);

        initialized = true;
    }

    /**
     * @param soundFile The sound file, in the .wav format, to load.
     * @throws UnsupportedAudioFileException Throws if the file format is not .wav
     * @throws IllegalStateException Throws if Sound.init() has not been called. Since JANGL.init() initializes sound under the hood, you usually should not encounter this issue.
     */
    public Sound(File soundFile) throws UnsupportedAudioFileException, IllegalStateException {
        if (!initialized) {
            throw new IllegalStateException("Sound.init() must be called before creating a sound object.");
        }

        this.sourceID = alGenSources();

        this.bufferID = this.loadSound(soundFile);
        alSourcei(sourceID, AL_BUFFER, this.bufferID);
    }

    private int determineFormat(int channels) {
        if (channels == 1) {
            return AL_FORMAT_MONO16;
        } else {
            return AL_FORMAT_STEREO16;
        }
    }

    /**
     * @param soundFile The sound file to load
     * @return The sound buffer ID
     * @throws UncheckedIOException If the soundFile could not be found
     */
    private int loadSound(File soundFile) throws UncheckedIOException {
        IntBuffer channelsBuffer = BufferUtils.createIntBuffer(1);
        IntBuffer sampleRateBuffer = BufferUtils.createIntBuffer(1);

        ShortBuffer rawAudioBuffer = stb_vorbis_decode_filename(soundFile.getPath(), channelsBuffer, sampleRateBuffer);

        if (rawAudioBuffer == null) {
            // TODO: do error handling
            System.out.println("Could not load sound... do this error handling later");
            System.exit(1);
        }

        int channels = channelsBuffer.get();
        int sampleRate = sampleRateBuffer.get();

        int format = this.determineFormat(channels);

        int bufferID = alGenBuffers();
        alBufferData(bufferID, format, rawAudioBuffer, sampleRate);

        return bufferID;
    }

    /**
     * Plays the sound
     */
    public void play() {
        alSourcePlay(this.sourceID);
    }

    /**
     * Pauses the sound.
     */
    public void pause() {
        alSourcePause(this.sourceID);
    }

    /**
     * Stops the sound.
     */
    public void stop() {
        alSourceStop(this.sourceID);
    }

    /**
     * Stops the sound and sets its state to the initial state.
     */
    public void rewind() {
        alSourceRewind(this.sourceID);
    }

    /**
     * @param volume The volume of the sound, where 0 = 0% volume and 1 = 100% volume.
     * @throws IllegalArgumentException Throws if the given volume is outside the range of [0, 1].
     */
    public void setVolume(float volume) throws IllegalArgumentException {
        if (volume > 1 || volume < 0) {
            throw new IllegalArgumentException("The volume of a sound must be a float within the range [0, 1]");
        }

        alSourcef(this.sourceID, AL_GAIN, volume);
    }

    /**
     * Set the audio to loop or not loop. Looping is off by default.
     *
     * @param loop true to make the audio loop, false to make the audio not loop.
     */
    public void setLooping(boolean loop) {
        if (loop) {
            alSourcei(this.sourceID, AL_LOOPING, AL_TRUE);
        } else {
            alSourcei(this.sourceID, AL_LOOPING, AL_FALSE);
        }
    }

    @Override
    public void close() {
        alDeleteSources(this.sourceID);
        alDeleteBuffers(this.bufferID);
    }
}