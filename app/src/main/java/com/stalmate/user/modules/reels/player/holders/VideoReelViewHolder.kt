package com.stalmate.user.modules.reels.player.holders

import com.stalmate.user.databinding.ItemFullViewReelBinding
import com.stalmate.user.databinding.ItemReelBinding


class VideoReelViewHolder(binding: ItemReelBinding) : ReelViewHolder(binding.root) {
    val customPlayerView = binding.feedPlayerView;
    val tvUserName = binding.tvUserName;
    val imgUserProfile = binding.imgUserProfile;
    val likeCount = binding.like;
    val shareShareCount = binding.share;
    val commentCount = binding.comment;
    val likeIcon=binding.likeIcon

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
    val tvTaggedPeopleCount =
        binding.tvTaggedPeopleCount;
    val layoutTagged =
        binding.layoutTagged;
    val tvMusic = binding.tvMusicName;
    val tvMusicArtist = binding.tvMusicArtist;
    val ivMusicImage = binding.ivMusic;
    val layoutMusic = binding.layoutMusic;
    val ivMenu = binding.ivMenu;
    val tvLocation =
        binding.tvLocation;
    val ivLocation =
        binding.ivLocation;
}