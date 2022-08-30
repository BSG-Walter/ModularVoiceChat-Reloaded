package fr.nathanael2611.modularvoicechat.audio.speaker;

import fr.nathanael2611.modularvoicechat.api.VoicePlayEvent;
import fr.nathanael2611.modularvoicechat.api.VoiceProperties;
import fr.nathanael2611.modularvoicechat.audio.api.NoExceptionCloseable;
import fr.nathanael2611.modularvoicechat.audio.api.IAudioDecoder;
import fr.nathanael2611.modularvoicechat.audio.impl.OpusDecoder;
import fr.nathanael2611.modularvoicechat.audio.impl.OpusManager;
import fr.nathanael2611.modularvoicechat.util.Helpers;
import fr.nathanael2611.modularvoicechat.util.Utils;
import net.minecraftforge.common.MinecraftForge;

import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

/**
 *
 * Based on: https://github.com/MC-U-Team/Voice-Chat/blob/1.15.2/audio-client/src/main/java/info/u_team/voice_chat/audio_client/speaker/SpeakerBufferPusher.java
 */
public class SpeakerBufferPusher implements NoExceptionCloseable
{

    private final SpeakerBuffer buffer;
    private final IAudioDecoder decoder;
    private final Future<?> future;

    public SpeakerBufferPusher(ExecutorService executor, int id, SpeakerData speakerData)
    {
        this.buffer = new SpeakerBuffer(10);
        this.decoder = OpusManager.createDecoder();
        this.future = executor.submit(() ->
        {
            while (!Thread.currentThread().isInterrupted())
            {
                if (speakerData.isAvailable(id) && speakerData.freeBuffer(id) > 0)
                {
                    SpeakerBuffer.AudioEntry entry = buffer.getNextPacket();
                    Helpers.log("data disponible = " + Arrays.toString(entry.getPacket()));
                    if(entry.isEnd())
                    {
                        speakerData.flush(id);
                        //speakerData.setVolume(0);
                    }
                    else
                    {
                        VoicePlayEvent event = new VoicePlayEvent(entry.getPacket(), entry.getVolumePercent(), entry.getProperties());
                        MinecraftForge.EVENT_BUS.post(event);
                        Helpers.log("eebent");
                        if (!event.isCanceled())
                        {
                            speakerData.write(id, event.getAudioSamples(), event.getVolumePercent());
                            Helpers.log("write = " + Arrays.toString(event.getAudioSamples()));
                            Helpers.log("volume = " + event.getVolumePercent());
                        }
                    }
                }
            }
        });
    }

    public void decodePush(byte[] opusPacket, int volumePercent, VoiceProperties properties)
    {
        Helpers.log("antes de decode " + Arrays.toString(opusPacket));
        short[] decodedPacket = decoder.decoder(opusPacket);
        Helpers.log("despues de decode " + Arrays.toString(decodedPacket));
        push(decodedPacket, volumePercent, properties);
    }

    private void push(short[] packet, int volumePercent, VoiceProperties properties)
    {
        buffer.pushPacket(packet, volumePercent, properties);
    }

    public void end()
    {
        buffer.pushEnd();
    }

    @Override
    public void close()
    {
        future.cancel(true);
        decoder.close();
    }

}
