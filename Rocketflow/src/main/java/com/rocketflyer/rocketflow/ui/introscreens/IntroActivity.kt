package com.rocketflyer.rocketflow.ui.introscreens

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.tracki.BR
import com.tracki.R
import com.tracki.databinding.ActivityIntroBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.consent.ConsentActivity
import com.tracki.ui.introscreens.introfragment.IntroScreenFragment
import com.tracki.utils.CommonUtils
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_intro.*
import javax.inject.Inject


/**
 * Created by rahul on 22/2/19
 */
class IntroActivity : BaseActivity<ActivityIntroBinding, IntroViewModel>(),
        IntroNavigator, HasSupportFragmentInjector {

    override fun skipClicked() {
        launchLoginScreen()
    }

    override fun nextClicked() {
        // checking for last playoutDotsage
        // if last page home screen will be launched
        val current = getItem(+1)
        if (current < 4) {
            // move to next screen
            mActivityIntroBinding.vpIntroScreens.currentItem = current
        } else {
            CommonUtils.preventTwoClick(btnNext)
            launchLoginScreen()
        }
    }

    private fun launchLoginScreen() {
        val intent = ConsentActivity.newIntent(this)
//        val intent = LoginActivity.newIntent(this)
        startActivity(intent)
        finish()
    }

    @Inject
    lateinit var mIntroViewModel: IntroViewModel
    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    private lateinit var mActivityIntroBinding: ActivityIntroBinding

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_intro
    override fun getViewModel() = mIntroViewModel

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityIntroBinding = viewDataBinding
        mIntroViewModel.navigator = this
        setUp()
    }

    private fun setUp() {
        // adding bottom dots
        addBottomDots(0)

        val mIntroPagerAdapter = IntroPagerAdapter(supportFragmentManager)
        mIntroPagerAdapter.addFragment(IntroScreenFragment.newInstance(0))
        mIntroPagerAdapter.addFragment(IntroScreenFragment.newInstance(1))
        mIntroPagerAdapter.addFragment(IntroScreenFragment.newInstance(2))
        mIntroPagerAdapter.addFragment(IntroScreenFragment.newInstance(3))
        mIntroPagerAdapter.addFragment(IntroScreenFragment.newInstance(4))

        mActivityIntroBinding.vpIntroScreens.adapter = mIntroPagerAdapter
        mActivityIntroBinding.vpIntroScreens.addOnPageChangeListener(viewPagerPageChangeListener)

    }

    //  viewpager change listener
    private var viewPagerPageChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageSelected(position: Int) {
            if (position == 4) {
                mActivityIntroBinding.btnSkip.visibility = View.GONE
                mActivityIntroBinding.btnNext.visibility = View.GONE
                mActivityIntroBinding.btnStarted.visibility = View.VISIBLE
            } else {
                mActivityIntroBinding.btnStarted.visibility = View.GONE
                mActivityIntroBinding.btnSkip.visibility = View.VISIBLE
                mActivityIntroBinding.btnNext.visibility = View.VISIBLE
            }
            addBottomDots(position)
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

        }

        override fun onPageScrollStateChanged(arg0: Int) {

        }
    }
    private lateinit var dots: Array<TextView?>

    private fun addBottomDots(currentPage: Int) {
        dots = arrayOfNulls(5)

        mActivityIntroBinding.layoutDots.removeAllViews()
        for (i in 0 until dots.size) {
            dots[i] = TextView(this)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dots[i]?.text = Html.fromHtml("&#8226;", 0)
            } else {
                dots[i]?.text = Html.fromHtml("&#8226;")
            }
            dots[i]?.textSize = 40f
//            dots[i]?.setBackgroundResource(R.drawable.inactive_dot)
            dots[i]?.setTextColor(ContextCompat.getColor(this, R.color.light_gray_a))
            mActivityIntroBinding.layoutDots.addView(dots[i])
        }

        if (dots.isNotEmpty())
            dots[currentPage]?.setTextColor(ContextCompat.getColor(this, R.color.colorPrimaryDark))
//            dots[currentPage]?.setBackgroundResource(R.drawable.active_dot)
    }

    private fun getItem(i: Int): Int {
        return mActivityIntroBinding.vpIntroScreens.currentItem + i
    }


    companion object {
        fun newIntent(context: Context) = Intent(context, IntroActivity::class.java)
    }
}