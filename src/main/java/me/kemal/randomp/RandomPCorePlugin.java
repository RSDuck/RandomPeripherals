package me.kemal.randomp;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public class RandomPCorePlugin implements IFMLLoadingPlugin {

	@Override
	public String[] getASMTransformerClass() {
		return new String[] { "me.kemal.randomp.asm.RandomPClassTransformer" };
	}

	@Override
	public String getModContainerClass() {
		return null;
	}

	@Override
	public String getSetupClass() {
		return null;
	}

	@Override
	public void injectData(Map<String, Object> data) {
	}

	@Override
	public String getAccessTransformerClass() {
		return null;
	}

}
