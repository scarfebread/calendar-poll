package config

import kotlinx.browser.window

class Config {
    val host: String = if (window.location.protocol.contains("https")) "https://scarfebread.co.uk" else "http://localhost:9090"
    val port: Int = if (window.location.protocol.contains("https")) 80 else 9090
    val domain: String = if (window.location.protocol.contains("https")) "scarfebread.co.uk" else "localhost"
}