package mountpack.form;

import mountpack.mobs.BatMountMob;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.ui.ButtonColor;

public class ToggleNightVisionComponent extends FormLocalTextButton {

    public void toggleNV() {
        BatMountMob.enableNV = !BatMountMob.enableNV;
    }

    public ToggleNightVisionComponent(int width) {
        super("ui", "toggleNV", 5, 5, width, FormInputSize.SIZE_24, ButtonColor.BASE);
        onClicked(event -> {
            toggleNV();
        });
    }
}
