package com.dclock.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.lang.ref.SoftReference;
import java.util.Hashtable;

public final class UIHelper
{
        private static final String TagIdentifier = "UIHelper";
        private static final Hashtable<String, SoftReference<Typeface>> fontCache = new Hashtable<String, SoftReference<Typeface>>();

        public static void setCustomFont(View view, Context ctx, AttributeSet attrs, int[] attributeSets, int fontId)
        {
            TypedArray a = ctx.obtainStyledAttributes(attrs, attributeSets);
            String customFont = a.getString(fontId);
            setCustomFont(view, ctx, customFont);
            a.recycle();
        }

        private static boolean setCustomFont(View view, Context ctx, String asset)
        {
            if (asset == null || asset.isEmpty())
            {
                return false;
            }

            Typeface tf = null;
            try {
                tf = getFont(ctx, asset);
                if (view instanceof TextView)
                {
                    ((TextView) view).setTypeface(tf);
                } else
                {
                    ((Button) view).setTypeface(tf);
                }
            } catch (Exception e)
            {
                Log.e(TagIdentifier, "Could not get typeface: " + asset, e);
                return false;
            }

            return true;
        }

        public static Typeface getFont(Context c, String name)
        {
            synchronized (fontCache)
            {
                if (fontCache.get(name) != null)
                {
                    SoftReference<Typeface> ref = fontCache.get(name);
                    if (ref.get() != null)
                    {
                        return ref.get();
                    }
                }

                Typeface typeface = Typeface.createFromAsset(
                        c.getAssets(),
                        "fonts/" + name
                );
                fontCache.put(name, new SoftReference<Typeface>(typeface));

                return typeface;
            }
        }
}