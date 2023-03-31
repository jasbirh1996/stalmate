package com.stalmate.user.view.dashboard.funtime

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.core.net.toUri
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.findNavController
import com.stalmate.user.R
import com.stalmate.user.base.BaseActivity
import com.stalmate.user.databinding.ActivityFuntimePostBinding
import com.stalmate.user.model.User
import com.stalmate.user.modules.reels.activity.ActivityFilter
import com.stalmate.user.utilities.Constants
import com.stalmate.user.view.dashboard.funtime.viewmodel.TagPeopleViewModel

class ActivityFuntimePost : BaseActivity() {
    lateinit var tagPeopleViewModel: TagPeopleViewModel
    lateinit var navController: NavController
    lateinit var binding: ActivityFuntimePostBinding
    val videoUri: Uri?
        get() = intent.getStringExtra(ActivityFilter.EXTRA_VIDEO)?.toUri()

    override fun onClick(viewId: Int, view: View?) {
    }

    lateinit var funtime: ResultFuntime
    var isEdit = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_funtime_post)!!
        setContentView(binding.root)
        tagPeopleViewModel = ViewModelProvider(this).get(TagPeopleViewModel::class.java)

        isEdit = intent.getBooleanExtra("isEdit", false)
        if (isEdit) {
            funtime = intent.getParcelableExtra<ResultFuntime>("data") as ResultFuntime
            funtime.tag_user.forEach {
                var user = User(
                    first_name = it.first_name,
                    last_name = it.last_name,
                    id = it._id,
                    profile_img1 = it.profile_img_1!!
                )
                Log.d("aklsjdasd", "aposdkasd")
                tagPeopleViewModel.addToList(user)
            }
        } else {
            tagPeopleViewModel.setPolicy(Constants.PRIVACY_TYPE_PUBLIC)
        }

        setUpNavigation()

    }


    override fun onBackPressed() {
        super.onBackPressed()
    }


    fun setUpNavigation() {
        navController = findNavController(R.id.nav_host_fragment)
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.funtime_post_navigation)
        if (isEdit) {
            graph.setStartDestination(R.id.FragmentFuntimePostEdit)
        } else {
            graph.setStartDestination(R.id.fragmentFuntimePost)
        }
        navController.graph = graph
    }
}