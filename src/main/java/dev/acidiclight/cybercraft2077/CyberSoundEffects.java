package dev.acidiclight.cybercraft2077;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nullable;
import java.util.Random;

@Mod.EventBusSubscriber
public class CyberSoundEffects
{
    private static final Random random = new Random();
    private static MusicTracks currentTracks = MusicTracks.Heist;

    public static SoundEvent cyber_encounter_win;
    public static SoundEvent cyberAlert;
    public static SoundEvent cyberDeath;

    public static CyberpunkMusic tigerclawsCombat;
    public static CyberpunkMusic tigerclawsHostile;
    public static CyberpunkMusic heistCombat;
    public static CyberpunkMusic heistHostile;
    public static CyberpunkMusic raphsCombat;
    public static CyberpunkMusic raphsHostile;
    public static CyberpunkMusic scavsCombat;
    public static CyberpunkMusic scavsHostile;


    private static CyberpunkMusic currentCombatTrack;
    private static CyberpunkMusic currentHostilesTrack;


    public static void StartCombatMusic()
    {
        Minecraft mc = Minecraft.getMinecraft();
        SoundHandler soundHandler = mc.getSoundHandler();

        boolean hasHostileMusic = currentHostilesTrack != null && soundHandler.isSoundPlaying(currentHostilesTrack);

        if (!hasHostileMusic)
        {
            currentTracks = GetNextMusicTrack();
            currentHostilesTrack=null;
            currentCombatTrack=null;
        }

        StopAnyActiveTracks();

        if (currentCombatTrack == null)
        {
            currentCombatTrack = LookupCombatMusic();
        }

        if (soundHandler.isSoundPlaying(currentCombatTrack))
            return;

        soundHandler.playSound(currentCombatTrack);
    }

    @SubscribeEvent
    public static void RegiusterSounds(RegistryEvent.Register<SoundEvent> event)
    {
        cyberAlert = RegisterSound(event, "alert");
        cyberDeath = RegisterSound(event, "death");
        cyber_encounter_win = RegisterSound(event, "encounter_win");


        tigerclawsCombat = CreateMusic(event, "tigerclaws_combat");
        tigerclawsHostile = CreateMusic(event, "tigerclaws_hostile");
        heistCombat = CreateMusic(event, "heist_combat");
        heistHostile = CreateMusic(event, "heist_hostile");
        raphsCombat = CreateMusic(event, "raphs_combat");
        raphsHostile = CreateMusic(event, "raphs_hostile");
        scavsCombat = CreateMusic(event, "scavs_combat");
        scavsHostile = CreateMusic(event, "scavs_hostile");
    }

    private static CyberpunkMusic CreateMusic(RegistryEvent.Register<SoundEvent> event, String name)
    {
        SoundEvent e = RegisterSound(event, name);

        return new CyberpunkMusic(e, SoundCategory.MUSIC);
    }

    private static CyberpunkMusic LookupHostileMusic()
    {
        switch(currentTracks)
        {
            case Scavengers:
                return scavsHostile;
            case Raphens:
                return raphsHostile;
            case Heist:
                return heistHostile;
            case Tigerclaws:
            default:
                return tigerclawsHostile;
        }
    }

    private static CyberpunkMusic LookupCombatMusic()
    {
        switch(currentTracks)
        {
            case Scavengers:
                return scavsCombat;
            case Raphens:
                return raphsCombat;
            case Heist:
                return heistCombat;
            case Tigerclaws:
            default:
                return tigerclawsCombat;
        }
    }

    private static SoundEvent RegisterSound(RegistryEvent.Register<SoundEvent> event, String soundName)
    {
        ResourceLocation location = new ResourceLocation(Cybercraft2077.MODID + ":" + soundName);

        SoundEvent e = new SoundEvent(location);
        e.setRegistryName(soundName);


        event.getRegistry().register(e);

        return e;
    }

    private static MusicTracks GetNextMusicTrack()
    {
        MusicTracks[] tracks = MusicTracks.values();

        MusicTracks result;

        do {
            result = tracks[random.nextInt(tracks.length)];
        } while (result == currentTracks);

        return result;
    }

    public static void StopAnyActiveTracks()
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null)
            return;

        // First we stop any music, including ambient Minecraft music, that's playing right now.
        SoundHandler soundHandler = mc.getSoundHandler();
        soundHandler.stop("", SoundCategory.MUSIC);;

        // Now we can disable default in-game music from playing. This only works if the mod
        // was able to patch MusicTicker, since there is no public Minecraft API for doing this.
        Cybercraft2077.setEnableDefaultMusic(false);
    }

    public static void RestoreMinecraftMusic()
    {
        // First we stop any of our own music
        StopAnyActiveTracks();;

        // Now we turn on MusicTicker
        Cybercraft2077.setEnableDefaultMusic(true);
    }
}


