package dev.acidiclight.cybercraft2077;

import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.SoundHandler;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.boss.EntityDragon;
import net.minecraft.entity.boss.EntityWither;
import net.minecraft.entity.monster.EntityGolem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingSetAttackTargetEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientEventListeners
{
    // Determines how many blocks on the X and Z planer to scan for enemies or dangers.
    private static int areaDistance = 45;

    // Determines how many blocks worth of height to scan for enemies and dangers.
    private static int areaHeight = 10;

    // Area around the player
    private static Bounds areaBounds = new Bounds();

    private static int hostilesCount;

    private static int attackTicks = 0;
    private static boolean underAttack = false;

    private static AreaState areaState = AreaState.Safe;

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void OnRenderLiving(RenderLivingEvent.Pre event)
    {
        EntityLivingBase entity = event.getEntity();
        if (entity == null)
            return;

        // Get location of the entity as a point
        Point entityLocation = new Point(
                entity.posX,
                entity.posY,
                entity.posZ
        );

        // Check if it's near the player
        if (!areaBounds.Contains(entityLocation))
            return;

        if (entity.isDead)
            return;

        if (entity instanceof EntityAnimal)
            return;

        // if eney is monster
        //      return;

        String entityName = entity.getName();

        hostilesCount++;
    }

    @SubscribeEvent(priority = EventPriority.HIGH)
    public static void OnTick(TickEvent.ClientTickEvent event)
    {
        // Make sure we only execute on the start phase of a tick
        if (event.phase != TickEvent.Phase.START)
            return;

        TickStartPhase();
    }

    private static void CountEntitiesNearPlayer(Minecraft mc)
    {
        double x = areaBounds.center.x;
        double y = areaBounds.center.y;
        double z = areaBounds.center.z;

        AxisAlignedBB boundingBox = new AxisAlignedBB(x - areaBounds.extents.x, y - areaBounds.extents.y, z - areaBounds.extents.z, x + areaBounds.extents.x, y + areaBounds.extents.y, z + areaBounds.extents.z);;

        hostilesCount = 0;
        for (Entity entity : mc.world.getEntitiesWithinAABB(EntityLivingBase.class, boundingBox))
        {
            if (entity.isDead)
                continue;

            if (entity instanceof EntityDragon || entity instanceof EntityWither)
            {
                // Force Combat mode when near an Ender Dragon or Wither boss.
                underAttack=true;
                continue;
            }

            if (entity instanceof EntityMob)
            {
                Cybercraft2077.logger.info(entity.getName());
                hostilesCount++;
                continue;
            }
        }

    }

    private static void TickStartPhase()
    {
        if (underAttack)
        {
            if (attackTicks > 0)
            {
                attackTicks--;
            }
            else
            {
                underAttack=false;
            }
        }

        // Get Minecraft itself
        // If we can't then return.
        Minecraft minecraft = Minecraft.getMinecraft();
        if (minecraft == null)
            return;

        // Check if we have a player. If we don't, we aren't in a world.
        if (minecraft.player == null)
            return;

        // Update the player location. This is where we scan for hostiles and dangers from.
        areaBounds.center.x = minecraft.player.posX;
        areaBounds.center.y = minecraft.player.posY;
        areaBounds.center.z = minecraft.player.posZ;

        // Set up area boundaries
        areaBounds.extents.x = areaDistance;
        areaBounds.extents.z = areaBounds.extents.x;
        areaBounds.extents.y = areaHeight;

        // Count hostiles
        CountEntitiesNearPlayer(minecraft);
    }

    private static AreaState DetermineNextAreaState()
    {
        Minecraft mc = Minecraft.getMinecraft();

        if (mc.world == null)
            return AreaState.Safe;

        if (mc.player == null)
            return AreaState.Safe;

        // In Creative and Spectator mode, enemies will never hurt the player.
        if (mc.player.isCreative() || mc.player.isSpectator())
            return AreaState.Safe;

        // Clear combat state if the player's dead. This prevents a bug where
        // respawns cause an encounter win sound to play.
        if (mc.player.isDead && areaState == AreaState.Combat)
        {
            underAttack = false;
            attackTicks=0;
        }

        if (hostilesCount == 0)
        {
            attackTicks=0;
            underAttack=false;
            return AreaState.Safe;
        }
        if (underAttack)
            return AreaState.Combat;
        else if (hostilesCount > 0 && areaState == AreaState.Combat)
            return AreaState.Combat;

        return AreaState.Hostile;
    }

    private static void PlayAlertSound()
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null)
            return;

        if (mc.world == null)
            return;

        mc.world.playSound(mc.player, areaBounds.center.x, areaBounds.center.y, areaBounds.center.z, CyberSoundEffects.cyberAlert, SoundCategory.MASTER, 1, 1);
    }

    private static void PlayEncounterWinSound()
    {
        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null)
            return;

        if (mc.world == null)
            return;

        // We do not play this sound if the player is dead. This prevents
        // fatal creeper explosions from triggering the win sound when no other enemies are nearby.
        if (mc.player.isDead)
            return;

        mc.world.playSound(mc.player, areaBounds.center.x, areaBounds.center.y, areaBounds.center.z, CyberSoundEffects.cyber_encounter_win, SoundCategory.MASTER, 1, 1);
    }

    private static void StartCombatMusic()
    {
        CyberSoundEffects.StartCombatMusic();
    }

    private static void UpdateAreaState()
    {
        AreaState lastState = areaState;

        AreaState newState = DetermineNextAreaState();

        if (newState != lastState)
        {
            switch (newState)
            {
                case Safe:
                    if (lastState == AreaState.Combat)
                    {
                        PlayEncounterWinSound();
                    }

                    CyberSoundEffects.RestoreMinecraftMusic();;
                    break;
                case Combat:
                    StartCombatMusic();;
                    PlayAlertSound();
                    break;
            }
        }

        areaState = newState;
    }

    private static String GetAreaText()
    {
        switch (areaState)
        {
            case Safe:
                return "Area: SAFE";
            case Hostile:
                return "Area: HOSTILE";
            case Combat:
                return "COMBAT";
            default:
                return null;
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void OnPlayerAttacked(LivingSetAttackTargetEvent event)
    {
        Entity target = event.getTarget();

        if (!(target instanceof EntityPlayer))
            return;

        EntityPlayer targetPlayer = (EntityPlayer) target;

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player == null)
            return;

        if (mc.world == null)
            return;

        if (!mc.player.getName().equals(targetPlayer.getName()))
            return;

        underAttack = true;
        attackTicks = 100;
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void OnLivingDeath(LivingDeathEvent event)
    {
        if (event.getEntity() == null)
            return;

        Entity entity = event.getEntity();

        if (entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer) entity;
            Minecraft mc = Minecraft.getMinecraft();

            if (!mc.player.getName().equals(player.getName()))
                return;

            if (mc.world == null)
                return;

            if (areaState != AreaState.Safe)
                CyberSoundEffects.RestoreMinecraftMusic();;

            underAttack=false;
            attackTicks=0;
            mc.world.playSound(mc.player, areaBounds.center.x, areaBounds.center.y, areaBounds.center.z, CyberSoundEffects.cyberDeath, SoundCategory.MASTER, 1, 1);
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void OnRenderOverlay(RenderGameOverlayEvent.Pre event)
    {
        String areaIndicatorText = GetAreaText();
        if (areaIndicatorText == null)
            return;

        Minecraft mc = Minecraft.getMinecraft();
        FontRenderer fontRenderer = mc.fontRenderer;

        int displayWidth = event.getResolution().getScaledWidth();
        int displayHeight = event.getResolution().getScaledHeight();

        int rectWidth = 175;
        int rectVertPadding = 4;
        int textHeight = fontRenderer.getWordWrappedHeight(areaIndicatorText, rectWidth);
        int rectHeight = textHeight + rectVertPadding;
        int textWidth = fontRenderer.getStringWidth(areaIndicatorText);

        int rectX = (displayWidth - rectWidth) / 2;
        int rectY = 15;

        int textX = rectX + ((rectWidth - textWidth) / 2);
        int textY = rectY + (rectVertPadding / 2);

        if (event.getType() != RenderGameOverlayEvent.ElementType.HOTBAR)
            return;

        UpdateAreaState();

        int bgColor = 0;

        if (areaState == AreaState.Safe)
            bgColor = 0xff1bf7aa;
        else if (areaState == AreaState.Hostile)
            bgColor = 0xfff7f71b;
        else if (areaState == AreaState.Combat)
            bgColor = 0xfff71b1b;




        Gui.drawRect(rectX, rectY, rectX + rectWidth,rectY + rectHeight, bgColor);

        fontRenderer.drawStringWithShadow(areaIndicatorText, textX, textY, 0xffffffff);
    }
}

