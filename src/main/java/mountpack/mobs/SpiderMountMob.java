package mountpack.mobs;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;

import necesse.engine.localization.Localization;
import necesse.engine.modifiers.ModifierValue;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.MobDrawable;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.mobs.summon.summonFollowingMob.mountFollowingMob.MountFollowingMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.TextureDrawOptionsEnd;
import necesse.gfx.drawables.OrderableDrawables;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.item.Item;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;
import necesse.level.maps.light.GameLight;

public class SpiderMountMob extends MountFollowingMob {

  public static GameTexture texture;
  public static GameTexture textureShadow;
  public static GameTexture textureMask;
  public static boolean canHaveSpiders = true;
  
  public SpiderMountMob() {
    super(100);
    setSpeed(90.0F);
    setFriction(10.0F);
    setSwimSpeed(1.0F);
    setKnockbackModifier(0.2F);
    this.collision = new Rectangle(-20, -20, 40, 40);
    this.hitBox = new Rectangle(-30, -25, 60, 50);
    this.selectBox = new Rectangle(-40, -45, 80, 60);
    this.overrideMountedWaterWalking = false; 
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
    if (!isMounted()) {
      this.moveX = 0.0F;
      this.moveY = 0.0F;
    }
  }
  
  protected String getInteractTip(PlayerMob perspective, boolean debug) {
    if (isMounted())
      return null; 
    return Localization.translate("controls", "usetip");
  }

  public void addDrawables(List<MobDrawable> list, OrderableDrawables tileList, OrderableDrawables topList, Level level, int x, int y, TickManager tickManager, GameCamera camera, PlayerMob perspective) {
    super.addDrawables(list, tileList, topList, level, x, y, tickManager, camera, perspective);
    GameLight light = level.getLightLevel(x / 32, y / 32);
    int drawX = camera.getDrawX(x) - 48;
    int drawY = camera.getDrawY(y) - 60;

    Point sprite;

    if (isAccelerating()) {
       sprite = getAnimSprite(x, y, this.dir);
    } else {
        sprite = getIdleAnimSprite(x, y, this.dir);
    }

    PlayerMob player = getFollowingPlayer();

    // There was probably a better way... 
    if (player != null) {
        // Moving right, facing left
        if (player.isAttacking && moveX > 0.0F && player.dir == 3)
            drawX -= 35;
        // Moving left, facing right
        if (player.isAttacking && moveX < 0.0F && player.dir == 1)
            drawX += 35;
        // Moving down, facing left
        if (player.isAttacking && moveY < 0.0F && player.dir == 3)
            drawX -= 18;
        // Moing down, facing right
        if (player.isAttacking && moveY < 0.0F && player.dir == 1)
            drawX += 18;
        // Moving up, facing right
        if (player.isAttacking && moveY > 0.0F && player.dir == 1)
            drawX += 18;
        // Moving up, facing left
        if (player.isAttacking && moveY > 0.0F && player.dir == 3)
            drawX -= 18;
        // Moving down, moving right, facing right
        if (player.isAttacking && moveY < 0.0F && moveX > 0.0F && player.dir == 1)
            drawX -= 0;
        // Moving down, moving left, facing right
        if (player.isAttacking && moveY < 0.0F && moveX < 0.0F && player.dir == 1)
            drawX -= 35;
        // Moving up, moving left, facing right
        if (player.isAttacking && moveY > 0.0F && moveX < 0.0F && player.dir == 1)
            drawX -= 35;
        // Moving up, moving right, facing right
        if (player.isAttacking && moveY > 0.0F && moveX > 0.0F && player.dir == 1)
            drawX += 0;
        // Moving down, moving right, facing left
        if (player.isAttacking && moveY < 0.0F && moveX > 0.0F && player.dir == 3)
            drawX += 35;
        // Moving down, moving left, facing left
        if (player.isAttacking && moveY < 0.0F && moveX < 0.0F && player.dir == 3)
            drawX -= 0;
        // Moving up, moving left, facing left
        if (player.isAttacking && moveY > 0.0F && moveX < 0.0F && player.dir == 3)
            drawX -= 0;
        // Moving up, moving right, facing left
        if (player.isAttacking && moveY > 0.0F && moveX > 0.0F && player.dir == 3)
            drawX += 35;
    }
        
    final TextureDrawOptionsEnd options = texture.initDraw().sprite(sprite.x, sprite.y, 96).light(light).pos(drawX, drawY);
    list.add(new MobDrawable() {
          public void draw(TickManager tickManager) {}
          public void drawBehindRider(TickManager tickManager) {
            options.draw();
          }
        });
    TextureDrawOptionsEnd textureDrawOptionsEnd2 = textureShadow.initDraw().sprite(sprite.x, sprite.y, 96).light(light).pos(drawX, drawY);
    tileList.add(tm -> textureDrawOptionsEnd2.draw());
  }
  
  public Point getAnimSprite(int x, int y, int dir) {
    return new Point((int)(getWorldEntity().getTime() / getRockSpeed()) % 4, dir % 4);
  }

  public Point getIdleAnimSprite(int x, int y, int dir) {
    return new Point((int)(getWorldEntity().getTime() / 2000) % 4, dir % 4);
  }

  public int getRockSpeed() {
    return 100;
  }
  
  public int getWaterRockSpeed() {
    return 100;
  }

  public CollisionFilter getLevelCollisionFilter() {
    return super.getLevelCollisionFilter().allLiquidTiles();
  }
  
  public Point getSpriteOffset(int spriteX, int spriteY) {
    Point p = new Point(0, 0);
    if (isAccelerating()) {
        if (spriteX == 1 || spriteX == 2)
        p.y = 2; 
    }
    p.x += getRiderDrawXOffset();
    p.y += getRiderDrawYOffset();
    return p;
  }
  
  public int getRiderDrawYOffset() {
    PlayerMob player = getFollowingPlayer();
    int yOffset = 0;
    if (player != null) {
        if ( player.dir == 1 || player.dir == 3) {
            yOffset = -25;
            return yOffset;
        } else {
            yOffset = -25;
            return yOffset;
        }
    } else {
        return yOffset;
    }
  }

  public int getRiderDrawXOffset() {
    PlayerMob player = getFollowingPlayer();
    int xOffset = 0;
    if (player != null) {
        if (player.dir == 1 || player.dir == 3) {
            if (player.dir == 1) {
                xOffset = 18;
            }
            if (player.dir == 3) {
                xOffset = -18;
            }
            return xOffset;
        } else {
            return xOffset;
        }
    } else {
        return xOffset;
    }
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

  public ModifierValue<?>[] getDefaultModifiers() {
    if (isMounted())
      return new ModifierValue[] { new ModifierValue<>(BuffModifiers.SLOW, 0.0F).max(0F) };
    return new ModifierValue[]{};
  }

  public ModifierValue<?>[] getDefaultRiderModifiers() {
    PlayerMob following = getFollowingPlayer();

    Item spiderstaff = ItemRegistry.getItem("spiderstaff");
    Item cryostaff = ItemRegistry.getItem("cryostaff");
    Item frostpiercer = ItemRegistry.getItem("frostpiercer");
    Item magicbranch = ItemRegistry.getItem("magicbranch");
    Item slimecanister = ItemRegistry.getItem("slimecanister");
    Item vulturestaff = ItemRegistry.getItem("vulturestaff");
    Item skeletonstaff = ItemRegistry.getItem("skeletonstaff");

    if (
        following != null && following.isAttacking && following.attackingItem.item == cryostaff ||
        following != null && following.isAttacking && following.attackingItem.item == frostpiercer ||
        following != null && following.isAttacking && following.attackingItem.item == magicbranch ||
        following != null && following.isAttacking && following.attackingItem.item == slimecanister ||
        following != null && following.isAttacking && following.attackingItem.item == vulturestaff ||
        following != null && following.isAttacking && following.attackingItem.item == skeletonstaff
    )
        canHaveSpiders = false;
        

    else if (following != null && following.isAttacking && following.attackingItem.item == spiderstaff)
        canHaveSpiders = true;

    if (canHaveSpiders)
        return new ModifierValue[]{ new ModifierValue<>(BuffModifiers.MAX_SUMMONS, 10) };
    return new ModifierValue[]{ new ModifierValue<>(BuffModifiers.MAX_SUMMONS, -100) };

  }
  
}
