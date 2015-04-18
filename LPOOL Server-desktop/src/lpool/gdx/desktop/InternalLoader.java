package lpool.gdx.desktop;

import aurelienribon.bodyeditor.BodyEditorLoader;

import com.badlogic.gdx.Gdx;

public class InternalLoader {

	public InternalLoader() {
	}
	
	public static BodyEditorLoader loadBodyFromJSON(String fileName)
	{
		return new BodyEditorLoader(Gdx.files.internal(fileName));
	}
}
