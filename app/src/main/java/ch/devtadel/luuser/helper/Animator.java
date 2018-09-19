package ch.devtadel.luuser.helper;

import android.animation.ValueAnimator;
import android.support.v7.widget.CardView;

public class Animator {
    private ValueAnimator cardPressAnim;

    public Animator(){
        cardPressAnim = ValueAnimator.ofFloat(0f, 5f);
        cardPressAnim.setDuration(500);

    }

    public void animateCardPress(final CardView cardView){
        cardPressAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                cardView.setCardElevation((float)valueAnimator.getAnimatedValue());
            }
        });
        cardPressAnim.start();
    }

}
