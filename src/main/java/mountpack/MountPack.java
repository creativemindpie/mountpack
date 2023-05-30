package mountpack;

import mountpack.mobs.*;
import necesse.engine.registries.*;
import mountpack.items.BatMountItem;
import mountpack.items.SpiderMountItem;
import necesse.engine.modLoader.annotations.ModEntry;
import necesse.gfx.forms.Form;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.lootTable.lootItem.ChanceLootItem;
import necesse.entity.mobs.hostile.bosses.EvilsProtectorMob;
import necesse.entity.mobs.hostile.bosses.QueenSpiderMob;

@ModEntry
public class MountPack {

    public static Form toggleNV;

    public void init() {

        // Register our items
        ItemRegistry.registerItem("batmount", new BatMountItem(), 35000, true);
        ItemRegistry.registerItem("spidermount", new SpiderMountItem(), 35000, true);

        // Register our mob
        MobRegistry.registerMob("batmount", BatMountMob.class, false); 
        MobRegistry.registerMob("spidermount", SpiderMountMob.class, false); 
        
        // Add mounts to boss loot tables
        EvilsProtectorMob.lootTable.items.add(new ChanceLootItem(0.2F, "batmount"));
        QueenSpiderMob.lootTable.items.add(new ChanceLootItem(0.2F, "spidermount"));

        
    }

    public void initResources() {
        BatMountMob.texture = GameTexture.fromFile("mobs/batmount");
        BatMountMob.textureShadow = GameTexture.fromFile("mobs/batmount_shadow");
        BatMountMob.textureMask = GameTexture.fromFile("mobs/spidermount_mask");

        SpiderMountMob.texture = GameTexture.fromFile("mobs/spidermount");
        SpiderMountMob.textureShadow = GameTexture.fromFile("mobs/spidermount_shadow");
        SpiderMountMob.textureMask = GameTexture.fromFile("mobs/spidermount_mask");
        
    }

}
