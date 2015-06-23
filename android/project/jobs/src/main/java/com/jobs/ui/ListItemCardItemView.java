package com.jobs.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.dexafree.materialList.cards.internal.BaseTextCardItemView;

/**
 * Created by Jondar on 6/23/2015.
 */
public class ListItemCardItemView extends BaseTextCardItemView<ListItemCard> {
    public ListItemCardItemView(Context context) {
        super(context);
    }

    public ListItemCardItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ListItemCardItemView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void build(final ListItemCard card) {
        super.build(card);
        TextView subtitle = (TextView)this.findViewById(com.dexafree.materialList.R.id.subtitleTextView);
        subtitle.setText(card.getSubtitle());
        subtitle.setTextColor(card.getSubtitleColor());
        final TextView button = (TextView)this.findViewById(com.dexafree.materialList.R.id.ok_button);
        button.setText(card.getButtonText());
        button.setTextColor(card.getButtonTextColor());
        Drawable drawable = button.getCompoundDrawables()[0];
        drawable.setColorFilter(card.getButtonTextColor(), PorterDuff.Mode.SRC_IN);
        button.setCompoundDrawablesWithIntrinsicBounds(this.resize(drawable, 50, 50), (Drawable)null, (Drawable)null, (Drawable)null);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if(card.getOnButtonPressedListener() != null) {
                    card.getOnButtonPressedListener().onButtonPressedListener(button, card);
                }

            }
        });
    }

    private Drawable resize(Drawable image, int width, int height) {
        Bitmap b = ((BitmapDrawable)image).getBitmap();
        Bitmap bitmapResize = Bitmap.createScaledBitmap(b, width, height, false);
        return new BitmapDrawable(this.getResources(), bitmapResize);
    }
}
