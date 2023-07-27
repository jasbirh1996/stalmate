package com.stalmate.user.modules.reels.player.holders

import com.stalmate.user.databinding.ItemFullViewReelBinding
import com.stalmate.user.databinding.ItemReelBinding


class VideoReelViewHolder(binding: ItemReelBinding) : ReelViewHolder(binding.root) {
    var isVideo = false
    val customPlayerView = binding.feedPlayerView;
    val tvUserName = binding.tvUserName;
    val imgUserProfile = binding.imgUserProfile;
    val likeCount = binding.like;
    val shareShareCount = binding.share;
    val commentCount = binding.comment;
    val likeIcon = binding.likeIcon

    val likeButton = binding.likeContainer;
    val shareButton = binding.shareContainer;
    val commentButton = binding.commentContainer;

    val soundIcon = binding.ivSoundButton;
    val tvStatusDescription = binding.tvStatusDescription;
    val tvStoryPostTime = binding.tvStoryPostTime;
    val videoThumbnail =
        binding.feedThumbnailView;
    val tvLocation =
        binding.tvLocation;
    val ivLocation =
        binding.ivLocation;
}


class VideoReelFullViewHolder(binding: ItemFullViewReelBinding) : ReelViewHolder(binding.root) {
    var isVideo = false
    val progressBarBuffering = binding.progressBarBuffering;
    val videoLayout = binding.videoLayout;
    val customPlayerView = binding.feedPlayerView;
    val tvUserName = binding.tvUserName;
    val imgUserProfile = binding.imgUserProfile;
    val ivLikeIcon = binding.ivLikeIcon;
    val likeCount = binding.tvLikeCount;
    val shareCount = binding.tvShareCount;
    val commentCount = binding.tvCommentCount;
    val buttonLike = binding.buttonLike;
    val buttonShare = binding.buttonShare;
    val buttonComment = binding.buttonComment;
    val tvStatusDescription = binding.tvStatusDescription;
    val tvStoryPostTime = binding.tvStoryPostTime;
    val videoThumbnail = binding.feedThumbnailView;
    val buttonAdd = binding.addPostButton;
    val ivMenu = binding.ivMenu;

    val ivSoundButton = binding.ivSoundButton;

    val tvLocation = binding.tvLocation;
    val ivLocation = binding.ivLocation;

    val ivMusicImage = binding.ivMusic;
    val tvMusic = binding.tvMusicName;
    val tvMusicArtist = binding.tvMusicArtist;
    val layoutMusic = binding.layoutMusic;

    val layoutTagged = binding.layoutTagged;
    val tvTaggedPeopleCount = binding.tvTaggedPeopleCount;
}