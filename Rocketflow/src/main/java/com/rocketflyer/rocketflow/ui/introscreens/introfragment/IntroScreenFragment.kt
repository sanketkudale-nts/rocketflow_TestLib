package com.rocketflyer.rocketflow.ui.introscreens.introfragment


import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.tracki.BR
import com.tracki.R
import com.tracki.databinding.FragmentIntroScreenBinding
import com.tracki.ui.base.BaseFragment
import com.tracki.utils.AppConstants
import javax.inject.Inject

/**
 * Created by rahul on 22/2/19
 */
class IntroScreenFragment : BaseFragment<FragmentIntroScreenBinding, IntroScreenViewModel>(), IntroScreenNavigator {

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory

    private lateinit var mIntroScreenViewModel: IntroScreenViewModel
    private lateinit var mFragmentIntroScreenBinding: FragmentIntroScreenBinding

//    private val drawables = arrayOf(R.drawable.ic_add_buddy,
//            R.drawable.ic_create_task, R.drawable.ic_location_aware,
//            R.drawable.ic_movement_aware, R.drawable.ic_audit_aware)
    private val drawablesMain = arrayOf(R.drawable.screen_0,R.drawable.screen_1,
            R.drawable.screen_2, R.drawable.screen_3,
            R.drawable.screen_4)
    private val titles = arrayOf("ADD BUDDY",
            "ASSIGN TASK", "LOCATION AWARE",
            "MOVEMENT AWARE", "AUDIT AWARE")
    private val descriptions = arrayOf("We empower building a community which helps \npeople track their buddies/fleet and \nget tracked.",
            "Yeah, we know where are you and we do that using \nour custom battery efficient algorithms.",
            "Yeah, we know where are you and we do that using \nour custom battery efficient algorithms.",
            "So no more fast and furious on the ground. Harsh \nbraking, Aggressive Acceleration, harsh cornering, \nand much more all through the app.",
            "Your data is protected and private to your defined \ncircle only. User can anytime change the settings \nand control the tracking data.")


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_intro_screen
    }

    override fun getViewModel(): IntroScreenViewModel {
//        mIntroScreenViewModel = ViewModelProvider(this).get(IntroScreenViewModel::class.java)
        mIntroScreenViewModel = ViewModelProviders.of(this, mViewModelFactory).get(IntroScreenViewModel::class.java)
        return mIntroScreenViewModel
    }

    private var position: Int? = null
    private var content: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mIntroScreenViewModel.navigator = this
        if (arguments != null) {
            position = requireArguments().get(AppConstants.Extra.EXTRA_INTRO_POSITION) as Int?
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mFragmentIntroScreenBinding = viewDataBinding

       // mFragmentIntroScreenBinding.ivIntroPic.setImageResource(drawables[position!!])
        mFragmentIntroScreenBinding.ivImage.setImageResource(drawablesMain[position!!])
       // mFragmentIntroScreenBinding.tvIntroTitle.text = titles[position!!]
        //mFragmentIntroScreenBinding.tvIntroContent.text = descriptions[position!!]
    }

    companion object {

        @JvmStatic
        fun newInstance(position: Int): IntroScreenFragment {
            val args = Bundle()
            args.putInt(AppConstants.Extra.EXTRA_INTRO_POSITION, position)
            val fragment = IntroScreenFragment()
            fragment.arguments = args
            return fragment
        }
    }

}