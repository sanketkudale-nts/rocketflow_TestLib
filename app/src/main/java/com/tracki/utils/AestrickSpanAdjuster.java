package com.tracki.utils;

import android.text.TextPaint;
import android.text.style.MetricAffectingSpan;

public class AestrickSpanAdjuster extends MetricAffectingSpan {
    double ratio = 0.5;

    public AestrickSpanAdjuster(double ratio) {
        this.ratio = ratio;
    }

    @Override
    public void updateDrawState(TextPaint paint) {
        paint.baselineShift += (int) (paint.ascent() * ratio);
    }

    @Override
    public void updateMeasureState(TextPaint paint) {
        paint.baselineShift += (int) (paint.ascent() * ratio);
    }
}
