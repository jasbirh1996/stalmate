package com.stalmate.user.modules.reels.player

import android.graphics.Rect
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.stalmate.user.commonadapters.AdapterFeed
import com.stalmate.user.modules.reels.adapter.ReelFullViewAdapter
import com.stalmate.user.modules.reels.player.holders.VideoReelFullViewHolder
import com.stalmate.user.modules.reels.player.holders.VideoReelViewHolder


class VideoAutoPlayHelper(var recyclerView: RecyclerView) {

    private var lastPlayerView: InstaLikePlayerView? = null
    val MIN_LIMIT_VISIBILITY =
        20; // When playerView will be less than 20% visible than it will stop the player

    var currentPlayingVideoItemPos = -1; // -1 indicates nothing playing

    fun onScrolled(recyclerView: RecyclerView, feedAdapter: RecyclerView.Adapter<*>) {
        val firstVisiblePosition: Int = findFirstVisibleItemPosition()
        val lastVisiblePosition: Int = findLastVisibleItemPosition()
        val pos = getMostVisibleItem(firstVisiblePosition, lastVisiblePosition)
        if (pos == -1) {
            /*check if current view is more than MIN_LIMIT_VISIBILITY*/
            if (currentPlayingVideoItemPos != -1) {
                val viewHolder: RecyclerView.ViewHolder =
                    recyclerView.findViewHolderForAdapterPosition(currentPlayingVideoItemPos)!!
                val currentVisibility = getVisiblePercentage(viewHolder);
                if (currentVisibility < MIN_LIMIT_VISIBILITY) {
                    lastPlayerView?.removePlayer()
                }
                currentPlayingVideoItemPos = -1;
            }
        } else {
            if (currentPlayingVideoItemPos != pos) {
                currentPlayingVideoItemPos = pos;
                attachVideoPlayerAt(pos);
            }
        }
    }

    private fun attachVideoPlayerAt(pos: Int) {
        if (recyclerView.adapter is ReelAdapter) {
            val feedViewHolder: VideoReelViewHolder =
                (recyclerView.findViewHolderForAdapterPosition(pos) as VideoReelViewHolder?)!!
            if (feedViewHolder is VideoReelViewHolder) {
                /** in case its a video**/
                if (lastPlayerView == null || (lastPlayerView != feedViewHolder.customPlayerView)) {
                    if (feedViewHolder.isVideo) {
                        feedViewHolder.customPlayerView.startPlaying()
                        // stop last player
                        lastPlayerView?.removePlayer();
                        lastPlayerView = feedViewHolder.customPlayerView;
                    } else {
                        /** in case its a image**/
                        if (lastPlayerView != null) {
                            // stop last player
                            lastPlayerView?.removePlayer();
                            lastPlayerView = null
                        }
                    }
                }
            }
        }
        if (recyclerView.adapter is AdapterFeed) {
            val feedViewHolder: AdapterFeed.FeedViewHolder =
                (recyclerView.findViewHolderForAdapterPosition(pos) as AdapterFeed.FeedViewHolder?)!!
            if (feedViewHolder is AdapterFeed.FeedViewHolder) {
                /** in case its a video**/
                if (lastPlayerView == null || (lastPlayerView != feedViewHolder.customPlayerView)) {
                    if (feedViewHolder.isVideo) {
                        feedViewHolder.customPlayerView.startPlaying()
                        // stop last player
                        lastPlayerView?.removePlayer();
                        lastPlayerView = feedViewHolder.customPlayerView;
                    } else {
                        /** in case its a image**/
                        if (lastPlayerView != null) {
                            // stop last player
                            lastPlayerView?.removePlayer();
                            lastPlayerView = null
                        }
                    }
                }
            }
        }
        if (recyclerView.adapter is ReelFullViewAdapter) {
            val feedViewHolder: VideoReelFullViewHolder = (recyclerView.findViewHolderForAdapterPosition(pos) as VideoReelFullViewHolder?)!!
            if (feedViewHolder is VideoReelFullViewHolder) {
                /** in case its a video**/
                if (lastPlayerView == null || (lastPlayerView != feedViewHolder.customPlayerView)) {
                    if (feedViewHolder.isVideo) {
                        feedViewHolder.customPlayerView.startPlaying()
                        // stop last player
                        lastPlayerView?.removePlayer()
                        lastPlayerView = feedViewHolder.customPlayerView;
                    } else {
                        /** in case its a image**/
                        if (lastPlayerView != null) {
                            // stop last player
                            lastPlayerView?.removePlayer();
                            lastPlayerView = null
                        }
                    }
                }
            }
        }
    }

    private fun getMostVisibleItem(firstVisiblePosition: Int, lastVisiblePosition: Int): Int {

        var maxPercentage = -1;
        var pos = -1;
        for (i in firstVisiblePosition..lastVisiblePosition) {
            val viewHolder: RecyclerView.ViewHolder =
                recyclerView.findViewHolderForAdapterPosition(i)!!

            val currentPercentage = getVisiblePercentage(viewHolder);
            if (currentPercentage > maxPercentage) {
                maxPercentage = currentPercentage.toInt();
                pos = i;
            }

        }

        if (maxPercentage == -1 || maxPercentage < MIN_LIMIT_VISIBILITY) {
            return -1;
        }

        return pos;
    }

    private fun getVisiblePercentage(
        holder: RecyclerView.ViewHolder
    ): Float {
        val rect_parent = Rect()
        recyclerView.getGlobalVisibleRect(rect_parent)
        val location = IntArray(2)
        holder.itemView.getLocationOnScreen(location)

        val rect_child = Rect(
            location[0],
            location[1],
            location[0] + holder.itemView.getWidth(),
            location[1] + holder.itemView.getHeight()
        )

        val rect_parent_area =
            ((rect_child.right - rect_child.left) * (rect_child.bottom - rect_child.top)).toFloat()
        val x_overlap = Math.max(
            0,
            Math.min(rect_child.right, rect_parent.right) - Math.max(
                rect_child.left,
                rect_parent.left
            )
        ).toFloat()
        val y_overlap = Math.max(
            0,
            Math.min(rect_child.bottom, rect_parent.bottom) - Math.max(
                rect_child.top,
                rect_parent.top
            )
        ).toFloat()
        val overlapArea = x_overlap * y_overlap
        val percent = overlapArea / rect_parent_area * 100.0f

        return percent
    }


    private fun findFirstVisibleItemPosition(): Int {
        if (recyclerView.layoutManager is LinearLayoutManager) {
            return (recyclerView.layoutManager as LinearLayoutManager?)!!.findFirstVisibleItemPosition()
        }

        return -1
    }

    private fun findLastVisibleItemPosition(): Int {
        if (recyclerView.layoutManager is LinearLayoutManager) {
            return (recyclerView.layoutManager as LinearLayoutManager).findLastVisibleItemPosition()
        }
        return -1
    }

    fun startObserving() {
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (recyclerView.adapter is ReelAdapter) {
                    onScrolled(recyclerView, recyclerView.adapter as ReelAdapter)
                }
                if (recyclerView.adapter is AdapterFeed) {
                    onScrolled(recyclerView, recyclerView.adapter as AdapterFeed)
                }
                if (recyclerView.adapter is ReelFullViewAdapter) {
                    onScrolled(recyclerView, recyclerView.adapter as ReelFullViewAdapter)
                }
            }
        })
    }
}