package dev.acidiclight.cybercraft2077;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ITickableSound;
import net.minecraft.client.audio.PositionedSound;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;

public class CyberpunkMusic extends PositionedSound implements ITickableSound {
    private boolean done = false;

    protected CyberpunkMusic(SoundEvent soundIn, SoundCategory categoryIn) {
        super(soundIn, categoryIn);
        repeat = true;
        volume = 1;
        pitch = 1;
        repeatDelay=0;
        attenuationType = AttenuationType.NONE;
    }

    @Override
    public boolean isDonePlaying() {
        return done;
    }

    @Override
    public void update() {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null) {
            this.done = true;
            return;
        }

        BlockPos playerPos = mc.player.getPosition();
        this.xPosF = playerPos.getX();
        this.yPosF = playerPos.getY();
        this.zPosF = playerPos.getZ();
    }
}
