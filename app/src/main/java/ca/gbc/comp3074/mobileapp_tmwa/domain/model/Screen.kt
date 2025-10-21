package ca.gbc.comp3074.mobileapp_tmwa.domain.model

enum class Screen(
    val route: String,
    val title: String
) {
    HOME(route = "home", title = "Home"),
    LOGIN(route = "login", title = "Login"),
    REGISTER(route = "register", title = "Register"),
    PROFILE(route = "profile", title = "Profile"),


}