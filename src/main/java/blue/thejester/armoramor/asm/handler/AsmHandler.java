package blue.thejester.armoramor.asm.handler;

import net.minecraft.inventory.EntityEquipmentSlot;

public class AsmHandler {
    public static float limitDefense(float val) {
        float base = val > 18f ? 18f : val;
        float softA = (val > 22f) ? 4f : ((val > 18f) ? (val-18) : 0);
        float softB = (val > 22f) ? val-22 : 0;
        return base + (softA / 2f) + (softB / 6f);
    }
}
