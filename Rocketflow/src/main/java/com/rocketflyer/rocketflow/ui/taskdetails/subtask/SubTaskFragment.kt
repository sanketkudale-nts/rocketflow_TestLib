package com.rocketflyer.rocketflow.ui.taskdetails.subtask

import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.response.config.DashBoardBoxItem
import com.tracki.data.model.response.config.WorkFlowCategories
import com.tracki.data.network.HttpManager
import com.tracki.databinding.LayoutFragmentSubtasklistBinding
import com.tracki.ui.base.BaseFragment
import com.tracki.ui.tasklisting.TaskPagerAdapter
import com.tracki.ui.tasklisting.assignedtome.AssignedtoMeFragment
import com.tracki.ui.tasklisting.ihaveassigned.TabDataClass
import com.tracki.utils.AppConstants
import kotlinx.android.synthetic.main.layout_fragment_subtasklist.*
import javax.inject.Inject


/**
 * Created by Vikas Kesharvani on 16/09/20.
 * rocketflyer technology pvt. ltd
 * vikas.kesharvani@rocketflyer.in
 */
class SubTaskFragment : BaseFragment<LayoutFragmentSubtasklistBinding, SubTaskViewModel>() {

    lateinit var mSubTaskViewModel: SubTaskViewModel
    lateinit var mPagerAdapter: TaskPagerAdapter

    @Inject
    lateinit var mViewModelFactory: ViewModelProvider.Factory
    private lateinit var mBinding: LayoutFragmentSubtasklistBinding
    private var fromDate: Long = 0
    private var toDate: Long = 0
    private var allowSubTask:Boolean=false
    private var subTaskCategoryId : ArrayList<String>?=null
    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var preferencesHelper: PreferencesHelper

    private val fragments: MutableList<TabDataClass> = ArrayList()

    var taskId: String? = null
    private var referenceId:String?=null

    companion object {
        //allowSubTask,subTaskCategoryId
        fun newInstance(allowSubTask: Boolean, subTaskCategoryId: ArrayList<String>?, categoryId: String?, fromDate: Long, toDate: Long, taskId: String?, referenceId: String?): SubTaskFragment? {
            val args = Bundle()
            args.putBoolean(AppConstants.Extra.EXTRA_ALLOW_SUB_TASK, allowSubTask)
            args.putStringArrayList(AppConstants.Extra.EXTRA_SUB_TASK_CATEGORY_ID, subTaskCategoryId)
            args.putString(AppConstants.Extra.EXTRA_CATEGORY_ID, categoryId)
            args.putString(AppConstants.Extra.EXTRA_PAREN_TASK_ID, taskId)
            args.putString(AppConstants.Extra.EXTRA_PARENT_REF_ID, referenceId)
            args.putLong(AppConstants.Extra.FROM_DATE, fromDate)
            args.putLong(AppConstants.Extra.FROM_TO, toDate)
            val fragment = SubTaskFragment()
            fragment.arguments = args
            return fragment
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.layout_fragment_subtasklist
    }

    override fun getViewModel(): SubTaskViewModel {
        mSubTaskViewModel = ViewModelProviders.of(this, mViewModelFactory).get(SubTaskViewModel::class.java)
        return mSubTaskViewModel
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mBinding = viewDataBinding
        mPagerAdapter = TaskPagerAdapter(childFragmentManager)
        getTaskData()
    }

    private fun getTaskData() {
        if (arguments != null) {
            var categoryId = requireArguments().getString(AppConstants.Extra.EXTRA_CATEGORY_ID)
             taskId = requireArguments()!!.getString(AppConstants.Extra.EXTRA_PAREN_TASK_ID)
             referenceId = requireArguments().getString(AppConstants.Extra.EXTRA_PARENT_REF_ID)
            allowSubTask = requireArguments().getBoolean(AppConstants.Extra.EXTRA_ALLOW_SUB_TASK,false)
            subTaskCategoryId = requireArguments().getStringArrayList(AppConstants.Extra.EXTRA_SUB_TASK_CATEGORY_ID)
            if (requireArguments().getLong(AppConstants.Extra.FROM_DATE) != 0L) {
                fromDate = requireArguments().getLong(AppConstants.Extra.FROM_DATE, 0)
            }
            if (requireArguments().getLong(AppConstants.Extra.FROM_TO) != 0L) {
                toDate = requireArguments().getLong(AppConstants.Extra.FROM_TO, 0)
            }
            if (subTaskCategoryId != null && subTaskCategoryId!!.size > 0) {
                for (data in subTaskCategoryId!!) {
                    val dashBoardBoxItem = DashBoardBoxItem()
                    dashBoardBoxItem.categoryId = data
                    dashBoardBoxItem.loadBy = "SUB_TASKS"
                    var map = Gson().toJson(dashBoardBoxItem)
                    fragments.add(TabDataClass(AssignedtoMeFragment.newInstance(map, fromDate, toDate,taskId,referenceId,false), getLabelName(data)))

                }
            }
            mPagerAdapter.setFragments(fragments)
            vpTask.adapter = mPagerAdapter
            tabLayout.tabGravity = TabLayout.GRAVITY_FILL
            if (subTaskCategoryId != null && subTaskCategoryId!!.size < 3)
            tabLayout.tabMode = TabLayout.MODE_FIXED
            else{
                tabLayout.tabMode = TabLayout.MODE_SCROLLABLE
            }
            tabLayout.setupWithViewPager(vpTask)

            vpTask.offscreenPageLimit = tabLayout.tabCount
            vpTask.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
            if (subTaskCategoryId != null && subTaskCategoryId!!.size > 1) {
                tabLayout.visibility = View.VISIBLE
            } else {
                tabLayout.visibility = View.GONE
            }

        }
    }

    fun getLabelName(categoryId: String?): String? {
        var workFlowCategoriesList: List<WorkFlowCategories> = preferencesHelper.workFlowCategoriesList
        var lebel: String? = null

        for (i in workFlowCategoriesList) {
            if (i.categoryId != null)
                if (i.categoryId == categoryId) {
                    if (i.name != null)
                        lebel = i.name
                }
        }
        return lebel
    }
}