package com.stalmate.user.modules.reels.player.holders

import com.stalmate.user.databinding.ItemFullViewReelBinding
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



class VideoReelFullViewHolder(binding: ItemFullViewReelBinding) : ReelViewHolder(binding.root) {
    val customPlayerView = binding.feedPlayerView;
    val tvUserName = binding.tvUserName;
    val imgUserProfile = binding.imgUserProfile;
    val likeCount = binding.tvLikeCount;
    val shareCount = binding.tvShareCount;
    val commentCount = binding.tvCommentCount;
    val buttonLike = binding.buttonLike;
    val buttonShare = binding.buttonShare;
    val buttonComment = binding.buttonComment;
    val tvStatusDescription = binding.tvStatusDescription;
    val tvStoryPostTime = binding.tvStoryPostTime;
    val videoThumbnail =
        binding.feedThumbnailView;
    val buttonAdd = binding.addPostButton;

    val tvMusic = binding.tvMusicName;
    val tvMusicArtist = binding.tvMusicArtist;
    val ivMusicImage = binding.ivMusic;
    val layoutMusic = binding.layoutMusic;
    val ivMenu = binding.ivMenu;
}