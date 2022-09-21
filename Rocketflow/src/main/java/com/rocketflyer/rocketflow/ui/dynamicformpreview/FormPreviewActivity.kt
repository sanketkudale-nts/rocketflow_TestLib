package com.rocketflyer.rocketflow.ui.dynamicformpreview

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.tracki.BR
import com.tracki.R
import com.tracki.data.model.response.config.DataType.*
import com.tracki.data.model.response.config.DynamicFormData
import com.tracki.data.model.response.config.FormData
import com.tracki.databinding.ActivityFormPreviewBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.utils.AppConstants
import com.tracki.utils.CommonUtils
import com.tracki.utils.DateTimeUtil
import java.io.File
import java.io.Serializable
import javax.inject.Inject

class FormPreviewActivity : BaseActivity<ActivityFormPreviewBinding, FormPreviewViewModel>(),
        FormPreviewNavigator {



    @Inject
    lateinit var mFormPreviewViewModel: FormPreviewViewModel

    private var mActivityFormPreviewBinding: ActivityFormPreviewBinding? = null
    private var formMap: HashMap<String, List<DynamicFormData>>? = null
    private var tabLayout: TabLayout? = null
    private var viewPager: ViewPager? = null

    override fun getBindingVariable() = BR.viewModel
    override fun getLayoutId() = R.layout.activity_form_preview
    override fun getViewModel() = mFormPreviewViewModel
    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityFormPreviewBinding!!.coordinatorLayout, getString(R.string.please_check_your_internet_connection))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityFormPreviewBinding = viewDataBinding
        mFormPreviewViewModel.navigator = this
        //set toolbar title here
        setToolbar(mActivityFormPreviewBinding?.toolbar, AppConstants.TEXT_PREVIEW_FORM)
        if (intent != null) {
            if (intent.hasExtra(AppConstants.Extra.EXTRA_FORM_DETAILS)) {
                formMap = intent.getSerializableExtra(AppConstants.Extra.EXTRA_FORM_DETAILS) as HashMap<String, List<DynamicFormData>>
            }
        }

        viewPager = mActivityFormPreviewBinding?.viewPager
        if(formMap!=null) {
            setupViewPager(viewPager, formMap)

        }

        tabLayout = mActivityFormPreviewBinding?.tablayout
        tabLayout?.setupWithViewPager(viewPager)
    }

    private fun setupViewPager(viewPager: ViewPager?, list: HashMap<String, List<DynamicFormData>>?) {
        val adapter = ViewPagerAdapter(supportFragmentManager)
        for ((key, value) in list!!) {
            adapter.addFragment(OneFragment.getInstance(value as Serializable), key)
            println("$key = $value")
        }
        viewPager?.adapter = adapter
    }

    companion object {
        const val TAG = "FormPreviewActivity"
        fun newIntent(context: Context) = Intent(context, FormPreviewActivity::class.java)
    }

    internal inner class ViewPagerAdapter(manager: FragmentManager) : FragmentPagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
        private val mFragmentList = ArrayList<Fragment>()
        private val mFragmentTitleList = ArrayList<String>()

        override fun getItem(position: Int): Fragment {
            return mFragmentList[position]
        }

        override fun getCount(): Int {
            return mFragmentList.size
        }

        fun addFragment(fragment: Fragment, title: String) {
            mFragmentList.add(fragment)
            mFragmentTitleList.add(title)
        }

        override fun getPageTitle(position: Int): CharSequence? {
            return mFragmentTitleList[position]
        }
    }
}

class OneFragment : Fragment() {

    companion object {

        @JvmStatic
        fun getInstance(list: Serializable): OneFragment {
            val oneFragment = OneFragment()
            val bundle = Bundle()
            bundle.putSerializable("hashMap", list)
            oneFragment.arguments = bundle
            return oneFragment
        }
    }

    var formDataList: ArrayList<FormData>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val bundle = arguments
        val formList = bundle?.getSerializable("hashMap") as List<DynamicFormData>?
        formDataList = ArrayList()
        if (formList != null) {
            for (i in formList.indices) {

                println(formList[i].toString())

                val formData = FormData()
                formData.name = formList[i].key
                val mainValue = formList[i].value
                if (formList[i].type == SIGNATURE || formList[i].type == FILE) {
                    formData.type = FILE
                    val list = ArrayList<File>()
                    if (mainValue != null) {
                        val f = File(mainValue)
                        list.add(f)
                    }
                    formData.file = list
                } else if (formList[i].type == CAMERA) {
                    formData.type = FILE
                    if (mainValue != null) {
                        formData.file = CommonUtils.getFileLists(mainValue)
                    }
                } else {
                    when (formList[i].type) {
                        DATE_RANGE -> {
                            val dateRange = mainValue?.split("-".toRegex())
                            val date1 = dateRange!![0].toLong()
                            val date2 = dateRange[1].toLong()
                            formData.enteredValue = "${DateTimeUtil.getParsedDate(date1, DateTimeUtil.DATE_FORMAT)} - ${DateTimeUtil.getParsedDate(date2, DateTimeUtil.DATE_FORMAT)}"
                            formData.type = TEXT
                            formDataList?.add(formData)
                        }
                        TIME -> {
                            formData.enteredValue = DateTimeUtil.getFormattedTime(mainValue?.toLong(), DateTimeUtil.TIME_FORMAT_2)
                            formData.type = TEXT
                            formDataList?.add(formData)
                        }
                        DATE -> {
                            formData.enteredValue = DateTimeUtil.getParsedDate(mainValue?.toLong()!!, DateTimeUtil.DATE_FORMAT)
                            formData.type = TEXT
                            formDataList?.add(formData)
                        }
                        DATE_TIME -> {
                            formData.enteredValue = DateTimeUtil.getParsedDate(mainValue?.toLong()!!, DateTimeUtil.DATE_TIME_FORMAT_2)
                            formData.type = TEXT
                            formDataList?.add(formData)
                        }
                        else -> {
                            formData.enteredValue = mainValue
                            formData.type = TEXT
                            formDataList?.add(formData)
                        }
                    }

                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_form_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val rvDynamicForm = view.findViewById<RecyclerView>(R.id.rvFormPreview)
        rvDynamicForm.layoutManager = LinearLayoutManager(activity)
//        previewAdapter = PreviewAdapter(formDataList!!)
        rvDynamicForm.adapter = PreviewAdapter(formDataList!!)
    }

}