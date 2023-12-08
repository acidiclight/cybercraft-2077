package dev.acidiclight.cybercraft2077;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Blocks;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;

@Mod(modid = Cybercraft2077.MODID, name = Cybercraft2077.NAME, version = Cybercraft2077.VERSION)
public class Cybercraft2077 {
    public static final String MODID = "cybercraft2077";
    public static final String NAME = "Cybercraft 2077";
    public static final String VERSION = "20.77";

    public static Logger logger;

    private static PatchedMusicTicker patchedMusicTicker;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        // some example code
        logger.info("DIRT BLOCK >> {}", Blocks.DIRT.getRegistryName());

        PatchMusicTicker();
    }

    private static void PatchMusicTicker() {
        // The vanilla MusicTicker doesn't let us disable/enable random music.
        // We must patch it via reflection to replace it with a version that does.
        Class<?> mcClass = Minecraft.class;
        Class<?> mtClass = MusicTicker.class;
        Minecraft mc = Minecraft.getMinecraft();

        Field[] fields = mcClass.getDeclaredFields();

        for (Field field : fields) {
            Class<?> valueClass = field.getType();

            String valueClassName = valueClass.getName();
            String mtClassName = mtClass.getName();

            if (!mtClassName.equals(valueClassName))
                continue;


            patchedMusicTicker = new PatchedMusicTicker(mc);
            field.setAccessible(true);
            ;

            try {
                field.set(mc, patchedMusicTicker);
                setEnableDefaultMusic(true);
            } catch (IllegalAccessException ex) {
                logger.error("Could not patch MusicTicker!");
                logger.error(ex.toString());
            }
            field.setAccessible(false);
            break;
        }
    }

    public static void setEnableDefaultMusic(boolean value)
    {
        if (patchedMusicTicker == null)
        {
            logger.warn("setEnableDefaultMusic called with unsuccessfully-patched MusicTicker, this won't work");
            return;
        }

        patchedMusicTicker.setEnabled(value);
    }
}


