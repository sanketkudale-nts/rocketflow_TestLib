package com.tracki.ui.selectorder

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.*
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.Nullable
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.GetManualLocationRequest
import com.tracki.data.model.request.LinkInventoryRequest
import com.tracki.data.model.request.SelectedProduct
import com.tracki.data.model.response.config.*
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivitySelectOrderBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.cart.CartActivity
import com.tracki.ui.custom.GlideApp
import com.tracki.ui.newcreatetask.NewCreateTaskActivity
import com.tracki.ui.scanqrcode.ProductScan
import com.tracki.ui.scanqrcode.ScanQrAndBarCodeActivity
import com.tracki.ui.taskdetails.timeline.ProductMapAdapter
import com.tracki.ui.tasklisting.PaginationListener
import com.tracki.utils.*
import com.trackthat.lib.models.BaseResponse
import javax.inject.Inject


open class SelectOrderActivity : BaseActivity<ActivitySelectOrderBinding, SelectOrderViewModel>(),
    SubCategoryAdapter.OnSubCategorySelected, SelectProductAdapter.OnProductAddListener,
    View.OnClickListener, SelectOrderNavigator {
    private var linkOption: LinkOptions? = null
    private var linkingType: TaggingType? = null
    private var ctaId: String? = null
    private var categoryId: String? = null
    private lateinit var searchView: SearchView
    lateinit var binding: ActivitySelectOrderBinding
    lateinit var selectProductAdapter: SelectProductAdapter
    lateinit var stageAdapter: SubCategoryAdapter
    private var taskId: String? = null
    private var target: String? = null
    private var flavourId: String? = null

    @Inject
    lateinit var selectOrderViewModel: SelectOrderViewModel


    open var savedOrderMap: HashMap<String, CatalogProduct>? = null

    @Inject
    lateinit var mPref: PreferencesHelper

    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private val REQUEST_CAMERA = 3
    private var isLoading = false
    private var mLayoutManager: LinearLayoutManager? = null
    private var rvProducts: RecyclerView? = null
    private var paginationRequest: PaginationRequest? = null
    private var cid: String? = null
    private var geoId: String? = null
    private var deliveryChargeAmount: Float? = 0F
    private var deliverMode: String? = null
    private var buddyName: String? = null
    private var buddyId: String? = null
    private var fleetId: String? = null
    private var globalSearch: Boolean = false
    private var dynamicPricing: Boolean = false
    private var geoTagging: Boolean = false

    @Inject
    lateinit var httpManager: HttpManager
    private var allowGeography: Boolean = false

    companion object {
        private val TAG = SelectOrderActivity::class.java.simpleName
        fun newIntent(context: Context) = Intent(context, SelectOrderActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        selectOrderViewModel.navigator = this
        setToolbar(binding.toolbar, "Select Product")


        binding.btnPlaceOrder.setOnClickListener(this)
        if (intent.hasExtra(AppConstants.Extra.EXTRA_CATEGORIES)) {
            var categoryMap: Map<String, String>? = null
            val str: String = intent.getStringExtra(AppConstants.Extra.EXTRA_CATEGORIES)!!
            categoryMap = Gson().fromJson(
                str,
                object : TypeToken<java.util.HashMap<String?, String?>?>() {}.type
            )
            if (categoryMap != null && categoryMap!!.containsKey("categoryId")) {
                categoryId = categoryMap!!.get("categoryId")
                CommonUtils.showLogMessage("e", "categoryId", categoryId)


            }
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_ID)) {
            taskId = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_ID)
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_TARGET)) {
            target = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_TARGET)
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_TAG_IN_FLAVOUR_ID)) {
            flavourId = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_TAG_IN_FLAVOUR_ID)
        }
        if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_DYNAMIC_PRICING)) {
            dynamicPricing =
                intent.getBooleanExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_DYNAMIC_PRICING, false)
        }


        if (intent.hasExtra(AppConstants.Extra.EXTRA_CTA_ID))
            ctaId = intent.getStringExtra(AppConstants.Extra.EXTRA_CTA_ID)
        getSavedMap()
        stageAdapter = SubCategoryAdapter(this)
        selectProductAdapter = SelectProductAdapter(this)
        if (categoryId != null) {
            getInventoryConfig(categoryId!!)
            CommonUtils.showLogMessage("e", "flavourId", flavourId)
            selectProductAdapter.setLinkOption(linkOption!!)
            if (linkingType != null && linkingType == TaggingType.SINGLE
                && linkOption != null && linkOption == LinkOptions.DIRECT
            ) {
                binding.btnPlaceOrder.text = getString(R.string.proceed)

            }

        }
        binding.stageAdapter = stageAdapter
        if (geoTagging) {
            var getUserManualLocationData = GetManualLocationRequest()
            getUserManualLocationData.userGeoReq = allowGeography
            showLoading()
            selectOrderViewModel.getHubList(
                httpManager,
                getUserManualLocationData
            )
        } else {
            showLoading()
            selectOrderViewModel.getProductCategory(flavourId, categoryId, httpManager, null)
        }

        binding.tvClear.setOnClickListener {
            globalSearch = false
            binding.etSearch.setText("")
            binding.rvCategory.visibility = View.VISIBLE
            binding.rlSearchResult.visibility = View.GONE
            binding.tvCountSearchResult.text = ""
            binding.tvResults.text = ""
            if (cid != null) {
                currentPage = PaginationListener.PAGE_START
                getProductFromFromServer(cid!!)
            }
        }
        binding.ivSearch.setOnClickListener {
            var keyword = binding.etSearch.text.toString()
            if (keyword.isNotEmpty()) {
                globalSearch = true
                binding.rvCategory.visibility = View.GONE
                binding.rlSearchResult.visibility = View.VISIBLE
                showLoading()
                getProducts(null)
            } else {
                TrackiToast.Message.showShort(this, "Please enter Code/Name")
            }


        }


    }

    private fun getInventoryConfig(categoryId: String?) {
        if (categoryId == null)
            return
        val listCategory = mPref.workFlowCategoriesList
        val workFlowCategories = WorkFlowCategories()
        workFlowCategories.categoryId = categoryId
        if (listCategory.contains(workFlowCategories)) {
            val position: Int = listCategory.indexOf(workFlowCategories)
            if (position != -1) {
                val myCatData: WorkFlowCategories = listCategory.get(position)
                // userGeoReq=myCatData.getAllowGeography();
                allowGeography = myCatData.allowGeography

                if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.linkingType != null) {
                    linkingType = myCatData.inventoryConfig!!.linkingType

                }
                if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.linkOption != null) {
                    linkOption = myCatData.inventoryConfig!!.linkOption

                }
                if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.flavorId != null && myCatData.inventoryConfig!!.flavorId!!.isNotEmpty()) {
                    if (flavourId == null)
                        flavourId = myCatData.inventoryConfig!!.flavorId
                }
                if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.geoTagging != null) {
                    geoTagging = myCatData.inventoryConfig!!.geoTagging!!
                }
                if (!intent.hasExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_DYNAMIC_PRICING)) {
                    if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.dynamicPricing != null) {
                        dynamicPricing = myCatData.inventoryConfig!!.dynamicPricing!!

                    }
                }
            }
        }
    }


    private fun setRecyclerView() {
        if (rvProducts == null) {
            rvProducts = binding.rvProducts
            //  mLayoutManager= (LinearLayoutManager) rvAttendance.getLayoutManager();
            try {
                mLayoutManager = LinearLayoutManager(this)
                mLayoutManager!!.orientation = RecyclerView.VERTICAL
                rvProducts!!.layoutManager = mLayoutManager
                rvProducts!!.itemAnimator = DefaultItemAnimator()

            } catch (e: IllegalArgumentException) {
            }
            rvProducts!!.adapter = selectProductAdapter
        }

        rvProducts!!.addOnScrollListener(object : PaginationListener(mLayoutManager!!) {
            override fun loadMoreItems() {
                this@SelectOrderActivity.isLoading = true
                if (cid != null) {
                    currentPage++
                    getProductFromFromServer(cid!!)
                }
            }

            override fun isLastPage(): Boolean {
                return this@SelectOrderActivity.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@SelectOrderActivity.isLoading
            }
        })
    }

    private fun getProductFromFromServer(cid: String) {
        getProducts(cid)
    }


    override fun getLayoutId(): Int {
        return R.layout.activity_select_order
    }


    fun enableControls(enable: Boolean) {
        if (enable) {
            binding.llButton.visibility = View.VISIBLE
        } else {
            binding.llButton.visibility = View.GONE
        }

    }

    override fun onCategorySelected(data: CataLogProductCategory) {
        if (data.cid != null)
            currentPage = PaginationListener.PAGE_START
        getProductFromFromServer(data.cid!!)

    }


    private fun getSavedMap() {
        if (mPref.userDetail != null && mPref.userDetail.userId != null) {
            if (CommonUtils.getTotalItemCount(mPref.userDetail.userId!!, mPref) > 0) {
                if (mPref.getProductInCartWRC() != null && mPref.getProductInCartWRC()!!
                        .containsKey(mPref.userDetail.userId)
                ) {
                    savedOrderMap = mPref.getProductInCartWRC()!![mPref.userDetail.userId]
                }
            } else {
                savedOrderMap = HashMap()
            }
        }
    }

    override fun addProduct(data: CatalogProduct, position: Int) {
        if (linkingType != null && linkingType == TaggingType.SINGLE && linkOption != null && linkOption == LinkOptions.DIRECT) {
            Log.e("position", "" + position)
            savedOrderMap = HashMap()
            if (data.addInOrder) {
                savedOrderMap!![data.pid!!] = data
            } else {
                if (savedOrderMap!!.contains(data.pid)) {
                    savedOrderMap!!.remove(data.pid)
//                    Log.e("remove product", data.name)
                }
            }
            for (index in selectProductAdapter.getAllList().indices) {
                if (index != position) {
                    selectProductAdapter.getAllList()[index].addInOrder = false
                    selectProductAdapter.getAllList()[index].noOfProduct = 0
                }
            }
            selectProductAdapter.notifyDataSetChanged()
        } else {
            if (savedOrderMap == null) {
                savedOrderMap = HashMap()
            }
            if (data.addInOrder) {
                savedOrderMap!![data.pid!!] = data
            } else {
                if (savedOrderMap!!.contains(data.pid)) {
                    savedOrderMap!!.remove(data.pid)
//                    Log.e("remove product", data.name)
                }
            }

        }
        var saveOrderInCart = HashMap<String, Map<String, CatalogProduct>>()
        if (mPref.getProductInCartWRC() != null)
            saveOrderInCart.putAll(mPref.getProductInCartWRC()!!)
        saveOrderInCart[mPref.userDetail.userId!!] = savedOrderMap!!
        mPref.saveProductInCartWRC(saveOrderInCart)
        var jsonConverter2 = JSONConverter<Map<String, Map<String, CatalogProduct>>>()
        var str2 = jsonConverter2.objectToJson(mPref.getProductInCartWRC())
//        Log.e("map_addProduct", str2)


        invalidateOptionsMenu()
        var jsonConverter = JSONConverter<HashMap<String, CatalogProduct>>()
        var str = jsonConverter.objectToJson(savedOrderMap)
        Log.e("map", str)
        if (savedOrderMap!!.isNotEmpty())
            binding.llButton.visibility = View.VISIBLE
        else {
            binding.llButton.visibility = View.GONE
        }

    }


    override fun removeProduct(data: CatalogProduct, position: Int) {

    }


    private fun getProducts(cid: String?) {
        if (currentPage == PaginationListener.PAGE_START) {
            selectProductAdapter.clearList()
            binding.rvProducts.removeAllViewsInLayout()

        }

        paginationRequest = PaginationRequest()
        paginationRequest!!.limit = 10
        paginationRequest!!.offset = (currentPage - 1) * 10
        paginationRequest!!.dataCount = 10
        if (globalSearch) {
            var keyword = binding.etSearch.text.toString()
            paginationRequest!!.keyword = keyword
            selectOrderViewModel.getProductByKeyWord(
                categoryId,
                flavourId,
                geoId,
                httpManager,
                paginationRequest
            )
        } else {
            this.cid = cid
            selectOrderViewModel.getProduct(
                httpManager,
                categoryId,
                cid,
                flavourId,
                geoId,
                paginationRequest
            )
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.search_view, menu)
        val myActionMenuItem = menu!!.findItem(R.id.action_search)
        myActionMenuItem.isVisible = false
        searchView = myActionMenuItem.actionView as SearchView

        val menuItem: MenuItem = menu.findItem(R.id.action_cart)
        val qrCode: MenuItem = menu.findItem(R.id.action_qr_code)

        if (linkingType != null && linkingType == TaggingType.SINGLE
            && linkOption != null && linkOption == LinkOptions.DIRECT
        ) {
            menuItem.isVisible = false
            qrCode.isVisible = false

        } else {
            menuItem.isVisible = true
            qrCode.isVisible = true
        }

        var count = 0
        var jsonConverter = JSONConverter<Map<String, Map<String, CatalogProduct>>>()
        var str = jsonConverter.objectToJson(mPref.productInCartWRC)
        Log.e("map_menu", str)
        if (mPref.userDetail != null && mPref.userDetail.userId != null)
            count = CommonUtils.getTotalItemCount(mPref.userDetail.userId!!, mPref)
        menuItem.setIcon(buildCounterDrawable(count, R.drawable.ic_cart))
        qrCode.icon = buildCounterDrawable(0,R.drawable.ic_qr_code)
        return true
    }

    val PLACE_ORDER = 174

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.btnPlaceOrder -> {
                viewCart()
            }
            R.id.btnCancel -> {
                onCancel()
            }
        }
    }


    fun onSuccess() {
        val returnIntent = Intent()
//            returnIntent.putExtra("result", result)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun onLinkInventorySucess() {
        if (mPref.userDetail != null && mPref.userDetail!!.userId != null) {
            if (CommonUtils.getTotalItemCount(mPref.userDetail!!.userId!!, mPref) > 0) {
                if (mPref.getProductInCartWRC() != null && mPref.getProductInCartWRC()!!
                        .containsKey(mPref.userDetail!!.userId!!)
                ) {

                    var saveOrderInCart = HashMap<String, Map<String, CatalogProduct>>()
                    if (mPref.getProductInCartWRC() != null)
                        saveOrderInCart.putAll(mPref.getProductInCartWRC()!!)
                    saveOrderInCart.remove(mPref.userDetail!!.userId!!)
                    mPref.saveProductInCartWRC(saveOrderInCart)
                    var jsonConverter2 = JSONConverter<Map<String, Map<String, CatalogProduct>>>()
                    var str2 = jsonConverter2.objectToJson(mPref.getProductInCartWRC())
                    Log.e("delete_map", str2)

                }
            }
        }
        val returnIntent = Intent()
//            returnIntent.putExtra("result", result)
        setResult(Activity.RESULT_OK, returnIntent)
        finish()
    }

    fun onCancel() {
        val returnIntent = Intent()
        setResult(Activity.RESULT_CANCELED, returnIntent)
        finish()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PLACE_ORDER) {
            if (resultCode == Activity.RESULT_OK) {
                onSuccess()
            } else {
                getSavedMap()
                invalidateOptionsMenu()
                selectProductAdapter.clearList()
                binding.rvProducts.removeAllViewsInLayout()
                if (cid != null) {
                    currentPage = PaginationListener.PAGE_START
                    getProductFromFromServer(cid!!)
                }
            }
        } else if (requestCode == AppConstants.REQUEST_CODE_CREATE_TASK_DIRECT) {
            if (resultCode == Activity.RESULT_OK) {
                onLinkInventorySucess()
            }
        } else if (requestCode == AppConstants.REQUEST_CODE_CREATE_TASK) {
            if (resultCode == Activity.RESULT_OK) {
                taskId = data!!.getStringExtra(AppConstants.Extra.EXTRA_TASK_ID)
                buddyName = data!!.getStringExtra(AppConstants.Extra.EXTRA_BUDDY_NAME)
                buddyId = data!!.getStringExtra(AppConstants.Extra.EXTRA_BUDDY_ID)
                fleetId = data!!.getStringExtra(AppConstants.Extra.EXTRA_FLEET_ID)
                var linkRequest = LinkInventoryRequest()
                if (savedOrderMap != null && savedOrderMap!!.isNotEmpty()) {
//                    var map = HashMap<String, Int>()
                    var map = ArrayList<SelectedProduct>()
                    for (data in savedOrderMap!!.values) {
                        // map[data.pid!!] = data.noOfProduct
                        var product = SelectedProduct()
                        product.productId = data.pid
                        product.quantity = data.noOfProduct
                        product.price = data.sellingPrice
                        map.add(product)
                    }
                    linkRequest.products = map
                }
                linkRequest.categoryId = categoryId
                linkRequest.taskId = taskId
//                linkRequest.autoApprove = true
                val listCategory = mPref.workFlowCategoriesList
                if (categoryId != null) {
                    val workFlowCategories = WorkFlowCategories()
                    workFlowCategories.categoryId = categoryId
                    if (listCategory.contains(workFlowCategories)) {
                        val position: Int = listCategory.indexOf(workFlowCategories)
                        if (position != -1) {
                            val myCatData: WorkFlowCategories = listCategory.get(position)
                            // userGeoReq=myCatData.getAllowGeography();
                            if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.approvalType != null && myCatData.inventoryConfig!!.approvalType == ApprovalType.MANUAL
                                && myCatData.inventoryConfig!!.approvalOn != null && myCatData.inventoryConfig!!.approvalOn == ApprovalOn.SUB_TASK
                            ) {
                                linkRequest.createSubTask = true
                                var subTaskConfig = myCatData.subTaskConfig
                                if (subTaskConfig != null && subTaskConfig.categories!!.isNotEmpty()) {
                                   // linkRequest.subCategoryId = subTaskConfig.categories!![0]
                                    linkRequest.subTaskCategory = subTaskConfig.categories!![0]
                                    linkRequest.parentTaskId = taskId
                                }

                            }
                            if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.flavorId != null && myCatData.inventoryConfig!!.flavorId!!.isNotEmpty()) {
                                linkRequest.flavorId = myCatData.inventoryConfig!!.flavorId
                            }
                            if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.availabilityType != null)
                                linkRequest.type = myCatData.inventoryConfig!!.availabilityType
                            if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.approvalType != null) {
                                linkRequest.autoApprove =
                                    myCatData.inventoryConfig!!.approvalType == ApprovalType.AUTO
                            }
                        }
                    }
                }
                val jsonConverter: JSONConverter<LinkInventoryRequest> = JSONConverter()
                var strRequest = jsonConverter.objectToJson(linkRequest)
                CommonUtils.showLogMessage("e", "strRequest", strRequest);
                showLoading()
                selectOrderViewModel.linkInventory(httpManager, linkRequest)

            }
            if (resultCode == Activity.RESULT_CANCELED) {

            }
        }

    }

    private fun buildCounterDrawable(count: Int, backgroundImageId: Int): Drawable? {
        val inflater = LayoutInflater.from(this)
        val view: View = inflater.inflate(R.layout.counter_menu_item_layout, null)
        view.setBackgroundResource(backgroundImageId)
        if (count == 0) {
            val counterTextPanel = view.findViewById<View>(R.id.counterValuePanel)
            counterTextPanel.visibility = View.GONE
        } else {
            val textView = view.findViewById<View>(R.id.count) as TextView
            textView.text = "" + count
        }
        view.measure(
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
            View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
        )
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.isDrawingCacheEnabled = true
        view.drawingCacheQuality = View.DRAWING_CACHE_QUALITY_HIGH
        val bitmap = Bitmap.createBitmap(view.drawingCache)
        view.isDrawingCacheEnabled = false
        return BitmapDrawable(resources, bitmap)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_cart -> {
                viewCart()
                return true
            }

            R.id.action_qr_code -> {
                getCameraPermission()
            }

        }
        return super.onOptionsItemSelected(item)

    }

    private fun viewCart() {
        if (savedOrderMap == null || savedOrderMap!!.isEmpty()) {
            TrackiToast.Message.showShort(this, getString(R.string.cart_is_empty_please_add_item))
            return
        }
        var list = savedOrderMap!!
        if (linkingType != null && linkingType == TaggingType.SINGLE
            && linkOption != null && linkOption == LinkOptions.DIRECT
        ) {

            if (list.isNotEmpty()) {
                linkInventory()
            } else {
                TrackiToast.Message.showShort(
                    this,
                    getString(R.string.please_add_product)
                )
            }
        } else {
            if (list.isNotEmpty()) {
                cartTaskWork()
            } else {
                TrackiToast.Message.showShort(
                    this,
                    getString(R.string.cart_is_empty_please_add_item)
                )
            }
        }
    }

    private fun cartTaskWork() {
        var map = HashMap<String, Int>()
        for (data in savedOrderMap!!.values) {
            map[data.pid!!] = data.noOfProduct
        }
        var intent = Intent(this, CartActivity::class.java)

        intent.putExtra(AppConstants.DELIVERY_CHARGE, deliveryChargeAmount)
        if (taskId != null)
            intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, taskId)
        if (target != null)
            intent.putExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_TARGET, target)
        if (flavourId != null)
            intent.putExtra(AppConstants.Extra.EXTRA_TASK_TAG_IN_FLAVOUR_ID, flavourId)
        if (categoryId != null)
            intent.putExtra(AppConstants.Extra.EXTRA_CATEGORY_ID, categoryId)
        if (ctaId != null)
            intent.putExtra(AppConstants.Extra.EXTRA_CTA_ID, ctaId)
        if (deliverMode != null)
            intent.putExtra(AppConstants.DELIVERY_MODE, deliverMode)
        intent.putExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_DYNAMIC_PRICING, dynamicPricing)
        startActivityForResult(intent, PLACE_ORDER)
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getViewModel(): SelectOrderViewModel {
        return selectOrderViewModel
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }

    override fun handleProductCategoryResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?
    ) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@SelectOrderActivity)) {

            val jsonConverter: JSONConverter<CatalogCategoryResponse> = JSONConverter()
            var responseMain: CatalogCategoryResponse = jsonConverter.jsonToObject(
                result.toString(),
                CatalogCategoryResponse::class.java
            ) as CatalogCategoryResponse
            if (responseMain.data != null && responseMain.data!!.isNotEmpty()) {
                stageAdapter!!.addItems(responseMain.data!!)
                if (responseMain.data!!.size == 1)
                    binding.rvCategory.visibility = View.GONE
                var catFirst: CataLogProductCategory? = responseMain.data!![0]
                if (catFirst != null) {
                    catFirst.selected = true
                    onCategorySelected(catFirst)
                }

            } else {

            }
        } else {
            binding.llButton.visibility = View.GONE
        }
    }

    override fun handleProductResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        this.isLoading = false
        if (CommonUtils.handleResponse(callback, error, result, this@SelectOrderActivity)) {
            var jsonConverter = JSONConverter<CatalogProductResponse>()
            var responseMain: CatalogProductResponse = jsonConverter.jsonToObject(
                result.toString(),
                CatalogProductResponse::class.java
            ) as CatalogProductResponse
            if (responseMain.data != null && responseMain.data!!.isNotEmpty()) {
                if (globalSearch) {
                    binding.rlSearchResult.visibility = View.VISIBLE
                    binding.tvCountSearchResult.text = "" + responseMain.count
                    binding.rlSearchResult.visibility = View.VISIBLE
                    binding.tvResults.text = " Search results for '${binding.etSearch.text}'"
                } else {
                    binding.tvCountSearchResult.text = ""
                    binding.tvResults.text = ""
                }
                var list = responseMain.data!!
                if (savedOrderMap != null && savedOrderMap!!.isNotEmpty()) {
                    binding.llButton.visibility = View.VISIBLE
                    for (data in list) {
                        if (savedOrderMap!!.contains(data.pid)) {
                            var newdata = savedOrderMap!![data.pid]!!
                            data.addInOrder = newdata.addInOrder
                            data.noOfProduct = newdata.noOfProduct
                        }
                    }
                }
                setRecyclerView()
                selectProductAdapter!!.addItems(list)
                binding.rlSearch.visibility = View.VISIBLE
                CommonUtils.showLogMessage(
                    "e", "adapter total_count =>",
                    "" + selectProductAdapter.itemCount
                )
                CommonUtils.showLogMessage(
                    "e", "fetch total_count =>",
                    "" + responseMain.count
                )
                isLastPage = selectProductAdapter.itemCount >= responseMain.count

            } else {
                if (globalSearch) {
                    binding.rlSearchResult.visibility = View.VISIBLE
                    binding.tvCountSearchResult.text = "" + 0
                    binding.tvResults.text = " Search results for '${binding.etSearch.text}'"
                } else {
                    binding.rlSearchResult.visibility = View.GONE
                    binding.tvCountSearchResult.text = ""
                    binding.tvResults.text = ""
                }
                setRecyclerView()
                selectProductAdapter!!.addItems(ArrayList())
            }
        } else {
            if (globalSearch) {
                binding.rlSearchResult.visibility = View.VISIBLE
                binding.tvCountSearchResult.text = "" + 0
                binding.tvResults.text = " Search results for '${binding.etSearch.text}'"
            } else {
                binding.rlSearchResult.visibility = View.GONE
                binding.tvCountSearchResult.text = ""
                binding.tvResults.text = ""
            }
        }
    }

    override fun handleCreateOrderResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@SelectOrderActivity)) {
            var jsonConverter = JSONConverter<BaseResponse>()
            var responseMain: BaseResponse = jsonConverter.jsonToObject(
                result.toString(),
                BaseResponse::class.java
            ) as BaseResponse

            if (responseMain.responseMsg != null) {
                TrackiToast.Message.showShort(this, responseMain.responseMsg)
                onLinkInventorySucess()
            } else {
                onLinkInventorySucess()
            }
        }
    }

    override fun linkInventoryResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            onLinkInventorySucess()

        }
    }

    override fun handleHubListResponse(callback: ApiCallback, result: Any?, error: APIError?) {
//        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {

            val geoLocation = Gson().fromJson<GetUserManualLocationData>(
                result.toString(),
                GetUserManualLocationData::class.java
            )
            if (!geoLocation.data.isNullOrEmpty()) {
                val list = java.util.ArrayList<LocData>()
                val hmIterator: Iterator<*> = geoLocation.data!!.entries.iterator()
                while (hmIterator.hasNext()) {
                    val mapElement = hmIterator.next() as Map.Entry<*, *>
                    val buddy = LocData()
                    buddy.id = mapElement.key.toString()
                    buddy.name = mapElement.value.toString()
                    list.add(buddy)
                }
                if (list.isNotEmpty()) {
                    geoId = list[0].id
                    binding.tvHubs.text = list[0].name
                    binding.rlHubs.visibility = View.VISIBLE
                    binding.rlHubs.setOnClickListener {
                        popupShowHubs(list)
                    }
                    selectOrderViewModel.getProductCategory(
                        flavourId,
                        categoryId,
                        httpManager,
                        null
                    )
                }

            }else{
                hideLoading()
                binding.rlSearch.visibility=View.GONE
                selectProductAdapter!!.addItems(ArrayList())
                setRecyclerView()
            }


        }else{
            hideLoading()
            binding.rlSearch.visibility=View.GONE
            selectProductAdapter!!.addItems(ArrayList())
            setRecyclerView()
        }
    }

    fun linkInventory() {

        if (linkOption != null && linkOption == LinkOptions.DIRECT) {
            if (intent.hasExtra(AppConstants.Extra.EXTRA_CTA_ID)) {
                // TODO Code for order tagging
                return
            } else {
                var list = selectProductAdapter.getAllList().filter { it.addInOrder }
                if (list.isNotEmpty()) {
                    var product = list[0]
                    if (product.prodInfoMap != null && product.prodInfoMap!!.isNotEmpty())
                        openDialogShowMap(list)
                    else {
                        var invIds = ArrayList<String>()
                        for (data in list) {
                            invIds.add(data.pid!!)
                        }
                        createTaskSingleDirect(invIds, product.name)
                    }

                } else {
                    TrackiToast.Message.showShort(this, "Please add items")
                }
            }

        } else {
            if (intent.hasExtra(AppConstants.Extra.EXTRA_CTA_ID)) {
                if (intent.hasExtra(AppConstants.Extra.EXTRA_TASK_ID)) {
                    taskId = intent.getStringExtra(AppConstants.Extra.EXTRA_TASK_ID)
                }
                var ctaId = intent.getStringExtra(AppConstants.Extra.EXTRA_CTA_ID)


                var linkRequest = LinkInventoryRequest()
                linkRequest.categoryId = categoryId
                linkRequest.taskId = taskId
                if (savedOrderMap != null && savedOrderMap!!.isNotEmpty()) {
//                    var map = HashMap<String, Int>()
                    var map = ArrayList<SelectedProduct>()
                    for (data in savedOrderMap!!.values) {
                        // map[data.pid!!] = data.noOfProduct
                        var product = SelectedProduct()
                        product.productId = data.pid
                        product.quantity = data.noOfProduct
                        product.price = data.sellingPrice
                        map.add(product)
                    }
                    linkRequest.products = map
                }

                linkRequest.ctaId = ctaId
                val listCategory = mPref.workFlowCategoriesList
                if (categoryId != null) {
                    val workFlowCategories = WorkFlowCategories()
                    workFlowCategories.categoryId = categoryId
                    if (listCategory.contains(workFlowCategories)) {
                        val position: Int = listCategory.indexOf(workFlowCategories)
                        if (position != -1) {
                            val myCatData: WorkFlowCategories = listCategory.get(position)
                            // userGeoReq=myCatData.getAllowGeography();
                            if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.approvalType != null && myCatData.inventoryConfig!!.approvalType == ApprovalType.MANUAL
                                && myCatData.inventoryConfig!!.approvalOn != null && myCatData.inventoryConfig!!.approvalOn == ApprovalOn.SUB_TASK
                            ) {
                                linkRequest.createSubTask = true
                                var subTaskConfig = myCatData.subTaskConfig
                                if (subTaskConfig != null && subTaskConfig.categories!!.isNotEmpty()) {
                                   // linkRequest.subCategoryId = subTaskConfig.categories!![0]
                                    linkRequest.subTaskCategory = subTaskConfig.categories!![0]
                                    linkRequest.parentTaskId = taskId
                                }

                            }
                            if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.flavorId != null && myCatData.inventoryConfig!!.flavorId!!.isNotEmpty()) {
                                linkRequest.flavorId = myCatData.inventoryConfig!!.flavorId
                            }
                            if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.availabilityType != null)
                                linkRequest.type = myCatData.inventoryConfig!!.availabilityType
                            if (myCatData.inventoryConfig != null && myCatData.inventoryConfig!!.approvalType != null) {
                                linkRequest.autoApprove =
                                    myCatData.inventoryConfig!!.approvalType == ApprovalType.AUTO
                            }
                            linkRequest.type = myCatData.inventoryConfig!!.availabilityType
                        }
                    }
                }
                val jsonConverter: JSONConverter<LinkInventoryRequest> = JSONConverter()
                var strRequest = jsonConverter.objectToJson(linkRequest)
                CommonUtils.showLogMessage("e", "strRequest", strRequest);
                showLoading()
                selectOrderViewModel.linkInventory(httpManager, linkRequest)


            } else {
                var list = selectProductAdapter.getAllList().filter { it.addInOrder }
                if (list.isNotEmpty()) {
                    val intent = NewCreateTaskActivity.newIntent(this)
                    intent.putExtra(AppConstants.Extra.FROM, "taskListing")
                    val dashBoardBoxItem = DashBoardBoxItem()
                    dashBoardBoxItem.categoryId = categoryId
                    intent.putExtra(
                        AppConstants.Extra.EXTRA_CATEGORIES,
                        Gson().toJson(dashBoardBoxItem)
                    )
                    intent.putExtra(
                        AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU,
                        true
                    )
                    startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK)
                } else {
                    TrackiToast.Message.showShort(this, "Please add items")
                }
            }
        }


    }

    @SuppressLint("SetTextI18n")
    private fun openDialogShowMap(listOfProduct: List<CatalogProduct>?) {
        if (listOfProduct == null)
            return
        val listOrderDetais = ArrayList<ProductMap>()
        var product: CatalogProduct? = listOfProduct[0]
        if (product == null)
            return

        val hmIterator: Iterator<*> = product.prodInfoMap!!.entries.iterator()
        while (hmIterator.hasNext()) {
            val mapElement = hmIterator.next() as Map.Entry<*, *>
            val orderInnerData =
                ProductMap(mapElement.key.toString(), mapElement.value.toString())
            listOrderDetais.add(orderInnerData)
        }

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.layout_show_product_map_with_continue_button)
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
        val ivBack = dialog.findViewById<View>(R.id.ivBack) as ImageView
        val productImage = dialog.findViewById<ImageView>(R.id.productImage)
        val tvDetails = dialog.findViewById<TextView>(R.id.tvDetails)
        val btnPlaceOrder = dialog.findViewById<Button>(R.id.btnPlaceOrder)
        val rvProductMap = dialog.findViewById<RecyclerView>(R.id.rvProductMap)
        if (product.img != null && product.img!!.isNotEmpty()) {
            productImage.visibility = View.VISIBLE
            GlideApp.with(this).load(product.img)
                .placeholder(R.drawable.ic_picture)
                .into(productImage)
            productImage.setOnClickListener {
//                openDialogShowImage(product.img!!)
            }
        } else {
            productImage.visibility = View.GONE
            productImage.setImageResource(R.drawable.ic_picture)
        }
        btnPlaceOrder.setOnClickListener {
            var invIds = ArrayList<String>()
            for (data in listOfProduct) {
                invIds.add(data.pid!!)
            }
            dialog.dismiss()
            createTaskSingleDirect(invIds, product.name)
        }

        if (listOrderDetais.isNotEmpty()) {
            var adapter = ProductMapAdapter(listOrderDetais)
            rvProductMap.adapter = adapter
        }

        dialog.window!!.attributes = lp
        ivBack.setOnClickListener { dialog.dismiss() }
        if (!dialog.isShowing) dialog.show()
    }

    private fun createTaskSingleDirect(invIds: java.util.ArrayList<String>, reffId: String?) {
        val intent = NewCreateTaskActivity.newIntent(this)
        intent.putExtra(AppConstants.Extra.FROM, "taskListing")
        val dashBoardBoxItem = DashBoardBoxItem()
        dashBoardBoxItem.categoryId = categoryId
        intent.putExtra(
            AppConstants.Extra.EXTRA_CATEGORIES,
            Gson().toJson(dashBoardBoxItem)
        )
        intent.putStringArrayListExtra(
            "invIds",
            invIds
        )
        if (reffId != null) {
            intent.putExtra(
                "reffId",
                reffId
            )
        }
        intent.putExtra(
            "directMapping",
            true
        )
        intent.putExtra(
            AppConstants.Extra.EXTRA_BUDDY_LIST_CALLING_FROM_DASHBOARD_MENU,
            true
        )
        startActivityForResult(intent, AppConstants.REQUEST_CODE_CREATE_TASK_DIRECT)
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

    fun popupShowHubs(list: ArrayList<LocData>) {
        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.hubs_list_pop)
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialog.window!!.attributes)
        lp.width = WindowManager.LayoutParams.WRAP_CONTENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.dimAmount = 0.8f
        val window = dialog.window
        window!!.setLayout(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
        window.setGravity(Gravity.CENTER)
        val rVMultiSelect = dialog.findViewById<RecyclerView>(R.id.rVMultiSelect)
        val ivClose = dialog.findViewById<ImageView>(R.id.ivClose)
        val etSearch = dialog.findViewById<EditText>(R.id.etSearch)
        var adapter = HubListSelectedAdapter(this)
        adapter.setHubSelectedListener(object : HubListSelectedAdapter.SelectedHubListener {
            override fun onSelectedHub(data: LocData) {
                dialog.dismiss()
                geoId = data.id
                binding.tvHubs.text = data.name
                if (cid != null) {
                    currentPage = PaginationListener.PAGE_START
                    getProductFromFromServer(cid!!)
                }
            }

        })
        adapter.addItems(list)
        rVMultiSelect.adapter = adapter
        dialog.window!!.attributes = lp
        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}
            override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
                if (charSequence.length >= 2) {
                    rVMultiSelect.removeAllViewsInLayout()
                    adapter!!.filter!!.filter(charSequence)
                } else
                    adapter!!.filter!!.filter("")
            }

            override fun afterTextChanged(editable: Editable) {

            }
        })

        ivClose.setOnClickListener {
            dialog.dismiss()
        }
        if (!dialog.isShowing)
            dialog.show()
    }

    open fun getCameraPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                openScanActivity()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.CAMERA),
                    REQUEST_CAMERA
                )
            }
        } else {
            openScanActivity()
        }
    }

    open fun openScanActivity() {
        val sharedPreferences: SharedPreferences =
            getSharedPreferences("Scan", MODE_PRIVATE)
        sharedPreferences.edit().putString("Scan", "Product").apply()
        //list send
        val listDataTemp = selectProductAdapter.getAllList().filter { it.addInOrder }
        val intent = ProductScan.newIntent(this, listDataTemp as ArrayList<CatalogProduct>)
        intent.putExtra(AppConstants.DELIVERY_CHARGE, deliveryChargeAmount)
        if (taskId != null)
            intent.putExtra(AppConstants.Extra.EXTRA_TASK_ID, taskId)
        if (target != null)
            intent.putExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_TARGET, target)
        if (flavourId != null)
            intent.putExtra(AppConstants.Extra.EXTRA_TASK_TAG_IN_FLAVOUR_ID, flavourId)
        if (categoryId != null)
            intent.putExtra(AppConstants.Extra.EXTRA_CATEGORY_ID, categoryId)
        if (ctaId != null)
            intent.putExtra(AppConstants.Extra.EXTRA_CTA_ID, ctaId)
        if (deliverMode != null)
            intent.putExtra(AppConstants.DELIVERY_MODE, deliverMode)
        intent.putExtra(AppConstants.Extra.EXTRA_TASK_TAG_INV_DYNAMIC_PRICING, dynamicPricing)
        startActivity(intent)

    }


}