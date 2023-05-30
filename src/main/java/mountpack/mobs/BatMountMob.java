package mountpack.mobs;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import mountpack.MountPack;
import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.state.MainGame;
import necesse.engine.tickManager.TickManager;
import necesse.engine.world.WorldEntity;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.MountFollowingMob;
import necesse.gfx.GameResources;
import necesse.gfx.PlayerSprite;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.DrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptions;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.MainGameFormManager;
import necesse.gfx.gameTexture.GameTexture;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class BatMountMob extends MountFollowingMob {

  public static GameTexture texture;
  public static GameTexture textureShadow;
  public static GameTexture textureMask;

  public static boolean isBatMounted;
  public static boolean enableNV;

  public static Form toggleNV;
  
  public BatMountMob() {
    super(100);
    setSpeed(110.0F);
    setFriction(1.0F);
    setSwimSpeed(1.0F);
    this.collision = new Rectangle(-10, -7, 20, 14);
    this.hitBox = new Rectangle(-14, -14, 28, 28);
    this.selectBox = new Rectangle(-15, -15, 30, 30);
    this.overrideMountedWaterWalking = true;
  }

  public void serverTick() {
    super.serverTick();
    if (!isMounted()) {
      this.moveX = 0.0F;
      this.moveY = 0.0F;
    }
  }
  
  public void clientTick() {
    super.clientTick();

    PlayerMob player = getFollowingPlayer();

    if (player != null && player.getLevel() != null && getLevel().getClient().getPlayer() == player && isMounted() && enableNV) {

      WorldEntity playerWorld = player.getWorldEntity();

        if (GameResources.debugColorShader != null && playerWorld.isNight()) {
          GameResources.debugColorShader.use();
          GameResources.debugColorShader.pass1f("green", 1.6F);
          GameResources.debugColorShader.pass1f("contrast", 0.6F);
          GameResources.debugColorShader.stop();
        }

    } else {

      if (GameResources.debugColorShader != null) {
        GameResources.debugColorShader.use();
        GameResources.debugColorShader.pass1f("green", 1.0F);
        GameResources.debugColorShader.pass1f("contrast", 1.0F);
        GameResources.debugColorShader.stop();
      }

    }

    if (player != null && player.getLevel() != null && getLevel().getClient().getPlayer() == player && isMounted()) {
      //isBatMounted = true;
      MountPack.toggleNV.setHidden(false);
    } else {
      if (MountPack.toggleNV != null)
        MountPack.toggleNV.setHidden(true);
    }

  }

  public int getFlyingHeight() {
    return 1000;
  }
  
  protected String getInteractTip(PlayerMob perspective, boolean debug) {
    if (isMounted())
      return null; 
    return Localization.translate("controls", "usetip");
  }

  public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
    GameLight light = level.getLightLevel(x / 32, y / 32);
    int drawX = camera.getDrawX(x) - 32;
    int drawY = camera.getDrawY(y) - 40;
    Point sprite = getAnimSprite(x, y, this.dir);
    drawY += getBobbing(x, y);

    PlayerMob player = getFollowingPlayer();

    final TextureDrawOptionsEnd options = texture.initDraw().sprite(sprite.x, sprite.y, 64).light(light).pos(drawX, drawY);
    list.add(new MobDrawable() {
          public void draw(TickManager tickManager) {}
          
          public void drawBehindRider(TickManager tickManager) {
            options.draw();
          }
        });

    topList.add(tm -> {
      //PlayerMob player = getFollowingPlayer();
      options.draw();
      if (player != null && isMounted()) {
        DrawOptions playerSprite = PlayerSprite.getDrawOptions(player, x, y, light, camera, null);
        playerSprite.draw();
      }
    });

    addShadowDrawables(topList, x, y, light, camera);
  }
  
  public Point getAnimSprite(int x, int y, int dir) {
    return new Point((int)(getWorldEntity().getTime() / getRockSpeed()) % 4, dir % 4);
  }
  
  public TextureDrawOptions getShadowDrawOptions(int x, int y, GameLight light, GameCamera camera) {
    GameTexture shadowTexture = textureShadow;
    int drawX = camera.getDrawX(x) - 32;
    int drawY = camera.getDrawY(y) - 40;
    drawY += getBobbing(x, y);
    drawY += 70;
    return (TextureDrawOptions)shadowTexture.initDraw().sprite(0, this.dir, 64).light(light).pos(drawX, drawY);
  }

  public int getRockSpeed() {
    return 500;
  }
  
  public int getWaterRockSpeed() {
    return 500;
  }
  
  public Point getSpriteOffset(int spriteX, int spriteY) {
    Point p = new Point(0, 0);
    if (spriteX == 0 || spriteX == 2)
      p.y = -1; 
    p.x += getRiderDrawXOffset();
    p.y += getRiderDrawYOffset();
    return p;
  }
  
  public int getRiderDrawYOffset() {
    return -8;
  }
  
  public int getRiderArmSpriteX() {
    return 1;
  }
  
  public int getRiderDir(int startDir) {
    return (startDir) % 4;
  }

  public GameTexture getRiderMask() {
    return textureMask;
  }

  public int getRiderMaskYOffset() {
    return -9;
  }

  public CollisionFilter getLevelCollisionFilter() {
    PlayerMob player = getFollowingPlayer();

    if (player != null)
      if (player.getLevel() != null && player.getLevel().isCave)
        return super.getLevelCollisionFilter().overrideFilter(tp -> ((tp.object()).object.isWall || (tp.object()).object.isRock));
    return super.getLevelCollisionFilter().overrideFilter(tp -> ((tp.object()).object.isWall));
  }
  
  public ModifierValue<?>[] getDefaultRiderModifiers() {
    return (ModifierValue<?>[])new ModifierValue[] { 
        new ModifierValue<>(BuffModifiers.TRAVEL_DISTANCE, Integer.valueOf(3)), 
        new ModifierValue<>(BuffModifiers.WATER_WALKING, Boolean.valueOf(true))
      };
  }
  
}
