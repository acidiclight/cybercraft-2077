package dev.acidiclight.cybercraft2077;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.MusicTicker;
import net.minecraft.util.ITickable;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class PatchedMusicTicker extends MusicTicker implements ITickable
{
    private boolean enableMusic = false;

    public PatchedMusicTicker(Minecraft mcIn) {
        super(mcIn);
    }

    @Override
    public void update()
    {
        if (enableMusic)
            super.update();
    }

    public void setEnabled(boolean value)
    {
        this.enableMusic=value;
    }
}
