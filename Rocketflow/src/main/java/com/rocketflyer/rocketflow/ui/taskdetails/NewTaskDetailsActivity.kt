package com.rocketflyer.rocketflow.ui.taskdetails


import android.R.attr.fragment
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Point
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.ImageView
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.TabLayoutOnPageChangeListener
import com.google.zxing.WriterException
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.QrCodeRequest
import com.tracki.data.model.response.config.WorkFlowCategories
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityNewTaskDetailsBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.taskdetails.subtask.SubTaskFragment
import com.tracki.ui.taskdetails.timeline.TaskDetailsFragment
import com.tracki.ui.tasklisting.TaskPagerAdapter
import com.tracki.ui.tasklisting.ihaveassigned.TabDataClass
import com.tracki.utils.*
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_new_task_details.*
import java.security.NoSuchAlgorithmException
import java.security.spec.InvalidKeySpecException
import javax.inject.Inject


open class NewTaskDetailsActivity : BaseActivity<ActivityNewTaskDetailsBinding, TaskDetailsActivityViewModel>(), HasSupportFragmentInjector {

    private var categoryId: String? = null
    private var from: String? = null

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    @Inject
    lateinit var mNewTaskViewModel: TaskDetailsActivityViewModel
    private lateinit var mActivityNewTaskDetailBinding: ActivityNewTaskDetailsBinding

    @Inject
    lateinit var mPagerAdapter: TaskPagerAdapter

    @Inject
    lateinit var httpManager: HttpManager

    @Inject
    lateinit var preferencesHelper: PreferencesHelper


    private val fragments: MutableList<TabDataClass> = ArrayList()

    private val TAG = "NewTaskDetailsActivity"

    private var fromDate: Long = 0
    private var toDate: Long = 0
    private var allowSubTask: Boolean = false
    private var subTaskCategoryId: ArrayList<String>? = null
    var taskId: String? = null
    private var referenceId: String? = null
    private var snackBar: Snackbar?=null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(mActivityNewTaskDetailBinding.coordinatorLayout, getString(R.string.please_check_your_internet_connection))
    }
    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_new_task_details
    }

    override fun getViewModel(): TaskDetailsActivityViewModel {
        return mNewTaskViewModel!!
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityNewTaskDetailBinding = viewDataBinding
        setToolbar(mActivityNewTaskDetailBinding.toolbar, "Task Details")

        getTaskData()

    }


    private fun getTaskData() {
        if (intent != null) {

            if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_ID)) {
                taskId = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_ID)
                from = intent.getStringExtra(AppConstants.Extra.FROM)
                categoryId = intent.getStringExtra(AppConstants.Extra.EXTRA_CATEGORY_ID)
                allowSubTask = intent.getBooleanExtra(AppConstants.Extra.EXTRA_ALLOW_SUB_TASK, false)
                if(intent.hasExtra(AppConstants.Extra.EXTRA_SUB_TASK_CATEGORY_ID))
                    subTaskCategoryId = intent.getStringArrayListExtra(AppConstants.Extra.EXTRA_SUB_TASK_CATEGORY_ID)
                if(intent.hasExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID))
                    referenceId = intent.getStringExtra(AppConstants.Extra.EXTRA_PARENT_REF_ID)
                //task= intent.getSerializableExtra(AppConstants.Extra.EXTRA_TASK) as Task?
                if (intent.hasExtra(AppConstants.Extra.FROM_DATE)) {
                    fromDate = intent.getLongExtra(AppConstants.Extra.FROM_DATE, 0)
                }
                if (intent.hasExtra(AppConstants.Extra.FROM_TO)) {
                    toDate = intent.getLongExtra(AppConstants.Extra.FROM_TO, 0)
                }

                fragments.add(TabDataClass(TaskDetailsFragment.newInstance(taskId, categoryId, from), "TimeLine"))

                if (allowSubTask) {
                    tabLayout.visibility = View.VISIBLE
                    fragments.add(TabDataClass(SubTaskFragment.newInstance(allowSubTask, subTaskCategoryId, categoryId, fromDate, toDate, taskId, referenceId), getLabelName(categoryId)))
                } else {
                    tabLayout.visibility = View.GONE
                }

                mPagerAdapter.setFragments(fragments)
                vpTask.adapter = mPagerAdapter
                tabLayout.tabGravity = TabLayout.GRAVITY_FILL
                tabLayout.tabMode = TabLayout.MODE_FIXED
                tabLayout.setupWithViewPager(vpTask)

                vpTask.offscreenPageLimit = tabLayout.tabCount
                vpTask.addOnPageChangeListener(TabLayoutOnPageChangeListener(tabLayout))


            }

        }
    }

    fun getLabelName(categoryId: String?): String? {
        var workFlowCategoriesList: List<WorkFlowCategories> ?= preferencesHelper.workFlowCategoriesList
        var lebel: String? = "Sub Task"

        workFlowCategoriesList?.let{
            for (i in it) {
                if (i.categoryId != null)
                    if (i.categoryId == categoryId) {
                        if (i.subTaskConfig != null && i.subTaskConfig!!.label != null)
                            lebel = i.subTaskConfig!!.label
                    }
            }
        }

        return lebel
    }

    override fun onResume() {
        super.onResume()


    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    private fun openQrCode() {
        var qrCodeRequest = QrCodeRequest()
        qrCodeRequest.taskId = taskId
        qrCodeRequest.from = from
        qrCodeRequest.categoryId = categoryId
        qrCodeRequest.allowSubTask = allowSubTask
        qrCodeRequest.subTaskCategoryId = subTaskCategoryId
        qrCodeRequest.referenceId = referenceId
        qrCodeRequest.fromDate = fromDate
        qrCodeRequest.toDate = toDate
        var jsonConverter = JSONConverter<QrCodeRequest>()
        var strData = jsonConverter.objectToJson(qrCodeRequest).toString()
        val manager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = manager.defaultDisplay
        val point = Point()
        display.getSize(point)
        val width: Int = point.x
        val height: Int = point.y
        var smallerDimension = if (width < height) width else height
        smallerDimension = smallerDimension * 3 / 4

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
                ColorDrawable(
                        Color.TRANSPARENT))
        dialog.setContentView(R.layout.item_show_qr_code)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.8f
        val window = dialog.window
        window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
        window.setGravity(Gravity.CENTER)
        val imageView = dialog.findViewById<ImageView>(R.id.ivImages)

        val encryptionAndDecryption = EncryptionAndDecryption()
        try {
            strData = encryptionAndDecryption.getEncryptData(strData)
            CommonUtils.showLogMessage("e", "encrypt_data", strData)

            //Toast.makeText(ScanBarCodeActivity.this,barcodeSparseArray.valueAt(0)+"" , Toast.LENGTH_SHORT).show();
            CommonUtils.showLogMessage("e", "decrypt_data", encryptionAndDecryption.getDecryptData(strData))
        } catch (e: InvalidKeySpecException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
        val qrgEncoder = QRGEncoder(strData, null, QRGContents.Type.TEXT, smallerDimension)
        qrgEncoder.setColorBlack(R.color.colorPrimary)
        qrgEncoder.setColorWhite(Color.WHITE)
        try {
            // Getting QR-Code as Bitmap
            var bitmap :Bitmap= qrgEncoder.getBitmap()
            // Setting Bitmap to ImageView
            Glide.with(this)
                    .load(bitmap)
                    .error(R.drawable.ic_picture)
                    .placeholder(R.drawable.ic_picture)
                    .into(imageView)
        } catch (e: WriterException) {
            Log.e(TAG, e.toString())
        }

        dialog.window!!.attributes = lp
       // imageView.setOnClickListener { dialog.dismiss() }
        if (!dialog.isShowing) dialog.show()
    }
    override fun dispatchTouchEvent(event: MotionEvent): Boolean {
        if (event.action == MotionEvent.ACTION_DOWN) {
            try {
                if (mPagerAdapter.getItem(0) is TaskDetailsFragment) {
                    (mPagerAdapter.getItem(0) as TaskDetailsFragment).hideBottomSheetFromOutSide(event)
                }else if (mPagerAdapter.getItem(1) is TaskDetailsFragment){
                    (mPagerAdapter.getItem(1) as TaskDetailsFragment).hideBottomSheetFromOutSide(event)
                }
            }catch (e:ClassCastException){

            }

        }
        return super.dispatchTouchEvent(event)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        for (fragment in supportFragmentManager.fragments) {
            fragment.onActivityResult(requestCode, resultCode, data)
        }
    }
}
