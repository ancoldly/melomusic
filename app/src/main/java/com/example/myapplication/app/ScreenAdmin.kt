package com.example.myapplication.app


sealed class ScreenAdmin(
    val route:String
) {
    object AdminHomeScreen : ScreenAdmin("adminHome_screen")
    object AdminAddMusicScreen : ScreenAdmin("adminAddMusic_screen")
    object AdminAddCategoryScreen : ScreenAdmin("adminAddCategory_screen")
    object AdminAddPodcastScreen : ScreenAdmin("adminAddPodcast_screen")

    object AdminMusicScreen : ScreenAdmin("adminMusic_screen")
    object AdminCategoryScreen : ScreenAdmin("adminCategory_screen")
    object AdminPodcastScreen : ScreenAdmin("adminPodcast_screen")

    object AdminUserScreen : ScreenAdmin("adminUser_screen")

    object AdminEditCategoryScreen : ScreenAdmin("adminEditCategory_screen")

    object AdminEditMusicScreen : ScreenAdmin("adminEditMusic_screen")

    object AdminEditPodcastScreen : ScreenAdmin("adminEditPodcast_screen")
}