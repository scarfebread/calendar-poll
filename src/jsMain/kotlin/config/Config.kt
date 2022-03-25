package config

import kotlinx.browser.window

class Config {
    val host: String = if (window.location.protocol.contains("https")) "https://scarfebread.co.uk" else "http://localhost:9090"
}