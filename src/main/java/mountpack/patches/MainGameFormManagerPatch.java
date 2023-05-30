package mountpack.patches;

import mountpack.MountPack;
import mountpack.form.ToggleNightVisionComponent;
import mountpack.mobs.BatMountMob;
import necesse.engine.Screen;
import necesse.engine.modLoader.annotations.ModConstructorPatch;
import necesse.engine.modLoader.annotations.ModMethodPatch;
import necesse.engine.network.client.Client;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.MainGameFormManager;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import net.bytebuddy.asm.Advice;

public class MainGameFormManagerPatch {

    //public static Form toggleNV;
    public static FormLocalTextButton toggleNVButton;
    public static int toolbarHeight;
    public static MainGameFormManager formManager;

    @ModMethodPatch(target = MainGameFormManager.class, name="setup", arguments = {})
    public static class setupPatch {
        @Advice.OnMethodExit
        public static void onExit(@Advice.This MainGameFormManager setupFormManager) {

            toolbarHeight = setupFormManager.toolbar.getHeight();
            formManager = setupFormManager;

            MountPack.toggleNV = setupFormManager.addComponent(new Form("toggle", 200, 30));
            MountPack.toggleNV.drawBase = false;
            MountPack.toggleNV.addComponent(new ToggleNightVisionComponent(MountPack.toggleNV.getWidth() - 10));
            MountPack.toggleNV.setHidden(true);

        }
    }

    @ModMethodPatch(target = MainGameFormManager.class, name = "frameTick", arguments = {TickManager.class})
    public static class frameTickPatch {
        @Advice.OnMethodExit
        public static void onExit() {
            
            if (formManager.inventory.isHidden()) {
                MountPack.toggleNV.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() - formManager.toolbar.getHeight() - 50);
            } else {
                MountPack.toggleNV.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() - formManager.toolbar.getHeight() - 230);
            }
                
        }
    }

    @ModMethodPatch(target = MainGameFormManager.class, name = "onWindowResized", arguments = {})
    public static class windowResizePatch {
        @Advice.OnMethodExit
        public static void onExit() {
            if (MountPack.toggleNV == null) return;
            MountPack.toggleNV.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() - toolbarHeight - 50);
        }
    }
}
