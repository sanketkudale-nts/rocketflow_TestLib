package com.tracki.ui.productdetails

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.tracki.BR
import com.tracki.R
import com.tracki.databinding.FragmentLayoutImageStockBinding
import com.tracki.ui.base.BaseFragment
import com.tracki.ui.introscreens.introfragment.IntroScreenNavigator
import com.tracki.ui.introscreens.introfragment.IntroScreenViewModel
import javax.inject.Inject

class ImageFragment : BaseFragment<FragmentLayoutImageStockBinding,ImageScreenVewModel>() ,
    IntroScreenNavigator {

    private var url: String?=null
    lateinit var binding:FragmentLayoutImageStockBinding

    lateinit var imageVieModel: ImageScreenVewModel


    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = getViewDataBinding()

        if (url != null && url!!.isNotEmpty()) {
            Glide.with(binding.ivImage.context)
                .load(url)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .placeholder(R.drawable.ic_picture)
                .error(R.drawable.ic_picture)
                .into(binding.ivImage)
        }

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        imageVieModel.navigator = this
        if (arguments != null) {
            url = requireArguments().getString("url")

        }
    }

    override fun getLayoutId(): Int {
        return R.layout.fragment_layout_image_stock
    }





    companion object {

        @JvmStatic
        fun newInstance(url: String): ImageFragment {
            val args = Bundle()
            args.putString("url", url)
            val fragment = ImageFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModel(): ImageScreenVewModel {
        imageVieModel = ViewModelProviders.of(this, mViewModelFactory).get(
            ImageScreenVewModel::class.java)
        return imageVieModel
    }
}
