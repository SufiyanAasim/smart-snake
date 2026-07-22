package project;

import javax.sound.midi.*;

/**
 * ==============================================================================
 * Project: Smart Snake
 * Module: SoundManager (MIDI Synth Chiptunes & Audio Effects)
 * Authors:
 *   - Mohammad Sufiyan Aasim (sufiyanaasim@outlook.com / GitHub: SufiyanAasim)
 *   - Fahad Bin Nasir (fahadabbasi17025@gmail.com / GitHub: FahadBinNasir)
 * ==============================================================================
 */
public class SoundManager {
    private Synthesizer synth;
    private MidiChannel channel;
    private boolean soundEnabled = true;

    public SoundManager() {
        // Load synthesizer asynchronously to prevent startup delay
        new Thread(() -> {
            try {
                synth = MidiSystem.getSynthesizer();
                synth.open();
                MidiChannel[] channels = synth.getChannels();
                if (channels != null && channels.length > 0) {
                    channel = channels[0];
                    // Program change: Lead 1 (square synth wave) - very 8-bit retro!
                    channel.programChange(80);
                }
            } catch (Exception e) {
                System.err.println("MIDI Synthesizer failed to load: " + e.getMessage());
            }
        }).start();
    }

    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
    }

    public boolean isSoundEnabled() {
        return soundEnabled;
    }

    public void playEatSound() {
        if (!soundEnabled || channel == null) return;
        new Thread(() -> {
            try {
                channel.noteOn(72, 90); // C5
                Thread.sleep(70);
                channel.noteOff(72);
                channel.noteOn(79, 90); // G5
                Thread.sleep(120);
                channel.noteOff(79);
            } catch (InterruptedException ignored) {}
        }).start();
    }

    public void playSpecialEatSound() {
        if (!soundEnabled || channel == null) return;
        new Thread(() -> {
            try {
                channel.noteOn(72, 100); // C5
                Thread.sleep(60);
                channel.noteOff(72);
                channel.noteOn(76, 100); // E5
                Thread.sleep(60);
                channel.noteOff(76);
                channel.noteOn(79, 100); // G5
                Thread.sleep(60);
                channel.noteOff(79);
                channel.noteOn(84, 100); // C6
                Thread.sleep(150);
                channel.noteOff(84);
            } catch (InterruptedException ignored) {}
        }).start();
    }

    public void playShieldBreakSound() {
        if (!soundEnabled || channel == null) return;
        new Thread(() -> {
            try {
                channel.noteOn(88, 110); // E6
                Thread.sleep(80);
                channel.noteOff(88);
                channel.noteOn(76, 110); // E5
                Thread.sleep(80);
                channel.noteOff(76);
                channel.noteOn(64, 110); // E4
                Thread.sleep(150);
                channel.noteOff(64);
            } catch (InterruptedException ignored) {}
        }).start();
    }

    public void playCollisionSound() {
        if (!soundEnabled || channel == null) return;
        new Thread(() -> {
            try {
                channel.noteOn(50, 100); // D3
                Thread.sleep(120);
                channel.noteOff(50);
                channel.noteOn(45, 100); // A2
                Thread.sleep(120);
                channel.noteOff(45);
                channel.noteOn(38, 100); // D2
                Thread.sleep(250);
                channel.noteOff(38);
            } catch (InterruptedException ignored) {}
        }).start();
    }

    public void playHoverSound() {
        if (!soundEnabled || channel == null) return;
        new Thread(() -> {
            try {
                channel.noteOn(67, 45); // G4
                Thread.sleep(40);
                channel.noteOff(67);
            } catch (InterruptedException ignored) {}
        }).start();
    }
}
