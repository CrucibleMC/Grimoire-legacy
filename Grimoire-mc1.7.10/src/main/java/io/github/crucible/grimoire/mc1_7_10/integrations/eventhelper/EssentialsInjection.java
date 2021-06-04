//package io.github.crucible.grimoire.mc1_7_10.integrations.eventhelper;
//
//import org.bukkit.Bukkit;
//
//import com.earth2me.essentials.Essentials;
//import com.gamerforea.eventhelper.util.ConvertUtils;
//import com.gamerforea.eventhelper.util.InjectionUtils;
//
//import net.minecraft.entity.player.EntityPlayer;
//
//public class EssentialsInjection {
//
//    public static IEssentialsInjection getInjection() {
//        Class<?> clazz = InjectionUtils.injectClass("Essentials", EssentialsInjection.class);
//        if (clazz != null) {
//            try {
//                return (IEssentialsInjection) clazz.newInstance();
//            } catch (Throwable throwable) {
//                throwable.printStackTrace();
//            }
//        }
//        return null;
//    }
//
//    public static final class Inj implements IEssentialsInjection {
//        @Override
//        public boolean inGodMode(EntityPlayer player) {
//            try {
//                return this.getEssPlugin().getUser(ConvertUtils.toBukkitEntity(player)).isGodModeEnabled();
//            } catch (Exception e) {
//                return false;
//            }
//        }
//
//        private Essentials getEssPlugin() {
//            return (Essentials) Bukkit.getPluginManager().getPlugin("Essentials");
//        }
//    }
//}
