package com.killmonster.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;

public class Message extends Label {

    private float lifetime;
    private float timer;

    public Message (CharSequence text, Skin skin, float lifetime) {
        super(text, skin);
        this.lifetime = lifetime;
    }

    public Message (CharSequence text, Skin skin, String styleName, float lifetime) {
        super(text, skin, styleName);
        this.lifetime = lifetime;
    }

    /** Creates a message label, using a {@link LabelStyle} that has a BitmapFont with the specified name
     * from the skin and the specified color. */
    public Message (CharSequence text, Skin skin, String fontName, Color color, float lifetime) {
        super(text, skin, fontName, color);
        this.lifetime = lifetime;
    }

    /** Creates a label, using a {@link LabelStyle} that has a BitmapFont with the specified name
     * and the specified color from the skin. */
    public Message (CharSequence text, Skin skin, String fontName, String colorName, float lifetime) {
        super(text, skin, fontName, colorName);
        this.lifetime = lifetime;
    }

    public Message (CharSequence text, LabelStyle style, float lifetime) {
        super(text, style);
        this.lifetime = lifetime;
    }


    public void update(float delta) {
        this.timer += delta;
    }

    public boolean hasExpired() {
        return timer >= lifetime;
    }

}