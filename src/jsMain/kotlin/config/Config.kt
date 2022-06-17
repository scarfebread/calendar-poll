package config

import kotlinx.browser.window

class Config {
    val runningLocally: Boolean = !window.location.protocol.contains("https")
    val host: String = if (runningLocally) "http://localhost:9090" else "https://scarfebread.co.uk"
    val port: Int = if (runningLocally) 9090 else 443
    val domain: String = if (runningLocally) "localhost" else "scarfebread.co.uk"
}