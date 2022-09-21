package com.rocketflyer.rocketflow.ui.productdetails


import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Html
import android.view.Gravity
import android.view.View
import android.widget.DatePicker
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.ViewPager
import com.google.android.material.snackbar.Snackbar
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.tracki.BR
import com.tracki.R
import com.tracki.data.local.prefs.PreferencesHelper
import com.tracki.data.network.APIError
import com.tracki.data.network.ApiCallback
import com.tracki.data.network.HttpManager
import com.tracki.databinding.ActivityProductDetailsBinding
import com.tracki.ui.base.BaseActivity
import com.tracki.ui.introscreens.IntroPagerAdapter
import com.tracki.ui.productlist.ProductInStoreAdapter
import com.tracki.ui.products.ProductDetails.Companion.offPercentage
import com.tracki.ui.products.ProductDetailsResponse
import com.tracki.ui.selectorder.CatalogProduct
import com.tracki.ui.selectorder.PaginationRequest
import com.tracki.ui.stockhistorydetails.StockHistoryDetailsActivity
import com.tracki.ui.tasklisting.PaginationListener
import com.tracki.utils.*
import com.tracki.utils.DateTimeUtil.Companion.getParsedDate
import dagger.android.AndroidInjector
import dagger.android.DispatchingAndroidInjector
import dagger.android.support.HasSupportFragmentInjector
import java.util.*
import javax.inject.Inject
import kotlin.collections.ArrayList

class ProductDetailsActivity :
    BaseActivity<ActivityProductDetailsBinding, ProductDetailsViewModel>(),
    ProductDetailsNavigator, HasSupportFragmentInjector, StockHistoryAdapter.OnStockHistoryDetails,
    StockPlacesAdapter.OnPlaceSelected{

    @Inject
    lateinit var mProductDetailsViewModel: ProductDetailsViewModel

    @Inject
    lateinit var mPref: PreferencesHelper

    private lateinit var binding: ActivityProductDetailsBinding

    @Inject
    lateinit var httpManager: HttpManager

    private var snackBar: Snackbar? = null

    @Inject
    lateinit var fragmentDispatchingAndroidInjector: DispatchingAndroidInjector<Fragment>

    private var mMapCategory: Map<String, String>? = null
    var flavorId: String? = null
    var pid: String? = null
    var geoId:String? = null

    private var currentPage = PaginationListener.PAGE_START
    private var isLastPage = false
    private var isLoading = false
    private var mLayoutManager: LinearLayoutManager? = null
    private var rvStocks: RecyclerView? = null
    private var rvPlaces: RecyclerView? = null
    private var paginationRequest: PaginationRequest? = null
    override fun networkAvailable() {
        if (snackBar != null) snackBar!!.dismiss()
    }

    private var product: CatalogProduct? = null

    lateinit var stockAdapter: StockHistoryAdapter
    lateinit var placeAdapter: StockPlacesAdapter

    private var fromDate: Long = 0
    private var toDate: Long = 0
    override fun networkUnavailable() {
        snackBar = CommonUtils.showNetWorkConnectionIssue(
            binding.coordinatorLayout,
            getString(R.string.please_check_your_internet_connection)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = viewDataBinding
        mProductDetailsViewModel.navigator = this
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


                }
            }

        }
        if (intent.hasExtra("data")) {
            product = intent.getParcelableExtra<CatalogProduct>("data")
        }
        if (product != null && !product!!.name.isNullOrEmpty()) {
            setToolbar(binding.toolbar, "${product!!.name}")
        } else {
            setToolbar(binding.toolbar, "Product")
        }
        stockAdapter = StockHistoryAdapter(this)
        placeAdapter = StockPlacesAdapter(this)
        setDatePrevious()
        if (product != null && product!!.pid != null) {
            showLoading()
            pid = product!!.pid
//                pid = "fa772d78-9de4-40a9-90b3-0f839175aca1"
            mProductDetailsViewModel.getProductDetails(httpManager, pid)
        }
        binding.tvDate.setOnClickListener {
            openDatePicker()
        }


    }

    private fun setDatePrevious() {
        val cal = Calendar.getInstance()
        if (mPref.getDefDateRange() == 0) {
            cal[Calendar.HOUR_OF_DAY] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.SECOND] = 0
        } else {
            cal.add(Calendar.DAY_OF_MONTH, -1 * mPref.getDefDateRange())
            cal[Calendar.HOUR_OF_DAY] = 0
            cal[Calendar.MINUTE] = 0
            cal[Calendar.SECOND] = 0
        }


        fromDate = cal.time.time
        toDate = toDateDateAdd24Hour()!!.getTime()
        binding.tvDate.setText(getParsedDate(fromDate) + " -To- " + getParsedDate(toDate))
    }

    private fun toDateDateAdd24Hour(): Date? {
        val cal = Calendar.getInstance()
        //cal.add(Calendar.DAY_OF_MONTH, preferencesHelper.getMaxDateRange());
        cal[Calendar.HOUR_OF_DAY] = 23
        cal[Calendar.MINUTE] = 59
        cal[Calendar.SECOND] = 0
        return cal.time
    }

    private fun openDatePicker() {
        val c = Calendar.getInstance()
        //c.add(Calendar.DAY_OF_MONTH, preferencesHelper.getMaxDateRange()==0?15:preferencesHelper.getMaxDateRange());
        if (fromDate != 0L) {
            c.timeInMillis = fromDate
        }
        c[Calendar.HOUR_OF_DAY] = 0
        c[Calendar.MINUTE] = 0
        c[Calendar.SECOND] = 0
        val mYear = c[Calendar.YEAR]
        val mMonth = c[Calendar.MONTH]
        val mDay = c[Calendar.DAY_OF_MONTH]
        var minTime = 0L
        val calMinTIme = Calendar.getInstance()
        if (mPref.getMaxPastDaysAllowed() != 0) calMinTIme.add(
            Calendar.DAY_OF_MONTH,
            -1 * mPref.getMaxPastDaysAllowed()
        )
        calMinTIme[Calendar.HOUR_OF_DAY] = 0
        calMinTIme[Calendar.MINUTE] = 0
        calMinTIme[Calendar.SECOND] = 0
        minTime = calMinTIme.timeInMillis
        CommonUtils.openDatePicker(
            this, mYear, mMonth,
            mDay, minTime, 0
        ) { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val calendar = Calendar.getInstance()
            calendar[Calendar.YEAR] = year
            calendar[Calendar.MONTH] = monthOfYear
            calendar[Calendar.DAY_OF_MONTH] = dayOfMonth
            calendar[Calendar.HOUR_OF_DAY] = 0
            calendar[Calendar.MINUTE] = 0
            calendar[Calendar.SECOND] = 0
            // fromDate=0;
            // tvFromDate.setText(DateTimeUtil.getParsedDate(calendar.getTimeInMillis()));
            // toDate = 0;
            CommonUtils.openDatePicker(
                this,
                mYear,
                mMonth,
                mDay,
                calendar.timeInMillis,
                0
            ) { view_: DatePicker?, yearEnd: Int, monthOfYearEnd: Int, dayOfMonthEnd: Int ->
                fromDate = calendar.timeInMillis
                val calEnd = Calendar.getInstance()
                calEnd[Calendar.YEAR] = yearEnd
                calEnd[Calendar.MONTH] = monthOfYearEnd
                calEnd[Calendar.DAY_OF_MONTH] = dayOfMonthEnd
                calEnd[Calendar.HOUR_OF_DAY] = 23
                calEnd[Calendar.MINUTE] = 59
                calEnd[Calendar.SECOND] = 0
                toDate = calEnd.timeInMillis
                binding.tvDate.setText(
                    getParsedDate(fromDate) + " -To- " + getParsedDate(toDate)
                )
                currentPage = PaginationListener.PAGE_START
                showLoading()
                if (pid != null)
                    getStockEntry(pid!!,null)
            }
        }

    }

    override fun getBindingVariable(): Int {
        return BR.viewModel
    }

    override fun getLayoutId(): Int {
        return R.layout.activity_product_details
    }

    override fun getViewModel(): ProductDetailsViewModel {
        return mProductDetailsViewModel
    }

    override fun handleResponse(callback: ApiCallback, result: Any?, error: APIError?) {
    }

    override fun handStockEntryResponse(callback: ApiCallback, result: Any?, error: APIError?) {
        hideLoading()
        this.isLoading = false
        if (CommonUtils.handleResponse(callback, error, result, this)) {
            val jsonConverter: JSONConverter<StockEntryResponse> = JSONConverter()
            var response: StockEntryResponse =
                jsonConverter.jsonToObject(result.toString(), StockEntryResponse::class.java)

            if (response.outputStock != null)
                binding.tvTotalStockOut.text = "" + response.outputStock
            if (response.inputStock != null)
                binding.tvTotalStockIn.text = "" + response.inputStock
            binding.llStockInOut.visibility=View.VISIBLE
            if (response.data != null && response.data!!.isNotEmpty()) {
                var list = response.data
                setRecyclerView()
                stockAdapter!!.addItems(list!!)
                CommonUtils.showLogMessage(
                    "e", "adapter total_count =>",
                    "" + stockAdapter.itemCount
                )
                CommonUtils.showLogMessage(
                    "e", "fetch total_count =>",
                    "" + response.count
                )
                isLastPage = stockAdapter.itemCount >= response.count
            }

        }else{
            binding.llStockInOut.visibility=View.GONE
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
            if (response.data != null) {
                binding.llMain.visibility = View.VISIBLE
                if(product!=null&&product!!.name==null){
                    setToolbar(binding.toolbar, "${response.data!!.name}")
                }
                if (response.data!!.name != null) {
                    binding.tvProductName.text = response.data!!.name
                }
                if (response.data!!.description != null && response.data!!.description!!.isNotEmpty()) {
                    binding.tvProductDescription.visibility = View.VISIBLE
                    binding.tvProductDescription.text = response.data!!.description
                } else {
                    binding.tvProductDescription.visibility = View.GONE
                }
                if (response.data!!.sellingPrice != null) {
                    binding.tvActPrice.text = "₹ " + response.data!!.sellingPrice
                } else {
                    binding.tvActPrice.visibility = View.GONE
                }
                if (response.data!!.price != null) {
                    binding.tvMrp.text = "MRP ₹ ${response.data!!.price} |"
                } else {
                    binding.tvActPrice.visibility = View.GONE
                }
                offPercentage(binding.tvOff, response.data)
                if (response.data!!.productStock != null) {
                    binding.llCurrentStock.visibility = View.VISIBLE
                    binding.tvNumberUnit.text = "${response.data!!.productStock!!.value} Unit"
                } else {
                    binding.llCurrentStock.visibility = View.GONE
                }

                if (response.data!!.stock) {
                    binding.cvStock.visibility = View.VISIBLE
                    if (pid != null) {
                        getStockEntry(pid!!,null)
                    }
                } else {
                    binding.cvStock.visibility = View.GONE
                }
                if (response.data!!.images != null && response.data!!.images!!.isNotEmpty()) {
                    binding.rlViewPager.visibility = View.VISIBLE
                    setImage(response.data!!.images!!)
                } else {
                    binding.rlViewPager.visibility = View.GONE
                }

                if(response.data!!.geoTagging!!){
                    if(response.data!!.geoMap != null){
                        rvPlaces = binding.rvPlaces
                    rvPlaces!!.visibility = View.VISIBLE
                    rvPlaces!!.adapter = placeAdapter

                    Log.i("GeoTagging","value: ${response.data!!.geoTagging}")
                    var mplaceList = ArrayList<GeoMappingPlaces>()
                        var data = GeoMappingPlaces()
                        data.geoId = null
                        data.geoName = "All"
                        data.selected=true
                        this.geoId = null
                        mplaceList.add(data)
                    for((key,value) in response.data!!.geoMap!!){
                        var data = GeoMappingPlaces()
                        data.geoId = key
                        data.geoName = value
//                        this.geoId = value
                        mplaceList.add(data)
                    }
                    binding.rvPlaces.visibility = View.VISIBLE
                    placeAdapter.addItems(mplaceList)
                    }
                }


            }

        }
    }

    private fun setRecyclerView() {
        if (rvStocks == null) {
            rvStocks = binding.rvStocks
            //  mLayoutManager= (LinearLayoutManager) rvAttendance.getLayoutManager();
            try {
                mLayoutManager = LinearLayoutManager(this)
                mLayoutManager!!.orientation = RecyclerView.VERTICAL
                rvStocks!!.layoutManager = mLayoutManager
                rvStocks!!.itemAnimator = DefaultItemAnimator()

                //Place list
            } catch (e: IllegalArgumentException) {
            }
            rvStocks!!.adapter = stockAdapter


        }
        binding.nestedScrollView!!.setOnScrollChangeListener(NestedScrollView.OnScrollChangeListener { v, scrollX, scrollY, oldScrollX, oldScrollY ->
            if (v.getChildAt(v.childCount - 1) != null) {
                if (scrollY >= v.getChildAt(v.childCount - 1).measuredHeight - v.measuredHeight &&
                    scrollY > oldScrollY
                ) {
                    val visibleItemCount = mLayoutManager!!.childCount
                    val totalItemCount = mLayoutManager!!.itemCount
                    val pastVisiblesItems = mLayoutManager!!.findFirstVisibleItemPosition()
                    if (!this@ProductDetailsActivity.isLoading && !this@ProductDetailsActivity.isLastPage) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            this@ProductDetailsActivity.isLoading = true
                            if (pid != null) {
                                currentPage++
                                getStockEntry(pid!!,null)
                            }
                        }
                    }
                }
            }
        })

    }

    private fun getStockEntry(pid: String,gid:String?) {
        if (currentPage == PaginationListener.PAGE_START) {
            stockAdapter.clearList()
            binding.rvStocks.removeAllViewsInLayout()

        }
        paginationRequest = PaginationRequest()
        paginationRequest!!.limit = 10
        paginationRequest!!.offset = (currentPage - 1) * 10
        paginationRequest!!.dataCount = 10
        mProductDetailsViewModel.getStockEntry(
            httpManager,
            pid,
            paginationRequest!!,
            fromDate,
            toDate,
            geoId = gid
        )
    }

    private lateinit var dots: Array<TextView?>

    private fun addBottomDots(currentPage: Int, size: Int) {
        if(size<=1)
            return
        dots = arrayOfNulls(size)


        binding.layoutDots.removeAllViews()
        for (i in 0 until dots.size) {
            dots[i] = TextView(this)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dots[i]?.text = Html.fromHtml("&#8226;", 0)
            } else {
                dots[i]?.text = Html.fromHtml("&#8226;")
            }
            dots[i]?.textSize = 60f
//            dots[i]?.setBackgroundResource(R.drawable.inactive_dot)
            dots[i]?.setTextColor(ContextCompat.getColor(this, R.color.slider_bolls))
            binding.layoutDots.addView(dots[i])
        }

        if (dots.isNotEmpty())
            dots[currentPage]?.setTextColor(Color.parseColor("#8E8E8E"))
//            dots[currentPage]?.setBackgroundResource(R.drawable.active_dot)
    }

    private fun setImage(list: ArrayList<String>) {
        addBottomDots(0, list.size)
        val mIntroPagerAdapter = IntroPagerAdapter(supportFragmentManager)
        for (url in list) {
            mIntroPagerAdapter.addFragment(ImageFragment.newInstance(url))
        }
        addBottomDots(0, list.size)

        binding.vpIntroScreens.adapter = mIntroPagerAdapter
        val NUM_PAGES = list.size
        val handler = Handler()
        val Update = Runnable {
            if (currentPage == NUM_PAGES) {
                currentPage = 0
            }
            binding.vpIntroScreens.setCurrentItem(currentPage++, true)
        }
        val swipeTimer = Timer()
        swipeTimer.schedule(object : TimerTask() {
            override fun run() {
                handler.post(Update)
            }
        }, 500, 3000)
        binding.vpIntroScreens.addOnPageChangeListener(viewPagerPageChangeListener)

        binding.vpIntroScreens.setPageTransformer(false,
            ViewPager.PageTransformer { view, position ->
                if (position <= -1.0F || position >= 1.0F) {
                    view.translationX = view.width * position;
                    view.alpha = 0.0F;
                } else if (position == 0.0F) {
                    view.translationX = view.width * position;
                    view.alpha = 1.0F;
                } else {
                    // position is between -1.0F & 0.0F OR 0.0F & 1.0F
                    view.translationX = view.width * -position;
                    view.alpha = 1.0F - Math.abs(position)
                }

            })

    }

    private var viewPagerPageChangeListener = object : ViewPager.OnPageChangeListener {

        override fun onPageSelected(position: Int) {

            currentPage = position
            addBottomDots(currentPage, dots.size)
        }

        override fun onPageScrolled(arg0: Int, arg1: Float, arg2: Int) {

        }

        override fun onPageScrollStateChanged(arg0: Int) {

        }
    }

    override fun supportFragmentInjector(): AndroidInjector<Fragment> {
        return fragmentDispatchingAndroidInjector
    }

    override fun onStockEntryClick(data: StockEntry) {
        var intent = Intent(this, StockHistoryDetailsActivity::class.java)
        intent.putExtra("pid", pid)
        intent.putExtra("day", data.date)
        startActivity(intent)
    }

    override fun onPlaceSelected(data: GeoMappingPlaces) {
        if(pid!=null){
            showLoading()
            currentPage = PaginationListener.PAGE_START
            getStockEntry(pid!!,data.geoId)
        }

    }


}