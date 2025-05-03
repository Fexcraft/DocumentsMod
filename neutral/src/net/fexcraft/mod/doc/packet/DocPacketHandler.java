package net.fexcraft.mod.doc.packet;

import net.fexcraft.app.json.JsonMap;
import net.fexcraft.mod.uni.IDL;
import net.fexcraft.mod.uni.world.EntityW;

/**
 * @author Ferdinand Calo' (FEX___96)
 */
public abstract class DocPacketHandler {

    public static DocPacketHandler INSTANCE = null;

    public abstract void sendSync(EntityW player, JsonMap map);

}
