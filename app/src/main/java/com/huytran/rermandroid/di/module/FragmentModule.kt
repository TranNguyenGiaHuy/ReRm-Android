package com.huytran.rermandroid.di.module

import com.huytran.rermandroid.fragment.*
import com.huytran.rermandroid.fragment.base.BaseFragment
import dagger.Module
import dagger.android.ContributesAndroidInjector

@Module
abstract class FragmentModule {

    @ContributesAndroidInjector
    abstract fun contributeBaseFragment(): BaseFragment

    @ContributesAndroidInjector
    abstract fun contributeTestFragment(): TestFragment

    @ContributesAndroidInjector
    abstract fun contributeExploreFragment(): ExploreFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileFragment(): ProfileFragment

    @ContributesAndroidInjector
    abstract fun contributeRoomDetailFragment(): RoomDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeProfileDetailFragment(): ProfileDetailFragment

    @ContributesAndroidInjector
    abstract fun contributeLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    abstract fun contributeSignupFragment(): SignupFragment

    @ContributesAndroidInjector
    abstract fun contributeCreatePostFragment(): CreatePostFragment

    @ContributesAndroidInjector
    abstract fun contributeManagePostFragment(): ManagePostFragment

    @ContributesAndroidInjector
    abstract fun contributeManageEditPostFragment(): EditPostFragment

    @ContributesAndroidInjector
    abstract fun contributeManageContractFragment(): ManageContractFragment

    @ContributesAndroidInjector
    abstract fun contributeSavedRoomFragment(): SavedRoomFragment

    @ContributesAndroidInjector
    abstract fun contributeChatFragment(): ChatFragment

    @ContributesAndroidInjector
    abstract fun contributeChatDialogFragment(): ChatDialogFragment

    @ContributesAndroidInjector
    abstract fun contributeDatePickerFragment(): DatePickerFragment

    @ContributesAndroidInjector
    abstract fun contributeNotificationFragment(): NotificationFragment

    @ContributesAndroidInjector
    abstract fun contributeFQAFragment(): FQAFragment

}