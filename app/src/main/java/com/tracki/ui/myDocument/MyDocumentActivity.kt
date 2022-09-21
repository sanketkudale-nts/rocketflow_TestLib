package com.tracki.ui.myDocument

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.annotation.Nullable
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tracki.BR
import com.tracki.R
import com.tracki.data.model.request.getDocsRequest
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityMyDocumentBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.common.DoubleButtonDialog
import com.tracki.ui.common.OnClickListener
import com.tracki.ui.selectorder.PaginationRequest
import com.tracki.ui.tasklisting.PaginationListener
import com.tracki.ui.tasklisting.PagingData
import com.tracki.ui.uploadDocument.DocType
import com.tracki.ui.uploadDocument.Documents
import com.tracki.ui.uploadDocument.UploadDocumentActivity
import com.tracki.utils.AppConstants
import com.tracki.utils.CommonUtils
import com.tracki.utils.JSONConverter
import com.tracki.utils.Log
import kotlinx.android.synthetic.main.activity_my_document.*
import kotlinx.android.synthetic.main.layout_select_document_type_popup.*
import javax.inject.Inject

class MyDocumentActivity() : BaseActivity<ActivityMyDocumentBinding, MyDocumentViewModel>(),
    DocumentNavigator, DocumentsAdapter.onPopupDeleteSelected {

    private  var docData: DocsData?=null

    @Inject
    lateinit var mDocumentNewViewModel: MyDocumentViewModel

    @Inject
    lateinit var httpManager: HttpManager

    lateinit var getDocsRequest: getDocsRequest
    lateinit var mActivityMyDocumentBinding: ActivityMyDocumentBinding
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private var selectedDocType: DocType? = null
    private val UPLOADED_NEW_DOC_ID: Int = 1113

    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false

    private var mLayoutManager: LinearLayoutManager? = null
    private var rvDocuments: RecyclerView? = null
    private var paginationRequest: PaginationRequest? = null
    @Inject
    lateinit var adapter: DocumentsAdapter

    companion object {
        private val TAG = MyDocumentActivity::class.java.simpleName
        fun newIntent(context: Context) = Intent(context, MyDocumentActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mActivityMyDocumentBinding = viewDataBinding
        mDocumentNewViewModel.navigator = this
        bottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(R.layout.layout_select_document_type_popup)

        mActivityMyDocumentBinding.ivBackArrow.setOnClickListener {
            onBackPressed()
        }

        mActivityMyDocumentBinding.ivFilter.setOnClickListener{
            showTimeListDialog()
        }


        mActivityMyDocumentBinding.fabDocument.setOnClickListener {
            startActivityForResult(
                UploadDocumentActivity.newIntent(this)
                    .putExtra(AppConstants.Extra.EXTRA_IS_EDIT_MODE, false),
                UPLOADED_NEW_DOC_ID
            )
        }
        populateDocTypeDropDown()
        getData()

    }

    private fun getData() {
        getDocsRequest = getDocsRequest(0, 0, selectedDocType)
        if (currentPage == PaginationListener.PAGE_START) {
            adapter.clearList()
            mActivityMyDocumentBinding.rvDocuments.removeAllViewsInLayout()

        }

        paginationRequest = PaginationRequest()
        paginationRequest!!.limit = 10
        paginationRequest!!.offset = (currentPage - 1) * 10
//        paginationRequest!!.offset = (currentPage - 1)
        paginationRequest!!.dataCount = 10
        mDocumentNewViewModel.getDocument(httpManager, getDocsRequest,paginationRequest)
    }

    private fun getFilterData(docType: DocType?) {
        selectedDocType=docType
        currentPage = PaginationListener.PAGE_START
        getData()
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_my_document
    }

    override fun getViewModel(): MyDocumentViewModel {
        return mDocumentNewViewModel
    }
    private fun setRecyclerView() {
        if (rvDocuments == null) {
            rvDocuments = mActivityMyDocumentBinding.rvDocuments
            //  mLayoutManager= (LinearLayoutManager) rvAttendance.getLayoutManager();
            try {
                mLayoutManager = LinearLayoutManager(this)
                mLayoutManager!!.orientation = RecyclerView.VERTICAL
                rvDocuments!!.layoutManager = mLayoutManager
                rvDocuments!!.itemAnimator = DefaultItemAnimator()

            } catch (e: IllegalArgumentException) {
            }
            rvDocuments!!.adapter = adapter
        }

        rvDocuments!!.addOnScrollListener(object : PaginationListener(mLayoutManager!!) {
            override fun loadMoreItems() {
                this@MyDocumentActivity.isLoading = true
                currentPage++
                getData()
            }

            override fun isLastPage(): Boolean {
                return this@MyDocumentActivity.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@MyDocumentActivity.isLoading
            }
        })
    }

    override fun handleDocumentResponse(callback: ApiCallback?, result: Any?, error: APIError?) {
        hideLoading()

        if (CommonUtils.handleResponse(callback, error, result, this@MyDocumentActivity)) {
            val jsonConverter: JSONConverter<DocsResponse> = JSONConverter()
            var response: DocsResponse? =
                jsonConverter.jsonToObject(
                    result.toString(),
                    DocsResponse::class.java
                ) as DocsResponse
            if (response != null) {
                if (response.data != null) {
                    this@MyDocumentActivity.isLoading = false
                    setRecyclerView()
                    adapter.addItems(this@MyDocumentActivity, response.data as ArrayList<DocsData>)
                    CommonUtils.showLogMessage(
                        "e", "adapter total_count =>",
                        "" + adapter.itemCount
                    )
                    CommonUtils.showLogMessage(
                        "e", "fetch total_count =>",
                        "" + response.dataCount
                    )
                    isLastPage = adapter.itemCount >= response.dataCount
                }
            }
        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == UPLOADED_NEW_DOC_ID) {
            if (resultCode == RESULT_OK) {
                selectedDocType=null
//                selectedDocType=DocType.valueOf(data!!.getStringExtra(AppConstants.Extra.DOC_TYPE)!!)
                currentPage = PaginationListener.PAGE_START
                getData()
            }
        }
    }

    override fun handleDeleteResponse(callback: ApiCallback?, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@MyDocumentActivity)) {
            val jsonConverter: JSONConverter<DocDeleteResponse> = JSONConverter()
            var response: DocDeleteResponse =
                jsonConverter.jsonToObject(
                    result.toString(),
                    DocDeleteResponse::class.java
                ) as DocDeleteResponse
            if(docData!=null)
            adapter.deleteItem(this.docData!!)
        }
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {

    }

    override fun onPopupDeleteSelected(docData: DocsData) {
        val dialog = DoubleButtonDialog(this@MyDocumentActivity,
            true,
            null,
            getString(R.string.delete_user_docs),
            getString(R.string.yes),
            getString(R.string.no),
            object : OnClickListener {
                override fun onClickCancel() {}
                override fun onClick() {
                    showLoading()
                    this@MyDocumentActivity.docData=docData
                    mDocumentNewViewModel.deleteDocument(httpManager,docData.documentId.toString())
                }
            })
        dialog.show()

    }

    override fun onPopupViewSelected(docData: DocsData) {
        startActivityForResult(
            UploadDocumentActivity.newIntent(this)
                .putExtra(AppConstants.Extra.EXTRA_DATA, docData)
                .putExtra(AppConstants.Extra.EXTRA_IS_EDIT_MODE, true),
            UPLOADED_NEW_DOC_ID
        )
       // openDialogShowImage(docData.imageUrl!!)
    }



    private fun showTimeListDialog() {
        val ivCancel = bottomSheetDialog.findViewById<ImageView>(R.id.ivCancel)
        val filterButton = bottomSheetDialog.findViewById<Button>(R.id.btn_filter)
        val clearButton = bottomSheetDialog.findViewById<Button>(R.id.btn_clear)
        ivCancel!!.setOnClickListener {
            bottomSheetDialog.dismissWithAnimation = true
            bottomSheetDialog.dismiss()
        }
        clearButton!!.setOnClickListener {
            bottomSheetDialog.dismissWithAnimation = true
            bottomSheetDialog.dismiss()
            selectedDocType=null
            currentPage = PaginationListener.PAGE_START
            getData()
        }
        filterButton!!.setOnClickListener{
            showLoading()
            getFilterData(selectedDocType)
            bottomSheetDialog.hide()
        }


        bottomSheetDialog.show()
    }


    private fun populateDocTypeDropDown() {
        var doclist: MutableList<Documents> = java.util.ArrayList()
        var pan = Documents("PAN", DocType.PAN)
        doclist.add(pan)
        var aadhar = Documents("AADHAR", DocType.AADHAR)
        doclist.add(aadhar)
        var driving = Documents("DRIVING LICENCE", DocType.DRIVING_LICENCE)
        doclist.add(driving)

        var voterId = Documents("VOTER ID", DocType.VOTER_ID)
        doclist.add(voterId)

        var passport = Documents("PASSPORT", DocType.PASSPORT)
        doclist.add(passport)

        var docNameList: MutableList<String?> = java.util.ArrayList()
        for (list in doclist) {
            docNameList.add(list.name)
        }

        var arrayAdapter = object : ArrayAdapter<String?>(
            this,
            android.R.layout.simple_spinner_item,
            docNameList
        ) {
            override fun getView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val v = super.getView(position, convertView, parent)
                val externalFont = Typeface.createFromAsset(
                    parent.context.assets,
                    "fonts/campton_book.ttf"
                )
                (v as TextView).typeface = externalFont
                return v
            }

            override fun getDropDownView(
                position: Int,
                convertView: View?,
                parent: ViewGroup
            ): View {
                val v = super.getDropDownView(position, convertView, parent)
                val externalFont = Typeface.createFromAsset(
                    parent.context.assets,
                    "fonts/campton_book.ttf"
                )
                (v as TextView).typeface = externalFont
                //v.setBackgroundColor(Color.GREEN);
                return v
            }
        }
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        bottomSheetDialog.spnDocumentsType!!.adapter = arrayAdapter
        bottomSheetDialog.spnDocumentsType!!.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(
                    parent: AdapterView<*>,
                    view: View,
                    position: Int,
                    id: Long
                ) {
                    val selectedItem =
                        parent.getItemAtPosition(position).toString()
                    selectedDocType = doclist[position].type
//                        CommonUtils.showLogMessage("e", "hubId", hubId);


                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }

            }

    }

    private fun openDialogShowImage(url: String) {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.layout_show_image_big)
//        dialog.window!!.attributes.windowAnimations = R.style.DialogZoomOutAnimation
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.8f
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.CENTER)
        val imageView = dialog.findViewById<View>(R.id.ivImages) as ImageView

        Glide.with(this)
            .asBitmap()
            .load(url)
            .error(R.drawable.ic_picture)
            .placeholder(R.drawable.ic_picture)
            .into(object : CustomTarget<Bitmap?>() {
                override fun onLoadCleared(@Nullable placeholder: Drawable?) {
                }

                override fun onResourceReady(
                    resource: Bitmap,
                    transition: com.bumptech.glide.request.transition.Transition<in Bitmap?>?
                ) {
                    imageView.setImageBitmap(resource)
                }
            })
        /*  Glide.with(context!!)
                  .load(url)
                  .error(R.drawable.ic_picture)
                  .placeholder(R.drawable.ic_picture)
                  .into(imageView)*/
        dialog.window!!.attributes = lp
        // imageView.setOnClickListener { dialog.dismiss() }
        if (!dialog.isShowing) dialog.show()
    }
}