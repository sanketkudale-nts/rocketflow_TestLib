package com.rocketflyer.rocketflow.ui.productlist

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tracki.R
import com.tracki.BR
import com.tracki.TrackiApplication
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityProductListBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.category.ProductCategoryNavigator
import com.tracki.ui.common.DoubleButtonDialog
import com.tracki.ui.common.OnClickListener
import com.tracki.ui.productdetails.ProductDetailsActivity
import com.tracki.ui.products.AddProductActivity
import com.tracki.ui.selectorder.*
import com.tracki.ui.selectorder.CatalogProductResponse
import com.tracki.ui.tasklisting.PaginationListener
import com.tracki.utils.*
import java.util.HashMap
import javax.inject.Inject

class ProductListActivity : BaseActivity<ActivityProductListBinding, ProductListViewModel>(),
    ProductListNavigator, SubCategoryAdapter.OnSubCategorySelected,
    ProductInStoreAdapter.OnProductSelectedListener {
    lateinit var binding: ActivityProductListBinding

    @Inject
    lateinit var productListViewModel: ProductListViewModel


    @Inject
    lateinit var mPref: PreferencesHelper

    @Inject
    lateinit var httpManager: HttpManager

    private var mMapCategory: Map<String, String>? = null
    var flavorId: String? = null

    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var mLayoutManager: LinearLayoutManager? = null
    private var rvProducts: RecyclerView? = null
    private var paginationRequest: PaginationRequest? = null

    private var globalSearch: Boolean = false

    enum class LOADBY {
        ACTIVE, INACTIVE, ALL
    }

    private var position: Int? = null
    private var isDelete = false

    var isInnerSunCat = false
    var subCatId: String? = null
    var cid: String? = null
    var subCat = false
    var rsubCat = false
    var stageAdapter: SubCategoryAdapter? = null
    lateinit var productAdapter: ProductInStoreAdapter


    companion object {
        private val TAG = ProductListActivity::class.java.simpleName
        fun newIntent(context: Context) = Intent(context, ProductListActivity::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        productListViewModel.navigator = this

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
                    setToolbar(binding.toolbar, "Products")

                }
            }

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
                if (TrackiApplication.getApiMap().containsKey(ApiType.SEARCH_PRODUCT)) {
                    globalSearch = true
                    binding.rvCategory.visibility = View.GONE
                    binding.rlSearchResult.visibility = View.VISIBLE
                    showLoading()
                    getProducts(null)
                }
            } else {
                TrackiToast.Message.showShort(this, "Please enter name")
            }


        }

        productAdapter = ProductInStoreAdapter(this)
        stageAdapter = SubCategoryAdapter(this)
        binding.stageAdapter = stageAdapter

        if(flavorId!=null)
        productListViewModel.getTerminalCategory(httpManager,null,flavorId)
        binding.ivAddProduct.setOnClickListener {
            addNewProduct()
        }
    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_product_list
    }

    override fun getViewModel(): ProductListViewModel {
        return productListViewModel
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }



    override fun handleSubCategoryResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }

    override fun handleTerminalCategoryResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?
    ) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@ProductListActivity)) {

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

        }
    }

    override fun handleProductResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        this.isLoading = false
        if (CommonUtils.handleResponse(callback, error, result, this@ProductListActivity)) {
            var jsonConverter = JSONConverter<CatalogProductResponse>()
            var responseMain: CatalogProductResponse = jsonConverter.jsonToObject(
                result.toString(),
                CatalogProductResponse::class.java
            ) as CatalogProductResponse
            if (responseMain.data != null && responseMain.data!!.isNotEmpty()) {
                binding.ivAddProduct.visibility=View.VISIBLE
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

                setRecyclerView()
                productAdapter!!.addItems(list)
                binding.rlSearch.visibility = View.VISIBLE
                CommonUtils.showLogMessage(
                    "e", "adapter total_count =>",
                    "" + productAdapter.itemCount
                )
                CommonUtils.showLogMessage(
                    "e", "fetch total_count =>",
                    "" + responseMain.count
                )
                isLastPage = productAdapter.itemCount >= responseMain.count

            } else {
                binding.ivAddProduct.visibility=View.GONE
                if (globalSearch) {

                    binding.rlSearchResult.visibility = View.VISIBLE
                    binding.tvCountSearchResult.text = "" + 0
                    binding.tvResults.text = " Search results for '${binding.etSearch.text}'"
                } else {
                    binding.tvCountSearchResult.text = ""
                    binding.tvResults.text = ""
                    binding.ivAddProduct.visibility=View.GONE
                }
                setRecyclerView()
                productAdapter!!.addItems(ArrayList())
            }
        } else {
            binding.ivAddProduct.visibility=View.GONE
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

    override fun handleDeleteProductCategoryResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?
    ) {
        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@ProductListActivity)) {
            if (!isDelete) {

            } else {
                productAdapter.removeAt(position!!, productAdapter.getAllList().size)
            }
            if (productAdapter!!.getAllList().isEmpty()) {
                binding.ivAddProduct.visibility = View.GONE
                binding.etSearch.visibility = View.GONE
            }
        }

    }

    override fun handleUpdateProductStatusResponse(
        callback: ApiCallback,
        result: Any?,
        error: APIError?
    ) {

        hideLoading()
        if (CommonUtils.handleResponse(callback, error, result, this@ProductListActivity)) {

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
            rvProducts!!.adapter = productAdapter
        }

        rvProducts!!.addOnScrollListener(object : PaginationListener(mLayoutManager!!) {
            override fun loadMoreItems() {
                this@ProductListActivity.isLoading = true
                if (cid != null) {
                    currentPage++
                    getProductFromFromServer(cid!!)
                }
            }

            override fun isLastPage(): Boolean {
                return this@ProductListActivity.isLastPage
            }

            override fun isLoading(): Boolean {
                return this@ProductListActivity.isLoading
            }
        })
    }

    private fun getProductFromFromServer(cid: String) {
        getProducts(cid)
    }

    private fun getProducts(cid: String?) {
        if (currentPage == PaginationListener.PAGE_START) {
            productAdapter.clearList()
            binding.rvProducts.removeAllViewsInLayout()
            // TODO: need to apply when nth level category check rvProducts!!.adapter = selectProductAdapter;)

        }

        paginationRequest = PaginationRequest()
        paginationRequest!!.limit = 10
        paginationRequest!!.offset = (currentPage - 1) * 10
        paginationRequest!!.dataCount = 10
        if (globalSearch) {
            var keyword = binding.etSearch.text.toString()
            paginationRequest!!.keyword = keyword
            productListViewModel.getProductByKeyWord(httpManager, paginationRequest)
        } else {
            this.cid = cid
            if (TrackiApplication.getApiMap().containsKey(ApiType.GET_PRODUCTS))
                productListViewModel.getProducts(httpManager, paginationRequest, flavorId, cid)
        }


    }

    override fun onCategorySelected(data: CataLogProductCategory) {
        if (data.cid != null)
            currentPage = PaginationListener.PAGE_START
        showLoading()
        getProductFromFromServer(data.cid!!)
    }

    override fun onEditProduct(product: CatalogProduct, position: Int) {
        if(mMapCategory!=null) {
            val addproduct = Intent(this, AddProductActivity::class.java)
            addproduct.putExtra(
                AppConstants.Extra.EXTRA_CATEGORIES,
                Gson().toJson(mMapCategory)
            )
            addproduct.putExtra("action","edit")
            addproduct.putExtra("data",product)
            startActivityForResult(addproduct,ADD_PRODUCT)
        }
    }

    override fun onProductClicked(product: CatalogProduct) {
        if(mMapCategory!=null) {
            var config = CommonUtils.getFlavourConfigFromFlavourId(flavorId, mPref)
            if (config != null && config!!.stock!!) {
                val addproduct = Intent(this, ProductDetailsActivity::class.java)
                addproduct.putExtra(
                    AppConstants.Extra.EXTRA_CATEGORIES,
                    Gson().toJson(mMapCategory)
                )
                addproduct.putExtra("data",product)
                addproduct.putExtra("action","edit")
                startActivityForResult(addproduct,ADD_PRODUCT)
            }

        }
    }

    override fun onRemovedProduct(product: CatalogProduct, position: Int) {
        val dialog = DoubleButtonDialog(this,
            true,
            null,
            getString(R.string.delete_product),
            getString(R.string.yes),
            getString(R.string.no),
            object : OnClickListener {
                override fun onClickCancel() {}
                override fun onClick() {
                    this@ProductListActivity.position = position
                    isDelete = true
                    productListViewModel.deleteProduct(httpManager, product)
                }
            })
        dialog.show()
    }

    override fun onStatusChange(product: CatalogProduct, position: Int) {
        this.position = position
        isDelete = false
        productListViewModel.updateStatusProductCategory(httpManager,product)
    }


    var ADD_PRODUCT=54875

    override fun addNewProduct() {
        if(mMapCategory!=null) {
            val addproduct = Intent(this, AddProductActivity::class.java)
            addproduct.putExtra(
                AppConstants.Extra.EXTRA_CATEGORIES,
                Gson().toJson(mMapCategory)
            )
            addproduct.putExtra("action","Add")
            if(cid!=null){
                addproduct.putExtra(
                    "cid",
                    cid
                )
            }
            startActivityForResult(addproduct,ADD_PRODUCT)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (resultCode) {
            Activity.RESULT_OK -> {
                if (requestCode == ADD_PRODUCT) {
                    currentPage = PaginationListener.PAGE_START
                    productAdapter.clearList()
                    binding.etSearch.setText("")
                    binding.rvProducts.removeAllViewsInLayout()
                    getProductFromFromServer(cid!!)
                }

            }
            Activity.RESULT_CANCELED -> {

            }
        }

    }

}