package com.stalmate.user.modules.reels.player.holders

import com.stalmate.user.databinding.ItemReelBinding


class VideoReelViewHolder(binding: ItemReelBinding) : ReelViewHolder(binding.root) {
    val customPlayerView = binding.feedPlayerView;
    val tvUserName = binding.tvUserName;
    val imgUserProfile = binding.imgUserProfile;
    val like = binding.like;
    val share = binding.share;
    val comment = binding.comment;
    val tvStatusDescription = binding.tvStatusDescription;
    val tvStoryPostTime = binding.tvStoryPostTime;
    val videoThumbnail =
        binding.feedThumbnailView;




}