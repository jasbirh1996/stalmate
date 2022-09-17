package com.stalmate.user.view.dashboard.Friend

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityCategoryCreateBinding
import com.stalmate.user.view.dashboard.Friend.categoryadapter.AdapterCategory
import com.stalmate.user.view.language.AdapterLanguage

class ActivityCategoryCreate : BaseActivity(), AdapterCategory.Callbackk {

    private lateinit var binding : ActivityCategoryCreateBinding
    lateinit var adapterCategory: AdapterCategory


    override fun onClick(viewId: Int, view: View?) {

    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= DataBindingUtil.setContentView(this, R.layout.activity_category_create)

        /*toolbar*/
        setUpToolbar()

        /*getcateoryListing*/
        getCategoryListing()


        binding.popMenu.setOnClickListener {
            val popupmenu = PopupMenu(applicationContext, binding.popMenu)
            popupmenu.inflate(R.menu.menu_category)


            popupmenu.setOnMenuItemClickListener { item ->

                when(item!!.itemId){

                    R.id.category_A -> {

                    }

                }

                false
            }

            popupmenu.show()
        }

    }

    private fun getCategoryListing() {

        adapterCategory = AdapterCategory(networkViewModel, this, this)
        binding.rvCategoryList.adapter = adapterCategory

        networkViewModel.categoryFriendLiveData()
        networkViewModel.categoryFriendLiveData.observe(this) {

            adapterCategory.submitList(it!!.results)

        }

    }

    private fun setUpToolbar() {

        binding.toolbar.backButtonLeftText.visibility = View.VISIBLE
        binding.toolbar.backButtonLeftText.text = getString(R.string.category)

    }

    override fun onClickItem(id: String, name: String) {
        TODO("Not yet implemented")
    }

    override fun onClickDeleteItem(id: String, name: String) {
        TODO("Not yet implemented")
    }
}