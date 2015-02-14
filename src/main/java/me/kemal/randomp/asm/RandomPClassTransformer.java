package me.kemal.randomp.asm;

import cpw.mods.fml.relauncher.FMLRelaunchLog;
import net.minecraft.launchwrapper.IClassTransformer;

public class RandomPClassTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (basicClass == null)
			return null;

		FMLRelaunchLog.info("RandomPTransformerClass opened");
		if (transformedName.equals("dan200.computercraft.api.ComputercraftAPI"))
			FMLRelaunchLog.info("CC Klasse zu transformieren");
		
		return basicClass;
	}

}
