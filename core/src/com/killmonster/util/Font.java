package com.killmonster.util;

import com.killmonster.GameStateManager;
import com.badlogic.gdx.assets.loaders.FileHandleResolver;
import com.badlogic.gdx.assets.loaders.resolvers.InternalFileHandleResolver;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGeneratorLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader;
import com.badlogic.gdx.graphics.g2d.freetype.FreetypeFontLoader.FreeTypeFontLoaderParameter;

public class Font {

	private static final String FONT_FILE = "interface/font/Vormgevers.ttf";
	private static BitmapFont defaultFont;
	
	private GameStateManager gsm;
	
	public Font(GameStateManager gsm) {
		this.gsm = gsm;
		
		FileHandleResolver resolver = new InternalFileHandleResolver();
		gsm.getAssets().setLoader(FreeTypeFontGenerator.class, new FreeTypeFontGeneratorLoader(resolver));
		gsm.getAssets().setLoader(BitmapFont.class, ".ttf", new FreetypeFontLoader(resolver));
		
		// Next, let's define the params and then load our bigger font
		FreeTypeFontLoaderParameter font = new FreeTypeFontLoaderParameter();
		font.fontFileName = FONT_FILE;
		font.fontParameters.size = 16;
		gsm.getAssets().load(FONT_FILE, BitmapFont.class, font);
		gsm.getAssets().finishLoading();
		
		defaultFont = gsm.getAssets().get(FONT_FILE, BitmapFont.class);
	}

	public BitmapFont getDefaultFont() {
		return defaultFont;
	}
}