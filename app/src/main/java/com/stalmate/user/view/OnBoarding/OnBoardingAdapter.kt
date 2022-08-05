package com.stalmate.user.view.OnBoarding

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.viewpager.widget.PagerAdapter
import com.bumptech.glide.Glide
import com.stalmate.user.R

class OnBoardingAdapter(private  var context : Context, private var onBoardingList : List<OnBoardingModel>):PagerAdapter() {
    override fun getCount(): Int {
        return onBoardingList.size
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return  view == `object`
    }


    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        //super.destroyItem(container, position, `object`)

        container.removeView(`object` as View)
    }


    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        //return super.instantiateItem(container, position)
        val view = LayoutInflater.from(context).inflate(R.layout.layout_splash , null)

        val img : ImageView = view.findViewById(R.id.img)
        val title : TextView = view.findViewById(R.id.titleTv)
        val description : TextView = view.findViewById(R.id.descriptionTV)

        Glide.with(context).asGif().load(onBoardingList[position].imageUrl).into(img)
        title.text = onBoardingList[position].title
        description.text = onBoardingList[position].desc

        container.addView(view)
        return view

    }
}