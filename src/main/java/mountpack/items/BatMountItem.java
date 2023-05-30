package mountpack.items;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.inventory.item.mountItem.MountItem;

public class BatMountItem extends MountItem {
  public BatMountItem() {
    super("batmount");
    this.rarity = Item.Rarity.LEGENDARY;
  }
  
  public ListGameTooltips getTooltips(InventoryItem item, PlayerMob perspective) {
    ListGameTooltips tooltips = super.getTooltips(item, perspective);
    tooltips.add(Localization.translate("itemtooltip", "batmounttip"));
    return tooltips;
  }
}