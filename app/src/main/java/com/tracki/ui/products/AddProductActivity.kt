package com.tracki.ui.products

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.Typeface
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.*
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tracki.BR
import com.tracki.R
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.model.request.UpdateFileRequest
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityAddProductBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.utils.*
import com.tracki.utils.image_utility.Compressor
import com.tracki.utils.image_utility.ImagePicker
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.util.HashMap
import javax.inject.Inject
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.snackbar.Snackbar
import com.tracki.data.local.prefs.AppPreferencesHelper
import com.tracki.data.model.BaseResponse
import com.tracki.data.model.response.config.*
import com.tracki.ui.category.ProductDescription
import com.tracki.ui.category.ProductDescriptionAdapter
import com.tracki.ui.custom.ExecutorThread
import com.tracki.ui.dynamicform.DynamicFormActivity
import com.tracki.ui.dynamicform.dynamicfragment.FormSubmitListener
import com.tracki.ui.newdynamicform.NewDynamicFormFragment
import com.tracki.ui.selectorder.CatalogProduct
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import kotlinx.android.synthetic.main.activity_new_create_task.*
import java.util.concurrent.ConcurrentHashMap


class AddProductActivity : BaseActivity<ActivityAddProductBinding, AddProductViewModel>(),
    AddProductNavigator, View.OnClickListener, FormSubmitListener, ProductDescriptionAdapter.OnProductDescriptionListener, HasSupportFragmentInjector {

    enum class PRODUCT_FILED(var label: String) {
        INVENTORY_NAME("Inventory Name"), BRAND("Brand"), SELLING_PRICE("Selling Price"), MSP("Minimum Support Price"),
        REFERENCE_NUMBER("Reference Number"), QUANTITY("Quantity"), UNIT("Unit"), MAX_ORDER_LIMIT("Maximum Unit per Order"), MIN_STOCK_QUANTITY(
            "Min Stock Quantity"
        ),
        DESCRIPTION("Description"), SPECIFICATION("Specification"), IMAGES("Images");
    }

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>
    private var dfId: String? = null
    private var dfdId: String? = null
    private var isEditable: Boolean = true
    lateinit var binding: ActivityAddProductBinding

    @Inject
    lateinit var addProductViewModel: AddProductViewModel


    @Inject
    lateinit var mPref: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager
    private var action: String? = null
    private var imageUrl: String? = null
    private var compressedImage: File? = null
    private var actualImage: File? = null
    private val REQUEST_READ_STORAGE: Int = 1112
    private val PICK_IMAGE_FILE_ID: Int = 1113
    private var mMapCategory: Map<String, String>? = null
    var flavorId: String? = null
    var mainData: ArrayList<FormData>? = null
    private var snackBar: Snackbar? = null
    private var mainMap: HashMap<String, ArrayList<FormData>>? = null

    private var dynamicFragment: NewDynamicFormFragment? = null
    private var dynamicFormsNew: DynamicFormsNew? = null

    private var mDynamicHandler: DynamicHandler? = null
    private var handlerThread: ExecutorThread? = null

    var titleText: TextView? = null
    var percentageText: TextView? = null
    var currentStatusText: TextView? = null
    var progressBar: ProgressBar? = null
    var rlProgress: RelativeLayout? = null
    var rlSubmittingData: RelativeLayout? = null
    var addProdrequest: AddProductRequest? = null

    var IMAGES: AllowedField? = null
    var INVENTORY_NAME: AllowedField? = null
    var MRP: AllowedField? = null
    var SELLING_PRICE: AllowedField? = null
    var QUANTITY: AllowedField? = null
    var UNIT: AllowedField? = null
    var MAX_ORDER_LIMIT: AllowedField? = null
    var MIN_STOCK_QUANTITY: AllowedField? = null
    var REFERENCE_NUMBER: AllowedField? = null
    var SPECIFICATION: AllowedField? = null
    var DESCRIPTION: AllowedField? = null
    private var unit: String? = null
    private var cid: String? = null
    private var packUnit: String? = null
    private var product: CatalogProduct? = null
    lateinit var adapter: ProductDescriptionAdapter
    private var position: Int? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        uiCompomnent()
        handlerThread = ExecutorThread()
        addProductViewModel.navigator = this
        setToolbar(binding.toolbar, "Add Product")
        adapter = ProductDescriptionAdapter(this)
        binding.rvDescriptions.adapter = adapter
        if (intent.hasExtra(AppConstants.Extra.EXTRA_CATEGORIES)) {
            var categoryMap = intent.getStringExtra(AppConstants.Extra.EXTRA_CATEGORIES)
            CommonUtils.showLogMessage("e", "categoryMap", categoryMap)
            mMapCategory = Gson().fromJson<Map<String, String>>(
                categoryMap,
                object : TypeToken<HashMap<String?, String?>?>() {}.type
            )
            if (mMapCategory != null) {
                if (mMapCategory!!.containsKey("flavorId")) {
                    flavorId = mMapCategory!!["flavorId"]

                    if (intent.hasExtra("action")) {
                        action = intent.getStringExtra("action")
                        if (action.equals("Add")) {
                            var config = CommonUtils.getFlavourConfigFromFlavourId(flavorId, mPref)
                            if (config != null && !config.prodLabel.isNullOrEmpty()) {
                                setToolbar(binding.toolbar, "Add ${config.prodLabel}")
                            } else {
                                setToolbar(binding.toolbar, "Add Product")
                            }
                            performUiTask(flavorId)
                            dfId = CommonUtils.getDfIdFromFlavourId(flavorId, mPref)
                            showDynamicForm(dfId, ArrayList())
                        } else {
                            var config = CommonUtils.getFlavourConfigFromFlavourId(flavorId, mPref)
                            if (config != null && !config.prodLabel.isNullOrEmpty()) {
                                setToolbar(binding.toolbar, "Update ${config.prodLabel}")
                            } else {
                                setToolbar(binding.toolbar, "Update Product")
                            }
                            if (intent.hasExtra("data")) {
                                product = intent.getParcelableExtra<CatalogProduct>("data")
                            }
                            performUiTask(flavorId)
                            dfId = CommonUtils.getDfIdFromFlavourId(flavorId, mPref)
                            if (product != null && product!!.pid != null && dfId != null) {
                                showLoading()
                                addProductViewModel.getProductDetails(httpManager, product!!.pid)
                            }
                            binding.btnAddNow.text = getString(R.string.update)
                        }
                    }


                }
            }
            if (intent.hasExtra("cid")) {
                cid = intent.getStringExtra("cid")
            }
            binding.rlTakeImage.setOnClickListener(this)
            binding.ivDelete.setOnClickListener(this)
            binding.btnAddNow.setOnClickListener(this)
            binding.edPerUnit.setOnClickListener(this)
            binding.edPackUnit.setOnClickListener(this)
            binding.tvAddDescription.setOnClickListener(this)

        }
        if (mPref.units == null)
            if (TrackiApplication.getApiMap().containsKey(ApiType.GET_UNITS)) {
                addProductViewModel.getUnits(httpManager);
            }
    }

    private fun uiCompomnent() {
        var viewProgress = binding.viewProgress
        titleText = viewProgress.findViewById<TextView>(R.id.tvTitle)
        currentStatusText = viewProgress!!.findViewById<TextView>(R.id.currentStatusText)
        percentageText = viewProgress!!.findViewById<TextView>(R.id.tvPercentage)
        progressBar = viewProgress!!.findViewById<ProgressBar>(R.id.pb_loading)
        rlSubmittingData = viewProgress!!.findViewById<RelativeLayout>(R.id.rlSubmittingData)
        rlProgress = viewProgress!!.findViewById<RelativeLayout>(R.id.rlProgress)
    }

    private fun validation(): Boolean {
        var sucess = false
        var productName = binding.etProductName.text.toString().trim()
        var mrp = binding.etMrp.text.toString().trim()
        var sellingPrice = binding.etSellPrice.text.toString().trim()
        var quantity = binding.etQuantity.text.toString().trim()
        var unit = binding.edPerUnit.text.toString().trim()
        var maxOrderLimit = binding.etMaxOrderLimit.text.toString().trim()
        var minStockQuantity = binding.edMinStockQuantity.text.toString().trim()
        var reffNumber = binding.etReffNumber.text.toString().trim()
//        var productSpecs = binding.etProductSpecs.text.toString().trim()
        var description = binding.etProductDescription.text.toString().trim()
        var packunit = binding.edPackUnit.text.toString().trim()
        var from = binding.etFrom.text.toString().trim()
        var to = binding.etTo.text.toString().trim()
        if (IMAGES != null && IMAGES!!.visible && IMAGES!!.required && imageUrl.isNullOrEmpty()) {
            TrackiToast.Message.showShort(
                this,
                if (IMAGES!!.errMsg.isNullOrBlank()) "Please enter product name" else IMAGES!!.errMsg
            )
            sucess = false
            return sucess
        } else if (INVENTORY_NAME != null && INVENTORY_NAME!!.visible && INVENTORY_NAME!!.required && productName.isEmpty()) {
            TrackiToast.Message.showShort(
                this,
                if (INVENTORY_NAME!!.errMsg.isNullOrBlank()) "Please enter product name" else INVENTORY_NAME!!.errMsg
            )
            sucess = false
            return sucess
        } else if (MRP != null && MRP!!.visible && MRP!!.required && mrp.isEmpty()) {
            TrackiToast.Message.showShort(
                this,
                if (MRP!!.errMsg.isNullOrBlank()) "Please enter mrp" else MRP!!.errMsg
            )
            sucess = false
            return sucess
        } else if (SELLING_PRICE != null && SELLING_PRICE!!.visible && SELLING_PRICE!!.required && sellingPrice.isEmpty()) {
            TrackiToast.Message.showShort(
                this,
                if (SELLING_PRICE!!.errMsg.isNullOrBlank()) "Please enter selling price" else SELLING_PRICE!!.errMsg
            )
            sucess = false
            return sucess
        } else if (QUANTITY != null && QUANTITY!!.visible && QUANTITY!!.required && quantity.isEmpty()) {
            TrackiToast.Message.showShort(
                this,
                if (QUANTITY!!.errMsg.isNullOrBlank()) "Please enter quantity" else QUANTITY!!.errMsg
            )
            sucess = false
            return sucess

        } else if (UNIT != null && UNIT!!.visible && UNIT!!.required && unit.isEmpty()) {
            TrackiToast.Message.showShort(
                this,
                if (UNIT!!.errMsg.isNullOrBlank()) "Please select unit" else UNIT!!.errMsg
            )
            sucess = false
            return sucess

        } else if (UNIT != null && UNIT!!.visible && UNIT!!.required &&
            unit.isNotEmpty() && unit.uppercase() == "PACK" && packunit.isEmpty()
        ) {
            TrackiToast.Message.showShort(
                this,
                "Please select pack unit"
            )
            sucess = false
            return sucess
        } else if (UNIT != null && UNIT!!.visible && UNIT!!.required &&
            unit.isNotEmpty() && unit.uppercase() == "PACK" && from.isEmpty()
        ) {
            TrackiToast.Message.showShort(
                this,
                "Please enter from"
            )
            sucess = false
            return sucess
        } else if (UNIT != null && UNIT!!.visible && UNIT!!.required &&
            unit.isNotEmpty() && unit.uppercase() == "PACK" && to.isEmpty()
        ) {
            TrackiToast.Message.showShort(
                this,
                "Please enter to"
            )
            sucess = false
            return sucess
        } else if (MAX_ORDER_LIMIT != null && MAX_ORDER_LIMIT!!.visible && MAX_ORDER_LIMIT!!.required && maxOrderLimit.isEmpty()) {
            TrackiToast.Message.showShort(
                this,
                if (MAX_ORDER_LIMIT!!.errMsg.isNullOrBlank()) "Please enter max order limit" else MAX_ORDER_LIMIT!!.errMsg
            )
            sucess = false
            return sucess
        } else if (MIN_STOCK_QUANTITY != null && MIN_STOCK_QUANTITY!!.visible && MIN_STOCK_QUANTITY!!.required && minStockQuantity.isEmpty()) {
            TrackiToast.Message.showShort(
                this,
                if (MIN_STOCK_QUANTITY!!.errMsg.isNullOrBlank()) "Please enter minimum stock quantity" else MIN_STOCK_QUANTITY!!.errMsg
            )
            sucess = false
            return sucess

        } else if (REFERENCE_NUMBER != null && REFERENCE_NUMBER!!.visible && REFERENCE_NUMBER!!.required && reffNumber.isEmpty()) {
            TrackiToast.Message.showShort(
                this,
                if (REFERENCE_NUMBER!!.errMsg.isNullOrBlank()) "Please enter reference number" else REFERENCE_NUMBER!!.errMsg
            )
            sucess = false
            return sucess
        } else if (SPECIFICATION != null && SPECIFICATION!!.visible && SPECIFICATION!!.required && adapter.getAllList().isEmpty()) {
            TrackiToast.Message.showShort(
                this,
                if (SPECIFICATION!!.errMsg.isNullOrBlank()) "Please enter product specification" else SPECIFICATION!!.errMsg
            )
            sucess = false
            return sucess

        } else if (DESCRIPTION != null && DESCRIPTION!!.visible && DESCRIPTION!!.required && description.isEmpty()) {
            TrackiToast.Message.showShort(
                this,
                if (DESCRIPTION!!.errMsg.isNullOrBlank()) "Please enter description" else DESCRIPTION!!.errMsg
            )
            sucess = false
            return sucess

        } else {
            sucess = true
            return sucess
        }


    }

    private fun performUiTask(flavorId: String?) {

        if (flavorId == null)
            return
        IMAGES = CommonUtils.getFlavourField(flavorId, PRODUCT_FILED.IMAGES.name, mPref)
        if (IMAGES != null) {
            binding.llAddProductImage.visibility = if (IMAGES!!.visible) View.VISIBLE else View.GONE
            binding.tvProductImage.text =
                if (IMAGES!!.label.isNullOrEmpty()) PRODUCT_FILED.IMAGES.label else IMAGES!!.label
            if (product != null && !product!!.img.isNullOrEmpty()) {
                imageUrl = product!!.img
                CatalogProduct.loadCatalogProduct(binding.ivActImage, product!!.img)
                binding.cardViewTakeImage.visibility = View.GONE
                binding.cardViewActImage.visibility = View.VISIBLE
            }
        } else {
            binding.llAddProductImage.visibility = View.GONE
        }
        INVENTORY_NAME =
            CommonUtils.getFlavourField(flavorId, PRODUCT_FILED.INVENTORY_NAME.name, mPref)
        if (INVENTORY_NAME != null) {
            binding.tilProductName.visibility =
                if (INVENTORY_NAME!!.visible) View.VISIBLE else View.GONE
            binding.tvLabelProductName.text =
                if (INVENTORY_NAME!!.label.isNullOrEmpty()) PRODUCT_FILED.INVENTORY_NAME.label else INVENTORY_NAME!!.label
            binding.etProductName.hint = binding.tvLabelProductName.text
            if (product != null && !product!!.name.isNullOrEmpty()) {
                binding.etProductName.setText(product!!.name)
            }
        } else {
            binding.tilProductName.visibility = View.GONE
        }

        MRP = CommonUtils.getFlavourField(flavorId, PRODUCT_FILED.MSP.name, mPref)
        SELLING_PRICE =
            CommonUtils.getFlavourField(flavorId, PRODUCT_FILED.SELLING_PRICE.name, mPref)
        if (MRP != null && SELLING_PRICE != null) {
            binding.tilMrpUnit.visibility = if (MRP!!.visible) View.VISIBLE else View.GONE
            binding.tilSellingPrice.visibility =
                if (SELLING_PRICE!!.visible) View.VISIBLE else View.GONE
            binding.tvLabelMRPName.text =
                if (MRP!!.label.isNullOrEmpty()) PRODUCT_FILED.MSP.label else MRP!!.label
            binding.etMrp.hint = binding.tvLabelMRPName.text
            binding.tvLabelSellingPrice.text =
                if (SELLING_PRICE!!.label.isNullOrEmpty()) PRODUCT_FILED.SELLING_PRICE.label else SELLING_PRICE!!.label
            binding.etSellPrice.hint = binding.tvLabelSellingPrice.text
            if (MRP!!.visible && SELLING_PRICE!!.visible) {
                binding.llMrp.visibility =  View.VISIBLE
                binding.tilSellingPrice.visibility = View.VISIBLE
                val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
                lp.weight = 1f
                binding.tilMrpUnit.layoutParams = lp
                binding.tilSellingPrice.layoutParams = lp
                if (product != null && product!!.sellingPrice != null) {
                    binding.etSellPrice.setText(product!!.sellingPrice.toString())
                }
                if (product != null && product!!.price != null) {
                    binding.etMrp.setText(product!!.price.toString())
                }
            } else if (MRP!!.visible) {
                val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
                lp.weight = 2f
                binding.tilMrpUnit.layoutParams = lp
            } else if (SELLING_PRICE!!.visible) {
                val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
                lp.weight = 2f
                binding.tilSellingPrice.layoutParams = lp
            }
        } else if (MRP != null && MRP!!.visible) {
            binding.llMrp.visibility =  View.VISIBLE
            binding.tilSellingPrice.visibility = View.VISIBLE
            binding.tvLabelMRPName.text =
                if (MRP!!.label.isNullOrEmpty()) PRODUCT_FILED.MSP.label else MRP!!.label
            binding.etMrp.hint = binding.tvLabelMRPName.text
            val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.weight = 2f
            binding.tilMrpUnit.layoutParams = lp

            if (product != null && product!!.price != null) {
                binding.etMrp.setText(product!!.price.toString())
            }
        } else if (SELLING_PRICE != null && SELLING_PRICE!!.visible) {
            binding.tilSellingPrice.visibility = View.VISIBLE
            binding.llMrp.visibility =  View.VISIBLE
            binding.tvLabelSellingPrice.text =
                if (SELLING_PRICE!!.label.isNullOrEmpty()) PRODUCT_FILED.SELLING_PRICE.label else SELLING_PRICE!!.label
            binding.etSellPrice.hint = binding.tvLabelSellingPrice.text
            val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.weight = 2f
            binding.tilSellingPrice.layoutParams = lp
            if (product != null && product!!.sellingPrice != null) {
                binding.etSellPrice.setText(product!!.sellingPrice.toString())
            }
        }else{
            binding.tilMrpUnit.visibility =  View.GONE
            binding.llMrp.visibility =  View.GONE
            binding.tilSellingPrice.visibility = View.GONE
        }

        QUANTITY = CommonUtils.getFlavourField(flavorId, PRODUCT_FILED.QUANTITY.name, mPref)
        UNIT =
            CommonUtils.getFlavourField(flavorId, PRODUCT_FILED.UNIT.name, mPref)
        if (QUANTITY != null && UNIT != null) {
            binding.tilQuantity.visibility = if (QUANTITY!!.visible) View.VISIBLE else View.GONE
            binding.tilUnit.visibility =
                if (UNIT!!.visible) View.VISIBLE else View.GONE
            binding.tvLabelQuantityName.text =
                if (QUANTITY!!.label.isNullOrEmpty()) PRODUCT_FILED.QUANTITY.label else QUANTITY!!.label
            binding.etQuantity.hint = binding.tvLabelQuantityName.text

            binding.tvLabelUnit.text =
                if (UNIT!!.label.isNullOrEmpty()) PRODUCT_FILED.UNIT.label else UNIT!!.label
            binding.edPerUnit.hint = binding.tvLabelUnit.text
            if (UNIT!!.visible && QUANTITY!!.visible) {
                binding.llQuantityUnit.visibility = View.VISIBLE
                val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
                lp.weight = 1f
                binding.tilQuantity.layoutParams = lp
                binding.tilUnit.layoutParams = lp

                if (product != null && product!!.unitValue != null ) {
                    binding.etQuantity.setText(product!!.unitValue.toString())
                }
                if (product != null && product!!.unitType != null && product!!.unitType != null) {
                    unit = product!!.unitType
                    binding.edPerUnit.setText(unit!!.lowercase().capitalize().toString())
                }
                if (product != null && product!!.unitType != null && product!!.unitType != null &&product!!.unitType!!.equals(
                        "PACK"
                    )
                ) {
                    unit = product!!.unitType
                    if (product!!.packInfo != null && product!!.packInfo!!.unitType != null) {
                        packUnit = product!!.packInfo!!.unitType
                        binding.edPackUnit.setText(packUnit!!.lowercase().capitalize())
                    }
                    if (product!!.packInfo != null && product!!.packInfo!!.from != null) {
                        binding.etFrom.setText(product!!.packInfo!!.from!!.toString())
                    }
                    if (product!!.packInfo != null && product!!.packInfo!!.to != null) {
                        binding.etTo.setText(product!!.packInfo!!.to.toString())
                    }

                    binding.llPackInfo.visibility = View.VISIBLE
                    binding.nestedScrollView.postDelayed(
                        { binding.nestedScrollView.fullScroll(View.FOCUS_DOWN) },
                        200
                    )
                    binding.edPerUnit.setText(unit.toString())
                }

            } else if (QUANTITY!!.visible) {
                val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
                lp.weight = 2f
                binding.tilQuantity.layoutParams = lp

            } else if (UNIT!!.visible) {
                val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
                lp.weight = 2f

                binding.tilUnit.layoutParams = lp
            }
        } else if (QUANTITY != null && QUANTITY!!.visible) {
            binding.llQuantityUnit.visibility = View.VISIBLE
            binding.tvLabelQuantityName.text =
                if (QUANTITY!!.label.isNullOrEmpty()) PRODUCT_FILED.QUANTITY.label else QUANTITY!!.label
            val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.weight = 2f
            binding.tilQuantity.layoutParams = lp
            binding.edPerUnit.hint = binding.tvLabelQuantityName.text

            if (product != null && product!!.unitValue != null ) {
                binding.etQuantity.setText(product!!.unitValue.toString())
            }
        } else if (UNIT != null && UNIT!!.visible) {
            binding.llQuantityUnit.visibility = View.VISIBLE
            binding.tvLabelUnit.text =
                if (UNIT!!.label.isNullOrEmpty()) PRODUCT_FILED.UNIT.label else UNIT!!.label
            val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.weight = 2f
            binding.tilUnit.layoutParams = lp
            binding.edPerUnit.hint = binding.tvLabelUnit.text
            if (product != null && product!!.unitType != null && product!!.unitType != null) {
                unit = product!!.unitType
                binding.edPerUnit.setText(unit!!.lowercase().capitalize().toString())
            }
            if (product != null && product!!.unitType != null && product!!.unitType != null &&product!!.unitType!!.equals(
                    "PACK"
                )
            ) {
                unit = product!!.unitType
                if (product!!.packInfo != null && product!!.packInfo!!.unitType != null) {
                    packUnit = product!!.packInfo!!.unitType
                    binding.edPackUnit.setText(packUnit!!.lowercase().capitalize())
                }
                if (product!!.packInfo != null && product!!.packInfo!!.from != null) {
                    binding.etFrom.setText(product!!.packInfo!!.from!!.toString())
                }
                if (product!!.packInfo != null && product!!.packInfo!!.to != null) {
                    binding.etTo.setText(product!!.packInfo!!.to.toString())
                }

                binding.llPackInfo.visibility = View.VISIBLE
                binding.nestedScrollView.postDelayed(
                    { binding.nestedScrollView.fullScroll(View.FOCUS_DOWN) },
                    200
                )
                binding.edPerUnit.setText(unit.toString())
            }
        }else{
            binding.tilQuantity.visibility =  View.GONE
            binding.tilUnit.visibility = View.GONE
            binding.llQuantityUnit.visibility = View.GONE
        }


        MAX_ORDER_LIMIT =
            CommonUtils.getFlavourField(flavorId, PRODUCT_FILED.MAX_ORDER_LIMIT.name, mPref)
        MIN_STOCK_QUANTITY =
            CommonUtils.getFlavourField(flavorId, PRODUCT_FILED.MIN_STOCK_QUANTITY.name, mPref)
        if (MAX_ORDER_LIMIT != null && MIN_STOCK_QUANTITY != null) {
            binding.llMaxOrderLimitMinQuantity.visibility = View.VISIBLE
            binding.tilMaxOrderLimit.visibility =
                if (MAX_ORDER_LIMIT!!.visible) View.VISIBLE else View.GONE
            binding.tilMinStockQuantity.visibility =
                if (MIN_STOCK_QUANTITY!!.visible) View.VISIBLE else View.GONE
            binding.tvLabelMaximumOrderLimit.text =
                if (MAX_ORDER_LIMIT!!.label.isNullOrEmpty()) PRODUCT_FILED.MAX_ORDER_LIMIT.label else MAX_ORDER_LIMIT!!.label
            binding.etMaxOrderLimit.hint = binding.tvLabelMaximumOrderLimit.text
            binding.tvLabelMinStockQuantity.text =
                if (MIN_STOCK_QUANTITY!!.label.isNullOrEmpty()) PRODUCT_FILED.MIN_STOCK_QUANTITY.label else MIN_STOCK_QUANTITY!!.label
            binding.edMinStockQuantity.hint = binding.tvLabelMinStockQuantity.text
            if (product!=null&&product!!.maxOrderLimit != null ) {
                binding.etMaxOrderLimit.setText(product!!.maxOrderLimit.toString())
            }
            if (product!=null&&product!!.productStock != null &&product!!.productStock!!.minValue!=null) {
                binding.edMinStockQuantity.setText(product!!.productStock!!.minValue.toString())
            }
            if (MAX_ORDER_LIMIT!!.visible && MIN_STOCK_QUANTITY!!.visible) {
                val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
                lp.weight = 1f
                binding.tilMaxOrderLimit.layoutParams = lp
                binding.tilMinStockQuantity.layoutParams = lp
            } else if (MAX_ORDER_LIMIT!!.visible) {
                val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
                lp.weight = 2f
                binding.tilMaxOrderLimit.layoutParams = lp
            } else if (MIN_STOCK_QUANTITY!!.visible) {
                val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
                lp.weight = 2f
                binding.tilMinStockQuantity.layoutParams = lp
            }
        } else if (MAX_ORDER_LIMIT != null && MAX_ORDER_LIMIT!!.visible) {
            binding.llMaxOrderLimitMinQuantity.visibility = View.VISIBLE
            binding.tvLabelMaximumOrderLimit.text =
                if (MAX_ORDER_LIMIT!!.label.isNullOrEmpty()) PRODUCT_FILED.MAX_ORDER_LIMIT.label else MAX_ORDER_LIMIT!!.label
            val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.weight = 2f
            binding.tilMaxOrderLimit.layoutParams = lp
            binding.etMaxOrderLimit.hint = binding.tvLabelMaximumOrderLimit.text
            if (product!=null&&product!!.maxOrderLimit != null ) {
                binding.etMaxOrderLimit.setText(product!!.maxOrderLimit.toString())
            }

        } else if (MIN_STOCK_QUANTITY != null && MIN_STOCK_QUANTITY!!.visible) {
            binding.llMaxOrderLimitMinQuantity.visibility = View.VISIBLE
            binding.tvLabelMinStockQuantity.text =
                if (MIN_STOCK_QUANTITY!!.label.isNullOrEmpty()) PRODUCT_FILED.MIN_STOCK_QUANTITY.label else MIN_STOCK_QUANTITY!!.label
            val lp = LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT)
            lp.weight = 2f
            binding.tilUnit.layoutParams = lp
            binding.edMinStockQuantity.hint = binding.tvLabelMinStockQuantity.text
            if (product!=null&&product!!.productStock != null &&product!!.productStock!!.minValue!=null) {
                binding.edMinStockQuantity.setText(product!!.productStock!!.minValue.toString())
            }
        }else{
            binding.tilMaxOrderLimit.visibility = View.GONE
            binding.tilMinStockQuantity.visibility = View.GONE
            binding.llMaxOrderLimitMinQuantity.visibility = View.GONE
        }

        REFERENCE_NUMBER =
            CommonUtils.getFlavourField(flavorId, PRODUCT_FILED.REFERENCE_NUMBER.name, mPref)
        if (REFERENCE_NUMBER != null) {
            binding.tilReffNumber.visibility =
                if (REFERENCE_NUMBER!!.visible) View.VISIBLE else View.GONE
            binding.tvLabelReffNumber.text =
                if (REFERENCE_NUMBER!!.label.isNullOrEmpty()) PRODUCT_FILED.REFERENCE_NUMBER.label else REFERENCE_NUMBER!!.label
            binding.etReffNumber.hint = binding.tvLabelReffNumber.text
            if (product != null && product!!.upcNumber != null) {
                binding.etReffNumber.setText(product!!.upcNumber.toString())
            }
        } else {
            binding.tilReffNumber.visibility = View.GONE
        }

        SPECIFICATION =
            CommonUtils.getFlavourField(flavorId, PRODUCT_FILED.SPECIFICATION.name, mPref)
        if (SPECIFICATION != null) {
            binding.tilProductSpecs.visibility =
                if (SPECIFICATION!!.visible) View.VISIBLE else View.GONE
            binding.tvLabelProductSpecs.text =
                if (SPECIFICATION!!.label.isNullOrEmpty()) PRODUCT_FILED.DESCRIPTION.label else SPECIFICATION!!.label

           /* if(product!=null&&product!!.specifications!=null&&product!!.specifications!!.isNotEmpty())
            adapter.addItems(product!!.specifications!!)*/

        } else {
            binding.tilProductSpecs.visibility = View.GONE
        }

        DESCRIPTION =
            CommonUtils.getFlavourField(flavorId, PRODUCT_FILED.DESCRIPTION.name, mPref)
        if (DESCRIPTION != null) {
            binding.tilProductDescription.visibility =
                if (DESCRIPTION!!.visible) View.VISIBLE else View.GONE
            binding.tvLabelProductDescription.text =
                if (DESCRIPTION!!.label.isNullOrEmpty()) PRODUCT_FILED.DESCRIPTION.label else DESCRIPTION!!.label
            binding.etProductDescription.hint = binding.tvLabelProductDescription.text
            if (product != null && product!!.description != null) {
                binding.etProductDescription.setText(product!!.description.toString())
            }
        } else {
            binding.tilProductDescription.visibility = View.GONE
        }


    }

    override fun onProcessClick(
        list: ArrayList<FormData>,
        dynamicActionConfig: DynamicActionConfig?,
        currentFormId: String?,
        dfdid: String?
    ) {

        if (validation()) {
            var productName = binding.etProductName.text.toString().trim()
            var mrp = binding.etMrp.text.toString().trim()
            var sellingPrice = binding.etSellPrice.text.toString().trim()
            var pquanity = binding.etQuantity.text.toString().trim()
            var maxOrderLimit = binding.etMaxOrderLimit.text.toString().trim()
            var minStockQuantity = binding.edMinStockQuantity.text.toString().trim()
            var reffNumber = binding.etReffNumber.text.toString().trim()
//            var productSpecs = binding.etProductSpecs.text.toString().trim()
            var description = binding.etProductDescription.text.toString().trim()

            var punit = binding.edPerUnit.text.toString().trim()
            var packunit = binding.edPackUnit.text.toString().trim()
            var from = binding.etFrom.text.toString().trim()
            var to = binding.etTo.text.toString().trim()
            addProdrequest = AddProductRequest()
            try {
                if (addProdrequest != null) {
                    addProdrequest?.productName = productName
                    addProdrequest?.upcNumber = reffNumber
                    if (sellingPrice.isNotEmpty()) {
                        addProdrequest?.sellingPrice = sellingPrice.toDouble()
                    }
                    if (maxOrderLimit.isNotEmpty()) {
                        addProdrequest?.maxOrderLimit = maxOrderLimit.toInt()
                    }
                    if (minStockQuantity.isNotEmpty()) {
                        addProdrequest?.minStockQuantity = minStockQuantity.toInt()
                    }
                    if (mrp.isNotEmpty()) {
                        addProdrequest?.price = mrp.toDouble()
                    }
                    if(adapter.getAllList().isNotEmpty())
                        addProdrequest!!.specifications=adapter.getAllList()
                    addProdrequest?.description = description
                    addProdrequest?.flavorId = flavorId
                    addProdrequest?.img = imageUrl
                    var unitInfo = UnitInfo()

                    if (pquanity.isNotEmpty())
                        unitInfo.quantity = pquanity.toFloat()
                    unitInfo.type = unit
                    if (binding.llPackInfo.visibility == View.VISIBLE) {
                        var packInfo = PackInfo()
                        packInfo.unitType = packUnit
                        if (from.isNotEmpty())
                            packInfo.from = from.toLong()
                        if (to.isNotEmpty())
                            packInfo.to = to.toLong()
                        unitInfo.packInfo = packInfo
                    }
                    //
                    addProdrequest?.unitInfo = unitInfo
                }
            } catch (e: Exception) {
                TrackiToast.Message.showShort(this, e.message)
            }

            hideKeyboard()
            //showLoading()
            var json = JSONConverter<AddProductRequest>().objectToJson(addProdrequest)
            CommonUtils.showLogMessage("e", "jsondata", json)

            var bundle = Bundle();
            bundle.putParcelable("ADD_PRODUCT_REQUEST", addProdrequest);
            Log.e(TAG, "config type----> ${dynamicActionConfig?.action} ")
            if (mainMap == null) {
                mainMap = HashMap()
            }
            //add form data after validation into the map
            if (list.isNotEmpty()) {
                mainMap?.set(currentFormId!!, list)
                var jsonConverter = JSONConverter<HashMap<String, ArrayList<FormData>>>()
                var data = jsonConverter.objectToJson(mainMap!!)
                CommonUtils.showLogMessage("e", "allowed field", data.toString())

            }
            var jsonConverter = JSONConverter<ArrayList<FormData>>();
            Log.e(DynamicFormActivity.TAG, jsonConverter.objectToJson(list))
            if (dynamicActionConfig?.action == Type.DISPOSE) {

                mainData = ArrayList()

                for ((_, value) in mainMap!!) {
                    mainData?.addAll(value)
                }

                if (mainData != null && mainData!!.isNotEmpty()!!) {
                    val hashMapFileRequest = HashMap<String, ArrayList<File>>()
                    for (i in mainData?.indices!!) {

                        val v = mainData!![i].file
                        if (v != null && v.isNotEmpty()) {

                            if (mainData!![i].type == DataType.CAMERA && v[0].path.equals(
                                    AppConstants.ADD_MORE,
                                    ignoreCase = true
                                )
                            ) {

                                v.removeAt(0)
                            }
                            if (mainData!![i].type == DataType.CAMERA && v.size > 0 && v[v.size - 1].path.equals(
                                    AppConstants.ADD_MORE,
                                    ignoreCase = true
                                )
                            ) {
                                v.removeAt(v.size - 1)
                            }
                            if (v.isNotEmpty()) {
                                var listIterator = v.listIterator()
                                while (listIterator.hasNext()) {
                                    if (!listIterator.next().exists()) {
                                        listIterator.remove();
                                    }
                                }
                            }

                            if (v.isNotEmpty()) {
                                val key = mainData!![i].name!!
                                hashMapFileRequest[key] = v
                            }
                        }
                        Log.e(
                            "NewCreateTaskActivity", mainData!![i].name + "<------->"
                                    + mainData!![i].enteredValue
                        )
                    }
                    var jsonConverter = JSONConverter<HashMap<String, ArrayList<File>>>();
                    Log.e(DynamicFormActivity.TAG, jsonConverter.objectToJson(hashMapFileRequest))
                    Log.e(DynamicFormActivity.TAG, "Size =>" + hashMapFileRequest.size)
                    if (hashMapFileRequest.isNotEmpty()) {


                        if (NetworkUtils.isNetworkConnected(this@AddProductActivity)) {

                            if (NetworkUtils.isConnectedFast(this@AddProductActivity)) {
                                count = 0
                                Log.e(TAG, "worker thread open")
                                // showLoading()
                                binding.btnAddNow.visibility = View.GONE
                                binding.viewProgress.visibility = View.VISIBLE
                                CommonUtils.makeScreenDisable(this)
                                nestedScrollView.postDelayed(
                                    { nestedScrollView.fullScroll(View.FOCUS_DOWN) },
                                    200
                                )
                                fileUploadCounter = 0
                                val thread = HandlerThread("workkker")
                                thread.start()
                                //start a handler
                                mDynamicHandler = DynamicHandler(thread.looper, dynamicActionConfig)
//                        start a thread other than main
                                handlerThread?.setRequestParams(
                                    mDynamicHandler!!,
                                    hashMapFileRequest,
                                    httpManager,
                                    null
                                )
                                //start thread
                                handlerThread?.start()
                            } else {
                                CommonUtils.showSnakbarForNetworkSettings(
                                    this@AddProductActivity,
                                    nestedScrollView,
                                    AppConstants.SLOW_INTERNET_CONNECTION_MESSAGE
                                )
                            }


                        } else {
                            // TrackiToast.Message.showShort(this, getString(R.string.please_check_your_internet_connection_you_are_offline_now))
                        }

                    } else {
                        showLoading()
                        perFormCreateTask()
                    }
                } else {
                    showLoading()
                    perFormCreateTask()
                }

            }


        }


    }


    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_add_product
    }

    override fun getViewModel(): AddProductViewModel {
        return addProductViewModel
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val jsonConverter: JSONConverter<BaseResponse> = JSONConverter()
            var response: BaseResponse =
                jsonConverter.jsonToObject(result.toString(), BaseResponse::class.java)
            if (mPref.formDataMap != null && mPref.formDataMap.isNotEmpty()) {
                mPref.clear(AppPreferencesHelper.PreferencesKeys.PREF_KEY_IS_FORM_DATA_MAP);
            }
            onSuccess()
        }

    }

    fun onSuccess() {
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

    override fun handleUpdateResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val jsonConverter: JSONConverter<BaseResponse> = JSONConverter()
            var response: BaseResponse =
                jsonConverter.jsonToObject(result.toString(), BaseResponse::class.java)
            if (mPref.formDataMap != null && mPref.formDataMap.isNotEmpty()) {
                mPref.clear(AppPreferencesHelper.PreferencesKeys.PREF_KEY_IS_FORM_DATA_MAP);
            }
            onSuccess()
        }
    }

    override fun handleSendImageResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val profileResponse = Gson().fromJson(result.toString(), ProfileResponse::class.java)
            if (profileResponse != null) {
                imageUrl = profileResponse.imageUrl
            }
        }

    }

    override fun handleUnitsResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val unitsResponse = Gson().fromJson(result.toString(), UnitsResponse::class.java)
            if (unitsResponse != null && unitsResponse.successful) {
                mPref.setUnits(unitsResponse.data!!.UNIT!!)
                mPref.setPackUnits(unitsResponse.data!!.PACK_UNIT!!)

            }
        }
    }

    override fun handleProductDetailsResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?
    ) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val jsonConverter: JSONConverter<ProductDetailsResponse> = JSONConverter()
            var response: ProductDetailsResponse =
                jsonConverter.jsonToObject(result.toString(), ProductDetailsResponse::class.java)
            if (response.data != null && response.data!!.dfdId != null) {
                dfdId = response.data!!.dfdId
            }
            if (SPECIFICATION != null&&SPECIFICATION!!.visible) {
                if(response.data!=null&&response.data!!.specifications!=null&&response.data!!.specifications!!.isNotEmpty())
                    adapter.addItems(response.data!!.specifications!!)

            }

           /*
            "name": "Moti Chur",
            "img": "https://rocketflow-uat.s3.amazonaws.com/user-img/64a880d7-7fe7-471f-ac3e-b81d80db560b/IMG/1633004789774.png",
            "price": 35,
            "sellingPrice": 28,
            "unitType": "KG",
            "unitValue": 1,
            "packInfo": null,
            "active": false,
            "added": false,
            "discount": 20,
            "qty": 0,
            "description": "",
            "prodInfoMap": null,*/
           /* if(response.data!=null){
                var product:ProductDetails=response.data!!
                if(product.name!=null){
                    binding.etProductName.setText(product.name)
                }
                if(product.img!=null){
                    binding.etProductName.setText(product.name)
                }
            }*/
            if (response.data != null && response.data!!.dfData != null) {
                dfId = CommonUtils.getDfIdFromFlavourId(flavorId, mPref)
                showDynamicForm(dfId, response.data!!.dfData!!)
            } else {
                dfId = CommonUtils.getDfIdFromFlavourId(flavorId, mPref)
                showDynamicForm(dfId, ArrayList())
            }

        }

    }


    fun onPickImage() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                    Manifest.permission.CAMERA
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                proceedToImagePicking()
            } else {
                requestPermissions(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.CAMERA
                    ), REQUEST_READ_STORAGE
                )
            }
        } else {
            proceedToImagePicking()
        }
    }

    private fun proceedToImagePicking() {
        val chooseImageIntent: Intent = ImagePicker.getPickImageIntent(this)
        startActivityForResult(
            chooseImageIntent,
            PICK_IMAGE_FILE_ID
        )
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_READ_STORAGE) {
            if (grantResults.isNotEmpty()) {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED && grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    proceedToImagePicking()
                }
            }
        } else super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    @SuppressLint("CheckResult")
    private fun compressImage() {
        if (actualImage == null) {
            TrackiToast.Message.showShort(this, getString(R.string.please_choose_a_image))
        } else {


            Compressor(this)
                .compressToFileAsFlowable(actualImage)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ file ->
                    compressedImage = file
                    binding.cardViewTakeImage.visibility = View.GONE
                    binding.cardViewActImage.visibility = View.VISIBLE
                    binding.ivActImage.setImageURI(Uri.fromFile(compressedImage))
                    val updateFileRequest =
                        UpdateFileRequest(compressedImage!!, FileType.USER_PROFILE, "")
                    if (TrackiApplication.getApiMap()
                            .containsKey(ApiType.UPLOAD_FILE_AGAINEST_ENTITY)
                    ) {
                        showLoading()
                        addProductViewModel.uploadImage(updateFileRequest, httpManager)
                    }

                }) { throwable ->
                    throwable.printStackTrace()
                    TrackiToast.Message.showShort(
                        this@AddProductActivity,
                        throwable.message
                    )
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_FILE_ID) {
            actualImage = ImagePicker.getImageFileToUpload(this, resultCode, data)
            compressImage()
        }
    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.edPerUnit -> {
                val list: ArrayList<UnitData> = ArrayList<UnitData>()
                if (mPref.getUnits() != null && mPref.getUnits()!!.isNotEmpty()) {
                    val hmIterator: Iterator<*> = mPref.getUnits()!!.entries.iterator()
                    while (hmIterator.hasNext()) {
                        val mapElement = hmIterator.next() as Map.Entry<*, *>
                        val unitData = UnitData()
                        unitData.key = mapElement.key.toString()
                        unitData.name = mapElement.value.toString()
                        list.add(unitData)
                    }
                }
                showBottomSheetUnitDialog(list)
            }
            R.id.edPackUnit -> {
                val list: ArrayList<UnitData> = ArrayList<UnitData>()
                if (mPref.getPackUnits() != null && mPref.getPackUnits()!!.isNotEmpty()) {
                    val hmIterator: Iterator<*> = mPref.getPackUnits()!!.entries.iterator()
                    while (hmIterator.hasNext()) {
                        val mapElement = hmIterator.next() as Map.Entry<*, *>
                        val unitData = UnitData()
                        unitData.key = mapElement.key.toString()
                        unitData.name = mapElement.value.toString()
                        list.add(unitData)
                    }
                }
                showBottomSheetPackUnitDialog(list)
            }
            R.id.tvAddDescription -> {
                openDialogAddDescription(null, null)
            }
            R.id.rlTakeImage -> {
                onPickImage()
            }


            R.id.ivDelete -> {
                binding.cardViewTakeImage.visibility = View.VISIBLE
                binding.cardViewActImage.visibility = View.GONE
                compressedImage = null
                imageUrl = null
            }
            R.id.btnAddNow -> {
                if (dynamicFormsNew != null) {
                    if (dynamicFragment != null) {
                        dynamicFragment!!.onclickMainButton()
                    } else {
                        var dynamicActionConfig = DynamicActionConfig()
                        dynamicActionConfig.action = Type.DISPOSE
                        onProcessClick(ArrayList(), dynamicActionConfig, null, null)
                    }
                } else {
                    var dynamicActionConfig = DynamicActionConfig()
                    dynamicActionConfig.action = Type.DISPOSE
                    onProcessClick(ArrayList(), dynamicActionConfig, null, null)
                }
            }
        }

    }

    private fun showBottomSheetPackUnitDialog(list: ArrayList<UnitData>?) {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(R.layout.layout_item_untis)
        val ivCancel = bottomSheetDialog.findViewById<ImageView>(R.id.ivCancel)
       /* val unitsChipGroup = bottomSheetDialog.findViewById<ChipGroup>(R.id.unitsChipGroup)
        val tvHeading = bottomSheetDialog.findViewById<TextView>(R.id.tvHeading)
        tvHeading!!.text = getString(R.string.choose_pack_unit)
        unitsChipGroup!!.removeAllViews()
        if (list != null && list.isNotEmpty()) {
            for (selectedData in list) {
                var chip = getPackUnitChip(
                    unitsChipGroup!!,
                    selectedData.name!!,
                    selectedData.key!!,
                    bottomSheetDialog
                )
                unitsChipGroup.addView(chip)
            }

        }*/
        val rvUnits = bottomSheetDialog.findViewById<RecyclerView>(R.id.rvUnits)
        val tvHeading = bottomSheetDialog.findViewById<TextView>(R.id.tvHeading)
        //   unitsChipGroup!!.removeAllViews()
        if (list != null && list.isNotEmpty()) {
            var listOfUnits= ArrayList<UnitModel>()
            for (selectedData in list) {
                var unitModel=UnitModel()
                unitModel.value=selectedData.name!!
                unitModel.key=selectedData.key!!

                listOfUnits.add(unitModel)
            }
            var adapter=UnitModelAdapter(this)
            adapter.setListener(object : UnitModelAdapter.OnUnitListener {
                override fun onUnitSelected(data: UnitModel) {
                    packUnit = data.key.toString()
                    Log.e("packunit", packUnit)
                    Log.e("tag", data.key.toString())
                    binding.edPackUnit.setText(data.value)
                    bottomSheetDialog.dismiss()
                }

            })
            adapter.addItems(listOfUnits)
            rvUnits!!.adapter=adapter

        }
        ivCancel!!.setOnClickListener {
            bottomSheetDialog.dismissWithAnimation = true
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun getPackUnitChip(
        entryChipGroup: ChipGroup,
        text: String,
        id: String,
        bottomSheetDialog: BottomSheetDialog
    ): Chip? {
        val chip = Chip(this)
        chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.choice_chip))
        val paddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 12f,
            resources.displayMetrics
        ).toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = text
        val font = Typeface.createFromAsset(this.assets, "fonts/campton_light.ttf")
        chip.typeface = font
        chip.tag = id
        /*if(unit!=null&&unit==id){
            chip.isChecked=true
        }*/
        chip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                packUnit = buttonView.tag.toString()
                Log.e("packunit", packUnit)
                Log.e("tag", buttonView.tag.toString())
                binding.edPackUnit.setText(text)
                bottomSheetDialog.dismiss()
            } else {
                packUnit = null
                binding.edPackUnit.setText("")
                bottomSheetDialog.dismiss()
            }
        }
        return chip
    }


    private fun getChip(
        entryChipGroup: ChipGroup,
        text: String,
        id: String,
        bottomSheetDialog: BottomSheetDialog
    ): Chip? {
        val chip = Chip(this)
        chip.setChipDrawable(ChipDrawable.createFromResource(this, R.xml.choice_chip))
        val paddingDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_SP, 12f,
            resources.displayMetrics
        ).toInt()
        chip.setPadding(paddingDp, paddingDp, paddingDp, paddingDp)
        chip.text = text
        val font = Typeface.createFromAsset(this.assets, "fonts/campton_light.ttf")
        chip.typeface = font
        chip.tag = id
        /*if(unit!=null&&unit==id){
            chip.isChecked=true
        }*/
        chip.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                unit = buttonView.tag.toString()
                if (unit != null && unit.equals("PACK")) {
                    binding.llPackInfo.visibility = View.VISIBLE
                    binding.nestedScrollView.postDelayed(
                        { binding.nestedScrollView.fullScroll(View.FOCUS_DOWN) },
                        200
                    )
                } else {
                    binding.llPackInfo.visibility = View.GONE
                }
                binding.edPerUnit.setText(text)
                bottomSheetDialog.dismiss()
            } else {
                unit = null
                if (unit != null && unit.equals("PACK")) {
                    binding.llPackInfo.visibility = View.VISIBLE
                    binding.nestedScrollView.postDelayed(
                        { binding.nestedScrollView.fullScroll(View.FOCUS_DOWN) },
                        200
                    )
                } else {
                    binding.llPackInfo.visibility = View.GONE
                }
                binding.edPerUnit.setText("")
                bottomSheetDialog.dismiss()
            }
        }
        return chip
    }

    private fun showBottomSheetUnitDialog(list: ArrayList<UnitData>?) {
        val bottomSheetDialog = BottomSheetDialog(this, R.style.AppBottomSheetDialogTheme)
        bottomSheetDialog.setContentView(R.layout.layout_item_untis)
        val ivCancel = bottomSheetDialog.findViewById<ImageView>(R.id.ivCancel)
/*
        val unitsChipGroup = bottomSheetDialog.findViewById<ChipGroup>(R.id.unitsChipGroup)
*/
        val rvUnits = bottomSheetDialog.findViewById<RecyclerView>(R.id.rvUnits)
        val tvHeading = bottomSheetDialog.findViewById<TextView>(R.id.tvHeading)
     //   unitsChipGroup!!.removeAllViews()
        if (list != null && list.isNotEmpty()) {
            var listOfUnits= ArrayList<UnitModel>()
            for (selectedData in list) {
                var unitModel=UnitModel()
                unitModel.value=selectedData.name!!
                unitModel.key=selectedData.key!!
              /*  var chip = getChip(
                    unitsChipGroup!!,
                    selectedData.name!!,
                    selectedData.key!!,
                    bottomSheetDialog
                )*/
                listOfUnits.add(unitModel)
                /*unitsChipGroup.addView(chip)*/
            }
            var adapter=UnitModelAdapter(this)
            adapter.setListener(object : UnitModelAdapter.OnUnitListener {
                override fun onUnitSelected(data: UnitModel) {
                    unit = data.key.toString()
                    if (unit != null && unit.equals("PACK")) {
                        binding.llPackInfo.visibility = View.VISIBLE
                        binding.nestedScrollView.postDelayed(
                            { binding.nestedScrollView.fullScroll(View.FOCUS_DOWN) },
                            200
                        )
                    } else {
                        binding.llPackInfo.visibility = View.GONE
                    }
                    binding.edPerUnit.setText(data.value)
                    bottomSheetDialog.dismiss()
                }

            })
            adapter.addItems(listOfUnits)
            rvUnits!!.adapter=adapter

        }
        ivCancel!!.setOnClickListener {
            bottomSheetDialog.dismissWithAnimation = true
            bottomSheetDialog.dismiss()
        }

        bottomSheetDialog.show()
    }

    private fun replaceFragment(fragment: Fragment, dfId: String?) {
        val formName = CommonUtils.getFormByFormId(dfId)
        /* if (formName?.name != null) {
             binding?.toolbar?.title = formName.name
         } else {
             binding?.toolbar?.title = ""
         }*/
        val manager = supportFragmentManager
        val ft = manager.beginTransaction()
        ft.replace(R.id.container, fragment, dfId)
        container.visibility = View.VISIBLE
        if (formName == null) {
            container.visibility = View.GONE
        }
        ft.commit()
    }

    private fun showDynamicForm(
        dfId: String?, formadata: ArrayList<DynamicFormData>,
    ) {
        if (dfId == null)
            return
        dynamicFragment = NewDynamicFormFragment.getInstance(
            dfId,
            flavorId!!,
            isEditable,
            formadata
        )
        replaceFragment(dynamicFragment!!, dfId)
        dynamicFormsNew = CommonUtils.getFormByFormId(dfId)
        if (dynamicFormsNew != null && dynamicFormsNew!!.fields != null && dynamicFormsNew!!.fields!!.isNotEmpty()) {
            try {
                var formData: FormData? =
                    dynamicFormsNew!!.fields!!.filter { s -> s.type == DataType.BUTTON }
                        .filter { data -> data.actionConfig?.action == Type.DISPOSE || data.actionConfig?.action == Type.FORM || data.actionConfig?.action == Type.API }
                        .single()
                formData?.let {
                    it.value?.let {
                        binding.btnAddNow.text = formData.value
                    }
                }
            } catch (e: java.lang.Exception) {

            }

        } else {
            binding.btnAddNow.text = "Add Product"
        }
    }


    companion object {
        private val TAG = AddProductActivity::class.java.simpleName
        fun newIntent(context: Context) = Intent(context, AddProductActivity::class.java)
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    var count = 0
    var fileUploadCounter = 0

    inner class DynamicHandler(looper: Looper, var actionConfig: DynamicActionConfig) :
        Handler(looper) {
        override fun handleMessage(message: Message) {
            when (message.what) {
                2 -> {
                    var obj = message.obj as HandlerObject
                    fileUploadCounter += obj.chunkSize
                    var progressUploadText = "${fileUploadCounter}/${obj.totalSize}"
                    var percentage = ((fileUploadCounter * 100 / obj.totalSize))
                    Log.e(AddProductActivity.TAG, "progressUploadText=> " + progressUploadText)
                    Log.e(AddProductActivity.TAG, "percentage=> " + percentage.toString())
                    runOnUiThread {
                        progressBar!!.progress = percentage
                        percentageText!!.text = "$percentage %"
                        currentStatusText!!.text = progressUploadText

                    }


                }
                /*For Success */0 -> {

                if (CommonUtils.stringListHashMap.isNotEmpty()) {

                    //get hashMap from adapter and match the name with key of maps
                    // if found then replace entered value with url of image
                    runOnUiThread {
                        rlProgress!!.visibility = View.GONE
                        rlSubmittingData!!.visibility = View.VISIBLE

                    }

                    if (mainData?.isNotEmpty()!!) {
                        val finalMap = java.util.HashMap<String, String>()
                        for (i in mainData?.indices!!) {
                            try {
                                if (mainData!![i].type != DataType.BUTTON) {
                                    if (CommonUtils.stringListHashMap?.containsKey(mainData!![i].name)!!) {
                                        //Log.e("Upload Form List--->", mainData!![i].name!!)
                                        mainData!![i].enteredValue =
                                            CommonUtils.getCommaSeparatedList(CommonUtils.stringListHashMap[mainData!![i].name])
                                    }
                                    finalMap[mainData!![i].name!!] = mainData!![i].enteredValue!!
                                }
                            } catch (e: Exception) {
                                e.printStackTrace()
                            }
                        }
                        //assign empty object to map again

                        CommonUtils.stringListHashMap = ConcurrentHashMap()
                        if (actionConfig?.action == Type.DISPOSE) {
                            perFormCreateTask()
                        }
                    }
                } else {
                    Log.e(TAG, "Map is empty...Try Again")
                    handlerThread?.interrupt()
                    hideLoading()
                    CommonUtils.stringListHashMap = ConcurrentHashMap()
                    if (actionConfig?.action == Type.DISPOSE) {
                        perFormCreateTask()
                    }
                }
            }
                /*For Error*/1 -> {
                if (count == 0) {
                    runOnUiThread {
                        binding.btnAddNow.visibility = View.VISIBLE
                        binding.viewProgress.visibility = View.GONE
                        CommonUtils.makeScreenClickable(this@AddProductActivity)
                    }
                    count++

                    fileUploadCounter = 0

                    TrackiToast.Message.showShort(
                        this@AddProductActivity,
                        AppConstants.UNABLE_TO_PROCESS_REQUEST
                    )
                    //after getting error form the thread we interrupt the thread
                    handlerThread?.interrupt()
                    hideLoading()
                }
            }
            }
        }
    }

    private fun finalApiHit() {

    }

    private fun perFormCreateTask() {
        if (dynamicFormsNew != null) {
            var dynamicFormMainData = CommonUtils.createFormData(
                mainData, "", "", dynamicFormsNew!!.formId,
                dynamicFormsNew!!.version
            )
            Log.e(TAG, "Final Dynamic Form Data-----> $dynamicFormMainData")

            var taskData = dynamicFormMainData!!.taskData
            if (!taskData.taskData?.isEmpty()!!) {
                addProdrequest!!.dfData = taskData.taskData
            }
            if (cid != null)
                addProdrequest!!.cid = cid
            addProdrequest!!.dfId = dynamicFormMainData.dfId
        }


        var jsonConverter = JSONConverter<AddProductRequest>()
        var json = jsonConverter.objectToJson(addProdrequest)
        CommonUtils.showLogMessage("e", "task data", json)
        if (action != null && action.equals("Add")) {
            if (NetworkUtils.isNetworkConnected(this@AddProductActivity)) {
                if (TrackiApplication.getApiMap().containsKey(ApiType.ADD_PRODUCT)) {
                    addProductViewModel.addProduct(httpManager, addProdrequest!!)
                }
            } else {
                hideLoading()
                TrackiToast.Message.showShort(
                    this@AddProductActivity,
                    getString(R.string.no_internet)
                )
            }
        } else if (action != null && action.equals("edit")) {
            addProdrequest!!.dfdId = dfdId
            if(product!=null&&product!!.pid!=null){
                addProdrequest!!.pid = product!!.pid
            }
            if (product!=null&&product!!.cid!=null)
                addProdrequest!!.cid = product!!.cid
            if (NetworkUtils.isNetworkConnected(this@AddProductActivity)) {
                if (TrackiApplication.getApiMap().containsKey(ApiType.UPDATE_PRODUCT)) {
                    addProductViewModel.updateProduct(httpManager, addProdrequest!!)
                }
            } else {
                hideLoading()
                TrackiToast.Message.showShort(
                    this@AddProductActivity,
                    getString(R.string.no_internet)
                )
            }
        }

    }


    override fun networkAvailable() {
        if (snackBar != null)
            snackBar!!.dismiss()
    }

    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(
            binding.llMain,
            getString(R.string.please_check_your_internet_connection)
        )
    }

    private fun openDialogAddDescription(data: ProductDescription?, position: Int?) {

        val dialog = Dialog(this)
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.window!!.setBackgroundDrawable(
            ColorDrawable(
                Color.TRANSPARENT
            )
        )
        dialog.setContentView(R.layout.layout_add_product_description)
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
        val etHeadingValue = dialog.findViewById<EditText>(R.id.etHeadingValue)
        val etContentValue = dialog.findViewById<EditText>(R.id.etContentValue)
        val btnAdd = dialog.findViewById<Button>(R.id.btnAdd)
        if (data != null) {
            if (data.heading != null)
                etHeadingValue.setText(data.heading!!)
            if (data.content != null)
                etContentValue.setText(data.content!!)
        }

        dialog.window!!.attributes = lp
        ivBack.setOnClickListener { dialog.dismiss() }
        btnAdd.setOnClickListener {
            var heading = etHeadingValue.text.toString().trim()
            var content = etContentValue.text.toString().trim()
            var productDescription = ProductDescription()
            productDescription.heading = heading
            productDescription.content = content
            if (heading.isEmpty()) {
                TrackiToast.Message.showShort(this, getString(R.string.please_enter_heading))
            } else if (content.isEmpty()) {
                TrackiToast.Message.showShort(this, getString(R.string.please_enter_description))
            } else {
                if (data != null && position != null) {
                    adapter.getAllList()[position].content = content
                    adapter.getAllList()[position].heading = heading
                    adapter.notifyItemChanged(position)
                } else {
                    adapter.addItem(productDescription)
                }
            }
            dialog.dismiss()
        }
        if (!dialog.isShowing) dialog.show()
    }


    override fun onEditProductDescription(data: ProductDescription, position: Int) {
        this.position = position
        openDialogAddDescription(data, position)
    }

    override fun removeProductDescription(data: ProductDescription, position: Int) {
        this.position = position
        adapter.removeAt(position, adapter.getAllList().size)
    }
}